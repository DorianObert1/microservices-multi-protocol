package com.microservices.product.controller;

import com.microservices.product.dto.ProductDto;
import com.microservices.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProductRestController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto.Response>> getAllProducts() {
        log.info("🌐 [REST] GET /api/products");
        List<ProductDto.Response> products = productService.findAll()
                .stream()
                .map(productService::toResponse)
                .toList();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto.Response> getProductById(@PathVariable Long id) {
        log.info("🌐 [REST] GET /api/products/{}", id);
        return ResponseEntity.ok(productService.toResponse(productService.findById(id)));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDto.Response>> getByCategory(@PathVariable String category) {
        log.info("🌐 [REST] GET /api/products/category/{}", category);
        List<ProductDto.Response> products = productService.findByCategory(category)
                .stream()
                .map(productService::toResponse)
                .toList();
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<ProductDto.Response> createProduct(@Valid @RequestBody ProductDto.CreateRequest request) {
        log.info("🌐 [REST] POST /api/products - name: {}", request.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.toResponse(productService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto.Response> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDto.UpdateRequest request) {
        log.info("🌐 [REST] PUT /api/products/{}", id);
        return ResponseEntity.ok(productService.toResponse(productService.update(id, request)));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductDto.Response> updateStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        log.info("🌐 [REST] PATCH /api/products/{}/stock - quantity: {}", id, quantity);
        return ResponseEntity.ok(productService.toResponse(productService.updateStock(id, quantity)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("🌐 [REST] DELETE /api/products/{}", id);
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
