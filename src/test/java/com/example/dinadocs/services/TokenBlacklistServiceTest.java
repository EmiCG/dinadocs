package com.example.dinadocs.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenBlacklistServiceTest {

    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService();
    }

    @Test
    void testInvalidateToken() {
        String token = "tokenPrueba123";

        tokenBlacklistService.invalidateToken(token);

        assertTrue(tokenBlacklistService.isTokenInvalidated(token));
    }

    @Test
    void testIsTokenInvalidatedFalse() {
        String token = "tokenValido";

        assertFalse(tokenBlacklistService.isTokenInvalidated(token));
    }

    @Test
    void testIsTokenInvalidatedTrue() {
        String token = "tokenInvalido";
        tokenBlacklistService.invalidateToken(token);

        assertTrue(tokenBlacklistService.isTokenInvalidated(token));
    }

    @Test
    void testMultipleTokensInvalidation() {
        String token1 = "token1";
        String token2 = "token2";
        String token3 = "token3";

        tokenBlacklistService.invalidateToken(token1);
        tokenBlacklistService.invalidateToken(token2);

        assertTrue(tokenBlacklistService.isTokenInvalidated(token1));
        assertTrue(tokenBlacklistService.isTokenInvalidated(token2));
        assertFalse(tokenBlacklistService.isTokenInvalidated(token3));
    }
}
