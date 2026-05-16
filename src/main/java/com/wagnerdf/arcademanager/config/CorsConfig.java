package com.wagnerdf.arcademanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

	/**
	 * Configura e disponibiliza o filtro de CORS da aplicação.
	 *
	 * Este filtro permite que o frontend (rodando em outro domínio)
	 * consiga acessar os endpoints do backend sem bloqueio do navegador.
	 *
	 * Configurações aplicadas:
	 * - Permite envio de credenciais (ex: Authorization com JWT)
	 * - Libera acesso para a origem do frontend (localhost:8081)
	 * - Permite todos os headers
	 * - Permite todos os métodos HTTP (GET, POST, PUT, DELETE, etc)
	 *
	 * @return Instância configurada de CorsFilter
	 */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:8081"); // frontend
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
