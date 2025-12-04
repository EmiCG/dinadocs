package com.example.dinadocs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de CORS (Cross-Origin Resource Sharing) para la aplicación.
 * Permite que el frontend (en diferente dominio/puerto) pueda realizar peticiones HTTP
 * al backend de forma segura.
 * 
 * @author DynaDocs Team
 * @version 1.0
 * @since 2025-12-03
 */
@Configuration
public class CorsConfig {

    /**
     * Configura el filtro CORS para permitir peticiones desde cualquier origen.
     * Configuración actual:
     * <ul>
     *   <li>AllowCredentials: true - Permite envío de cookies y headers de autenticación</li>
     *   <li>AllowedOriginPattern: * - Acepta peticiones desde cualquier dominio</li>
     *   <li>AllowedHeaders: * - Acepta cualquier header HTTP</li>
     *   <li>AllowedMethods: OPTIONS, GET, POST, PUT, DELETE - Métodos HTTP permitidos</li>
     * </ul>
     * 
     * @return CorsFilter configurado para toda la aplicación
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
        
        config.addAllowedOriginPattern("*"); 
        
        config.addAllowedHeader("*");
        
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}