package com.example.dinadocs.controllers;

import com.example.dinadocs.models.User;
import com.example.dinadocs.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        authService.register(user); // Delegamos al servicio
        return "Usuario registrado con éxito";
    }

    @PostMapping("/login")
    public Map<String, String> authenticateUser(@RequestBody Map<String, String> request) {
        String token = authService.login(request.get("username"), request.get("password"));
        
        // Formateamos la respuesta
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }
}