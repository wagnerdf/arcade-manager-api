package com.wagnerdf.arcademanager.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    private String fullName;

    private String email;

    private String password;

    private Role role;

    private boolean active;
}