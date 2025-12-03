package com.example.dinadocs.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
    }

    @Test
    void testGenerateToken() {
        String username = "prueba@ejemplo.com";

        String token = jwtUtils.generateToken(username);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testValidateTokenAndGetUsername() {
        String username = "prueba@ejemplo.com";
        String token = jwtUtils.generateToken(username);

        String extractedUsername = jwtUtils.validateTokenAndGetUsername(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateInvalidToken() {
        String invalidToken = "tokenInvalido123";

        String result = jwtUtils.validateTokenAndGetUsername(invalidToken);

        assertNull(result);
    }

    @Test
    void testValidateExpiredToken() {
        String token = jwtUtils.generateToken("prueba@ejemplo.com");
        
        assertNotNull(token);
    }

    @Test
    void testGenerateTokenForDifferentUsers() {
        String user1 = "usuario1@ejemplo.com";
        String user2 = "usuario2@ejemplo.com";

        String token1 = jwtUtils.generateToken(user1);
        String token2 = jwtUtils.generateToken(user2);

        assertNotEquals(token1, token2);
        assertEquals(user1, jwtUtils.validateTokenAndGetUsername(token1));
        assertEquals(user2, jwtUtils.validateTokenAndGetUsername(token2));
    }
}
