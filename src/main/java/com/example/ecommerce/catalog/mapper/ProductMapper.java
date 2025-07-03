package com.example.ecommerce.catalog.mapper;

import org.springframework.stereotype.Component;

import com.example.ecommerce.catalog.dto.ProductDTO;
import com.example.ecommerce.catalog.model.Product;

@Component
public class ProductMapper {

    public ProductDTO toDto(Product product) {
        if (product == null) {
            return null;
        }
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        return dto;
    }

    public Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }
        Product entity = new Product();

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setStock(dto.getStock());
        return entity;
    }
}
