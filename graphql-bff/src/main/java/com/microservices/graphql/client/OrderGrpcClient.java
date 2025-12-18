package com.microservices.graphql.client;

import com.microservices.grpc.order.*;
import com.microservices.graphql.model.OrderItemModel;
import com.microservices.graphql.model.OrderModel;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderGrpcClient {

    @GrpcClient("order-service")
    private OrderServiceGrpc.OrderServiceBlockingStub orderStub;

    public List<OrderModel> getAllOrders() {
        log.info("📡 [GraphQL→gRPC] GetAllOrders");
        try {
            OrdersResponse response = orderStub.getAllOrders(GetAllOrdersRequest.newBuilder().build());
            return response.getOrdersList().stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        } catch (StatusRuntimeException e) {
            log.error("❌ Erreur: {}", e.getStatus());
            return Collections.emptyList();
        }
    }

    public Optional<OrderModel> getOrder(Long id) {
        log.info("📡 [GraphQL→gRPC] GetOrder({})", id);
        try {
            OrderResponse response = orderStub.getOrder(GetOrderRequest.newBuilder().setId(id).build());
            return Optional.of(toModel(response.getOrder()));
        } catch (StatusRuntimeException e) {
            log.error("❌ Order non trouvé: {}", e.getStatus());
            return Optional.empty();
        }
    }

    public List<OrderModel> getOrdersByUser(Long userId) {
        log.info("📡 [GraphQL→gRPC] GetOrdersByUser({})", userId);
        try {
            OrdersResponse response = orderStub.getOrdersByUser(
                    GetOrdersByUserRequest.newBuilder().setUserId(userId).build());
            return response.getOrdersList().stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        } catch (StatusRuntimeException e) {
            log.error("❌ Erreur: {}", e.getStatus());
            return Collections.emptyList();
        }
    }

    public OrderModel createOrder(Long userId, List<CreateOrderItemRequest> items, String shippingAddress) {
        log.info("📡 [GraphQL→gRPC] CreateOrder(userId={})", userId);
        OrderResponse response = orderStub.createOrder(CreateOrderRequest.newBuilder()
                .setUserId(userId)
                .addAllItems(items)
                .setShippingAddress(shippingAddress)
                .build());
        return toModel(response.getOrder());
    }

    public OrderModel updateOrderStatus(Long id, String status) {
        log.info("📡 [GraphQL→gRPC] UpdateOrderStatus({}, {})", id, status);
        OrderResponse response = orderStub.updateOrderStatus(UpdateOrderStatusRequest.newBuilder()
                .setId(id)
                .setStatus(OrderStatus.valueOf(status))
                .build());
        return toModel(response.getOrder());
    }

    public boolean cancelOrder(Long id) {
        log.info("📡 [GraphQL→gRPC] CancelOrder({})", id);
        CancelResponse response = orderStub.cancelOrder(
                CancelOrderRequest.newBuilder().setId(id).build());
        return response.getSuccess();
    }

    private OrderModel toModel(Order order) {
        List<OrderItemModel> items = order.getItemsList().stream()
                .map(item -> OrderItemModel.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderModel.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .items(items)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
