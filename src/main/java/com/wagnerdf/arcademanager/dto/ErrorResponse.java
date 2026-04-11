package com.wagnerdf.arcademanager.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * DTO padrão para respostas de erro da API.
 *
 * Este objeto é utilizado para padronizar as respostas
 * de erro retornadas ao cliente, facilitando o consumo
 * pelo frontend e melhorando a rastreabilidade.
 */
@Data
@Builder
public class ErrorResponse {

    /**
     * Data e hora em que o erro ocorreu.
     */
    private LocalDateTime timestamp;

    /**
     * Código de status HTTP da resposta.
     */
    private int status;

    /**
     * Nome do erro HTTP (ex: BAD_REQUEST, INTERNAL_SERVER_ERROR).
     */
    private String error;

    /**
     * Mensagem detalhada do erro.
     */
    private String message;

    /**
     * Caminho da requisição que gerou o erro.
     */
    private String path;
    
    private List<ValidationError> errors;
}
