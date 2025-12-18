package com.microservices.user.controller;

import com.microservices.user.dto.UserDto;
import com.microservices.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 🌐 REST CONTROLLER - User Service
 * 
 * API REST classique avec Spring MVC
 * Port: 8081
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserRestController {

    private final UserService userService;

    /**
     * GET /api/users
     * Récupérer tous les utilisateurs
     */
    @GetMapping
    public ResponseEntity<List<UserDto.Response>> getAllUsers() {
        log.info("🌐 [REST] GET /api/users");
        
        List<UserDto.Response> users = userService.findAll()
                .stream()
                .map(userService::toResponse)
                .toList();
        
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/{id}
     * Récupérer un utilisateur par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto.Response> getUserById(@PathVariable Long id) {
        log.info("🌐 [REST] GET /api/users/{}", id);
        
        UserDto.Response user = userService.toResponse(userService.findById(id));
        return ResponseEntity.ok(user);
    }

    /**
     * GET /api/users/username/{username}
     * Récupérer un utilisateur par username
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto.Response> getUserByUsername(@PathVariable String username) {
        log.info("🌐 [REST] GET /api/users/username/{}", username);
        
        UserDto.Response user = userService.toResponse(userService.findByUsername(username));
        return ResponseEntity.ok(user);
    }

    /**
     * POST /api/users
     * Créer un utilisateur
     */
    @PostMapping
    public ResponseEntity<UserDto.Response> createUser(@Valid @RequestBody UserDto.CreateRequest request) {
        log.info("🌐 [REST] POST /api/users - username: {}", request.getUsername());
        
        UserDto.Response user = userService.toResponse(userService.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /**
     * PUT /api/users/{id}
     * Mettre à jour un utilisateur
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto.Response> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserDto.UpdateRequest request) {
        log.info("🌐 [REST] PUT /api/users/{}", id);
        
        UserDto.Response user = userService.toResponse(userService.update(id, request));
        return ResponseEntity.ok(user);
    }

    /**
     * DELETE /api/users/{id}
     * Supprimer un utilisateur
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("🌐 [REST] DELETE /api/users/{}", id);
        
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/users/count
     * Compter les utilisateurs
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countUsers() {
        log.info("🌐 [REST] GET /api/users/count");
        
        return ResponseEntity.ok(Map.of("count", userService.count()));
    }

    /**
     * GET /api/users/health
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "user-service",
                "protocol", "REST"
        ));
    }
}
