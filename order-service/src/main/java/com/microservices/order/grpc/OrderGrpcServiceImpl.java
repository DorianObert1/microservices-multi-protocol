package com.microservices.order.grpc;

import com.microservices.grpc.order.*;
import com.microservices.order.dto.OrderDto;
import com.microservices.order.model.Order;
import com.microservices.order.model.OrderItem;
import com.microservices.order.service.OrderService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class OrderGrpcServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderService orderService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void getOrder(GetOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        log.info("⚡ [gRPC] GetOrder - ID: {}", request.getId());
        try {
            Order order = orderService.findById(request.getId());
            responseObserver.onNext(OrderResponse.newBuilder()
                    .setOrder(toProto(order))
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getOrdersByUser(GetOrdersByUserRequest request, StreamObserver<OrdersResponse> responseObserver) {
        log.info("⚡ [gRPC] GetOrdersByUser - userId: {}", request.getUserId());
        try {
            List<Order> orders = orderService.findByUserId(request.getUserId());
            OrdersResponse.Builder builder = OrdersResponse.newBuilder().setTotal(orders.size());
            orders.forEach(o -> builder.addOrders(toProto(o)));
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getAllOrders(GetAllOrdersRequest request, StreamObserver<OrdersResponse> responseObserver) {
        log.info("⚡ [gRPC] GetAllOrders");
        try {
            List<Order> orders = orderService.findAll();
            OrdersResponse.Builder builder = OrdersResponse.newBuilder().setTotal(orders.size());
            orders.forEach(o -> builder.addOrders(toProto(o)));
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void createOrder(CreateOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        log.info("⚡ [gRPC] CreateOrder - userId: {}", request.getUserId());
        try {
            List<OrderDto.ItemRequest> items = request.getItemsList().stream()
                    .map(i -> OrderDto.ItemRequest.builder()
                            .productId(i.getProductId())
                            .quantity(i.getQuantity())
                            .build())
                    .collect(Collectors.toList());

            OrderDto.CreateRequest createRequest = OrderDto.CreateRequest.builder()
                    .userId(request.getUserId())
                    .items(items)
                    .shippingAddress(request.getShippingAddress())
                    .build();

            Order order = orderService.createOrder(createRequest);
            responseObserver.onNext(OrderResponse.newBuilder().setOrder(toProto(order)).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void updateOrderStatus(UpdateOrderStatusRequest request, StreamObserver<OrderResponse> responseObserver) {
        log.info("⚡ [gRPC] UpdateOrderStatus - ID: {}, status: {}", request.getId(), request.getStatus());
        try {
            Order order = orderService.updateStatus(request.getId(), request.getStatus().name());
            responseObserver.onNext(OrderResponse.newBuilder().setOrder(toProto(order)).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void cancelOrder(CancelOrderRequest request, StreamObserver<CancelResponse> responseObserver) {
        log.info("⚡ [gRPC] CancelOrder - ID: {}", request.getId());
        try {
            orderService.cancelOrder(request.getId());
            responseObserver.onNext(CancelResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Commande annulée")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(CancelResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    private com.microservices.grpc.order.Order toProto(Order order) {
        com.microservices.grpc.order.Order.Builder builder = com.microservices.grpc.order.Order.newBuilder()
                .setId(order.getId())
                .setUserId(order.getUserId())
                .setTotalAmount(order.getTotalAmount().doubleValue())
                .setStatus(OrderStatus.valueOf(order.getStatus()))
                .setShippingAddress(order.getShippingAddress() != null ? order.getShippingAddress() : "")
                .setCreatedAt(order.getCreatedAt() != null ? order.getCreatedAt().format(FORMATTER) : "")
                .setUpdatedAt(order.getUpdatedAt() != null ? order.getUpdatedAt().format(FORMATTER) : "");

        for (OrderItem item : order.getItems()) {
            builder.addItems(com.microservices.grpc.order.OrderItem.newBuilder()
                    .setProductId(item.getProductId())
                    .setProductName(item.getProductName() != null ? item.getProductName() : "")
                    .setQuantity(item.getQuantity())
                    .setUnitPrice(item.getUnitPrice().doubleValue())
                    .setTotalPrice(item.getTotalPrice().doubleValue())
                    .build());
        }

        return builder.build();
    }
}
