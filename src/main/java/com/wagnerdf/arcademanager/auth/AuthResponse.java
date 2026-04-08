package com.wagnerdf.arcademanager.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

	/**
     * Token JWT gerado após autenticação bem-sucedida.
     */
    private String token;
}
