package com.wagnerdf.arcademanager.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
	private final HttpStatus status;

    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}