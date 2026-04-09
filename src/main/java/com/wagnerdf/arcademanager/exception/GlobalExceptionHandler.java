package com.wagnerdf.arcademanager.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wagnerdf.arcademanager.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Handler global de exceções da aplicação.
 *
 * Centraliza o tratamento de erros lançados pelos controllers,
 * garantindo respostas padronizadas para o cliente.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata exceções do tipo BusinessException.
     *
     * Retorna um objeto estruturado contendo detalhes do erro,
     * incluindo status HTTP definido pela própria exceção.
     *
     * @param ex Exceção de negócio lançada
     * @param request Requisição HTTP atual
     * @return ResponseEntity contendo ErrorResponse
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {

        HttpStatus status = ex.getStatus();

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.name())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }

    /**
     * Trata exceções genéricas não mapeadas.
     *
     * Atua como fallback para erros inesperados,
     * retornando um erro padronizado com status 500.
     *
     * @param ex Exceção capturada
     * @param request Requisição HTTP atual
     * @return ResponseEntity contendo ErrorResponse
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherExceptions(
            Exception ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.name())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(status).body(error);
    }
}