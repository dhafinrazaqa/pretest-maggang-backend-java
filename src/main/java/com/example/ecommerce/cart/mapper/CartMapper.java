package com.example.ecommerce.cart.mapper;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.ecommerce.cart.dto.CartDTO;
import com.example.ecommerce.cart.model.Cart;

@Component
public class CartMapper {

    private final CartItemMapper cartItemMapper;

    @Autowired
    public CartMapper(CartItemMapper cartItemMapper) {
        this.cartItemMapper = cartItemMapper;
    }

    public CartDTO toDto(Cart cart) {
        if (cart == null)
            return null;

        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser().getId());
        dto.setCreatedAt(cart.getCreatedAt());

        dto.setItems(cart.getCartItems().stream()
                .map(cartItemMapper::toDto)
                .collect(Collectors.toList()));

        double totalPrice = dto.getItems().stream()
                .mapToDouble(item -> item.getItemPrice() * item.getQuantity())
                .sum();
        dto.setTotalPrice(totalPrice);

        return dto;
    }
}
