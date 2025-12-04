package com.example.dinadocs.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de prueba para verificar autenticación JWT.
 * Endpoint protegido útil para validar que el sistema de seguridad funciona.
 * 
 * @author DynaDocs Team
 * @version 1.0
 * @since 2025-12-03
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * Endpoint de prueba protegido por JWT.
     * Solo accesible con un token válido.
     * 
     * @return mensaje de bienvenida
     */
    @GetMapping("/saludo")
    public String saludo() {
        return "¡Felicidades! Has entrado a la ZONA VIP con tu Token JWT 🎫";
    }
}