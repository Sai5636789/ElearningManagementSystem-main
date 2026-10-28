package com.elearning.dao;

import com.elearning.config.DatabaseConfig;
import com.elearning.model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDao {

    public List<Course> findAll(Integer subjectId) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT C.course_ID, C.Name, C.Description, C.Content, C.teacherID, C.Subject_ID, " +
                        "S.Name AS subject_name, CONCAT(T.fname, ' ', COALESCE(T.lname, '')) AS teacher_name, " +
                        "T.Email_ID AS teacher_email " +
                        "FROM Courses C " +
                        "LEFT JOIN Subjects S ON S.Subject_ID = C.Subject_ID " +
                        "LEFT JOIN Teacher T ON T.teacherID = C.teacherID "
        );
        if (subjectId != null) {
            sql.append("WHERE C.Subject_ID = ? ");
        }
        sql.append("ORDER BY C.Name");

        List<Course> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (subjectId != null) {
                ps.setInt(1, subjectId);
            }
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
                    String tName = rs.getString("teacher_name");
                    c.setTeacherName(tName != null ? tName.trim() : "");
                    c.setTeacherEmail(rs.getString("teacher_email"));
                    list.add(c);
                }
            }
        }
        return list;
    }

    public List<Course> findByTeacher(int teacherId) throws SQLException {
        String sql = "SELECT C.course_ID, C.Name, C.Description, C.Content, C.teacherID, C.Subject_ID, " +
                "S.Name AS subject_name " +
                "FROM Courses C " +
                "LEFT JOIN Subjects S ON S.Subject_ID = C.Subject_ID " +
                "WHERE C.teacherID = ? ORDER BY C.Name";

        List<Course> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
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

    public Optional<Course> findByName(String name) throws SQLException {
        String sql = "SELECT course_ID, Name, Description, Content, teacherID, Subject_ID FROM Courses WHERE Name = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Course(
                            rs.getInt("course_ID"),
                            rs.getString("Name"),
                            rs.getString("Description"),
                            rs.getString("Content"),
                            rs.getInt("teacherID"),
                            rs.getInt("Subject_ID")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    public boolean addCourse(Course course) throws SQLException {
        String sql = "INSERT INTO Courses (Name, Description, Content, teacherID, Subject_ID) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, course.getName());
            ps.setString(2, course.getDescription());
            ps.setString(3, course.getContent());
            ps.setInt(4, course.getTeacherId());
            ps.setInt(5, course.getSubjectId());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        course.setId(keys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean updateCourse(int courseId, String description, String content) throws SQLException {
        String sql = "UPDATE Courses SET Description = ?, Content = ? WHERE course_ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, description);
            ps.setString(2, content);
            ps.setInt(3, courseId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM Courses WHERE course_ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;
        }
    }
}
