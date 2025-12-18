package com.microservices.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTOs pour User
 */
public class UserDto {

    /**
     * Réponse utilisateur (sans mot de passe)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private boolean active;
        private LocalDateTime createdAt;
    }

    /**
     * Requête de création
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        
        @NotBlank(message = "Username est requis")
        @Size(min = 3, max = 50, message = "Username doit avoir entre 3 et 50 caractères")
        private String username;

        @NotBlank(message = "Email est requis")
        @Email(message = "Email invalide")
        private String email;

        @NotBlank(message = "Prénom est requis")
        private String firstName;

        @NotBlank(message = "Nom est requis")
        private String lastName;

        @NotBlank(message = "Mot de passe est requis")
        @Size(min = 6, message = "Mot de passe doit avoir au moins 6 caractères")
        private String password;
    }

    /**
     * Requête de mise à jour
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        
        @Email(message = "Email invalide")
        private String email;

        private String firstName;
        private String lastName;
        private Boolean active;
    }
}
