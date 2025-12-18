package com.microservices.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
        System.out.println("""
            
            ╔═══════════════════════════════════════════════════════╗
            ║         🛒 ORDER SERVICE - DÉMARRÉ                    ║
            ╠═══════════════════════════════════════════════════════╣
            ║                                                       ║
            ║  🌐 REST API:  http://localhost:8083/api/orders       ║
            ║  ⚡ gRPC:      localhost:9093                         ║
            ║                                                       ║
            ║  📡 gRPC Clients vers:                                ║
            ║     → user-service:9091                               ║
            ║     → product-service:9092                            ║
            ║                                                       ║
            ╚═══════════════════════════════════════════════════════╝
            """);
    }
}
