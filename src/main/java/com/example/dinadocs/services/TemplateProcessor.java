package com.example.dinadocs.services;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

/**
 * Servicio para procesar plantillas HTML con el motor Mustache.
 * Reemplaza los placeholders {{variable}} con valores dinámicos.
 * 
 * <p>Utiliza Mustache.java como motor de plantillas, que soporta:
 * <ul>
 *   <li>Variables simples: {{nombre}}</li>
 *   <li>Secciones/Loops: {{#items}} ... {{/items}}</li>
 *   <li>Condicionales: {{#condicion}} ... {{/condicion}}</li>
 *   <li>Secciones invertidas: {{^variable}} ... {{/variable}}</li>
 * </ul>
 * 
 * @author DynaDocs Team
 * @version 1.0
 * @since 2025-12-03
 * @see com.github.mustachejava.Mustache
 */
@Service
public class TemplateProcessor {

    /**
     * Procesa una plantilla HTML con Mustache reemplazando placeholders.
     * 
     * <p>Ejemplo:
     * <pre>
     * String template = "&lt;h1&gt;Hola {{nombre}}&lt;/h1&gt;";
     * Map data = Map.of("nombre", "Juan");
     * String result = processTemplate(template, data);
     * // result = "&lt;h1&gt;Hola Juan&lt;/h1&gt;"
     * </pre>
     *
     * @param templateContent contenido de la plantilla HTML con placeholders Mustache
     * @param data mapa con los datos dinámicos (clave: nombre del placeholder, valor: dato)
     * @return String con la plantilla procesada y placeholders reemplazados
     * @throws RuntimeException si ocurre un error durante el procesamiento
     */
    public String processTemplate(String templateContent, Map<String, Object> data) {
        try {
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new StringReader(templateContent), "template");
            StringWriter writer = new StringWriter();
            mustache.execute(writer, data).flush();
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error al procesar la plantilla", e);
        }
    }
}