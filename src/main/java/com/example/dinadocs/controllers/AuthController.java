package com.example.dinadocs.controllers;

import com.example.dinadocs.models.User;
import com.example.dinadocs.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        authService.register(user);
        return "Usuario registrado con éxito";
    }

    @PostMapping("/login")
    public Map<String, Object> authenticateUser(@RequestBody Map<String, String> request) {
        try {
            return authService.login(request.get("email"), request.get("password"));
        } catch (Exception e) {
            throw new RuntimeException("Error de autenticación: " + e.getMessage());
        }
    }

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