package com.trmo.floracare;

import com.trmo.floracare.services.impl.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        String secret = "mySuperLongSecretKeyThatIs512BitsLongExactly12345678901234567890";
        jwtService = new JwtService(secret);
        Field expirationField = JwtService.class.getDeclaredField("jwtExpirationMs");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, 3600000L);
    }

    @Test
    @DisplayName("Должен успешно создать jwt токен")
    void testCreateToken() {
        // given
        String userId = "test-user-id";

        // when
        String token = jwtService.createToken(userId);

        // then
        assertNotNull(token);
    }

    @Test
    @DisplayName("Должен успешно успешно проверить валидный jwt токен")
    void testValidateToken_ValidToken() {
        // given
        String userId = "test-user-id";
        String token = jwtService.createToken(userId);

        // when
        boolean isValid = jwtService.validateToken(token);

        // then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Должен успешно проверить невалидный jwt токен")
    void testValidateToken_InvalidToken() {
        // given
        String invalidToken = "invalid.token.value";

        // when
        boolean isValid = jwtService.validateToken(invalidToken);

        // then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Должен успешно извлечь user_id из jwt токена")
    void testGetUserIdFromToken() {
        // given
        String userId = "test-user-id";
        String token = jwtService.createToken(userId);

        // when
        String extractedUserId = jwtService.getUserIdFromToken(token);

        // then
        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("Должен определить истекший токен как невалидный")
    void testExpiredToken() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        // given
        Field expirationField = JwtService.class.getDeclaredField("jwtExpirationMs");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, 1000L);
        String userId = "test-user-id";
        String token = jwtService.createToken(userId);

        Thread.sleep(2000);

        // when
        boolean isValid = jwtService.validateToken(token);

        // then
        assertFalse(isValid);
    }
}
