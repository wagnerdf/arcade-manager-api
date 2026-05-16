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
    
    /**
     * Construtor da exceção de negócio customizada.
     *
     * Utilizado para lançar erros controlados dentro da aplicação,
     * permitindo retornar ao cliente uma mensagem, status HTTP
     * e um código de erro específico.
     *
     * @param message mensagem descritiva do erro
     * @param status status HTTP a ser retornado na resposta
     * @param code código interno de erro para identificação
     */
    public BusinessException(String message, HttpStatus status, ErrorCode code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    /**
     * Retorna o código de erro associado à exceção.
     *
     * Este código pode ser utilizado pelo frontend para
     * tratar erros de forma específica.
     *
     * @return código de erro da exceção
     */
    public ErrorCode getCode() {
        return code;
    }
}