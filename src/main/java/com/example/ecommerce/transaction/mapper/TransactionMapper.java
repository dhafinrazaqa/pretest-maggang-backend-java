package com.example.ecommerce.transaction.mapper;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.ecommerce.transaction.dto.TransactionDTO;
import com.example.ecommerce.transaction.model.Transaction;

@Component
public class TransactionMapper {

    private final TransactionItemMapper transactionItemMapper;

    @Autowired
    public TransactionMapper(TransactionItemMapper transactionItemMapper) {
        this.transactionItemMapper = transactionItemMapper;
    }

    public TransactionDTO toDto(Transaction transaction) {
        if (transaction == null)
            return null;

        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setUserId(transaction.getUser().getId());
        dto.setStatus(transaction.getStatus());
        dto.setTotalPrice(transaction.getTotalPrice());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setItems(transaction.getTransactionItems().stream()
                .map(transactionItemMapper::toDto)
                .collect(Collectors.toList()));
        return dto;
    }
}
