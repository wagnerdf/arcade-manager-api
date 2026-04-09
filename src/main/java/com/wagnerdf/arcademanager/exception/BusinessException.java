package com.wagnerdf.arcademanager.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
	private final HttpStatus status;

	 /**
     * Construtor da exceção de negócio.
     *
     * @param message Mensagem descritiva do erro
     * @param status Status HTTP que será retornado na resposta
     */
    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    /**
     * Retorna o status HTTP associado à exceção.
     *
     * @return Status HTTP da exceção
     */
    public HttpStatus getStatus() {
        return status;
    }
}