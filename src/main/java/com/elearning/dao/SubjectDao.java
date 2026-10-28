package com.elearning.dao;

import com.elearning.config.DatabaseConfig;
import com.elearning.model.Subject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SubjectDao {

    public List<Subject> findAll() throws SQLException {
        String sql = "SELECT Subject_ID, Name, Adminid FROM Subjects ORDER BY Name";
        List<Subject> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Subject(rs.getInt("Subject_ID"), rs.getString("Name"), rs.getInt("Adminid")));
            }
        }
        return list;
    }

    public Optional<Subject> findByName(String name) throws SQLException {
        String sql = "SELECT Subject_ID, Name, Adminid FROM Subjects WHERE Name = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Subject(rs.getInt("Subject_ID"), rs.getString("Name"), rs.getInt("Adminid")));
                }
            }
        }
        return Optional.empty();
    }

    public boolean addSubject(String name, int adminId) throws SQLException {
        String sql = "INSERT INTO Subjects (Name, Adminid) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setInt(2, adminId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteSubject(int subjectId) throws SQLException {
        String sql = "DELETE FROM Subjects WHERE Subject_ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            return ps.executeUpdate() > 0;
        }
    }
}
