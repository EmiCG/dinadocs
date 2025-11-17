package com.example.dinadocs.controllers;

import com.example.dinadocs.models.Template;
import com.example.dinadocs.models.User;
import com.example.dinadocs.services.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.nio.file.AccessDeniedException;
import java.util.List;

/**
 * Controlador (API REST Endpoints) para el CRUD de Plantillas.
 * Actúa como interfaz HTTP y delega toda la lógica al TemplateService.
 *
 */
@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    /**
     * Endpoint para crear una nueva plantilla.
     * POST /api/templates
     *
     */
    @PostMapping
    public ResponseEntity<Template> createTemplate(@RequestBody Template template,
                                                   @AuthenticationPrincipal User authUser) {
        Template newTemplate = templateService.save(template, authUser);
        return new ResponseEntity<>(newTemplate, HttpStatus.CREATED);
    }

    /**
     * Endpoint para listar plantillas (filtradas por rol).
     * GET /api/templates
     *
     */
    @GetMapping
    public ResponseEntity<List<Template>> getAllTemplates(@AuthenticationPrincipal User authUser) {
        List<Template> templates = templateService.findAllByRole(authUser);
        return ResponseEntity.ok(templates);
    }

    /**
     * Endpoint para obtener una plantilla específica por ID.
     * GET /api/templates/{id}
     *
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTemplateById(@PathVariable Long id,
                                             @AuthenticationPrincipal User authUser) {
        try {
            Template template = templateService.findById(id, authUser);
            return ResponseEntity.ok(template);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>("Acceso denegado", HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint para actualizar una plantilla existente.
     * PUT /api/templates/{id}
     *
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTemplate(@PathVariable Long id,
                                            @RequestBody Template templateDetails,
                                            @AuthenticationPrincipal User authUser) {
        try {
            Template updatedTemplate = templateService.update(id, templateDetails, authUser);
            return ResponseEntity.ok(updatedTemplate);
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>("Acceso denegado", HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint para eliminar una plantilla.
     * DELETE /api/templates/{id}
     *
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTemplate(@PathVariable Long id,
                                            @AuthenticationPrincipal User authUser) {
        try {
            templateService.delete(id, authUser);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return new ResponseEntity<>("Acceso denegado", HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}