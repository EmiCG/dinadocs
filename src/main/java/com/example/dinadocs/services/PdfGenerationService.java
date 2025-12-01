package com.example.dinadocs.services;

import com.example.dinadocs.models.GenerationRequest;
import com.example.dinadocs.models.Template;
import com.example.dinadocs.repositories.TemplateRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.List;

/**
 * Servicio (capa de lógica de negocio) para el módulo de generación de PDFs.
 *
 * @see com.example.dinadocs.controllers.PdfController
 * @see com.example.dinadocs.models.GenerationRequest
 */
@Service
public class PdfGenerationService {

    private final TemplateRepository templateRepository;
    private final TemplateProcessor templateProcessor;

    /**
     * Constructor para inyección de dependencias.
     * @param templateRepository Repositorio para acceder a las plantillas en la BD.
     * @param templateProcessor Procesador de plantillas para la fusión de datos.
     */
    public PdfGenerationService(TemplateRepository templateRepository, TemplateProcessor templateProcessor) {
        this.templateRepository = templateRepository;
        this.templateProcessor = templateProcessor;
    }
   
    /**
     * Funcion orquestadora del proceso completo de generación de PDF.
     *
     * @param request El DTO (GenerationRequest) con el tipo de plantilla y los datos.
     * @return Un array de bytes (byte[]) que representa el archivo PDF generado.
     * @throws IllegalArgumentException Si la validación de datos falla.
     * @throws NoSuchElementException Si el 'templateType' no se encuentra en la BD.
     * @throws RuntimeException Si la conversión de PDF falla.
     */
    public byte[] generatePdf(GenerationRequest request) {
        
        validateData(request);

        String templateType = request.getTemplateType();
        Template template = loadTemplateByType(templateType);
        Map<String, Object> data = request.getData();

        validatePlaceholders(template, data);

        // Procesar la plantilla dinámicamente usando TemplateProcessor
        String processedTemplate = templateProcessor.processTemplate(template.getContent(), data);

        byte[] pdfBytes = convertHtmlToPdf(processedTemplate);
        
        return pdfBytes;
    }

    /**
     * Valida la solicitud de entrada.
     * @param request El DTO de la solicitud.
     * @throws IllegalArgumentException Si 'data' o 'templateType' son nulos o vacíos.
     */
    private void validateData(GenerationRequest request) {
        Map<String, Object> data = request.getData();

        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Los datos (data) para la generación del documento no pueden estar vacíos.");
        }
        if (request.getTemplateType() == null || request.getTemplateType().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de plantilla (templateType) no puede estar vacío.");
        }
    }

    /**
     * Carga la entidad Template desde la base de datos usando el 'templateType'.
     * @param templateType El nombre (identificador) de la plantilla.
     * @return La entidad Template.
     * @throws NoSuchElementException Si no se encuentra una plantilla con ese nombre.
     */
    public Template loadTemplateByType(String templateType) {
        return templateRepository.findByName(templateType)
                .orElseThrow(() -> new NoSuchElementException("La plantilla '" + templateType + "' no existe."));
    }

    /**
     * Valida que todos los marcadores de posición (placeholders) requeridos por la plantilla
     * estén presentes en los datos proporcionados.
     *
     * @param template La plantilla que contiene los placeholders requeridos.
     * @param data Los datos proporcionados por el usuario.
     * @throws IllegalArgumentException Si falta algún placeholder requerido.
     */
    private void validatePlaceholders(Template template, Map<String, Object> data) {
        for (String placeholder : template.getPlaceholders()) {
            String normalizedPlaceholder = placeholder.replace("#", "");

            if (!data.containsKey(normalizedPlaceholder)) {
                System.out.println("Advertencia: Falta el dato para el marcador de posición opcional: " + placeholder);
                continue;
            }

            Object value = data.get(normalizedPlaceholder);
            if (value == null) {
                System.out.println("Advertencia: El valor para el marcador de posición opcional '" + placeholder + "' es nulo.");
                continue;
            }

            if (value instanceof List) {
                List<?> list = (List<?>) value;
                for (Object item : list) {
                    if (item instanceof Map) {
                        Map<?, ?> itemMap = (Map<?, ?>) item;
                        for (String subPlaceholder : template.getPlaceholders()) {
                            if (subPlaceholder.startsWith(normalizedPlaceholder + ".")) {
                                String subKey = subPlaceholder.replace(normalizedPlaceholder + ".", "");
                                if (!itemMap.containsKey(subKey)) {
                                    System.out.println("Advertencia: Falta el dato para el marcador de posición opcional: " + subPlaceholder);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param htmlContent El string de HTML/CSS ya fusionado.
     * @return El archivo PDF como un array de bytes.
     * @throws RuntimeException Si la conversión falla.
     */
    private byte[] convertHtmlToPdf(String htmlContent) {
        try {
            Document document = Jsoup.parse(htmlContent);
            document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ITextRenderer renderer = new ITextRenderer();
            
            renderer.setDocumentFromString(document.html());
            
            renderer.layout();
            renderer.createPDF(outputStream);

            byte[] pdfBytes = outputStream.toByteArray();
            
            outputStream.close();

            return pdfBytes;

        } catch (Exception e) {
            throw new RuntimeException("Error interno al convertir HTML a PDF: " + e.getMessage(), e);
        }
    }
}
