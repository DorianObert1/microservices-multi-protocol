package com.microservices.payment.grpc;

import com.microservices.grpc.order.GetOrderRequest;
import com.microservices.grpc.order.OrderResponse;
import com.microservices.grpc.order.OrderServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class OrderGrpcClient {

    @GrpcClient("order-service")
    private OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub;

    public OrderResponse getOrder(Long orderId) {
        GetOrderRequest request = GetOrderRequest.newBuilder()
                .setId(orderId)
                .build();
        return orderServiceStub.getOrder(request);
    }
}

