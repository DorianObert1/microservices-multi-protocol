package com.microservices.user.grpc;

import com.microservices.grpc.user.*;
import com.microservices.user.dto.UserDto;
import com.microservices.user.model.User;
import com.microservices.user.service.UserService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ⚡ gRPC SERVICE - User Service
 * 
 * Implémentation synchrone (bloquante) du service gRPC
 * Port: 9091
 */
@GrpcService
@RequiredArgsConstructor
@Slf4j
public class UserGrpcServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Récupérer un utilisateur par ID
     */
    @Override
    public void getUser(GetUserRequest request, StreamObserver<UserResponse> responseObserver) {
        log.info("⚡ [gRPC] GetUser - ID: {}", request.getId());
        
        try {
            User user = userService.findById(request.getId());
            UserResponse response = UserResponse.newBuilder()
                    .setUser(toProtoUser(user))
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            log.info("✅ [gRPC] User trouvé: {}", user.getUsername());
            
        } catch (Exception e) {
            log.error("❌ [gRPC] Erreur GetUser: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * Récupérer un utilisateur par username
     */
    @Override
    public void getUserByUsername(GetUserByUsernameRequest request, 
                                   StreamObserver<UserResponse> responseObserver) {
        log.info("⚡ [gRPC] GetUserByUsername - username: {}", request.getUsername());
        
        try {
            User user = userService.findByUsername(request.getUsername());
            UserResponse response = UserResponse.newBuilder()
                    .setUser(toProtoUser(user))
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("❌ [gRPC] Erreur GetUserByUsername: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * Récupérer tous les utilisateurs
     */
    @Override
    public void getAllUsers(GetAllUsersRequest request, StreamObserver<UsersResponse> responseObserver) {
        log.info("⚡ [gRPC] GetAllUsers");
        
        try {
            List<User> users = userService.findAll();
            
            UsersResponse.Builder responseBuilder = UsersResponse.newBuilder()
                    .setTotal(users.size());
            
            users.forEach(user -> responseBuilder.addUsers(toProtoUser(user)));
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            log.info("✅ [gRPC] {} utilisateurs retournés", users.size());
            
        } catch (Exception e) {
            log.error("❌ [gRPC] Erreur GetAllUsers: {}", e.getMessage());
            responseObserver.onError(Status.INTERNAL
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * Créer un utilisateur
     */
    @Override
    public void createUser(CreateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        log.info("⚡ [gRPC] CreateUser - username: {}", request.getUsername());
        
        try {
            UserDto.CreateRequest createRequest = UserDto.CreateRequest.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .password(request.getPassword())
                    .build();
            
            User user = userService.create(createRequest);
            
            UserResponse response = UserResponse.newBuilder()
                    .setUser(toProtoUser(user))
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            log.info("✅ [gRPC] User créé: {}", user.getUsername());
            
        } catch (Exception e) {
            log.error("❌ [gRPC] Erreur CreateUser: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * Mettre à jour un utilisateur
     */
    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        log.info("⚡ [gRPC] UpdateUser - ID: {}", request.getId());
        
        try {
            UserDto.UpdateRequest updateRequest = UserDto.UpdateRequest.builder()
                    .email(request.getEmail().isEmpty() ? null : request.getEmail())
                    .firstName(request.getFirstName().isEmpty() ? null : request.getFirstName())
                    .lastName(request.getLastName().isEmpty() ? null : request.getLastName())
                    .build();
            
            User user = userService.update(request.getId(), updateRequest);
            
            UserResponse response = UserResponse.newBuilder()
                    .setUser(toProtoUser(user))
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("❌ [gRPC] Erreur UpdateUser: {}", e.getMessage());
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    /**
     * Supprimer un utilisateur
     */
    @Override
    public void deleteUser(DeleteUserRequest request, StreamObserver<DeleteResponse> responseObserver) {
        log.info("⚡ [gRPC] DeleteUser - ID: {}", request.getId());
        
        try {
            userService.delete(request.getId());
            
            DeleteResponse response = DeleteResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("User deleted successfully")
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("❌ [gRPC] Erreur DeleteUser: {}", e.getMessage());
            DeleteResponse response = DeleteResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(e.getMessage())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    /**
     * Convertir User entity en Proto User
     */
    private com.microservices.grpc.user.User toProtoUser(User user) {
        return com.microservices.grpc.user.User.newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setActive(user.isActive())
                .setCreatedAt(user.getCreatedAt() != null 
                        ? user.getCreatedAt().format(FORMATTER) 
                        : "")
                .build();
    }
}
