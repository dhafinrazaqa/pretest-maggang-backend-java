package com.example.demo.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TransactionItemDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}
