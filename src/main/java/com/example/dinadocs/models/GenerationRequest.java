package com.example.dinadocs.models;

import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) para la petición de generación de PDF.
 * Define el contrato JSON que el cliente (Flutter) debe enviar.
 *
 * @see com.example.dinadocs.services.PdfGenerationService
 */
@Data
@NoArgsConstructor
public class GenerationRequest {

    /**
     * El nombre de la plantilla a utilizar.
     * (Ej: "Factura", "Perfil").
     * @see com.example.dinadocs.models.Template#name
     */
    private String templateType;

    /**
     * Mapa de pares clave-valor que contienen los datos dinámicos.
     * La clave (String) es el nombre del placeholder (ej: "nombre_cliente").
     * El valor (Object) es el dato a fusionar (ej: "Emiliano Contreras").
     *
     * Manejo de Imágenes: Las imágenes deben ser enviadas por el cliente
     * como un String Base64 completo (esta responsabilidad de delega al front).
     */
    private Map<String, Object> data; 

}
