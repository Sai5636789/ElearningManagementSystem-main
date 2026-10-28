package com.elearning.config;

import com.elearning.model.Role;
import com.elearning.model.UserSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionContextTest {

    @AfterEach
    void tearDown() {
        SessionContext.clear();
    }

    @Test
    @DisplayName("Verify user login sets session state and clear logs out")
    void testSessionLifecycle() {
        assertFalse(SessionContext.isLoggedIn());
        assertEquals(0, SessionContext.getCurrentUserId());
        assertNull(SessionContext.getCurrentRole());

        UserSession user = new UserSession(42, "johndoe", Role.STUDENT, "John", "Doe", "john@example.com");
        SessionContext.setCurrentUser(user);

        assertTrue(SessionContext.isLoggedIn());
        assertEquals(42, SessionContext.getCurrentUserId());
        assertEquals(Role.STUDENT, SessionContext.getCurrentRole());
        assertEquals("John Doe", SessionContext.getCurrentUser().getFullName());

        SessionContext.clear();
        assertFalse(SessionContext.isLoggedIn());
        assertNull(SessionContext.getCurrentUser());
    }
}
