package com.example.dinadocs;

import com.example.dinadocs.models.Role;
import com.example.dinadocs.models.Template;
import com.example.dinadocs.models.User;
import com.example.dinadocs.repositories.TemplateRepository;
import com.example.dinadocs.services.TemplateProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.dinadocs.services.TemplateService;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para TemplateService.
 * Se enfoca en la lógica de negocio y autorización (Nivel 2).
 *
 */
@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private TemplateProcessor templateProcessor;

    @InjectMocks
    private TemplateService templateService;

    // --- Datos de prueba ---
    private User adminUser;
    private User creatorUser;
    private User standardUser;
    private Template publicTemplate;
    private Template privateTemplate;

    /**
     * Configura los usuarios y plantillas de prueba antes de cada test.
     */
    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(Role.ADMIN);

        creatorUser = new User();
        creatorUser.setId(2L);
        creatorUser.setRole(Role.CREADOR);

        standardUser = new User();
        standardUser.setId(3L);
        standardUser.setRole(Role.USUARIO);

        publicTemplate = new Template();
        publicTemplate.setId(101L);
        publicTemplate.setPublic(true);
        publicTemplate.setOwner(creatorUser);

        privateTemplate = new Template();
        privateTemplate.setId(102L);
        privateTemplate.setPublic(false);
        privateTemplate.setOwner(standardUser);
    }

    /**
     * Prueba que un USUARIO al guardar, la plantilla es privada y tiene dueño.
     *
     */
    @Test
    void testSave_WhenUserIsUsuario_ShouldBePrivate() {
        Template newTemplate = new Template();
        newTemplate.setName("Plantilla de Usuario");
        newTemplate.setContent("<html>{{nombre}}</html>");

        // Simula la extracción de placeholders
        when(templateProcessor.extractPlaceholders(anyString())).thenReturn(Arrays.asList("nombre"));
        // Simula la acción de guardado
        when(templateRepository.save(any(Template.class))).thenReturn(newTemplate);

        // Llama al método
        Template savedTemplate = templateService.save(newTemplate, standardUser);

        // Verifica
        assertFalse(savedTemplate.isPublic());
        assertEquals(standardUser, savedTemplate.getOwner());
    }

    /**
     * Prueba que un CREADOR al guardar, la plantilla es pública.
     *
     */
    @Test
    void testSave_WhenUserIsCreator_ShouldBePublic() {
        Template newTemplate = new Template();
        newTemplate.setName("Plantilla de Creador");
        newTemplate.setContent("<html>{{titulo}}</html>");

        when(templateProcessor.extractPlaceholders(anyString())).thenReturn(Arrays.asList("titulo"));
        when(templateRepository.save(any(Template.class))).thenReturn(newTemplate);

        Template savedTemplate = templateService.save(newTemplate, creatorUser);

        assertTrue(savedTemplate.isPublic());
        assertEquals(creatorUser, savedTemplate.getOwner());
    }

    /**
     * Prueba que un USUARIO puede ver su plantilla privada.
     *
     */
    @Test
    void testFindById_WhenUserIsOwner_ShouldReturnTemplate() throws AccessDeniedException {
        // Configura el mock
        when(templateRepository.findById(102L)).thenReturn(Optional.of(privateTemplate));

        Template found = templateService.findById(102L, standardUser);

        assertNotNull(found);
        assertEquals(102L, found.getId());
    }

    /**
     * Prueba que un USUARIO puede ver una plantilla pública.
     *
     */
    @Test
    void testFindById_WhenTemplateIsPublic_ShouldReturnTemplate() throws AccessDeniedException {
        when(templateRepository.findById(101L)).thenReturn(Optional.of(publicTemplate));

        Template found = templateService.findById(101L, standardUser);

        assertNotNull(found);
        assertEquals(101L, found.getId());
    }

    /**
     * Prueba que un ADMIN puede ver una plantilla privada que no le pertenece.
     *
     */
    @Test
    void testFindById_WhenAdminIsNotOwner_ShouldReturnTemplate() throws AccessDeniedException {
        when(templateRepository.findById(102L)).thenReturn(Optional.of(privateTemplate));

        Template found = templateService.findById(102L, adminUser);

        assertNotNull(found);
    }

    /**
     * Prueba que un USUARIO NO puede ver una plantilla privada ajena.
     *
     */
    @Test
    void testFindById_WhenUserIsNotOwner_ShouldThrowAccessDenied() {
        // El 'creatorUser' (ID 2) intenta ver la plantilla del 'standardUser' (ID 3)
        when(templateRepository.findById(102L)).thenReturn(Optional.of(privateTemplate));

        assertThrows(AccessDeniedException.class, () -> {
            templateService.findById(102L, creatorUser);
        });
    }

    /**
     * Prueba que un USUARIO NO puede borrar una plantilla pública.
     *
     */
    @Test
    void testDelete_WhenUserTriesPublic_ShouldThrowAccessDenied() {
        when(templateRepository.findById(101L)).thenReturn(Optional.of(publicTemplate));

        assertThrows(AccessDeniedException.class, () -> {
            templateService.delete(101L, standardUser);
        });
    }

    /**
     * Prueba que un CREADOR NO puede borrar una plantilla pública.
     *
     */
    @Test
    void testDelete_WhenCreatorTriesPublic_ShouldThrowAccessDenied() {
        when(templateRepository.findById(101L)).thenReturn(Optional.of(publicTemplate));

        assertThrows(AccessDeniedException.class, () -> {
            templateService.delete(101L, creatorUser);
        }, "Solo un ADMIN puede borrar plantillas públicas.");
    }

    /**
     * Prueba que un USUARIO SÍ puede borrar su propia plantilla privada.
     *
     */
    @Test
    void testDelete_WhenUserIsOwner_ShouldDelete() throws AccessDeniedException {
        when(templateRepository.findById(102L)).thenReturn(Optional.of(privateTemplate));

        // Llama al método
        templateService.delete(102L, standardUser);

        // Verifica que el método delete del repositorio fue llamado
        verify(templateRepository, times(1)).delete(privateTemplate);
    }

    /**
     * Prueba que un ADMIN SÍ puede borrar una plantilla pública.
     *
     */
    @Test
    void testDelete_WhenAdminTriesPublic_ShouldDelete() throws AccessDeniedException {
        when(templateRepository.findById(101L)).thenReturn(Optional.of(publicTemplate));

        templateService.delete(101L, adminUser);

        verify(templateRepository, times(1)).delete(publicTemplate);
    }

    /**
     * Prueba que un ADMIN SÍ puede borrar una plantilla privada ajena.
     *
     */
    @Test
    void testDelete_WhenAdminTriesPrivate_ShouldDelete() throws AccessDeniedException {
        when(templateRepository.findById(102L)).thenReturn(Optional.of(privateTemplate));

        templateService.delete(102L, adminUser);

        verify(templateRepository, times(1)).delete(privateTemplate);
    }
}