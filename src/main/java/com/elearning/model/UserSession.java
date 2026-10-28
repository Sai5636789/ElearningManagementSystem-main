package com.elearning.model;

public class UserSession {

    private final int id;
    private final String username;
    private final Role role;
    private final String firstName;
    private final String lastName;
    private final String email;

    public UserSession(int id, String username, Role role, String firstName, String lastName, String email) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Role getRole() {
        return role;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        if (lastName == null || lastName.isBlank()) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }
}
