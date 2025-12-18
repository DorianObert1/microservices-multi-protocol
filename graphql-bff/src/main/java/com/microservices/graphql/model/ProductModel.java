package com.microservices.graphql.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductModel {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String category;
    private boolean available;
    private String createdAt;
}
