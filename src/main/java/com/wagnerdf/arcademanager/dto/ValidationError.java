package com.wagnerdf.arcademanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Representa um erro de validação de campo.
 */
@Data
@AllArgsConstructor
public class ValidationError {

    /**
     * Nome do campo com erro.
     */
    private String field;

    /**
     * Mensagem de erro associada ao campo.
     */
    private String message;
}
