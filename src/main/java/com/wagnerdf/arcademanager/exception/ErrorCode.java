package com.wagnerdf.arcademanager.exception;

/**
 * Enum que representa os códigos de erro da aplicação.
 *
 * Utilizado para padronizar respostas de erro e facilitar
 * o tratamento no frontend.
 */
public enum ErrorCode {

    USER_ALREADY_EXISTS,
    USER_NOT_FOUND,
    PASSWORD_MISMATCH,
    INVALID_CREDENTIALS,
    ACCESS_DENIED,
    VALIDATION_ERROR,
    INTERNAL_ERROR
}