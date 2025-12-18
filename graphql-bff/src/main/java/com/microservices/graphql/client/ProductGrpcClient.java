package com.microservices.graphql.client;

import com.microservices.grpc.product.*;
import com.microservices.graphql.model.ProductModel;
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
public class ProductGrpcClient {

    @GrpcClient("product-service")
    private ProductServiceGrpc.ProductServiceBlockingStub productStub;

    public List<ProductModel> getAllProducts() {
        log.info("📡 [GraphQL→gRPC] GetAllProducts");
        try {
            ProductsResponse response = productStub.getAllProducts(GetAllProductsRequest.newBuilder().build());
            return response.getProductsList().stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        } catch (StatusRuntimeException e) {
            log.error("❌ Erreur: {}", e.getStatus());
            return Collections.emptyList();
        }
    }

    public Optional<ProductModel> getProduct(Long id) {
        log.info("📡 [GraphQL→gRPC] GetProduct({})", id);
        try {
            ProductResponse response = productStub.getProduct(GetProductRequest.newBuilder().setId(id).build());
            return Optional.of(toModel(response.getProduct()));
        } catch (StatusRuntimeException e) {
            log.error("❌ Product non trouvé: {}", e.getStatus());
            return Optional.empty();
        }
    }

    public List<ProductModel> getProductsByCategory(String category) {
        log.info("📡 [GraphQL→gRPC] GetProductsByCategory({})", category);
        try {
            ProductsResponse response = productStub.getProductsByCategory(
                    GetProductsByCategoryRequest.newBuilder().setCategory(category).build());
            return response.getProductsList().stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        } catch (StatusRuntimeException e) {
            log.error("❌ Erreur: {}", e.getStatus());
            return Collections.emptyList();
        }
    }

    public ProductModel createProduct(String name, String description, Double price, Integer stock, String category) {
        log.info("📡 [GraphQL→gRPC] CreateProduct({})", name);
        ProductResponse response = productStub.createProduct(CreateProductRequest.newBuilder()
                .setName(name)
                .setDescription(description != null ? description : "")
                .setPrice(price)
                .setStock(stock)
                .setCategory(category != null ? category : "")
                .build());
        return toModel(response.getProduct());
    }

    public boolean updateStock(Long id, Integer quantity) {
        log.info("📡 [GraphQL→gRPC] UpdateStock({}, {})", id, quantity);
        StockUpdateResponse response = productStub.updateStock(
                UpdateStockRequest.newBuilder().setId(id).setQuantity(quantity).build());
        return response.getSuccess();
    }

    public boolean deleteProduct(Long id) {
        log.info("📡 [GraphQL→gRPC] DeleteProduct({})", id);
        DeleteResponse response = productStub.deleteProduct(DeleteProductRequest.newBuilder().setId(id).build());
        return response.getSuccess();
    }

    private ProductModel toModel(Product product) {
        return ProductModel.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .available(product.getAvailable())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
