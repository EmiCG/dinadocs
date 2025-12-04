package com.example.dinadocs.config;

import com.example.dinadocs.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // <--- NUEVO IMPORT
import org.springframework.security.crypto.password.PasswordEncoder;     // <--- NUEVO IMPORT
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad de la aplicación basada en Spring Security.
 * Define las reglas de autenticación, autorización y protección de endpoints.
 * 
 * <p>Características principales:
 * <ul>
 *   <li>Autenticación JWT (JSON Web Token) stateless</li>
 *   <li>Deshabilitación de CSRF para APIs REST</li>
 *   <li>Endpoints públicos para registro y login</li>
 *   <li>Protección de todos los demás endpoints</li>
 * </ul>
 * 
 * @author DynaDocs Team
 * @version 1.0
 * @since 2025-12-03
 * @see JwtFilter
 * @see com.example.dinadocs.security.JwtUtils
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    /**
     * Filtro JWT personalizado para validar tokens en cada petición.
     */
    private final JwtFilter jwtFilter;

    /**
     * Constructor para inyección de dependencias.
     * 
     * @param jwtFilter filtro JWT que valida los tokens de autenticación
     */
    public SecurityConfiguration(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Configura la cadena de filtros de seguridad de Spring Security.
     * 
     * <p>Configuración aplicada:
     * <ul>
     *   <li>CORS habilitado con configuración por defecto</li>
     *   <li>CSRF deshabilitado (no necesario en APIs REST stateless)</li>
     *   <li>Sesiones STATELESS (sin manejo de sesiones en servidor)</li>
     *   <li>Endpoints públicos: /api/auth/*, /error, /actuator/*, /swagger-ui/**</li>
     *   <li>Todos los demás endpoints requieren autenticación</li>
     *   <li>Headers X-Frame-Options deshabilitados</li>
     * </ul>
     * 
     * @param http objeto HttpSecurity para configurar la seguridad
     * @return SecurityFilterChain configurado
     * @throws Exception si ocurre un error durante la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(org.springframework.security.config.Customizer.withDefaults()) 
            .csrf(csrf -> csrf.disable()) 
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated() 
            )
            .headers(headers -> headers.frameOptions(frame -> frame.disable())) 
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); 

        return http.build();
    }

    /**
     * Proporciona el AuthenticationManager de Spring Security.
     * Se utiliza en el proceso de autenticación de usuarios.
     * 
     * @param config configuración de autenticación de Spring Security
     * @return AuthenticationManager configurado
     * @throws Exception si ocurre un error al obtener el authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configura el encoder de contraseñas usando BCrypt.
     * BCrypt es un algoritmo de hashing robusto con salt automático,
     * recomendado para almacenar contraseñas de forma segura.
     * 
     * @return PasswordEncoder configurado con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}