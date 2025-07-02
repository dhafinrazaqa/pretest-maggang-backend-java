package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class TransactionDTO {
    private Long id;
    private Long userId;
    private String status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private List<TransactionItemDTO> items;
}
