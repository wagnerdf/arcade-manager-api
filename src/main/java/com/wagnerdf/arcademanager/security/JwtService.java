package com.wagnerdf.arcademanager.security;

import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;

@Service
public class JwtService {

    // Chave secreta segura (32+ caracteres)
    private static final String SECRET_STRING = "chave-secreta-112233445566778899-segurança-com-32-caracteres";

    // Converte para SecretKey
    private final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1h
                .signWith(secretKey, SignatureAlgorithm.HS256) // usar SecretKey
                .compact();
    }
}