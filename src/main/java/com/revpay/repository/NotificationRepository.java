package com.revpay.repository;

import com.revpay.model.Notification;
import com.revpay.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {
    private static final Logger logger = LoggerUtil.getLogger(NotificationRepository.class);

    public void createNotification(Connection conn, Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications (user_id, message, type) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, notification.getUserId());
            stmt.setString(2, notification.getMessage());
            stmt.setString(3, notification.getType());
            stmt.executeUpdate();
        }
    }

    public void createNotification(Notification notification) {
        try (Connection conn = DBConnection.getConnection()) {
            createNotification(conn, notification);
        } catch (SQLException e) {
            logger.error("Error creating notification: " + e.getMessage());
        }
    }

    public List<Notification> getNotificationsByUserId(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY timestamp DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(new Notification(
                            rs.getInt("notification_id"),
                            rs.getInt("user_id"),
                            rs.getString("message"),
                            rs.getString("type"),
                            rs.getBoolean("is_read"),
                            rs.getTimestamp("timestamp")));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving notifications: " + e.getMessage());
        }
        return notifications;
    }

    public void markAllAsRead(int userId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error marking notifications as read: " + e.getMessage());
        }
    }
}
