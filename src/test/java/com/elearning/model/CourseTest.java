package com.elearning.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CourseTest {

    @Test
    @DisplayName("toString() returns the course name, not a class reference")
    void testToString() {
        Course c = new Course(1, "Intro to Java", "Basics", "Lesson content", 10, 2);
        assertEquals("Intro to Java", c.toString());
    }

    @Test
    @DisplayName("Getters reflect values set through the all-args constructor")
    void testConstructorAndGetters() {
        Course c = new Course(5, "Data Structures", "DS desc", "DS content", 3, 7);
        assertEquals(5, c.getId());
        assertEquals("Data Structures", c.getName());
        assertEquals("DS desc", c.getDescription());
        assertEquals("DS content", c.getContent());
        assertEquals(3, c.getTeacherId());
        assertEquals(7, c.getSubjectId());
    }

    @Test
    @DisplayName("setSubjectName and setTeacherName are independent of the constructor")
    void testJoinedFields() {
        Course c = new Course(1, "Algebra", "Math", "Content", 2, 3);
        c.setSubjectName("Mathematics");
        c.setTeacherName("Alice Smith");
        c.setTeacherEmail("alice@example.com");

        assertEquals("Mathematics", c.getSubjectName());
        assertEquals("Alice Smith", c.getTeacherName());
        assertEquals("alice@example.com", c.getTeacherEmail());
    }

    @Test
    @DisplayName("Setters overwrite constructor values")
    void testSetters() {
        Course c = new Course(1, "Old Name", "Old Desc", "Old Content", 1, 1);
        c.setName("New Name");
        c.setDescription("New Desc");
        c.setContent("New Content");

        assertEquals("New Name", c.getName());
        assertEquals("New Desc", c.getDescription());
        assertEquals("New Content", c.getContent());
    }
}
