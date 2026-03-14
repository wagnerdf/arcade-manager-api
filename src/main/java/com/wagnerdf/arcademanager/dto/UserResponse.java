package com.wagnerdf.arcademanager.dto;

import java.util.Set;

import com.wagnerdf.arcademanager.entity.Address;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private String id;

    private String fullName;

    private String email;

    private String phone;

    private Address address;

    private Set<GenreResponse> favoriteGenres;
}