package com.example.dinadocs.services;

import com.example.dinadocs.models.Template;
import com.example.dinadocs.models.User;
import com.example.dinadocs.models.Role;
import com.example.dinadocs.repositories.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.nio.file.AccessDeniedException;

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
        if (authUser.getRole() == Role.CREADOR || authUser.getRole() == Role.ADMIN) {
            template.setPublic(true);
            template.setOwner(authUser);
        } else {
            template.setPublic(false);
            template.setOwner(authUser);
        }
        return templateRepository.save(template);
    }

    /**
     * Lista las plantillas según el rol del usuario.
     * Lógica Nivel 2: ADMIN ve todo, el resto ve públicas + las suyas.
     *
     */
    public List<Template> findAllByRole(User authUser) {
        if (authUser.getRole() == Role.ADMIN) {
            return templateRepository.findAll();
        }
        return templateRepository.findByIsPublicTrueOrOwner(authUser);
    }

    /**
     * Busca una plantilla por ID, verificando permisos de acceso (lectura).
     * Lógica Nivel 2: Debe ser pública, O ser el dueño, O ser ADMIN.
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
     * Lógica Nivel 2:
     * 1. Solo ADMIN puede borrar plantillas públicas.
     * 2. Solo el DUEÑO o un ADMIN pueden borrar plantillas privadas.
     * 3. CREADOR no puede borrar (a menos que sea dueño de una privada).
     */
    public void delete(Long id, User authUser) throws AccessDeniedException {
        Template templateToDelete = findById(id, authUser);

        if (templateToDelete.isPublic() && authUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Solo un ADMIN puede borrar plantillas públicas.");
        }

        if (!templateToDelete.isPublic()) {
            boolean isOwner = Objects.equals(templateToDelete.getOwner().getId(), authUser.getId());
            boolean isAdmin = authUser.getRole() == Role.ADMIN;

            if (!isOwner && !isAdmin) {
                throw new AccessDeniedException("No tiene permiso para borrar esta plantilla privada.");
            }
        }

        templateRepository.delete(templateToDelete);
    }
}