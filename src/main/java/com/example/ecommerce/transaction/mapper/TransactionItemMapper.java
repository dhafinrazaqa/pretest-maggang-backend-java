package com.example.ecommerce.transaction.mapper;

import org.springframework.stereotype.Component;

import com.example.ecommerce.transaction.dto.TransactionItemDTO;
import com.example.ecommerce.transaction.model.TransactionItem;

@Component
public class TransactionItemMapper {
    public TransactionItemDTO toDto(TransactionItem transactionItem) {
        if (transactionItem == null)
            return null;
        TransactionItemDTO dto = new TransactionItemDTO();
        dto.setId(transactionItem.getId());
        dto.setProductId(transactionItem.getProductId());
        dto.setProductName(transactionItem.getProductName());
        dto.setQuantity(transactionItem.getQuantity());
        dto.setPrice(transactionItem.getPrice());
        return dto;
    }
}
