package com.example.dinadocs.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// clase para probar el endpoint protegido con JWT
// no es necesario para la documentación pero útil para pruebas

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/saludo")
    public String saludo() {
        return "¡Felicidades! Has entrado a la ZONA VIP con tu Token JWT 🎫";
    }
}