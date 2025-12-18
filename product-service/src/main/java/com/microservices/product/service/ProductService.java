package com.microservices.product.service;

import com.microservices.product.dto.ProductDto;
import com.microservices.product.exception.ResourceNotFoundException;
import com.microservices.product.model.Product;
import com.microservices.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        log.info("📋 Récupération de tous les produits");
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        log.info("🔍 Recherche produit ID: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    @Transactional(readOnly = true)
    public List<Product> findByIds(List<Long> ids) {
        log.info("🔍 Recherche produits IDs: {}", ids);
        return productRepository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public List<Product> findByCategory(String category) {
        log.info("🔍 Recherche produits catégorie: {}", category);
        return productRepository.findByCategory(category);
    }

    public Product create(ProductDto.CreateRequest request) {
        log.info("➕ Création produit: {}", request.getName());
        
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(request.getCategory())
                .available(request.getStock() > 0)
                .build();
        
        return productRepository.save(product);
    }

    public Product update(Long id, ProductDto.UpdateRequest request) {
        log.info("✏️ Mise à jour produit ID: {}", id);
        
        Product product = findById(id);
        
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getStock() != null) {
            product.setStock(request.getStock());
            product.setAvailable(request.getStock() > 0);
        }
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        
        return productRepository.save(product);
    }

    public Product updateStock(Long id, int quantity) {
        log.info("📦 Mise à jour stock ID: {}, qty: {}", id, quantity);
        
        Product product = findById(id);
        int newStock = product.getStock() + quantity;
        
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock insuffisant");
        }
        
        product.setStock(newStock);
        product.setAvailable(newStock > 0);
        
        return productRepository.save(product);
    }

    public void delete(Long id) {
        log.info("🗑️ Suppression produit ID: {}", id);
        Product product = findById(id);
        productRepository.delete(product);
    }

    public ProductDto.Response toResponse(Product product) {
        return ProductDto.Response.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .available(product.isAvailable())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
