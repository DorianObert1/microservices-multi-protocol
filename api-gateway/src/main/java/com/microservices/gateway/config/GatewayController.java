package com.microservices.gateway.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Contrôleur de la Gateway
 */
@RestController
public class GatewayController {

    /**
     * Health check de la Gateway
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "api-gateway",
                "timestamp", LocalDateTime.now().toString(),
                "routes", Map.of(
                        "user-service", "http://localhost:8081",
                        "product-service", "http://localhost:8082",
                        "order-service", "http://localhost:8083",
                        "graphql-bff", "http://localhost:8084"
                )
        ));
    }

    /**
     * Page d'accueil avec les informations de routage
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(Map.of(
                "name", "Microservices Multi-Protocol API Gateway",
                "version", "1.0.0",
                "endpoints", Map.of(
                        "REST Users", "/api/users",
                        "REST Products", "/api/products",
                        "REST Orders", "/api/orders",
                        "GraphQL", "/graphql",
                        "GraphiQL", "/graphiql"
                ),
                "protocols", Map.of(
                        "REST", "Public API endpoints (ports 8081-8083)",
                        "GraphQL", "Unified query interface (port 8084)",
                        "gRPC", "Internal service communication (ports 9091-9093)"
                )
        ));
    }

    /**
     * Fallback en cas d'erreur service
     */
    @GetMapping("/fallback")
    public ResponseEntity<Map<String, String>> fallback() {
        return ResponseEntity.status(503).body(Map.of(
                "status", "SERVICE_UNAVAILABLE",
                "message", "Le service demandé est temporairement indisponible. Veuillez réessayer."
        ));
    }
}
