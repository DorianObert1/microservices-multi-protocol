package com.microservices.graphql.model;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderModel {
    private Long id;
    private Long userId;
    private List<OrderItemModel> items;
    private Double totalAmount;
    private String status;
    private String shippingAddress;
    private String createdAt;
}
