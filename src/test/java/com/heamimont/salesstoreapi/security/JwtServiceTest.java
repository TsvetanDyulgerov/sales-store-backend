package com.heamimont.salesstoreapi.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;
    private String token;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        // Set a 32-byte secret (required for HS256)
        String secret = "12345678901234567890123456789012";
        ReflectionTestUtils.setField(jwtService, "secret", secret);
        jwtService.init();

        // Mock UserDetails
        userDetails = Mockito.mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        // Generate a valid token
        token = jwtService.generateToken(userDetails);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void extractClaim_ShouldReturnCustomClaim() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "ADMIN");

        String tokenWithClaims = jwtService.generateToken(extraClaims, userDetails);

        String role = jwtService.extractClaim(tokenWithClaims, claims -> claims.get("role", String.class));
        assertEquals("ADMIN", role);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_ForValidToken() {
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForInvalidUsername() {
        UserDetails otherUser = Mockito.mock(UserDetails.class);
        when(otherUser.getUsername()).thenReturn("wronguser");

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForExpiredToken() {
        // Create an expired token
        Map<String, Object> claims = new HashMap<>();
        String expiredToken = io.jsonwebtoken.Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24)) // issued yesterday
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // expired 1 sec ago
                .signWith((Key) ReflectionTestUtils.getField(jwtService, "signingKey"))
                .compact();

        assertFalse(jwtService.isTokenValid(expiredToken, userDetails));
    }
}
