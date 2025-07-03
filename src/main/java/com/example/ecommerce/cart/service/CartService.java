package com.example.ecommerce.cart.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ecommerce.cart.dto.CartDTO;
import com.example.ecommerce.cart.mapper.CartMapper;
import com.example.ecommerce.cart.model.Cart;
import com.example.ecommerce.cart.model.CartItem;
import com.example.ecommerce.catalog.model.Product;
import com.example.ecommerce.user.model.User;
import com.example.ecommerce.cart.repository.CartItemRepository;
import com.example.ecommerce.cart.repository.CartRepository;
import com.example.ecommerce.catalog.repository.ProductRepository;
import com.example.ecommerce.user.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
            ProductRepository productRepository, UserRepository userRepository,
            CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
    }

    @Transactional
    public Cart getOrCreateActiveCart(Long userId) {
        Optional<Cart> existingCart = cartRepository.findByUserId(userId);

        if (existingCart.isPresent()) {
            return existingCart.get();
        } else {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

            Cart newCart = new Cart();
            newCart.setUser(user);

            return cartRepository.save(newCart);
        }
    }

    @Transactional
    public CartDTO addItemToCart(Long userId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        Cart cart = getOrCreateActiveCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        if (product.getStock() < quantity) {
            throw new RuntimeException(
                    "Not enough stock for product: " + product.getName() + ". Available: " + product.getStock());
        }

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            if (product.getStock() < cartItem.getQuantity() + quantity) {
                throw new RuntimeException("Adding " + quantity + " would exceed stock. Available after current cart: "
                        + (product.getStock() - cartItem.getQuantity()));
            }
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cart.addCartItem(cartItem);
        }

        cartItemRepository.save(cartItem);

        Cart updatedCart = cartRepository.save(cart);
        return cartMapper.toDto(updatedCart);
    }

    @Transactional
    public CartDTO removeItemFromCart(Long userId, Long productId) {
        Cart cart = getOrCreateActiveCart(userId);

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            Product product = cartItem.getProduct();

            cart.removeCartItem(cartItem);
            cartItemRepository.delete(cartItem);

            Cart updatedCart = cartRepository.save(cart);
            return cartMapper.toDto(updatedCart);
        } else {
            throw new RuntimeException("Item not found in cart for product ID: " + productId);
        }
    }

    @Transactional
    public CartDTO getCartDTOByUserId(Long userId) {
        Cart cart = getOrCreateActiveCart(userId);
        return cartMapper.toDto(cart);
    }

    @Transactional
    public CartDTO updateCartItemQuantity(Long userId, Long productId, int newQuantity) {
        if (newQuantity <= 0) {
            return removeItemFromCart(userId, productId);
        }

        Cart cart = getOrCreateActiveCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            int oldQuantity = cartItem.getQuantity();

            if (product.getStock() + oldQuantity < newQuantity) {
                throw new RuntimeException("Not enough stock for product: " + product.getName()
                        + ". Available after current items: " + (product.getStock() + oldQuantity - newQuantity));
            }

            cartItem.setQuantity(newQuantity);
            cartItemRepository.save(cartItem);

            Cart updatedCart = cartRepository.save(cart);
            return cartMapper.toDto(updatedCart);
        } else {
            throw new RuntimeException("Item not found in cart. Use 'add' to add new items.");
        }
    }
}
