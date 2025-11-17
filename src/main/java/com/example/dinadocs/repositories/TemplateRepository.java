package com.example.dinadocs.repositories;

import com.example.dinadocs.models.Template;
import com.example.dinadocs.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio (Capa de Acceso a Datos) para la entidad Template.
 * Provee métodos CRUD y consultas personalizadas definidas en la especificación.
 *
 */
@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    /**
     * Método Custom: Busca una plantilla por su nombre.
     *
     */
    Optional<Template> findByName(String name);

    /**
     * Método Custom: Lista las plantillas públicas O las que pertenecen al usuario.
     * Esta es la consulta clave para la lógica de Nivel 2.
     *
     */
    List<Template> findByIsPublicTrueOrOwner(User user);
}