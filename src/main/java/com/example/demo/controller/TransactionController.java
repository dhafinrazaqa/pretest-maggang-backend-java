package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.TransactionDTO;
import com.example.demo.service.TransactionService;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/checkout/{userId}")
    public ResponseEntity<TransactionDTO> checkout(@PathVariable Long userId) {
        try {
            TransactionDTO transaction = transactionService.checkout(userId);
            return new ResponseEntity<>(transaction, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByUserId(@PathVariable Long userId) {
        List<TransactionDTO> transactions = transactionService.getTransactionsByUserId(userId);
        if (transactions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions/details/{transactionId}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long transactionId) {
        Optional<TransactionDTO> transaction = transactionService.getTransactionById(transactionId);
        return transaction.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
