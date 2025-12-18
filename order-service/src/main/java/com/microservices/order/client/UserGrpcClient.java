package com.microservices.order.client;

import com.microservices.grpc.user.*;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Client gRPC SYNCHRONE pour User Service
 * Utilise le BlockingStub (appels bloquants)
 */
@Service
@Slf4j
public class UserGrpcClient {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userStub;

    /**
     * Récupérer un utilisateur par ID
     */
    public Optional<User> getUser(Long userId) {
        log.info("📡 [gRPC Client] → user-service.GetUser({})", userId);
        try {
            UserResponse response = userStub.getUser(
                    GetUserRequest.newBuilder().setId(userId).build()
            );
            log.info("✅ [gRPC Client] User trouvé: {}", response.getUser().getUsername());
            return Optional.of(response.getUser());
        } catch (StatusRuntimeException e) {
            log.error("❌ [gRPC Client] User non trouvé: {}", e.getStatus());
            return Optional.empty();
        }
    }

    /**
     * Vérifier si un utilisateur existe
     */
    public boolean userExists(Long userId) {
        return getUser(userId).isPresent();
    }

    /**
     * Récupérer le username d'un utilisateur
     */
    public String getUsername(Long userId) {
        return getUser(userId)
                .map(User::getUsername)
                .orElse("Unknown");
    }
}
