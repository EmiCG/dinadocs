package com.example.dinadocs.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

/**
 * Utilidad para generar y validar tokens JWT (JSON Web Tokens).
 * Utiliza la librería jjwt (io.jsonwebtoken) con algoritmo HS256.
 * 
 * <p>Características del token:
 * <ul>
 *   <li>Algoritmo: HMAC-SHA256 (HS256)</li>
 *   <li>Tiempo de expiración: 24 horas (86400000 ms)</li>
 *   <li>Subject: email del usuario</li>
 *   <li>Clave secreta: definida en constante SECRET</li>
 * </ul>
 * 
 * @author DynaDocs Team
 * @version 1.0
 * @since 2025-12-03
 */
@Component
public class JwtUtils {

    /**
     * Clave secreta para firmar los tokens JWT.
     * <b>IMPORTANTE:</b> En producción debe moverse a variables de entorno.
     */
    private static final String SECRET = "EstaEsUnaClaveSuperSecretaQueNadiePuedeAdivinar123456";
    
    /**
     * Tiempo de expiración del token en milisegundos (24 horas).
     */
    private static final long EXPIRATION_TIME = 86400000; 

    /**
     * Clave HMAC generada a partir del SECRET para firmar tokens.
     */
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * Genera un nuevo token JWT para el usuario especificado.
     * El token incluye el email como subject y una fecha de expiración.
     * 
     * @param username el email del usuario (se usa como identificador único)
     * @return el token JWT firmado como String
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida un token JWT y extrae el username (email) del subject.
     * Verifica la firma, formato y expiración del token.
     * 
     * @param token el token JWT a validar
     * @return el username (email) extraído del token, o null si el token es inválido
     * @throws JwtException si el token es inválido, está expirado o la firma no coincide
     */
    public String validateTokenAndGetUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null;
        }
    }
}