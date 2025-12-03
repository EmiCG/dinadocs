package com.example.dinadocs.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TemplateProcessorTest {

    private TemplateProcessor templateProcessor;

    @BeforeEach
    void setUp() {
        templateProcessor = new TemplateProcessor();
    }

    @Test
    void testProcessTemplateSimple() {
        String template = "<html><body>Hola {{nombre}}</body></html>";
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", "Juan Pérez");

        String result = templateProcessor.processTemplate(template, data);

        assertNotNull(result);
        assertTrue(result.contains("Juan Pérez"));
        assertFalse(result.contains("{{nombre}}"));
    }

    @Test
    void testProcessTemplateMultiplePlaceholders() {
        String template = "<html><body>{{saludo}} {{nombre}}, tienes {{edad}} años.</body></html>";
        Map<String, Object> data = new HashMap<>();
        data.put("saludo", "Hola");
        data.put("nombre", "Juana");
        data.put("edad", 30);

        String result = templateProcessor.processTemplate(template, data);

        assertNotNull(result);
        assertTrue(result.contains("Hola"));
        assertTrue(result.contains("Juana"));
        assertTrue(result.contains("30"));
    }

    @Test
    void testProcessTemplateWithMissingData() {
        String template = "<html><body>Hola {{nombre}}, {{faltante}}</body></html>";
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", "Juan");

        String result = templateProcessor.processTemplate(template, data);

        assertNotNull(result);
        assertTrue(result.contains("Juan"));
    }

    @Test
    void testProcessTemplateEmptyData() {
        String template = "<html><body>{{nombre}}</body></html>";
        Map<String, Object> data = new HashMap<>();

        String result = templateProcessor.processTemplate(template, data);

        assertNotNull(result);
    }
}
