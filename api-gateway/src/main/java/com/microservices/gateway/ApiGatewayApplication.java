package com.microservices.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
        System.out.println("""
            
            ╔═══════════════════════════════════════════════════════════════╗
            ║               API GATEWAY - DÉMARRÉ                         ║
            ╠═══════════════════════════════════════════════════════════════╣
            ║                                                               ║
            ║   Gateway:     http://localhost:8080                        ║
            ║                                                               ║
            ║   Routes configurées:                                       ║
            ║     /api/users/**     → user-service:8081                     ║
            ║     /api/products/**  → product-service:8082                  ║
            ║     /api/orders/**    → order-service:8083                    ║
            ║     /graphql/**       → graphql-bff:8084                      ║
            ║     /graphiql         → graphql-bff:8084                      ║
            ║                                                               ║
            ╚═══════════════════════════════════════════════════════════════╝
            """);
    }
}
