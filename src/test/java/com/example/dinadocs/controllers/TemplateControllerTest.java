package com.example.dinadocs.controllers;

import com.example.dinadocs.models.Role;
import com.example.dinadocs.models.Template;
import com.example.dinadocs.models.User;
import com.example.dinadocs.services.TemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TemplateControllerTest {

    @Mock
    private TemplateService templateService;

    @InjectMocks
    private TemplateController templateController;

    private User testUser;
    private Template testTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("prueba@ejemplo.com");
        testUser.setRole(Role.USUARIO);

        testTemplate = new Template();
        testTemplate.setId(1L);
        testTemplate.setName("Plantilla de Prueba");
        testTemplate.setContent("<html>{{nombre}}</html>");
        testTemplate.setOwner(testUser);
    }

    @Test
    void testCreateTemplate() {
        when(templateService.save(any(Template.class), eq(testUser))).thenReturn(testTemplate);

        ResponseEntity<Template> response = templateController.createTemplate(testTemplate, testUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Plantilla de Prueba", response.getBody().getName());
        verify(templateService, times(1)).save(any(Template.class), eq(testUser));
    }

    @Test
    void testGetAllTemplates() {
        List<Template> templates = new ArrayList<>();
        templates.add(testTemplate);

        when(templateService.findAllByRole(testUser)).thenReturn(templates);

        ResponseEntity<List<Template>> response = templateController.getAllTemplates(testUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(templateService, times(1)).findAllByRole(testUser);
    }

    @Test
    void testGetTemplateByIdSuccess() throws AccessDeniedException {
        when(templateService.findById(1L, testUser)).thenReturn(testTemplate);

        ResponseEntity<?> response = templateController.getTemplateById(1L, testUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(templateService, times(1)).findById(1L, testUser);
    }

    @Test
    void testGetTemplateByIdAccessDenied() throws AccessDeniedException {
        when(templateService.findById(1L, testUser))
                .thenThrow(new AccessDeniedException("No tiene permiso para ver esta plantilla."));

        ResponseEntity<?> response = templateController.getTemplateById(1L, testUser);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Acceso denegado", response.getBody());
        verify(templateService, times(1)).findById(1L, testUser);
    }

    @Test
    void testGetTemplateByIdNotFound() throws AccessDeniedException {
        when(templateService.findById(999L, testUser))
                .thenThrow(new RuntimeException("Plantilla no encontrada con id: 999"));

        ResponseEntity<?> response = templateController.getTemplateById(999L, testUser);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(templateService, times(1)).findById(999L, testUser);
    }

    @Test
    void testUpdateTemplateSuccess() throws AccessDeniedException {
        Template updatedTemplate = new Template();
        updatedTemplate.setName("Plantilla Actualizada");
        updatedTemplate.setContent("<html>{{nombreActualizado}}</html>");

        when(templateService.update(eq(1L), any(Template.class), eq(testUser))).thenReturn(updatedTemplate);

        ResponseEntity<?> response = templateController.updateTemplate(1L, updatedTemplate, testUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(templateService, times(1)).update(eq(1L), any(Template.class), eq(testUser));
    }

    @Test
    void testUpdateTemplateAccessDenied() throws AccessDeniedException {
        when(templateService.update(eq(1L), any(Template.class), eq(testUser)))
                .thenThrow(new AccessDeniedException("Un USUARIO no puede modificar plantillas ajenas."));

        ResponseEntity<?> response = templateController.updateTemplate(1L, testTemplate, testUser);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Acceso denegado", response.getBody());
        verify(templateService, times(1)).update(eq(1L), any(Template.class), eq(testUser));
    }

    @Test
    void testDeleteTemplateSuccess() throws AccessDeniedException {
        doNothing().when(templateService).delete(1L, testUser);

        ResponseEntity<?> response = templateController.deleteTemplate(1L, testUser);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(templateService, times(1)).delete(1L, testUser);
    }

    @Test
    void testDeleteTemplateAccessDenied() throws AccessDeniedException {
        doThrow(new AccessDeniedException("Acceso denegado"))
                .when(templateService).delete(1L, testUser);

        ResponseEntity<?> response = templateController.deleteTemplate(1L, testUser);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Acceso denegado", response.getBody());
        verify(templateService, times(1)).delete(1L, testUser);
    }
}
