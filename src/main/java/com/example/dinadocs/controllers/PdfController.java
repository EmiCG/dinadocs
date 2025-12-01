package com.example.dinadocs.controllers;

// import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
// import org.jsoup.Jsoup;
// import org.jsoup.nodes.Document;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// import org.xhtmlrenderer.pdf.ITextRenderer;

import com.example.dinadocs.services.PdfGenerationService;
import com.example.dinadocs.models.GenerationRequest;

/**
 * Controlador REST para manejar peticiones de generación de PDF.
 *
 * @see com.example.dinadocs.services.PdfGenerationService
 */
@RestController
@RequestMapping("/api")
public class PdfController {

    private final PdfGenerationService pdfService;

    public PdfController(PdfGenerationService pdfService) {
        this.pdfService = pdfService;
    }

    /**
     * Endpoint para generar el PDF.
     * Recibe JSON, delega la lógica al servicio y devuelve el archivo binario.
     *
     * @param request El DTO (GenerationRequest) mapeado desde el JSON del body.
     * @return ResponseEntity<byte[]> (El PDF) o un ResponseEntity de error.
     */
    @PostMapping("/generatePDF")
    public ResponseEntity<?> generateDocument(@RequestBody GenerationRequest request) {
        try {
            // Generate the PDF using the service
            byte[] pdfBytes = pdfService.generatePdf(request);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);

            String filename = request.getTemplateType() + "_generado.pdf";
            headers.setContentDispositionFormData("attachment", filename);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage().getBytes(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
