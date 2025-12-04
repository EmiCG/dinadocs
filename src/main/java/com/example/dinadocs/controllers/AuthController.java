package com.example.dinadocs.controllers;

import com.example.dinadocs.models.User;
import com.example.dinadocs.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para endpoints de autenticación y autorización.
 * Gestiona el registro, login y logout de usuarios.
 * 
 * @author DynaDocs Team
 * @version 1.0
 * @since 2025-12-03
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    /**
     * Registra un nuevo usuario en el sistema.
     * 
     * @param user datos del usuario a registrar
     * @return mensaje de confirmación
     */
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        authService.register(user);
        return "Usuario registrado con éxito";
    }

    /**
     * Autentica un usuario y genera un token JWT.
     * 
     * @param request mapa con "email" y "password"
     * @return mapa con "token" y "role" del usuario autenticado
     * @throws RuntimeException si las credenciales son inválidas
     */
    @PostMapping("/login")
    public Map<String, Object> authenticateUser(@RequestBody Map<String, String> request) {
        try {
            return authService.login(request.get("email"), request.get("password"));
        } catch (Exception e) {
            throw new RuntimeException("Error de autenticación: " + e.getMessage());
        }
    }

    /**
     * Cierra la sesión del usuario invalidando su token JWT.
     * 
     * @param token el token JWT en el header Authorization
     * @return ResponseEntity con mensaje de confirmación o error
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");

        if (!authService.isTokenValid(jwt)) {
            return ResponseEntity.status(400).body("Token inválido o ya cerrado");
        }

        authService.logout(jwt);
        
        return ResponseEntity.ok("Sesión cerrada correctamente");
    }
}