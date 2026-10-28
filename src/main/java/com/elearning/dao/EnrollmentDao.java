package com.elearning.dao;

import com.elearning.config.DatabaseConfig;
import com.elearning.model.Course;
import com.elearning.model.Role;
import com.elearning.model.User;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDao {

    public boolean enroll(int courseId, int studentId) throws SQLException {
        String sql = "INSERT INTO Enrollments (Enrollment_Date, course_ID, stdID) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, courseId);
            ps.setInt(3, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean withdraw(int courseId, int studentId) throws SQLException {
        String sql = "DELETE FROM Enrollments WHERE course_ID = ? AND stdID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setInt(2, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean isEnrolled(int courseId, int studentId) throws SQLException {
        String sql = "SELECT 1 FROM Enrollments WHERE course_ID = ? AND stdID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Course> findEnrolledCourses(int studentId) throws SQLException {
        String sql = "SELECT C.course_ID, C.Name, C.Description, C.Content, C.teacherID, C.Subject_ID, S.Name AS subject_name " +
                "FROM Courses C " +
                "INNER JOIN Enrollments E ON E.course_ID = C.course_ID " +
                "LEFT JOIN Subjects S ON S.Subject_ID = C.Subject_ID " +
                "WHERE E.stdID = ? ORDER BY C.Name";

        List<Course> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Course c = new Course(
                            rs.getInt("course_ID"),
                            rs.getString("Name"),
                            rs.getString("Description"),
                            rs.getString("Content"),
                            rs.getInt("teacherID"),
                            rs.getInt("Subject_ID")
                    );
                    c.setSubjectName(rs.getString("subject_name"));
                    list.add(c);
                }
            }
        }
        return list;
    }

    public List<User> findCourseParticipants(int courseId) throws SQLException {
        List<User> participants = new ArrayList<>();

        // Add the teacher teaching this course
        String teacherSql = "SELECT T.teacherID, T.fname, T.lname, T.Email_ID, T.Gender FROM Teacher T " +
                "INNER JOIN Courses C ON C.teacherID = T.teacherID WHERE C.course_ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(teacherSql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User t = new User(rs.getInt("teacherID"), Role.TEACHER, rs.getString("fname"), rs.getString("lname"), rs.getString("Email_ID"), "");
                    t.setGender(rs.getString("Gender"));
                    participants.add(t);
                }
            }
        }

        // Add all enrolled students
        String studentSql = "SELECT S.stdID, S.fname, S.lname, S.Email_ID, S.Gender FROM Student S " +
                "INNER JOIN Enrollments E ON E.stdID = S.stdID WHERE E.course_ID = ? ORDER BY S.fname, S.lname";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(studentSql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User s = new User(rs.getInt("stdID"), Role.STUDENT, rs.getString("fname"), rs.getString("lname"), rs.getString("Email_ID"), "");
                    s.setGender(rs.getString("Gender"));
                    participants.add(s);
                }
            }
        }
        return participants;
    }

    public List<User> findEnrolledStudents(int courseId) throws SQLException {
        List<User> students = new ArrayList<>();
        String sql = "SELECT S.stdID, S.fname, S.lname, S.Email_ID, S.Gender FROM Student S " +
                "INNER JOIN Enrollments E ON E.stdID = S.stdID WHERE E.course_ID = ? ORDER BY S.fname, S.lname";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User s = new User(rs.getInt("stdID"), Role.STUDENT, rs.getString("fname"), rs.getString("lname"), rs.getString("Email_ID"), "");
                    s.setGender(rs.getString("Gender"));
                    students.add(s);
                }
            }
        }
        return students;
    }
}
