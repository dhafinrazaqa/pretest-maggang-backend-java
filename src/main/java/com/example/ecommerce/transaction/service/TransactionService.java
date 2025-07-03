package com.example.ecommerce.transaction.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.ecommerce.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ecommerce.transaction.dto.TransactionDTO;
import com.example.ecommerce.transaction.mapper.TransactionMapper;
import com.example.ecommerce.cart.model.Cart;
import com.example.ecommerce.cart.model.CartItem;
import com.example.ecommerce.catalog.model.Product;
import com.example.ecommerce.transaction.model.Transaction;
import com.example.ecommerce.transaction.model.TransactionItem;
import com.example.ecommerce.user.model.User;
import com.example.ecommerce.cart.repository.CartRepository;
import com.example.ecommerce.catalog.repository.ProductRepository;
import com.example.ecommerce.transaction.repository.TransactionItemRepository;
import com.example.ecommerce.transaction.repository.TransactionRepository;
import com.example.ecommerce.user.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionItemRepository transactionItemRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;
    private final CartRepository cartRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
            TransactionItemRepository transactionItemRepository,
            CartService cartService,
            ProductRepository productRepository,
            UserRepository userRepository,
            TransactionMapper transactionMapper,
            CartRepository cartRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionItemRepository = transactionItemRepository;
        this.cartService = cartService;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.transactionMapper = transactionMapper;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public TransactionDTO checkout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Cart cart = cartService.getOrCreateActiveCart(userId);

        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty for user: " + userId);
        }

        BigDecimal totalTransactionPrice = BigDecimal.ZERO;
        List<TransactionItem> newTransactionItems = new ArrayList<>();

        for (CartItem cartItem : cart.getCartItems()) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProduct().getId()));

            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product " + product.getName() +
                        ". Available: " + product.getStock() + ", Requested: " + cartItem.getQuantity());
            }

            TransactionItem transactionItem = new TransactionItem();
            transactionItem.setProductId(product.getId());
            transactionItem.setProductName(product.getName());
            transactionItem.setQuantity(cartItem.getQuantity());
            transactionItem.setPrice(product.getPrice());

            newTransactionItems.add(transactionItem);

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setStatus("COMPLETED");
        transaction.setTransactionItems(newTransactionItems);
        transaction.setTotalPrice(calculateTotalPrice(newTransactionItems));

        for (TransactionItem item : newTransactionItems) {
            item.setTransaction(transaction);
        }

        Transaction savedTransaction = transactionRepository.save(transaction);

        cart.getCartItems().clear();
        cartRepository.save(cart);

        return transactionMapper.toDto(savedTransaction);
    }

    private BigDecimal calculateTotalPrice(List<TransactionItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<TransactionDTO> getTransactionsByUserId(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return transactions.stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<TransactionDTO> getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .map(transactionMapper::toDto);
    }
}
