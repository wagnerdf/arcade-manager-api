package com.wagnerdf.arcademanager.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {

	 /**
     * Email do usuário utilizado para autenticação.
     */
	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email format")
    private String email;
    
    /**
     * Senha do usuário utilizada para autenticação.
     */
	@NotBlank(message = "Password is required")
    private String password;
}
