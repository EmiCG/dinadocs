package com.example.dinadocs.services;

import com.example.dinadocs.models.Role;
import com.example.dinadocs.models.Template;
import com.example.dinadocs.models.User;
import com.example.dinadocs.repositories.TemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias completas para TemplateService.
 * Cubre toda la lógica de negocio y autorización.
 */
@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    @Mock
    private TemplateRepository templateRepository;

    @InjectMocks
    private TemplateService templateService;

    private User adminUser;
    private User creatorUser;
    private User standardUser;
    private Template publicTemplate;
    private Template privateTemplate;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setEmail("admin@ejemplo.com");
        adminUser.setRole(Role.ADMIN);

        creatorUser = new User();
        creatorUser.setId(2L);
        creatorUser.setEmail("creador@ejemplo.com");
        creatorUser.setRole(Role.CREADOR);

        standardUser = new User();
        standardUser.setId(3L);
        standardUser.setEmail("usuario@ejemplo.com");
        standardUser.setRole(Role.USUARIO);

        publicTemplate = new Template();
        publicTemplate.setId(101L);
        publicTemplate.setName("Plantilla Pública");
        publicTemplate.setContent("<html>{{nombre}}</html>");
        publicTemplate.setPublic(true);
        publicTemplate.setOwner(creatorUser);

        privateTemplate = new Template();
        privateTemplate.setId(102L);
        privateTemplate.setName("Plantilla Privada");
        privateTemplate.setContent("<html>{{titulo}}</html>");
        privateTemplate.setPublic(false);
        privateTemplate.setOwner(standardUser);
    }


    @Test
    void testSave_WhenUserIsUsuario_ShouldBePrivate() {
        Template newTemplate = new Template();
        newTemplate.setName("Plantilla de Usuario");
        newTemplate.setContent("<html>{{datos}}</html>");

        when(templateRepository.save(any(Template.class))).thenReturn(newTemplate);

        Template savedTemplate = templateService.save(newTemplate, standardUser);

        assertFalse(savedTemplate.isPublic());
        assertEquals(standardUser, savedTemplate.getOwner());
        assertNotNull(savedTemplate.getPlaceholders());
        verify(templateRepository, times(1)).save(any(Template.class));
    }

    @Test
    void testSave_WhenUserIsCreator_ShouldBePublic() {
        Template newTemplate = new Template();
        newTemplate.setName("Plantilla de Creador");
        newTemplate.setContent("<html>{{info}}</html>");

        when(templateRepository.save(any(Template.class))).thenReturn(newTemplate);

        Template savedTemplate = templateService.save(newTemplate, creatorUser);

        assertTrue(savedTemplate.isPublic());
        assertEquals(creatorUser, savedTemplate.getOwner());
        verify(templateRepository, times(1)).save(any(Template.class));
    }

    @Test
    void testSave_ExtractsPlaceholdersCorrectly() {
        Template newTemplate = new Template();
        newTemplate.setName("Plantilla con Múltiples Marcadores");
        newTemplate.setContent("<html>{{nombre}} {{email}} {{edad}}</html>");

        when(templateRepository.save(any(Template.class))).thenReturn(newTemplate);

        Template savedTemplate = templateService.save(newTemplate, standardUser);

        assertNotNull(savedTemplate.getPlaceholders());
        assertEquals(3, savedTemplate.getPlaceholders().size());
        assertTrue(savedTemplate.getPlaceholders().contains("nombre"));
        assertTrue(savedTemplate.getPlaceholders().contains("email"));
        assertTrue(savedTemplate.getPlaceholders().contains("edad"));
    }


    @Test
    void testFindAllByRole_AdminSeesAll() {
        List<Template> allTemplates = Arrays.asList(publicTemplate, privateTemplate);
        when(templateRepository.findAll()).thenReturn(allTemplates);

        List<Template> result = templateService.findAllByRole(adminUser);

        assertEquals(2, result.size());
        verify(templateRepository, times(1)).findAll();
    }

    @Test
    void testFindAllByRole_CreatorSeesOnlyPublic() {
        List<Template> publicTemplates = Collections.singletonList(publicTemplate);
        when(templateRepository.findByIsPublicTrue()).thenReturn(publicTemplates);

        List<Template> result = templateService.findAllByRole(creatorUser);

        assertEquals(1, result.size());
        assertTrue(result.get(0).isPublic());
        verify(templateRepository, times(1)).findByIsPublicTrue();
    }

    @Test
    void testFindAllByRole_UsuarioSeesPublicAndOwned() {
        List<Template> templates = Arrays.asList(publicTemplate, privateTemplate);
        when(templateRepository.findByIsPublicTrueOrOwner(standardUser)).thenReturn(templates);

        List<Template> result = templateService.findAllByRole(standardUser);

        assertEquals(2, result.size());
        verify(templateRepository, times(1)).findByIsPublicTrueOrOwner(standardUser);
    }


    @Test
    void testFindById_WhenUserIsOwner_ShouldReturnTemplate() throws AccessDeniedException {
        when(templateRepository.findById(102L)).thenReturn(Optional.of(privateTemplate));

        Template found = templateService.findById(102L, standardUser);

        assertNotNull(found);
        assertEquals(102L, found.getId());
        verify(templateRepository, times(1)).findById(102L);
    }

    @Test
    void testFindById_WhenTemplateIsPublic_ShouldReturnTemplate() throws AccessDeniedException {
        when(templateRepository.findById(101L)).thenReturn(Optional.of(publicTemplate));

        Template found = templateService.findById(101L, standardUser);

        assertNotNull(found);
        assertEquals(101L, found.getId());
    }

    @Test
    void testFindById_WhenAdminIsNotOwner_ShouldReturnTemplate() throws AccessDeniedException {
        when(templateRepository.findById(102L)).thenReturn(Optional.of(privateTemplate));

        Template found = templateService.findById(102L, adminUser);

        assertNotNull(found);
    }

    @Test
    void testFindById_WhenUserIsNotOwner_ShouldThrowAccessDenied() {
        when(templateRepository.findById(102L)).thenReturn(Optional.of(privateTemplate));

        assertThrows(AccessDeniedException.class, () -> {
            templateService.findById(102L, creatorUser);
        });
    }

    @Test
    void testFindById_WhenTemplateNotFound_ShouldThrowException() {
        when(templateRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            templateService.findById(999L, standardUser);
        });
    }

    // === Tests para update() ===

    @Test
    void testUpdate_WhenOwnerUpdatesOwnTemplate_ShouldSucceed() throws AccessDeniedException {
        Template updatedDetails = new Template();
        updatedDetails.setName("Nombre Actualizado");
        updatedDetails.setContent("<html>{{actualizado}}</html>");

        when(templateRepository.findById(102L)).thenReturn(Optional.of(privateTemplate));
        when(templateRepository.save(any(Template.class))).thenReturn(privateTemplate);

        Template result = templateService.update(102L, updatedDetails, standardUser);

        assertEquals("Nombre Actualizado", result.getName());
        assertEquals("<html>{{actualizado}}</html>", result.getContent());
        verify(templateRepository, times(1)).save(any(Template.class));
    }

    @Test
    void testUpdate_WhenAdminUpdatesAnyTemplate_ShouldSucceed() throws AccessDeniedException {
        Template updatedDetails = new Template();
        updatedDetails.setName("Actualizado por Admin");
        updatedDetails.setContent("<html>{{admin}}</html>");

        when(templateRepository.findById(102L)).thenReturn(Optional.of(privateTemplate));
        when(templateRepository.save(any(Template.class))).thenReturn(privateTemplate);

        Template result = templateService.update(102L, updatedDetails, adminUser);

        assertNotNull(result);
        verify(templateRepository, times(1)).save(any(Template.class));
    }

    @Test
    void testUpdate_WhenUsuarioUpdatesOthersTemplate_ShouldThrowAccessDenied() {
        Template updatedDetails = new Template();
        updatedDetails.setName("Actualización No Autorizada");

        when(templateRepository.findById(101L)).thenReturn(Optional.of(publicTemplate));

        assertThrows(AccessDeniedException.class, () -> {
            templateService.update(101L, updatedDetails, standardUser);
        });
    }

    @Test
    void testUpdate_WhenCreadorUpdatesOthersTemplate_ShouldThrowAccessDenied() {
        Template updatedDetails = new Template();
        updatedDetails.setName("Actualización No Autorizada de Creador");

        when(templateRepository.findById(102L)).thenReturn(Optional.of(privateTemplate));

        assertThrows(AccessDeniedException.class, () -> {
            templateService.update(102L, updatedDetails, creatorUser);
        });
    }

    // === Tests para delete() ===

    @Test
    void testDelete_WhenUserIsOwnerOfPrivate_ShouldDelete() throws AccessDeniedException {
        when(templateRepository.findById(102L)).thenReturn(Optional.of(privateTemplate));

        templateService.delete(102L, standardUser);

        verify(templateRepository, times(1)).delete(privateTemplate);
    }

    @Test
    void testDelete_WhenUsuarioTriesPublic_ShouldThrowAccessDenied() {
        when(templateRepository.findById(101L)).thenReturn(Optional.of(publicTemplate));

        assertThrows(AccessDeniedException.class, () -> {
            templateService.delete(101L, standardUser);
        });
    }

    @Test
    void testDelete_WhenCreadorTriesPublic_ShouldThrowAccessDenied() {
        when(templateRepository.findById(101L)).thenReturn(Optional.of(publicTemplate));

        assertThrows(AccessDeniedException.class, () -> {
            templateService.delete(101L, creatorUser);
        });
    }

    @Test
    void testDelete_WhenAdminTriesPublic_ShouldDelete() throws AccessDeniedException {
        when(templateRepository.findById(101L)).thenReturn(Optional.of(publicTemplate));

        templateService.delete(101L, adminUser);

        verify(templateRepository, times(1)).delete(publicTemplate);
    }

    @Test
    void testDelete_WhenAdminTriesPrivate_ShouldDelete() throws AccessDeniedException {
        when(templateRepository.findById(102L)).thenReturn(Optional.of(privateTemplate));

        templateService.delete(102L, adminUser);

        verify(templateRepository, times(1)).delete(privateTemplate);
    }

    @Test
    void testDelete_WhenTemplateNotFound_ShouldThrowException() {
        when(templateRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            templateService.delete(999L, standardUser);
        });
    }

    // === Tests para validateTemplatePlaceholders() ===

    @Test
    void testValidatePlaceholders_WhenAllPresent_ShouldNotThrow() {
        Template template = new Template();
        template.setPlaceholders(Arrays.asList("nombre", "email"));

        Map<String, Object> data = new HashMap<>();
        data.put("nombre", "Juan");
        data.put("email", "juan@ejemplo.com");

        assertDoesNotThrow(() -> {
            templateService.validateTemplatePlaceholders(template, data);
        });
    }

    @Test
    void testValidatePlaceholders_WhenMissing_ShouldThrowException() {
        Template template = new Template();
        template.setPlaceholders(Arrays.asList("nombre", "email", "telefono"));

        Map<String, Object> data = new HashMap<>();
        data.put("nombre", "Juan");
        data.put("email", "juan@ejemplo.com");

        assertThrows(IllegalArgumentException.class, () -> {
            templateService.validateTemplatePlaceholders(template, data);
        });
    }
}
