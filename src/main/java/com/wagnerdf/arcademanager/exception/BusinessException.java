package com.wagnerdf.arcademanager.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
	private final HttpStatus status;
	private final ErrorCode code;

	 /**
     * Construtor da exceção de negócio.
     *
     * @param message Mensagem descritiva do erro
     * @param status Status HTTP que será retornado na resposta
     */
    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.code = null;
    }

    /**
     * Retorna o status HTTP associado à exceção.
     *
     * @return Status HTTP da exceção
     */
    public HttpStatus getStatus() {
        return status;
    }
    
    public BusinessException(String message, HttpStatus status, ErrorCode code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}