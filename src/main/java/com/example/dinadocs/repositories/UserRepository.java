package com.example.dinadocs.repositories;

import com.example.dinadocs.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio de acceso a datos para la entidad User.
 * Provee métodos para la autenticación y gestión de usuarios.
 *
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su correo electrónico.
     * Utilizado para el proceso de login.
     *
     * @param email el correo electrónico del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmail(String email);
}