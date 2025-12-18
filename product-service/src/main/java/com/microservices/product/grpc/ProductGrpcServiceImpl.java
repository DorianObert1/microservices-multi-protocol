package com.microservices.product.grpc;

import com.microservices.grpc.product.*;
import com.microservices.product.dto.ProductDto;
import com.microservices.product.model.Product;
import com.microservices.product.service.ProductService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class ProductGrpcServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductService productService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void getProduct(GetProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        log.info("⚡ [gRPC] GetProduct - ID: {}", request.getId());
        try {
            Product product = productService.findById(request.getId());
            responseObserver.onNext(ProductResponse.newBuilder()
                    .setProduct(toProto(product))
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getProductsByIds(GetProductsByIdsRequest request, StreamObserver<ProductsResponse> responseObserver) {
        log.info("⚡ [gRPC] GetProductsByIds - count: {}", request.getIdsCount());
        try {
            List<Product> products = productService.findByIds(request.getIdsList());
            ProductsResponse.Builder builder = ProductsResponse.newBuilder().setTotal(products.size());
            products.forEach(p -> builder.addProducts(toProto(p)));
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getAllProducts(GetAllProductsRequest request, StreamObserver<ProductsResponse> responseObserver) {
        log.info("⚡ [gRPC] GetAllProducts");
        try {
            List<Product> products = productService.findAll();
            ProductsResponse.Builder builder = ProductsResponse.newBuilder().setTotal(products.size());
            products.forEach(p -> builder.addProducts(toProto(p)));
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getProductsByCategory(GetProductsByCategoryRequest request, StreamObserver<ProductsResponse> responseObserver) {
        log.info("⚡ [gRPC] GetProductsByCategory - {}", request.getCategory());
        try {
            List<Product> products = productService.findByCategory(request.getCategory());
            ProductsResponse.Builder builder = ProductsResponse.newBuilder().setTotal(products.size());
            products.forEach(p -> builder.addProducts(toProto(p)));
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void createProduct(CreateProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        log.info("⚡ [gRPC] CreateProduct - {}", request.getName());
        try {
            ProductDto.CreateRequest createRequest = ProductDto.CreateRequest.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .price(BigDecimal.valueOf(request.getPrice()))
                    .stock(request.getStock())
                    .category(request.getCategory())
                    .build();
            Product product = productService.create(createRequest);
            responseObserver.onNext(ProductResponse.newBuilder().setProduct(toProto(product)).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void updateStock(UpdateStockRequest request, StreamObserver<StockUpdateResponse> responseObserver) {
        log.info("⚡ [gRPC] UpdateStock - ID: {}, qty: {}", request.getId(), request.getQuantity());
        try {
            Product product = productService.updateStock(request.getId(), request.getQuantity());
            responseObserver.onNext(StockUpdateResponse.newBuilder()
                    .setSuccess(true)
                    .setNewStock(product.getStock())
                    .setMessage("Stock mis à jour")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(StockUpdateResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void deleteProduct(DeleteProductRequest request, StreamObserver<DeleteResponse> responseObserver) {
        log.info("⚡ [gRPC] DeleteProduct - ID: {}", request.getId());
        try {
            productService.delete(request.getId());
            responseObserver.onNext(DeleteResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Product deleted")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(DeleteResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    private com.microservices.grpc.product.Product toProto(Product product) {
        return com.microservices.grpc.product.Product.newBuilder()
                .setId(product.getId())
                .setName(product.getName())
                .setDescription(product.getDescription() != null ? product.getDescription() : "")
                .setPrice(product.getPrice().doubleValue())
                .setStock(product.getStock())
                .setCategory(product.getCategory() != null ? product.getCategory() : "")
                .setAvailable(product.isAvailable())
                .setCreatedAt(product.getCreatedAt() != null ? product.getCreatedAt().format(FORMATTER) : "")
                .build();
    }
}
