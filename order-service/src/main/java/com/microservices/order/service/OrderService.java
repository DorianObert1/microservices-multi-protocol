package com.microservices.order.service;

import com.microservices.grpc.product.Product;
import com.microservices.order.client.ProductGrpcClient;
import com.microservices.order.client.UserGrpcClient;
import com.microservices.order.dto.OrderDto;
import com.microservices.order.exception.ResourceNotFoundException;
import com.microservices.order.model.Order;
import com.microservices.order.model.OrderItem;
import com.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserGrpcClient userGrpcClient;
    private final ProductGrpcClient productGrpcClient;

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        log.info("📋 Récupération de toutes les commandes");
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Order findById(Long id) {
        log.info("🔍 Recherche commande ID: {}", id);
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
    }

    @Transactional(readOnly = true)
    public List<Order> findByUserId(Long userId) {
        log.info("🔍 Recherche commandes user ID: {}", userId);
        return orderRepository.findByUserId(userId);
    }

    /**
     * Créer une commande avec validation via gRPC
     * - Vérifie que l'utilisateur existe (via gRPC → user-service)
     * - Récupère les produits (via gRPC → product-service)
     * - Calcule le total et crée la commande
     */
    public Order createOrder(OrderDto.CreateRequest request) {
        log.info("📝 Création commande pour user: {}", request.getUserId());
        
        // 1. Vérifier que l'utilisateur existe via gRPC
        if (!userGrpcClient.userExists(request.getUserId())) {
            throw new IllegalArgumentException("Utilisateur non trouvé: " + request.getUserId());
        }

        // 2. Récupérer les produits via gRPC
        List<Long> productIds = request.getItems().stream()
                .map(OrderDto.ItemRequest::getProductId)
                .collect(Collectors.toList());

        List<Product> products = productGrpcClient.getProductsByIds(productIds);

        if (products.size() != productIds.size()) {
            throw new IllegalArgumentException("Certains produits n'existent pas");
        }

        // 3. Créer un map pour accès rapide
        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 4. Créer la commande
        Order order = Order.builder()
                .userId(request.getUserId())
                .shippingAddress(request.getShippingAddress())
                .status("PENDING")
                .totalAmount(BigDecimal.ZERO)
                .build();

        // 5. Ajouter les items et calculer le total
        BigDecimal total = BigDecimal.ZERO;
        
        for (OrderDto.ItemRequest itemRequest : request.getItems()) {
            Product product = productMap.get(itemRequest.getProductId());
            
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new IllegalArgumentException(
                        "Stock insuffisant pour " + product.getName() + 
                        " (disponible: " + product.getStock() + ")");
            }

            BigDecimal unitPrice = BigDecimal.valueOf(product.getPrice());
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem item = OrderItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(itemTotal)
                    .build();

            order.addItem(item);
            total = total.add(itemTotal);
        }

        order.setTotalAmount(total);

        // 6. Sauvegarder
        Order savedOrder = orderRepository.save(order);
        log.info("✅ Commande créée: {} - Total: {}", savedOrder.getId(), total);

        // 7. Décrémenter les stocks via gRPC (optionnel)
        for (OrderDto.ItemRequest itemRequest : request.getItems()) {
            productGrpcClient.updateStock(itemRequest.getProductId(), -itemRequest.getQuantity());
        }

        return savedOrder;
    }

    public Order updateStatus(Long id, String status) {
        log.info("✏️ Mise à jour statut commande {} → {}", id, status);
        
        Order order = findById(id);
        order.setStatus(status.toUpperCase());
        
        return orderRepository.save(order);
    }

    public void cancelOrder(Long id) {
        log.info("❌ Annulation commande: {}", id);
        
        Order order = findById(id);
        
        if ("DELIVERED".equals(order.getStatus())) {
            throw new IllegalStateException("Impossible d'annuler une commande livrée");
        }

        // Restaurer les stocks via gRPC
        for (OrderItem item : order.getItems()) {
            productGrpcClient.updateStock(item.getProductId(), item.getQuantity());
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);
    }

    /**
     * Convertir Order en Response DTO avec enrichissement via gRPC
     */
    public OrderDto.Response toResponse(Order order) {
        // Récupérer le username via gRPC
        String username = userGrpcClient.getUsername(order.getUserId());

        List<OrderDto.ItemResponse> items = order.getItems().stream()
                .map(item -> OrderDto.ItemResponse.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderDto.Response.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .username(username)
                .items(items)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
