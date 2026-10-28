package com.elearning.dao;

import com.elearning.config.DatabaseConfig;
import com.elearning.model.Role;
import com.elearning.model.User;
import com.elearning.model.UserSession;
import com.elearning.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDao.class);

    private String getTableName(Role role) {
        return switch (role) {
            case ADMIN -> "Admin";
            case TEACHER -> "Teacher";
            case STUDENT -> "Student";
        };
    }

    private String getIdColumn(Role role) {
        return switch (role) {
            case ADMIN -> "Adminid";
            case TEACHER -> "teacherID";
            case STUDENT -> "stdID";
        };
    }

    public Optional<UserSession> authenticate(String username, String password, Role role) throws SQLException {
        String table = getTableName(role);
        String idCol = getIdColumn(role);
        String sql = "SELECT " + idCol + ", username, password, fname, lname, Email_ID FROM " + table + " WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (PasswordUtil.verify(password, storedHash)) {
                        int id = rs.getInt(idCol);
                        return Optional.of(new UserSession(
                                id,
                                rs.getString("username"),
                                role,
                                rs.getString("fname"),
                                rs.getString("lname"),
                                rs.getString("Email_ID")
                        ));
                    }
                }
            }
        }
        return Optional.empty();
    }

    public boolean register(User user, InputStream pictureStream, int pictureSize) throws SQLException {
        String table = getTableName(user.getRole());
        String hashedPassword = PasswordUtil.hash(user.getPassword());

        String sql;
        if (user.getRole() == Role.ADMIN) {
            sql = "INSERT INTO Admin (fname, lname, Email_ID, username, password, Gender, picture) VALUES (?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "INSERT INTO " + table + " (fname, lname, Email_ID, username, password, Registration_Date, Gender, picture) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        }

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getUsername());
            ps.setString(5, hashedPassword);

            if (user.getRole() == Role.ADMIN) {
                ps.setString(6, user.getGender());
                if (pictureStream != null && pictureSize > 0) {
                    ps.setBinaryStream(7, pictureStream, pictureSize);
                } else {
                    ps.setNull(7, java.sql.Types.BLOB);
                }
            } else {
                ps.setDate(6, Date.valueOf(LocalDate.now()));
                ps.setString(7, user.getGender());
                if (pictureStream != null && pictureSize > 0) {
                    ps.setBinaryStream(8, pictureStream, pictureSize);
                } else {
                    ps.setNull(8, java.sql.Types.BLOB);
                }
            }
            return ps.executeUpdate() > 0;
        }
    }

    public Optional<User> findById(int id, Role role) throws SQLException {
        String table = getTableName(role);
        String idCol = getIdColumn(role);
        String sql = "SELECT * FROM " + table + " WHERE " + idCol + " = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt(idCol));
                    u.setRole(role);
                    u.setFirstName(rs.getString("fname"));
                    u.setLastName(rs.getString("lname"));
                    u.setEmail(rs.getString("Email_ID"));
                    u.setUsername(rs.getString("username"));
                    u.setGender(rs.getString("Gender"));
                    u.setPicture(rs.getBytes("picture"));
                    return Optional.of(u);
                }
            }
        }
        return Optional.empty();
    }

    public boolean updateName(int id, Role role, String firstName, String lastName) throws SQLException {
        String table = getTableName(role);
        String idCol = getIdColumn(role);
        String sql = "UPDATE " + table + " SET fname = ?, lname = ? WHERE " + idCol + " = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateEmail(int id, Role role, String email) throws SQLException {
        String table = getTableName(role);
        String idCol = getIdColumn(role);
        String sql = "UPDATE " + table + " SET Email_ID = ? WHERE " + idCol + " = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updatePassword(int id, Role role, String newPassword) throws SQLException {
        String table = getTableName(role);
        String idCol = getIdColumn(role);
        String sql = "UPDATE " + table + " SET password = ? WHERE " + idCol + " = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, PasswordUtil.hash(newPassword));
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateProfilePicture(int id, Role role, InputStream pictureStream, int size) throws SQLException {
        String table = getTableName(role);
        String idCol = getIdColumn(role);
        String sql = "UPDATE " + table + " SET picture = ? WHERE " + idCol + " = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBinaryStream(1, pictureStream, size);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateLastLogin(int id, Role role) throws SQLException {
        if (role == Role.ADMIN) {
            return true;
        }
        String table = getTableName(role);
        String idCol = getIdColumn(role);
        String sql = "UPDATE " + table + " SET Last_Login = ? WHERE " + idCol + " = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteAccount(int id, Role role) throws SQLException {
        String table = getTableName(role);
        String idCol = getIdColumn(role);
        String sql = "DELETE FROM " + table + " WHERE " + idCol + " = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<User> findAllByRole(Role role) throws SQLException {
        String table = getTableName(role);
        String idCol = getIdColumn(role);
        String sql = "SELECT " + idCol + ", fname, lname, Email_ID, username, Gender, picture FROM " + table + " ORDER BY fname, lname";

        List<User> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt(idCol));
                u.setRole(role);
                u.setFirstName(rs.getString("fname"));
                u.setLastName(rs.getString("lname"));
                u.setEmail(rs.getString("Email_ID"));
                u.setUsername(rs.getString("username"));
                u.setGender(rs.getString("Gender"));
                u.setPicture(rs.getBytes("picture"));
                list.add(u);
            }
        }
        return list;
    }
}
