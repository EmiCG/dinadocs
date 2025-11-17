package com.example.dinadocs.models;

/**
 * Define los niveles de autorización del sistema.
 * Basado en la sección 1.4 del "Contrato de API y especificación.md".
 */
public enum Role {
    USUARIO,  // Gestiona plantillas privadas
    CREADOR,  // Gestiona plantillas públicas
    ADMIN     // Gestiona todo
}