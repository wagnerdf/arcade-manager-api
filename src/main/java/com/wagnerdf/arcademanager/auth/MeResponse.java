package com.wagnerdf.arcademanager.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MeResponse {

	/**
     * Email do usuário autenticado.
     */
    private String email;
    
    /**
     * Role (perfil de acesso) do usuário autenticado.
     */
    private String role;

}
