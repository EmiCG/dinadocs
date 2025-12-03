package com.example.dinadocs.controllers;

import com.example.dinadocs.models.Role;
import com.example.dinadocs.models.User;
import com.example.dinadocs.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterSuccess() {
        User user = new User();
        user.setEmail("prueba@ejemplo.com");
        user.setPassword("contrasena");

        when(authService.register(user)).thenReturn(user);

        String response = authController.register(user);

        assertEquals("Usuario registrado con éxito", response);
        verify(authService, times(1)).register(user);
    }

    @Test
    void testAuthenticateUserSuccess() {
        Map<String, String> request = new HashMap<>();
        request.put("email", "prueba@ejemplo.com");
        request.put("password", "contrasena");

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("token", "tokenPrueba123");
        expectedResponse.put("role", Role.USUARIO);

        when(authService.login("prueba@ejemplo.com", "contrasena")).thenReturn(expectedResponse);

        Map<String, Object> response = authController.authenticateUser(request);

        assertNotNull(response);
        assertEquals("tokenPrueba123", response.get("token"));
        assertEquals(Role.USUARIO, response.get("role"));
        verify(authService, times(1)).login("prueba@ejemplo.com", "contrasena");
    }

    @Test
    void testAuthenticateUserFailure() {
        Map<String, String> request = new HashMap<>();
        request.put("email", "prueba@ejemplo.com");
        request.put("password", "contrasenaIncorrecta");

        when(authService.login("prueba@ejemplo.com", "contrasenaIncorrecta"))
                .thenThrow(new RuntimeException("Contraseña incorrecta"));

        assertThrows(RuntimeException.class, () -> {
            authController.authenticateUser(request);
        });

        verify(authService, times(1)).login("prueba@ejemplo.com", "contrasenaIncorrecta");
    }

    @Test
    void testLogoutSuccess() {
        String token = "Bearer tokenPrueba";

        when(authService.isTokenValid("tokenPrueba")).thenReturn(true);
        when(authService.logout("tokenPrueba")).thenReturn("Sesión cerrada correctamente");

        ResponseEntity<String> response = authController.logout(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Sesión cerrada correctamente", response.getBody());
        verify(authService, times(1)).logout("tokenPrueba");
    }

    @Test
    void testLogoutInvalidToken() {
        String token = "Bearer tokenInvalido";

        when(authService.isTokenValid("tokenInvalido")).thenReturn(false);

        ResponseEntity<String> response = authController.logout(token);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Token inválido o ya cerrado", response.getBody());
        verify(authService, times(0)).logout(anyString());
    }
}
