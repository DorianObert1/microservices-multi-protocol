package com.microservices.order.client;

import com.microservices.grpc.product.*;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Client gRPC SYNCHRONE pour Product Service
 * Utilise le BlockingStub (appels bloquants)
 */
@Service
@Slf4j
public class ProductGrpcClient {

    @GrpcClient("product-service")
    private ProductServiceGrpc.ProductServiceBlockingStub productStub;

    /**
     * Récupérer un produit par ID
     */
    public Optional<Product> getProduct(Long productId) {
        log.info("📡 [gRPC Client] → product-service.GetProduct({})", productId);
        try {
            ProductResponse response = productStub.getProduct(
                    GetProductRequest.newBuilder().setId(productId).build()
            );
            log.info("✅ [gRPC Client] Product trouvé: {}", response.getProduct().getName());
            return Optional.of(response.getProduct());
        } catch (StatusRuntimeException e) {
            log.error("❌ [gRPC Client] Product non trouvé: {}", e.getStatus());
            return Optional.empty();
        }
    }

    /**
     * Récupérer plusieurs produits par IDs
     */
    public List<Product> getProductsByIds(List<Long> productIds) {
        log.info("📡 [gRPC Client] → product-service.GetProductsByIds({})", productIds);
        try {
            ProductsResponse response = productStub.getProductsByIds(
                    GetProductsByIdsRequest.newBuilder()
                            .addAllIds(productIds)
                            .build()
            );
            log.info("✅ [gRPC Client] {} produits récupérés", response.getProductsCount());
            return response.getProductsList();
        } catch (StatusRuntimeException e) {
            log.error("❌ [gRPC Client] Erreur: {}", e.getStatus());
            return Collections.emptyList();
        }
    }

    /**
     * Mettre à jour le stock d'un produit
     */
    public boolean updateStock(Long productId, int quantity) {
        log.info("📡 [gRPC Client] → product-service.UpdateStock({}, {})", productId, quantity);
        try {
            StockUpdateResponse response = productStub.updateStock(
                    UpdateStockRequest.newBuilder()
                            .setId(productId)
                            .setQuantity(quantity)
                            .build()
            );
            log.info("✅ [gRPC Client] Stock mis à jour: {}", response.getSuccess());
            return response.getSuccess();
        } catch (StatusRuntimeException e) {
            log.error("❌ [gRPC Client] Erreur mise à jour stock: {}", e.getStatus());
            return false;
        }
    }
}
