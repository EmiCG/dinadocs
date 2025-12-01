package com.example.dinadocs.services;

import com.example.dinadocs.models.Role;
import com.example.dinadocs.models.User;
import com.example.dinadocs.repositories.UserRepository;
import com.example.dinadocs.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenBlacklistService tokenBlacklistService; // Agregado

    // Lógica de Registro
    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole(Role.USUARIO);
        return userRepository.save(user);
    }

    // Lógica de Login
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtils.generateToken(email);
        } else {
            throw new RuntimeException("Contraseña incorrecta");
        }
    }

    // Lógica de Logout
    public String logout(String token) {
        String jwt = token.replace("Bearer ", "");
        tokenBlacklistService.invalidateToken(jwt);
        return "Sesión cerrada correctamente";
    }

    // Verificación de Token
    public boolean isTokenValid(String token) {
        String jwt = token.replace("Bearer ", "");
        return !tokenBlacklistService.isTokenInvalidated(jwt);
    }

}