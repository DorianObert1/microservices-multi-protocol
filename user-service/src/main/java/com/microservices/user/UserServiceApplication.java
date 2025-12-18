package com.microservices.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * USER SERVICE
 * 
 * Microservice exposant :
 * - REST API (Spring MVC) sur le port 8081
 * - gRPC Server sur le port 9091
 */
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        
        System.out.println("""
            
            ╔═══════════════════════════════════════════════════════╗
            ║           👤 USER SERVICE - DÉMARRÉ                   ║
            ╠═══════════════════════════════════════════════════════╣
            ║                                                       ║
            ║  🌐 REST API:  http://localhost:8081/api/users        ║
            ║  ⚡ gRPC:      localhost:9091                         ║
            ║                                                       ║
            ║  📋 Endpoints REST:                                   ║
            ║     GET    /api/users                                 ║
            ║     GET    /api/users/{id}                            ║
            ║     POST   /api/users                                 ║
            ║     PUT    /api/users/{id}                            ║
            ║     DELETE /api/users/{id}                            ║
            ║                                                       ║
            ╚═══════════════════════════════════════════════════════╝
            """);
    }
}
