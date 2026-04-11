package com.wagnerdf.arcademanager.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wagnerdf.arcademanager.controller.UserController;
import com.wagnerdf.arcademanager.security.JwtService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;
    
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    /**
     * Realiza a autenticação do usuário e gera um token JWT.
     *
     * Este endpoint valida as credenciais (email e senha) utilizando o
     * AuthenticationManager do Spring Security. Em caso de sucesso,
     * um token JWT é gerado e retornado ao cliente.
     *
     * @param request Objeto contendo email e senha do usuário
     * @return AuthResponse contendo o token JWT gerado
     */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token = jwtService.generateToken(request.getEmail());
        log.info("Fetching users list");

        return new AuthResponse(token);
    }
    
    /**
     * Retorna os dados do usuário autenticado.
     *
     * Este endpoint utiliza o contexto de segurança do Spring para obter
     * o usuário atualmente autenticado e retornar suas informações básicas,
     * como email e role.
     *
     * @param authentication Objeto de autenticação injetado automaticamente pelo Spring
     * @return MeResponse contendo email e role do usuário autenticado
     */
    @GetMapping("/me")
    public MeResponse getCurrentUser(Authentication authentication) {

        UserDetails user = (UserDetails) authentication.getPrincipal();

        return new MeResponse(
                user.getUsername(),
                user.getAuthorities().iterator().next().getAuthority()
        );
    }
}