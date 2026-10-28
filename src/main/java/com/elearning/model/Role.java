package com.elearning.model;

public enum Role {
    ADMIN("Admin"),
    TEACHER("Teacher"),
    STUDENT("Student");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Role fromString(String text) {
        if (text == null) {
            return null;
        }
        for (Role role : values()) {
            if (role.name().equalsIgnoreCase(text) || role.displayName.equalsIgnoreCase(text)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + text);
    }
}
