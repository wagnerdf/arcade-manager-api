package com.wagnerdf.arcademanager.dto;

import java.util.Set;

import com.wagnerdf.arcademanager.entity.Address;

import lombok.Data;

@Data
public class RegisterUserRequest {

    private String fullName;

    private String email;

    private String password;

    private String phone;

    private Address address;

    private Set<String> favoriteGenres;
}