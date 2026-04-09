package com.wagnerdf.arcademanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handler global de exceções da aplicação.
 *
 * Centraliza o tratamento de erros lançados pelos controllers,
 * garantindo respostas padronizadas para o cliente.
 *
 * Utiliza a anotação @RestControllerAdvice para interceptar
 * exceções de forma global em todos os endpoints.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	 /**
     * Trata exceções do tipo BusinessException.
     *
     * Retorna a mensagem definida na exceção juntamente com
     * o status HTTP configurado, permitindo respostas controladas
     * para erros de regra de negócio.
     *
     * @param ex Exceção de negócio lançada na aplicação
     * @return ResponseEntity contendo mensagem e status HTTP apropriado
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
    }

    /**
     * Trata exceções genéricas não mapeadas.
     *
     * Atua como fallback para qualquer erro inesperado na aplicação,
     * retornando status 500 (Internal Server Error).
     *
     * @param ex Exceção genérica capturada
     * @return ResponseEntity com mensagem de erro e status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
