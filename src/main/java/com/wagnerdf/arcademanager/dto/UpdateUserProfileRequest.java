package com.wagnerdf.arcademanager.dto;

import java.util.Set;

import com.wagnerdf.arcademanager.entity.Address;

import lombok.Data;

@Data
public class UpdateUserProfileRequest {

    private String fullName;

    private String phone;

    private Address address;

    private Set<String> favoriteGenres;
}
