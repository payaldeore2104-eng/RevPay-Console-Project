package com.revpay.repository;

import com.revpay.model.MoneyRequest;
import com.revpay.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MoneyRequestRepository {
    private static final Logger logger = LoggerUtil.getLogger(MoneyRequestRepository.class);

    public void createRequest(MoneyRequest request) {
        String sql = "INSERT INTO money_requests (sender_id, receiver_id, amount, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, request.getSenderId());
            stmt.setInt(2, request.getReceiverId());
            stmt.setDouble(3, request.getAmount());
            stmt.setString(4, request.getStatus());
            stmt.executeUpdate();
            logger.info("Money request created from " + request.getSenderId() + " to " + request.getReceiverId());
        } catch (SQLException e) {
            logger.error("Error creating money request: " + e.getMessage());
        }
    }

    public List<MoneyRequest> getRequestsByReceiverId(int receiverId) {
        List<MoneyRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM money_requests WHERE receiver_id = ? AND status = 'PENDING' ORDER BY timestamp DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    requests.add(mapResultSetToRequest(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving money requests: " + e.getMessage());
        }
        return requests;
    }

    public Optional<MoneyRequest> getRequestById(int requestId) {
        String sql = "SELECT * FROM money_requests WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRequest(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving money request: " + e.getMessage());
        }
        return Optional.empty();
    }

    public void updateStatus(Connection conn, int requestId, String status) throws SQLException {
        String sql = "UPDATE money_requests SET status = ? WHERE request_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            stmt.executeUpdate();
        }
    }

    public void updateStatus(int requestId, String status) {
        try (Connection conn = DBConnection.getConnection()) {
            updateStatus(conn, requestId, status);
        } catch (SQLException e) {
            logger.error("Error updating money request status: " + e.getMessage());
        }
    }

    private MoneyRequest mapResultSetToRequest(ResultSet rs) throws SQLException {
        return new MoneyRequest(
                rs.getInt("request_id"),
                rs.getInt("sender_id"),
                rs.getInt("receiver_id"),
                rs.getDouble("amount"),
                rs.getString("status"),
                rs.getTimestamp("timestamp"));
    }
}
