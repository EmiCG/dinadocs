package com.example.dinadocs.models;

/**
 * Define los niveles de autorización del sistema.
 * Basado en la sección 1.4 del "Contrato de API y especificación.md".
 * 
 * <p>Jerarquía de roles y permisos:
 * <ul>
 *   <li><b>USUARIO</b>: Puede crear y gestionar solo plantillas privadas</li>
 *   <li><b>CREADOR</b>: Puede crear y gestionar plantillas públicas y privadas</li>
 *   <li><b>ADMIN</b>: Acceso total, puede gestionar todas las plantillas del sistema</li>
 * </ul>
 * 
 * @author DynaDocs Team
 * @version 1.0
 * @since 2025-12-03
 */
public enum Role {
    /**
     * Usuario básico: gestiona únicamente plantillas privadas.
     */
    USUARIO,
    
    /**
     * Usuario creador: gestiona plantillas públicas y privadas.
     */
    CREADOR,
    
    /**
     * Administrador: acceso total al sistema.
     */
    ADMIN
}