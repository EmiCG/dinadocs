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
 * Servicio para procesar plantillas dinámicas con datos proporcionados.
 */
@Service
public class TemplateProcessor {

    /**
     * Procesa una plantilla con los datos proporcionados.
     *
     * @param templateContent Contenido de la plantilla (HTML con placeholders).
     * @param data Datos dinámicos para reemplazar los placeholders.
     * @return String con la plantilla procesada.
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