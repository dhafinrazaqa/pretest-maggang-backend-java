package com.example.demo.mapper;

import org.springframework.stereotype.Component;

import com.example.demo.dto.CartItemDTO;
import com.example.demo.model.CartItem;

@Component
public class CartItemMapper {

    public CartItemDTO toDto(CartItem cartItem) {
        if (cartItem == null)
            return null;

        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setProductId(cartItem.getProduct().getId());
        dto.setProductName(cartItem.getProduct().getName());
        dto.setQuantity(cartItem.getQuantity());
        dto.setItemPrice(cartItem.getProduct().getPrice().doubleValue());

        return dto;
    }
}
