package com.wagnerdf.arcademanager.logging;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    
    /**
     * Filtro responsável por adicionar um identificador único (Correlation ID)
     * para cada requisição HTTP processada pela aplicação.
     *
     * Este ID é utilizado para rastrear logs relacionados à mesma requisição,
     * facilitando debug, monitoramento e análise de fluxo.
     *
     * Funcionalidades:
     * - Obtém o Correlation ID do header "X-Correlation-Id" (caso exista)
     * - Gera um novo ID caso não seja informado pelo cliente
     * - Armazena o ID no MDC (Mapped Diagnostic Context) para uso em logs
     * - Adiciona o usuário autenticado no contexto de log (quando disponível)
     * - Retorna o Correlation ID no header da resposta
     *
     * @param request requisição HTTP recebida
     * @param response resposta HTTP enviada ao cliente
     * @param filterChain cadeia de filtros da aplicação
     *
     * @throws ServletException em caso de erro no processamento do filtro
     * @throws IOException em caso de erro de I/O
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String correlationId = request.getHeader(CORRELATION_ID_HEADER);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put("correlationId", correlationId);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String userEmail = authentication.getName();
            MDC.put("user", userEmail);
        }

        try {
            response.setHeader(CORRELATION_ID_HEADER, correlationId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear(); // MUITO IMPORTANTE
        }
    }
}
