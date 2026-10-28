package com.elearning.config;

import com.elearning.model.Role;
import com.elearning.model.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SessionContext {

    private static final Logger log = LoggerFactory.getLogger(SessionContext.class);
    private static volatile UserSession currentSession;

    private SessionContext() {
    }

    public static void setCurrentUser(UserSession session) {
        currentSession = session;
        if (session != null) {
            log.info("User logged in: id={}, username={}, role={}", session.getId(), session.getUsername(), session.getRole());
        }
    }

    public static UserSession getCurrentUser() {
        return currentSession;
    }

    public static int getCurrentUserId() {
        return currentSession != null ? currentSession.getId() : 0;
    }

    public static Role getCurrentRole() {
        return currentSession != null ? currentSession.getRole() : null;
    }

    public static boolean isLoggedIn() {
        return currentSession != null;
    }

    public static void clear() {
        if (currentSession != null) {
            log.info("User logged out: id={}, username={}", currentSession.getId(), currentSession.getUsername());
        }
        currentSession = null;
    }
}
