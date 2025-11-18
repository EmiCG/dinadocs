package com.example.dinadocs.repositories;

import com.example.dinadocs.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio (Capa de Acceso a Datos) para la entidad User.
 * Provee métodos CRUD y consultas personalizadas definidas en la especificación.
 *
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Método Custom: Busca un usuario por su email.
     *
     */
    Optional<User> findByEmail(String email);

}
