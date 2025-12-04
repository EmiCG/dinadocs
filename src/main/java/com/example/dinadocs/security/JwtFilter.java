package com.example.dinadocs.security;

import com.example.dinadocs.models.User;
import com.example.dinadocs.repositories.UserRepository;
import com.example.dinadocs.services.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Filtro de seguridad que intercepta cada petición HTTP para validar
 * el token JWT en el header Authorization.
 * 
 * <p>Este filtro se ejecuta una sola vez por petición (OncePerRequestFilter)
 * y realiza las siguientes validaciones:
 * <ul>
 *   <li>Extrae el token del header "Authorization: Bearer {token}"</li>
 *   <li>Verifica si el token está en la blacklist (invalidado)</li>
 *   <li>Valida la firma y expiración del token</li>
 *   <li>Carga el usuario desde la base de datos</li>
 *   <li>Establece la autenticación en el SecurityContext</li>
 * </ul>
 * 
 * @author DynaDocs Team
 * @version 1.0
 * @since 2025-12-03
 * @see JwtUtils
 * @see TokenBlacklistService
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserRepository userRepository;

    /**
     * Constructor para inyección de dependencias.
     * 
     * @param jwtUtils utilidad para generar y validar tokens JWT
     * @param tokenBlacklistService servicio para gestionar tokens invalidados
     * @param userRepository repositorio para recuperar usuarios de la BD
     */
    public JwtFilter(JwtUtils jwtUtils, TokenBlacklistService tokenBlacklistService, UserRepository userRepository) {
        this.jwtUtils = jwtUtils;
        this.tokenBlacklistService = tokenBlacklistService;
        this.userRepository = userRepository;
    }

    /**
     * Método principal que filtra cada petición HTTP.
     * Extrae, valida el token JWT y establece la autenticación.
     * 
     * <p>Flujo de ejecución:
     * <ol>
     *   <li>Extrae el token del header Authorization</li>
     *   <li>Verifica si está en blacklist (logout)</li>
     *   <li>Valida firma y extrae username</li>
     *   <li>Carga el usuario completo desde BD</li>
     *   <li>Establece autenticación en SecurityContext</li>
     *   <li>Continúa con la cadena de filtros</li>
     * </ol>
     * 
     * @param request la petición HTTP entrante
     * @param response la respuesta HTTP
     * @param filterChain cadena de filtros de Spring Security
     * @throws ServletException si ocurre un error de servlet
     * @throws IOException si ocurre un error de I/O
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // Verificar si el token está invalidado
            if (tokenBlacklistService.isTokenInvalidated(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token inválido o expirado");
                return;
            }

            try {
                String username = jwtUtils.validateTokenAndGetUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Recuperar el usuario desde la base de datos
                    User user = userRepository.findByEmail(username).orElse(null);
                    if (user != null) {
                        UsernamePasswordAuthenticationToken authToken = 
                            new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("Usuario autenticado establecido en el contexto: " + user.getEmail());
                    } else {
                        System.out.println("Usuario no encontrado en la base de datos: " + username);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error al validar el token: " + e.getMessage());
            }
        } else {
            System.out.println("Solicitud sin encabezado de autorización");
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Determina si este filtro debe omitirse para ciertas rutas públicas.
     * Las rutas de autenticación (/login, /register) no requieren token JWT.
     * 
     * @param request la petición HTTP a evaluar
     * @return true si el filtro debe omitirse, false en caso contrario
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // Se excluyen las rutas que deben ser públicas para todos los usuarios
        return path.equals("/login") || path.equals("/register");
    }
}