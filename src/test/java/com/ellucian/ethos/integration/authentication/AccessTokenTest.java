package com.ellucian.ethos.integration.authentication;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the API Token classes for basic functionality and for code coverage purposes.
 * @author adelimon
 * @since 0.0.1
 */
class AccessTokenTest {

    private LocalDateTime testStartTime = LocalDateTime.now().plusMinutes(90);

    @Test
    void tokenValidation() {
        AccessToken token = new AccessToken("jwt_string", LocalDateTime.now().plusMinutes(1));
        assertEquals(token.getToken(), "jwt_string");
        assertTrue(token.isValid());
    }

    @Test
    void tokenValidationNull() {
        AccessToken token = new AccessToken(null, LocalDateTime.now().plusMinutes(1));
        assertFalse(token.isValid());
    }

    @Test
    void tokenValidationExpired() {
        AccessToken token = new AccessToken("jwt_string", LocalDateTime.now().minusMinutes(1));
        assertFalse(token.isValid());
    }

    @Test
    void getAuthHeader() {
        AccessToken token = new AccessToken("jwt_string", LocalDateTime.now().plusMinutes(1));
        Map<String, String> headers = token.getAuthHeader();
        assertEquals(headers.size(), 1);
        assertTrue(headers.containsKey("Authorization"));
        assertEquals(headers.get("Authorization"), "Bearer jwt_string");
    }
}