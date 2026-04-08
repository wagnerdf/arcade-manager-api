package com.wagnerdf.arcademanager.auth;

import lombok.Data;

@Data
public class AuthRequest {

	 /**
     * Email do usuário utilizado para autenticação.
     */
    private String email;
    
    /**
     * Senha do usuário utilizada para autenticação.
     */
    private String password;
}
