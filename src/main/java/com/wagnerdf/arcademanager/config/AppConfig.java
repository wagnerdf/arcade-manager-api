package com.wagnerdf.arcademanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

	 /**
     * Cria e disponibiliza um bean de RestTemplate para consumo de APIs externas.
     *
     * Este bean é utilizado para realizar requisições HTTP (GET, POST, etc.)
     * para serviços externos, como integrações com APIs de terceiros.
     *
     * @return Instância configurada de RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}