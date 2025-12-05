package com.example.dinadocs.services;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servicio para procesar plantillas HTML con el motor Mustache.
 */
@Service
public class TemplateProcessor {

    /**
     * Extrae los nombres de los placeholders {{nombre}} de una plantilla HTML.
     * 
     * @param templateContent contenido de la plantilla HTML con placeholders Mustache
     * @return lista con los nombres de los placeholders encontrados
     */
    public List<String> extractPlaceholders(String templateContent) {
        List<String> matches = new ArrayList<>();

        if (templateContent == null || templateContent.isEmpty()) {
            return matches;
        }

        Pattern pattern = Pattern.compile("\\{\\{([^\\}]+)\\}\\}");
        Matcher matcher = pattern.matcher(templateContent);

        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        return matches;
    }

    /**
     * Procesa una plantilla HTML reemplazando los placeholders con datos reales.
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