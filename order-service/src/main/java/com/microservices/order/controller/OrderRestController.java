package com.microservices.order.controller;

import com.microservices.order.dto.OrderDto;
import com.microservices.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OrderRestController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDto.Response>> getAllOrders() {
        log.info("🌐 [REST] GET /api/orders");
        List<OrderDto.Response> orders = orderService.findAll()
                .stream()
                .map(orderService::toResponse)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto.Response> getOrderById(@PathVariable Long id) {
        log.info("🌐 [REST] GET /api/orders/{}", id);
        return ResponseEntity.ok(orderService.toResponse(orderService.findById(id)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto.Response>> getOrdersByUser(@PathVariable Long userId) {
        log.info("🌐 [REST] GET /api/orders/user/{}", userId);
        List<OrderDto.Response> orders = orderService.findByUserId(userId)
                .stream()
                .map(orderService::toResponse)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<OrderDto.Response> createOrder(@Valid @RequestBody OrderDto.CreateRequest request) {
        log.info("🌐 [REST] POST /api/orders - user: {}", request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.toResponse(orderService.createOrder(request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDto.Response> updateStatus(
            @PathVariable Long id,
            @RequestBody OrderDto.UpdateStatusRequest request) {
        log.info("🌐 [REST] PATCH /api/orders/{}/status - {}", id, request.getStatus());
        return ResponseEntity.ok(orderService.toResponse(orderService.updateStatus(id, request.getStatus())));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        log.info("🌐 [REST] POST /api/orders/{}/cancel", id);
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
}
