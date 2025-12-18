package com.microservices.graphql.client;

import com.microservices.grpc.user.*;
import com.microservices.graphql.model.UserModel;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserGrpcClient {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userStub;

    public List<UserModel> getAllUsers() {
        log.info("📡 [GraphQL→gRPC] GetAllUsers");
        try {
            UsersResponse response = userStub.getAllUsers(GetAllUsersRequest.newBuilder().build());
            return response.getUsersList().stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        } catch (StatusRuntimeException e) {
            log.error("❌ Erreur: {}", e.getStatus());
            return Collections.emptyList();
        }
    }

    public Optional<UserModel> getUser(Long id) {
        log.info("📡 [GraphQL→gRPC] GetUser({})", id);
        try {
            UserResponse response = userStub.getUser(GetUserRequest.newBuilder().setId(id).build());
            return Optional.of(toModel(response.getUser()));
        } catch (StatusRuntimeException e) {
            log.error("❌ User non trouvé: {}", e.getStatus());
            return Optional.empty();
        }
    }

    public Optional<UserModel> getUserByUsername(String username) {
        log.info("📡 [GraphQL→gRPC] GetUserByUsername({})", username);
        try {
            UserResponse response = userStub.getUserByUsername(
                    GetUserByUsernameRequest.newBuilder().setUsername(username).build());
            return Optional.of(toModel(response.getUser()));
        } catch (StatusRuntimeException e) {
            log.error("❌ User non trouvé: {}", e.getStatus());
            return Optional.empty();
        }
    }

    public UserModel createUser(String username, String email, String firstName, String lastName, String password) {
        log.info("📡 [GraphQL→gRPC] CreateUser({})", username);
        UserResponse response = userStub.createUser(CreateUserRequest.newBuilder()
                .setUsername(username)
                .setEmail(email)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setPassword(password)
                .build());
        return toModel(response.getUser());
    }

    public UserModel updateUser(Long id, String email, String firstName, String lastName) {
        log.info("📡 [GraphQL→gRPC] UpdateUser({})", id);
        UpdateUserRequest.Builder builder = UpdateUserRequest.newBuilder().setId(id);
        if (email != null) builder.setEmail(email);
        if (firstName != null) builder.setFirstName(firstName);
        if (lastName != null) builder.setLastName(lastName);
        UserResponse response = userStub.updateUser(builder.build());
        return toModel(response.getUser());
    }

    public boolean deleteUser(Long id) {
        log.info("📡 [GraphQL→gRPC] DeleteUser({})", id);
        DeleteResponse response = userStub.deleteUser(DeleteUserRequest.newBuilder().setId(id).build());
        return response.getSuccess();
    }

    private UserModel toModel(User user) {
        return UserModel.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
