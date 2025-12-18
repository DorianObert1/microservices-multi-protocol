package com.microservices.payment.service;

import com.microservices.payment.dto.PaymentDto;
import com.microservices.payment.dto.PaymentRequest;
import com.microservices.payment.grpc.OrderGrpcClient;
import com.microservices.payment.model.Payment;
import com.microservices.payment.model.PaymentStatus;
import com.microservices.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderGrpcClient orderGrpcClient;

    @Transactional
    public PaymentDto processPayment(PaymentRequest request) {
        // Verify order exists
        try {
            orderGrpcClient.getOrder(request.getOrderId());
        } catch (Exception e) {
            throw new RuntimeException("Order not found with id: " + request.getOrderId());
        }

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .transactionId(UUID.randomUUID().toString())
                .build();

        // Simulate payment processing
        // In a real world, we would call a payment gateway here
        // For now, let's assume it's successful if amount > 0
        if (payment.getAmount() > 0) {
            payment.setStatus(PaymentStatus.COMPLETED);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        Payment savedPayment = paymentRepository.save(payment);
        return mapToDto(savedPayment);
    }

    public PaymentDto getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        return mapToDto(payment);
    }

    public List<PaymentDto> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<PaymentDto> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentDto updatePaymentStatus(Long id, PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

        // State machine validation could go here
        payment.setStatus(status);
        Payment savedPayment = paymentRepository.save(payment);
        return mapToDto(savedPayment);
    }

    @Transactional
    public PaymentDto refundPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            payment.setStatus(PaymentStatus.REFUNDED);
        } else {
            throw new RuntimeException("Cannot refund payment with status: " + payment.getStatus());
        }

        Payment savedPayment = paymentRepository.save(payment);
        return mapToDto(savedPayment);
    }

    private PaymentDto mapToDto(Payment payment) {
        return PaymentDto.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}

