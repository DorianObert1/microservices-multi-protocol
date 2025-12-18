package com.microservices.graphql.client;

import com.microservices.grpc.payment.*;
import com.microservices.graphql.model.PaymentModel;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentGrpcClient {

    @GrpcClient("payment-service")
    private PaymentServiceGrpc.PaymentServiceBlockingStub paymentServiceStub;

    public Optional<PaymentModel> getPayment(Long id) {
        try {
            GetPaymentRequest request = GetPaymentRequest.newBuilder().setId(id).build();
            PaymentResponse response = paymentServiceStub.getPayment(request);
            return Optional.of(mapToModel(response));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<PaymentModel> getPaymentsByOrder(Long orderId) {
        GetPaymentsByOrderRequest request = GetPaymentsByOrderRequest.newBuilder().setOrderId(orderId).build();
        PaymentsResponse response = paymentServiceStub.getPaymentsByOrder(request);
        return response.getPaymentsList().stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
    }

    public PaymentModel processPayment(Long orderId, Double amount, String currency, String paymentMethod) {
        ProcessPaymentRequest request = ProcessPaymentRequest.newBuilder()
                .setOrderId(orderId)
                .setAmount(amount)
                .setCurrency(currency)
                .setPaymentMethod(paymentMethod)
                .build();
        PaymentResponse response = paymentServiceStub.processPayment(request);
        return mapToModel(response);
    }

    private PaymentModel mapToModel(PaymentResponse response) {
        return PaymentModel.builder()
                .id(response.getId())
                .orderId(response.getOrderId())
                .amount(response.getAmount())
                .currency(response.getCurrency())
                .status(response.getStatus())
                .paymentMethod(response.getPaymentMethod())
                .transactionId(response.getTransactionId())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }
}

