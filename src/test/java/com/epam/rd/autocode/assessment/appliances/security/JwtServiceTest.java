package com.epam.rd.autocode.assessment.appliances.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    private static final String TEST_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L); // 1 day

        userDetails = new User("test@test.com", "password", Collections.emptyList());
    }

    @Test
    @DisplayName("Should generate token and extract correct username")
    void generateToken_ShouldCreateValidToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(userDetails.getUsername(), extractedUsername);
    }

    @Test
    @DisplayName("isTokenValid() should return true for correct user and valid token")
    void isTokenValid_ShouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("isTokenValid() should return false for different user")
    void isTokenValid_ShouldReturnFalseForDifferentUser() {
        String token = jwtService.generateToken(userDetails);
        UserDetails wrongUser = new User("wrong@test.com", "password", Collections.emptyList());

        boolean isValid = jwtService.isTokenValid(token, wrongUser);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should throw SignatureException on invalid token signature")
    void extractUsername_ShouldThrowExceptionOnTemperedToken() {
        String token = jwtService.generateToken(userDetails);
        String temperedToken = token + "bad_signature";

        assertThrows(SignatureException.class, () -> jwtService.extractUsername(temperedToken));
    }

    @Test
    @DisplayName("Should throw ExpiredJwtException when token is expired")
    void isTokenValid_ShouldThrowExceptionOnExpiredToken() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L); // Токен вже протермінований
        String token = jwtService.generateToken(userDetails);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, userDetails));
    }

}