package com.example.ecommerce.catalog.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ecommerce.catalog.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
