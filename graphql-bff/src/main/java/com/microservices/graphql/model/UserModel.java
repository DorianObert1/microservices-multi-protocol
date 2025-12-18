package com.microservices.graphql.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean active;
    private String createdAt;
}
