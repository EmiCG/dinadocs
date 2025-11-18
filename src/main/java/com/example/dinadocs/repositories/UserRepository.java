package com.example.dinadocs.repositories;

import com.example.dinadocs.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Solo con escribir esta línea, Spring aprende a hacer:
    // SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);

    // NOTA: El método .save() ya viene incluido "gratis"
    // al extender de JpaRepository, no se necesita escribirlo.
}