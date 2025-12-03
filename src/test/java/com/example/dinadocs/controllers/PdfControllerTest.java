package com.example.dinadocs.controllers;

import com.example.dinadocs.models.GenerationRequest;
import com.example.dinadocs.services.PdfGenerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PdfControllerTest {

    @Mock
    private PdfGenerationService pdfService;

    @InjectMocks
    private PdfController pdfController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateDocumentSuccess() {
        GenerationRequest request = new GenerationRequest();
        request.setTemplateType("factura");
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", "Juan Pérez");
        request.setData(data);

        byte[] mockPdf = "Contenido PDF".getBytes();
        when(pdfService.generatePdf(request)).thenReturn(mockPdf);

        ResponseEntity<?> response = pdfController.generateDocument(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals(mockPdf, (byte[]) response.getBody());
        verify(pdfService, times(1)).generatePdf(request);
    }

    @Test
    void testGenerateDocumentBadRequest() {
        GenerationRequest request = new GenerationRequest();
        request.setTemplateType("factura");
        request.setData(new HashMap<>());

        when(pdfService.generatePdf(request))
                .thenThrow(new IllegalArgumentException("Los datos no pueden estar vacíos"));

        ResponseEntity<?> response = pdfController.generateDocument(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(pdfService, times(1)).generatePdf(request);
    }

    @Test
    void testGenerateDocumentInternalServerError() {
        GenerationRequest request = new GenerationRequest();
        request.setTemplateType("factura");
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", "Juan Pérez");
        request.setData(data);

        when(pdfService.generatePdf(request))
                .thenThrow(new RuntimeException("Error interno"));

        ResponseEntity<?> response = pdfController.generateDocument(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(pdfService, times(1)).generatePdf(request);
    }
}
