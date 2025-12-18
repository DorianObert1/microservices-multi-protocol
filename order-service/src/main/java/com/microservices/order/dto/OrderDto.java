package com.microservices.order.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ItemResponse {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long userId;
        private String username;
        private List<ItemResponse> items;
        private BigDecimal totalAmount;
        private String status;
        private String shippingAddress;
        private LocalDateTime createdAt;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ItemRequest {
        @NotNull(message = "Product ID requis")
        private Long productId;
        
        @NotNull(message = "Quantité requise")
        @Min(value = 1, message = "Quantité minimum: 1")
        private Integer quantity;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CreateRequest {
        @NotNull(message = "User ID requis")
        private Long userId;
        
        @NotEmpty(message = "Au moins un produit requis")
        private List<ItemRequest> items;
        
        @NotBlank(message = "Adresse de livraison requise")
        private String shippingAddress;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UpdateStatusRequest {
        @NotBlank(message = "Statut requis")
        private String status;
    }
}
