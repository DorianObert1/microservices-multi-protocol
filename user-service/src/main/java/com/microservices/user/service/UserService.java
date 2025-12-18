package com.microservices.user.service;

import com.microservices.user.dto.UserDto;
import com.microservices.user.exception.ResourceNotFoundException;
import com.microservices.user.model.User;
import com.microservices.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service métier User
 * Utilisé par REST Controller et gRPC Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;

    /**
     * Récupérer tous les utilisateurs
     */
    @Transactional(readOnly = true)
    public List<User> findAll() {
        log.info("📋 Récupération de tous les utilisateurs");
        return userRepository.findAll();
    }

    /**
     * Récupérer un utilisateur par ID
     */
    @Transactional(readOnly = true)
    public User findById(Long id) {
        log.info("🔍 Recherche utilisateur ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    /**
     * Récupérer un utilisateur par username
     */
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        log.info("🔍 Recherche utilisateur username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User non trouvé: " + username));
    }

    /**
     * Créer un utilisateur
     */
    public User create(UserDto.CreateRequest request) {
        log.info("➕ Création utilisateur: {}", request.getUsername());
        
        // Vérifier unicité username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username déjà utilisé: " + request.getUsername());
        }
        
        // Vérifier unicité email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email déjà utilisé: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(request.getPassword()) // En prod: encoder le mot de passe!
                .active(true)
                .build();

        User saved = userRepository.save(user);
        log.info("✅ Utilisateur créé: {} (ID: {})", saved.getUsername(), saved.getId());
        return saved;
    }

    /**
     * Mettre à jour un utilisateur
     */
    public User update(Long id, UserDto.UpdateRequest request) {
        log.info("✏️ Mise à jour utilisateur ID: {}", id);
        
        User user = findById(id);

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email déjà utilisé");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        User updated = userRepository.save(user);
        log.info("✅ Utilisateur mis à jour: {}", updated.getUsername());
        return updated;
    }

    /**
     * Supprimer un utilisateur
     */
    public void delete(Long id) {
        log.info("🗑️ Suppression utilisateur ID: {}", id);
        
        User user = findById(id);
        userRepository.delete(user);
        log.info("✅ Utilisateur supprimé: {}", user.getUsername());
    }

    /**
     * Compter les utilisateurs
     */
    @Transactional(readOnly = true)
    public long count() {
        return userRepository.count();
    }

    /**
     * Vérifier si un utilisateur existe
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Convertir User en Response DTO
     */
    public UserDto.Response toResponse(User user) {
        return UserDto.Response.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
