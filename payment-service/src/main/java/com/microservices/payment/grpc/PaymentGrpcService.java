package com.microservices.payment.grpc;

import com.microservices.grpc.payment.*;
import com.microservices.payment.dto.PaymentDto;
import com.microservices.payment.dto.PaymentRequest;
import com.microservices.payment.model.PaymentStatus;
import com.microservices.payment.service.PaymentService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class PaymentGrpcService extends PaymentServiceGrpc.PaymentServiceImplBase {

    private final PaymentService paymentService;

    @Override
    public void processPayment(ProcessPaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(request.getOrderId());
        paymentRequest.setAmount(request.getAmount());
        paymentRequest.setCurrency(request.getCurrency());
        paymentRequest.setPaymentMethod(request.getPaymentMethod());

        PaymentDto paymentDto = paymentService.processPayment(paymentRequest);
        responseObserver.onNext(mapToProto(paymentDto));
        responseObserver.onCompleted();
    }

    @Override
    public void getPayment(GetPaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {
        PaymentDto paymentDto = paymentService.getPaymentById(request.getId());
        responseObserver.onNext(mapToProto(paymentDto));
        responseObserver.onCompleted();
    }

    @Override
    public void getPaymentsByOrder(GetPaymentsByOrderRequest request, StreamObserver<PaymentsResponse> responseObserver) {
        List<PaymentDto> payments = paymentService.getPaymentsByOrderId(request.getOrderId());
        PaymentsResponse response = PaymentsResponse.newBuilder()
                .addAllPayments(payments.stream().map(this::mapToProto).collect(Collectors.toList()))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updatePaymentStatus(UpdatePaymentStatusRequest request, StreamObserver<PaymentResponse> responseObserver) {
        PaymentDto paymentDto = paymentService.updatePaymentStatus(request.getId(), PaymentStatus.valueOf(request.getStatus()));
        responseObserver.onNext(mapToProto(paymentDto));
        responseObserver.onCompleted();
    }

    @Override
    public void refundPayment(RefundPaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {
        PaymentDto paymentDto = paymentService.refundPayment(request.getId());
        responseObserver.onNext(mapToProto(paymentDto));
        responseObserver.onCompleted();
    }

    private PaymentResponse mapToProto(PaymentDto dto) {
        return PaymentResponse.newBuilder()
                .setId(dto.getId())
                .setOrderId(dto.getOrderId())
                .setAmount(dto.getAmount())
                .setCurrency(dto.getCurrency())
                .setStatus(dto.getStatus().name())
                .setPaymentMethod(dto.getPaymentMethod())
                .setTransactionId(dto.getTransactionId())
                .setCreatedAt(dto.getCreatedAt().toString())
                .setUpdatedAt(dto.getUpdatedAt().toString())
                .build();
    }
}

