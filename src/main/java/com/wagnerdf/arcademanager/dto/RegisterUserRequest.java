package com.wagnerdf.arcademanager.dto;

import java.util.Set;

import com.wagnerdf.arcademanager.entity.Address;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {

	@NotBlank(message = "Full name is required")
    private String fullName;

	@NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

	@NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

	@NotBlank(message = "Phone is required")
    private String phone;

	@Valid
    @NotNull(message = "Address is required")
    private Address address;

	@NotEmpty(message = "At least one favorite genre is required")
    private Set<String> favoriteGenres;
}