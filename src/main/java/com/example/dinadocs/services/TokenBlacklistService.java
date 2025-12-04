package com.example.dinadocs.services;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Servicio para gestionar la lista negra (blacklist) de tokens JWT invalidados.
 * Cuando un usuario cierra sesión, su token se agrega aquí para prevenir
 * su reutilización hasta que expire naturalmente.
 * 
 * <p><b>Nota de implementación:</b> Esta implementación usa un HashSet en memoria,
 * por lo que la blacklist se perderá al reiniciar la aplicación. Para producción,
 * considerar usar Redis, base de datos o un sistema de caché distribuido.
 * 
 * @author DynaDocs Team
 * @version 1.0
 * @since 2025-12-03
 * @see AuthService#logout(String)
 */
@Service
public class TokenBlacklistService {

    /**
     * Set que almacena los tokens JWT invalidados en memoria.
     * <b>ADVERTENCIA:</b> Se pierde al reiniciar la aplicación.
     */
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