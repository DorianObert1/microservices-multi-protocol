package com.microservices.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GraphqlBffApplication {
    public static void main(String[] args) {
        SpringApplication.run(GraphqlBffApplication.class, args);
        System.out.println("""
            
            ╔═══════════════════════════════════════════════════════╗
            ║         🔮 GRAPHQL BFF - DÉMARRÉ                      ║
            ╠═══════════════════════════════════════════════════════╣
            ║                                                       ║
            ║  🎮 GraphiQL:  http://localhost:8084/graphiql         ║
            ║  🔮 GraphQL:   http://localhost:8084/graphql          ║
            ║                                                       ║
            ║  📡 Agrège les données via gRPC:                      ║
            ║     → user-service:9091                               ║
            ║     → product-service:9092                            ║
            ║     → order-service:9093                              ║
            ║                                                       ║
            ╚═══════════════════════════════════════════════════════╝
            """);
    }
}
