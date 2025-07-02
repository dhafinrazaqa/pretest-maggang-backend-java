package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.TransactionItem;

@Repository
public interface TransactionItemRepository extends JpaRepository<TransactionItem, Long> {

}
