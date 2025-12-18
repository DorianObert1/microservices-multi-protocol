package com.microservices.product.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDto {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer stock;
        private String category;
        private boolean available;
        private LocalDateTime createdAt;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Nom requis")
        private String name;
        
        private String description;
        
        @NotNull(message = "Prix requis")
        @Positive(message = "Prix doit être positif")
        private BigDecimal price;
        
        @NotNull(message = "Stock requis")
        @Min(value = 0, message = "Stock ne peut pas être négatif")
        private Integer stock;
        
        private String category;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdateRequest {
        private String name;
        private String description;
        @Positive private BigDecimal price;
        @Min(0) private Integer stock;
        private String category;
    }
}
