package com.elearning.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTest {

    @Test
    @DisplayName("Verify BCrypt hash generation and positive verification")
    void testHashAndVerify() {
        String password = "secretPassword123";
        String hash = PasswordUtil.hash(password);

        assertNotNull(hash);
        assertNotEquals(password, hash);
        assertTrue(hash.startsWith("$2a$"));
        assertTrue(PasswordUtil.verify(password, hash));
        assertFalse(PasswordUtil.verify("wrongPassword", hash));
    }

    @Test
    @DisplayName("Verify legacy plaintext password compatibility")
    void testLegacyPlaintextFallback() {
        String plaintext = "admin";
        assertTrue(PasswordUtil.verify(plaintext, plaintext));
        assertFalse(PasswordUtil.verify("wrong", plaintext));
    }
}
