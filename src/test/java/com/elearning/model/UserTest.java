package com.elearning.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("getFullName() returns first + last separated by a space")
    void testFullName() {
        User u = new User(1, Role.STUDENT, "Jane", "Doe", "jane@test.com", "jdoe");
        assertEquals("Jane Doe", u.getFullName());
    }

    @Test
    @DisplayName("getFullName() returns only first name when last name is blank")
    void testFullNameBlankLast() {
        User u = new User(1, Role.TEACHER, "Alice", "", "alice@test.com", "alice");
        assertEquals("Alice", u.getFullName());
    }

    @Test
    @DisplayName("getFullName() returns only first name when last name is null")
    void testFullNameNullLast() {
        User u = new User(1, Role.ADMIN, "Bob", null, "bob@test.com", "bob");
        assertEquals("Bob", u.getFullName());
    }

    @Test
    @DisplayName("Role field round-trips correctly")
    void testRoleAssignment() {
        User u = new User();
        u.setRole(Role.TEACHER);
        assertEquals(Role.TEACHER, u.getRole());
        assertNotEquals(Role.STUDENT, u.getRole());
    }
}
