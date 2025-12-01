package com.example.dinadocs.services;

import com.example.dinadocs.models.Template;
import com.example.dinadocs.models.User;
import com.example.dinadocs.models.Role;
import com.example.dinadocs.repositories.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.nio.file.AccessDeniedException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servicio (Lógica de Negocio) para gestionar las Plantillas.
 * Implementa toda la autorización de Nivel 2 (reglas de roles y propiedad).
 *
 */
@Service
public class TemplateService {

    @Autowired
    private TemplateRepository templateRepository;

    /**
     * Guarda una plantilla, aplicando lógica de roles.
     * Lógica Nivel 2: Asigna 'owner' y 'isPublic' basado en el rol del usuario.
     *
     */
    public Template save(Template template, User authUser) {
        if (authUser.getRole().equals(Role.CREADOR)) {
            template.setPublic(true); // Todas las plantillas creadas por un CREADOR son públicas
        } else {
            template.setPublic(false);
        }
        template.setOwner(authUser);

        List<String> placeholders = extractPlaceholders(template.getContent());
        template.setPlaceholders(placeholders);
        return templateRepository.save(template);
    }

    /**
     * Lista las plantillas según el rol del usuario.
     *
     */
    public List<Template> findAllByRole(User authUser) {
        // Validacion TEMPORAL para probar sin usuario
        if (authUser == null) {
            return templateRepository.findByIsPublicTrue();
        }
        if (authUser.getRole() == Role.ADMIN) {
            return templateRepository.findAll();
        }
        if (authUser.getRole() == Role.CREADOR) {
            return templateRepository.findByIsPublicTrue();
        }
        return templateRepository.findByIsPublicTrueOrOwner(authUser);
    }

    /**
     * Busca una plantilla por ID, verificando permisos de acceso (lectura).
     *
     */
    public Template findById(Long id, User authUser) throws AccessDeniedException {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plantilla no encontrada con id: " + id));

        boolean isOwner = Objects.equals(template.getOwner().getId(), authUser.getId());
        boolean isAdmin = authUser.getRole() == Role.ADMIN;

        if (template.isPublic() || isOwner || isAdmin) {
            return template;
        }

        throw new AccessDeniedException("No tiene permiso para ver esta plantilla.");
    }

    /**
     * Actualiza una plantilla, verificando permisos de (escritura).
     * Lógica Nivel 2: Solo el dueño o un ADMIN pueden modificar.
     *
     */
    public Template update(Long id, Template templateDetails, User authUser) throws AccessDeniedException {
        Template templateToUpdate = findById(id, authUser);

        if (templateToUpdate.getOwner() == null) {
            throw new RuntimeException("La plantilla no tiene un propietario asignado.");
        }

        boolean isOwner = Objects.equals(templateToUpdate.getOwner().getId(), authUser.getId());
        boolean isAdmin = authUser.getRole() == Role.ADMIN;
        boolean isCreator = authUser.getRole() == Role.CREADOR;

        if (authUser.getRole() == Role.USUARIO && !isOwner) {
            throw new AccessDeniedException("Un USUARIO no puede modificar plantillas ajenas.");
        }

        if (isCreator && !isOwner && !isAdmin) {
            throw new AccessDeniedException("Un CREADOR solo puede modificar sus propias plantillas.");
        }

        templateToUpdate.setName(templateDetails.getName());
        templateToUpdate.setContent(templateDetails.getContent());

        return templateRepository.save(templateToUpdate);
    }

    /**
     * Elimina una plantilla, verificando permisos de (borrado).
     */
    public void delete(Long id, User authUser) throws AccessDeniedException {
        System.out.println("Intentando eliminar plantilla con ID: " + id);
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> {
                    System.out.println("Plantilla no encontrada con ID: " + id);
                    return new RuntimeException("Plantilla no encontrada");
                });

        System.out.println("Plantilla encontrada: " + template.getName() + ", Pública: " + template.isPublic());
        System.out.println("Rol del usuario autenticado: " + authUser.getRole());

        // Verificar permisos para eliminar
        if (template.isPublic()) {
            if (authUser.getRole() != Role.ADMIN) {
                System.out.println("Acceso denegado: Solo un administrador puede eliminar una plantilla pública");
                throw new AccessDeniedException("Solo un administrador puede eliminar una plantilla pública");
            }
        } else {
            if (!template.getOwner().equals(authUser) && !authUser.getRole().equals(Role.ADMIN)) {
                System.out.println("Acceso denegado: Solo el propietario o un administrador pueden eliminar esta plantilla privada");
                throw new AccessDeniedException("Solo el propietario o un administrador pueden eliminar esta plantilla privada");
            }
        }

        System.out.println("Permisos verificados. Eliminando plantilla...");
        templateRepository.delete(template);
        System.out.println("Plantilla eliminada exitosamente.");
    }

    /**
     * Método privado que usa Regex para encontrar todos los {{placeholders}}.
     */
    private List<String> extractPlaceholders(String htmlContent) {
        List<String> matches = new ArrayList<>();

        if (htmlContent == null || htmlContent.isEmpty()) {
            return matches;
        }

        Pattern pattern = Pattern.compile("\\{\\{([^\\}]+)\\}\\}");
        Matcher matcher = pattern.matcher(htmlContent);

        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        return matches;
    }

    /**
     * Valida que los datos proporcionados coincidan con los placeholders requeridos por la plantilla.
     *
     * @param template La plantilla que contiene los placeholders requeridos.
     * @param data Los datos proporcionados por el usuario.
     * @throws IllegalArgumentException Si falta algún placeholder requerido.
     */
    public void validateTemplatePlaceholders(Template template, Map<String, Object> data) {
        for (String placeholder : template.getPlaceholders()) {
            if (!data.containsKey(placeholder)) {
                throw new IllegalArgumentException("Falta el dato para el marcador de posición: " + placeholder);
            }
        }
    }
}