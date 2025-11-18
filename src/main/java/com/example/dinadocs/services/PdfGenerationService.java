package com.example.dinadocs.services;

import com.example.dinadocs.models.GenerationRequest;
import com.example.dinadocs.models.Template;
import com.example.dinadocs.repositories.TemplateRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Servicio (capa de lógica de negocio) para el módulo de generación de PDFs.
 *
 * @see com.example.dinadocs.controllers.PdfController
 * @see com.example.dinadocs.models.GenerationRequest
 */
@Service
public class PdfGenerationService {

    private final TemplateRepository templateRepository;

    /**
     * Constructor para inyección de dependencias.
     * @param templateRepository Repositorio para acceder a las plantillas en la BD.
     */
    public PdfGenerationService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
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
        String htmlTemplate = template.getContent();
        Map<String, Object> data = request.getData();

        String htmlFusionado = fuse(htmlTemplate, data);

        byte[] pdfBytes = convertHtmlToPdf(htmlFusionado);
        
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
    private Template loadTemplateByType(String templateType) {
        return templateRepository.findByName(templateType)
                .orElseThrow(() -> new NoSuchElementException("La plantilla '" + templateType + "' no existe."));
    }

    /**
     * Lógica de Fusión: Reemplaza los placeholders (ej. {{key}}) en el HTML 
     * con los valores del mapa 'data'.
     *
     * @param htmlTemplate El HTML de la plantilla (con {{placeholders}}).
     * @param data Los valores del usuario.
     * @return El HTML fusionado.
     */
    private String fuse(String htmlTemplate, Map<String, Object> data) {
        String fusedHtml = htmlTemplate;

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            String placeholder = "{{" + key + "}}";
            
            fusedHtml = fusedHtml.replace(placeholder, value);
        }
        
        fusedHtml = fusedHtml.replaceAll("\\{\\{.*?\\}\\}", "[DATO NO PROPORCIONADO]");

        return fusedHtml;
    }

    /**
     * RNF-03: Convierte una cadena de HTML/CSS en un array de bytes (PDF)
     * usando Jsoup y Flying Saucer (ITextRenderer).
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
