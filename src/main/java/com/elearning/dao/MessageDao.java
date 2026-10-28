package com.elearning.dao;

import com.elearning.config.DatabaseConfig;
import com.elearning.model.Message;
import com.elearning.model.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDao {

    /**
     * Inserts a message with sender role so the receiver-name lookup in
     * getInbox() can join the correct user table via the Users view.
     */
    public boolean sendMessage(int senderId, int receiverId, String text, Role senderRole)
            throws SQLException {
        String sql = "INSERT INTO Messages (message, time_Stamp, User_ID, toUser_ID, sender_role) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, text);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, senderId);
            ps.setInt(4, receiverId);
            ps.setString(5, senderRole.name());
            return ps.executeUpdate() > 0;
        }
    }

    /** Backwards-compatible overload that defaults to STUDENT sender. */
    public boolean sendMessage(int senderId, int receiverId, String text) throws SQLException {
        return sendMessage(senderId, receiverId, text, Role.STUDENT);
    }

    /**
     * Returns the inbox for a student, joining the Users view to resolve the
     * sender's display name regardless of whether the sender is a Student,
     * Teacher, or Admin.
     */
    public List<Message> getInbox(int studentId) throws SQLException {
        String sql = "SELECT M.Message_ID, M.message, M.time_Stamp, M.User_ID, M.toUser_ID, " +
                "CONCAT(U.fname, ' ', COALESCE(U.lname, '')) AS sender_name " +
                "FROM Messages M " +
                "LEFT JOIN Users U ON U.user_id = M.User_ID AND U.role = M.sender_role " +
                "WHERE M.toUser_ID = ? " +
                "  AND (M.deleted_by_receiver = FALSE OR M.deleted_by_receiver IS NULL) " +
                "ORDER BY M.time_Stamp DESC";

        List<Message> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Message m = new Message(
                            rs.getInt("Message_ID"),
                            rs.getString("message"),
                            rs.getTimestamp("time_Stamp").toLocalDateTime(),
                            rs.getInt("User_ID"),
                            rs.getInt("toUser_ID")
                    );
                    String senderName = rs.getString("sender_name");
                    m.setSenderName(senderName != null ? senderName.trim() : "Unknown");
                    list.add(m);
                }
            }
        }
        return list;
    }

    public List<Message> getSentBox(int studentId) throws SQLException {
        String sql = "SELECT M.Message_ID, M.message, M.time_Stamp, M.User_ID, M.toUser_ID, " +
                "CONCAT(S.fname, ' ', COALESCE(S.lname, '')) AS receiver_name " +
                "FROM Messages M " +
                "LEFT JOIN Student S ON S.stdID = M.toUser_ID " +
                "WHERE M.User_ID = ? " +
                "  AND (M.deleted_by_sender = FALSE OR M.deleted_by_sender IS NULL) " +
                "ORDER BY M.time_Stamp DESC";

        List<Message> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Message m = new Message(
                            rs.getInt("Message_ID"),
                            rs.getString("message"),
                            rs.getTimestamp("time_Stamp").toLocalDateTime(),
                            rs.getInt("User_ID"),
                            rs.getInt("toUser_ID")
                    );
                    String receiverName = rs.getString("receiver_name");
                    m.setReceiverName(receiverName != null ? receiverName.trim() : "Unknown");
                    list.add(m);
                }
            }
        }
        return list;
    }

    public boolean deleteFromInbox(int messageId, int studentId) throws SQLException {
        // Soft-delete from the receiver's perspective.
        String sql = "UPDATE Messages SET deleted_by_receiver = TRUE " +
                     "WHERE Message_ID = ? AND toUser_ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, messageId);
            ps.setInt(2, studentId);
            int updated = ps.executeUpdate();
            if (updated > 0) {
                cleanupIfDeletedByBoth(conn, messageId);
                return true;
            }
        }
        return false;
    }

    public boolean deleteFromSentBox(int messageId, int studentId) throws SQLException {
        String sql = "UPDATE Messages SET deleted_by_sender = TRUE " +
                     "WHERE Message_ID = ? AND User_ID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, messageId);
            ps.setInt(2, studentId);
            int updated = ps.executeUpdate();
            if (updated > 0) {
                cleanupIfDeletedByBoth(conn, messageId);
                return true;
            }
        }
        return false;
    }

    // Physically remove a message only when both parties have soft-deleted it.
    private void cleanupIfDeletedByBoth(Connection conn, int messageId) {
        String sql = "DELETE FROM Messages " +
                     "WHERE Message_ID = ? AND deleted_by_sender = TRUE AND deleted_by_receiver = TRUE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, messageId);
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}
