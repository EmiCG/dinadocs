package com.example.dinadocs.services;

import com.example.dinadocs.models.GenerationRequest;
import com.example.dinadocs.models.Template;
import com.example.dinadocs.repositories.TemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PdfGenerationServiceTest {

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private TemplateProcessor templateProcessor;

    @InjectMocks
    private PdfGenerationService pdfGenerationService;

    private Template testTemplate;
    private GenerationRequest testRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testTemplate = new Template();
        testTemplate.setId(1L);
        testTemplate.setName("factura");
        testTemplate.setContent("<html><body>{{nombre}}</body></html>");
        testTemplate.setPlaceholders(Arrays.asList("nombre"));

        testRequest = new GenerationRequest();
        testRequest.setTemplateType("factura");
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", "Juan Pérez");
        testRequest.setData(data);
    }

    @Test
    void testGeneratePdfSuccess() {
        when(templateRepository.findByName("factura")).thenReturn(Optional.of(testTemplate));
        when(templateProcessor.processTemplate(anyString(), anyMap()))
                .thenReturn("<html><body>Juan Pérez</body></html>");

        byte[] result = pdfGenerationService.generatePdf(testRequest);

        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(templateRepository, times(1)).findByName("factura");
        verify(templateProcessor, times(1)).processTemplate(anyString(), anyMap());
    }

    @Test
    void testGeneratePdfEmptyData() {
        testRequest.setData(new HashMap<>());

        assertThrows(IllegalArgumentException.class, () -> {
            pdfGenerationService.generatePdf(testRequest);
        });

        verify(templateRepository, times(0)).findByName(anyString());
    }

    @Test
    void testGeneratePdfNullData() {
        testRequest.setData(null);

        assertThrows(IllegalArgumentException.class, () -> {
            pdfGenerationService.generatePdf(testRequest);
        });

        verify(templateRepository, times(0)).findByName(anyString());
    }

    @Test
    void testGeneratePdfEmptyTemplateType() {
        testRequest.setTemplateType("");

        assertThrows(IllegalArgumentException.class, () -> {
            pdfGenerationService.generatePdf(testRequest);
        });

        verify(templateRepository, times(0)).findByName(anyString());
    }

    @Test
    void testGeneratePdfTemplateNotFound() {
        when(templateRepository.findByName("noexistente")).thenReturn(Optional.empty());
        testRequest.setTemplateType("noexistente");

        assertThrows(NoSuchElementException.class, () -> {
            pdfGenerationService.generatePdf(testRequest);
        });

        verify(templateRepository, times(1)).findByName("noexistente");
    }

    @Test
    void testLoadTemplateByTypeSuccess() {
        when(templateRepository.findByName("factura")).thenReturn(Optional.of(testTemplate));

        Template result = pdfGenerationService.loadTemplateByType("factura");

        assertNotNull(result);
        assertEquals("factura", result.getName());
        verify(templateRepository, times(1)).findByName("factura");
    }

    @Test
    void testLoadTemplateByTypeNotFound() {
        when(templateRepository.findByName("desconocido")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            pdfGenerationService.loadTemplateByType("desconocido");
        });

        verify(templateRepository, times(1)).findByName("desconocido");
    }
}
