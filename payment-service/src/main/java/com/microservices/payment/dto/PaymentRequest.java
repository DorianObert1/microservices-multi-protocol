package com.microservices.payment.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long orderId;
    private Double amount;
    private String currency;
    private String paymentMethod;
}

