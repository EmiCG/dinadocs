package com.example.dinadocs.services;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Servicio dedicado a manejar la lista de tokens invalidados.
 */
@Service
public class TokenBlacklistService {

    private final Set<String> invalidatedTokens = new HashSet<>();

    /**
     * Agrega un token a la lista de tokens invalidados.
     *
     * @param token El token a invalidar.
     */
    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }

    /**
     * Verifica si un token está invalidado.
     *
     * @param token El token a verificar.
     * @return true si el token está invalidado, false en caso contrario.
     */
    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.contains(token);
    }
}