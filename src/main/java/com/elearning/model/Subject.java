package com.elearning.model;

public class Subject {

    private int id;
    private String name;
    private int adminId;

    public Subject() {
    }

    public Subject(int id, String name, int adminId) {
        this.id = id;
        this.name = name;
        this.adminId = adminId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    @Override
    public String toString() {
        return name;
    }
}
