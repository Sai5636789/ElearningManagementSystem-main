package com.elearning.model;

import java.time.LocalDate;

public class Enrollment {

    private int id;
    private LocalDate enrollmentDate;
    private int courseId;
    private int studentId;
    private String courseName;
    private String studentName;

    public Enrollment() {
    }

    public Enrollment(int id, LocalDate enrollmentDate, int courseId, int studentId) {
        this.id = id;
        this.enrollmentDate = enrollmentDate;
        this.courseId = courseId;
        this.studentId = studentId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
