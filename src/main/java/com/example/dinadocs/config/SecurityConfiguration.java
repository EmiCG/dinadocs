package com.example.dinadocs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Clase de configuración principal para Spring Security.
 * Define la cadena de filtros de seguridad HTTP y las reglas de autorización 
 * para los endpoints de la API REST.
 */

@Configuration
public class SecurityConfiguration {

    /**
     * Define la cadena de filtros de seguridad para la aplicación.
     * * Actualmente, esta configuración deshabilita CSRF (necesario para APIs REST sin 
     * sesiones de navegador) y permite el acceso público temporal a toda la ruta /api/** * durante la fase de desarrollo sin autenticación.
     * * NOTA: Esta configuración debe ser ajustada en la Fase 2 para proteger la gestión 
     * de plantillas y requerir tokens JWT en la mayoría de los endpoints /api/**.
     * * @param httpSecurity Objeto usado para configurar la seguridad a nivel de HTTP.
     * @return El objeto SecurityFilterChain configurado.
     * @throws Exception Si ocurre un error durante la construcción de la configuración de seguridad.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            // 1. Necesario para APIs REST sin sesiones tradicionales
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/**").permitAll() 
                .anyRequest().authenticated()
            );

        return httpSecurity.build();
    }

}
