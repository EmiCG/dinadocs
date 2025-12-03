package com.example.dinadocs.services;

import com.example.dinadocs.models.Role;
import com.example.dinadocs.models.User;
import com.example.dinadocs.repositories.UserRepository;
import com.example.dinadocs.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("prueba@ejemplo.com");
        testUser.setPassword("contrasenaEncriptada");
        testUser.setRole(Role.USUARIO);
    }

    @Test
    void testRegisterSuccess() {
        User newUser = new User();
        newUser.setEmail("nuevousuario@ejemplo.com");
        newUser.setPassword("contrasena123");

        when(userRepository.findByEmail("nuevousuario@ejemplo.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("contrasena123")).thenReturn("contrasenaEncriptada");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User result = authService.register(newUser);

        assertNotNull(result);
        assertEquals(Role.USUARIO, result.getRole());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("contrasena123");
    }

    @Test
    void testRegisterUserAlreadyExists() {
        User existingUser = new User();
        existingUser.setEmail("prueba@ejemplo.com");
        existingUser.setPassword("contrasena123");

        when(userRepository.findByEmail("prueba@ejemplo.com")).thenReturn(Optional.of(testUser));

        assertThrows(RuntimeException.class, () -> {
            authService.register(existingUser);
        });

        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void testLoginSuccess() {
        when(userRepository.findByEmail("prueba@ejemplo.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("contrasena123", "contrasenaEncriptada")).thenReturn(true);
        when(jwtUtils.generateToken("prueba@ejemplo.com")).thenReturn("tokenPrueba");

        Map<String, Object> result = authService.login("prueba@ejemplo.com", "contrasena123");

        assertNotNull(result);
        assertEquals("tokenPrueba", result.get("token"));
        assertEquals(Role.USUARIO, result.get("role"));
        verify(jwtUtils, times(1)).generateToken("prueba@ejemplo.com");
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByEmail("noencontrado@ejemplo.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            authService.login("noencontrado@ejemplo.com", "contrasena123");
        });

        verify(jwtUtils, times(0)).generateToken(anyString());
    }

    @Test
    void testLoginIncorrectPassword() {
        when(userRepository.findByEmail("prueba@ejemplo.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("contrasenaIncorrecta", "contrasenaEncriptada")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            authService.login("prueba@ejemplo.com", "contrasenaIncorrecta");
        });

        verify(jwtUtils, times(0)).generateToken(anyString());
    }

    @Test
    void testLogout() {
        doNothing().when(tokenBlacklistService).invalidateToken("tokenPrueba");

        String result = authService.logout("Bearer tokenPrueba");

        assertEquals("Sesión cerrada correctamente", result);
        verify(tokenBlacklistService, times(1)).invalidateToken("tokenPrueba");
    }

    @Test
    void testIsTokenValidTrue() {
        when(tokenBlacklistService.isTokenInvalidated("tokenValido")).thenReturn(false);

        boolean result = authService.isTokenValid("Bearer tokenValido");

        assertTrue(result);
        verify(tokenBlacklistService, times(1)).isTokenInvalidated("tokenValido");
    }

    @Test
    void testIsTokenValidFalse() {
        when(tokenBlacklistService.isTokenInvalidated("tokenInvalido")).thenReturn(true);

        boolean result = authService.isTokenValid("Bearer tokenInvalido");

        assertFalse(result);
        verify(tokenBlacklistService, times(1)).isTokenInvalidated("tokenInvalido");
    }
}
