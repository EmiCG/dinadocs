package com.example.dinadocs.repositories;

import com.example.dinadocs.models.Template;
import com.example.dinadocs.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Template.
 * Provee métodos CRUD y consultas personalizadas definidas en la especificación.
 *
 */
@Repository
public interface TemplateRepository extends JpaRepository<Template, Long> {

    /**
     * Método Custom: Busca una plantilla por su nombre.
     *
     * @param name el nombre de la plantilla
     * @return Optional con la plantilla si existe
     */
    Optional<Template> findByName(String name);

    /**
     * Método Custom: Lista las plantillas públicas O las que pertenecen al usuario.
     * Esta es la consulta clave para la lógica de Nivel 2.
     *
     * @param user el usuario propietario
     * @return lista de plantillas accesibles
     */
    List<Template> findByIsPublicTrueOrOwner(User user);

    /**
     * Busca solo las plantillas que son públicas.
     * (metodo TEMPORAL para probar plantillas sin iniciar sesion con un usuario valido)
     * 
     * @return lista de plantillas públicas
     */
    List<Template> findByIsPublicTrue();
}