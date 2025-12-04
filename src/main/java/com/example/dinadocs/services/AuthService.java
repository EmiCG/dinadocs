package com.example.dinadocs.services;

import com.example.dinadocs.models.Role;
import com.example.dinadocs.models.User;
import com.example.dinadocs.repositories.UserRepository;
import com.example.dinadocs.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de autenticación y autorización de usuarios.
 * Gestiona el registro, login, logout y validación de tokens.
 * 
 * @author DynaDocs Team
 * @version 1.0
 * @since 2025-12-03
 * @see User
 * @see JwtUtils
 * @see TokenBlacklistService
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    /**
     * Registra un nuevo usuario en el sistema.
     * Verifica que el email no exista, encripta la contraseña
     * y asigna el rol USUARIO por defecto si no se especifica.
     * 
     * @param user objeto User con los datos del nuevo usuario
     * @return el usuario registrado y guardado en la base de datos
     * @throws RuntimeException si el email ya está registrado
     */
    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole(Role.USUARIO);
        return userRepository.save(user);
    }

    /**
     * Autentica a un usuario y genera un token JWT.
     * Valida las credenciales (email y contraseña) y retorna
     * un mapa con el token y el rol del usuario.
     * 
     * @param email el correo electrónico del usuario
     * @param password la contraseña en texto plano
     * @return Map con "token" (String) y "role" (Role)
     * @throws RuntimeException si el usuario no existe o la contraseña es incorrecta
     */
    public Map<String, Object> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtils.generateToken(email);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("role", user.getRole());
            return response;
        } else {
            throw new RuntimeException("Contraseña incorrecta");
        }
    }

    /**
     * Cierra la sesión del usuario invalidando su token JWT.
     * Agrega el token a la blacklist para prevenir su reutilización.
     * 
     * @param token el token JWT con prefijo "Bearer " o sin él
     * @return mensaje de confirmación de cierre de sesión
     */
    public String logout(String token) {
        String jwt = token.replace("Bearer ", "");
        tokenBlacklistService.invalidateToken(jwt);
        return "Sesión cerrada correctamente";
    }

    /**
     * Verifica si un token JWT sigue siendo válido (no está en blacklist).
     * 
     * @param token el token JWT a validar
     * @return true si el token es válido, false si fue invalidado (logout)
     */
    public boolean isTokenValid(String token) {
        String jwt = token.replace("Bearer ", "");
        return !tokenBlacklistService.isTokenInvalidated(jwt);
    }

}