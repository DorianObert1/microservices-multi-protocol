package com.microservices.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
        System.out.println("""
            
            ╔═══════════════════════════════════════════════════════╗
            ║         📦 PRODUCT SERVICE - DÉMARRÉ                  ║
            ╠═══════════════════════════════════════════════════════╣
            ║                                                       ║
            ║  🌐 REST API:  http://localhost:8082/api/products     ║
            ║  ⚡ gRPC:      localhost:9092                         ║
            ║                                                       ║
            ╚═══════════════════════════════════════════════════════╝
            """);
    }
}
