package com.example.ecommerce.cart.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class CartDTO {
    private Long id;
    private Long userId;
    private LocalDateTime createdAt;
    private List<CartItemDTO> items;
    private Double totalPrice;
}
