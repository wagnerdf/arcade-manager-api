package com.wagnerdf.arcademanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.wagnerdf.arcademanager.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	  /**
     * Define o encoder de senha utilizado pela aplicação.
     *
     * Utiliza o algoritmo BCrypt, que é atualmente uma das formas mais seguras
     * para armazenamento de senhas, incluindo salt automático.
     *
     * @return Implementação de PasswordEncoder baseada em BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Disponibiliza o AuthenticationManager do Spring para uso na aplicação.
     *
     * Este componente é utilizado no processo de autenticação, como no login,
     * para validar credenciais do usuário.
     *
     * @param authConfig Configuração de autenticação do Spring
     * @return Instância de AuthenticationManager
     * @throws Exception Caso ocorra erro na obtenção do manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configura a cadeia de filtros de segurança da aplicação.
     *
     * Define:
     * - Desativação de CSRF (API stateless)
     * - Endpoints públicos e protegidos
     * - Inclusão do filtro JWT antes do filtro padrão de autenticação
     *
     * Endpoints liberados:
     * - /auth/**
     * - /api/users/register
     * - /api/genres/**
     *
     * Todos os demais endpoints requerem autenticação.
     *
     * @param http Configuração de segurança HTTP
     * @return SecurityFilterChain configurado
     * @throws Exception Em caso de erro na configuração
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers("/api/users/register").permitAll()
            .requestMatchers("/api/genres/**").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
    
}