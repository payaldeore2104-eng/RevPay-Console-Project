package com.revpay.repository;

import com.revpay.model.Transaction;
import com.revpay.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
    private static final Logger logger = LoggerUtil.getLogger(TransactionRepository.class);

    public void logTransaction(Connection conn, Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions (sender_id, receiver_id, amount, type, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (transaction.getSenderId() == 0)
                stmt.setNull(1, Types.INTEGER);
            else
                stmt.setInt(1, transaction.getSenderId());
            if (transaction.getReceiverId() == 0)
                stmt.setNull(2, Types.INTEGER);
            else
                stmt.setInt(2, transaction.getReceiverId());
            stmt.setDouble(3, transaction.getAmount());
            stmt.setString(4, transaction.getType());
            stmt.setString(5, transaction.getStatus());
            stmt.executeUpdate();
        }
    }

    public void logTransaction(Transaction transaction) {
        try (Connection conn = DBConnection.getConnection()) {
            logTransaction(conn, transaction);
            logger.info("Transaction logged: " + transaction.getType());
        } catch (SQLException e) {
            logger.error("Error logging transaction: " + e.getMessage());
        }
    }

    public List<Transaction> getTransactionsByUserId(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE sender_id = ? OR receiver_id = ? ORDER BY timestamp DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving transactions: " + e.getMessage());
        }
        return transactions;
    }

    public List<Transaction> getTransactionsByFilters(int userId, Timestamp startDate, Timestamp endDate,
            Double minAmount, Double maxAmount, String type) {
        List<Transaction> transactions = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM transactions WHERE (sender_id = ? OR receiver_id = ?) ");
        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(userId);

        if (startDate != null) {
            sql.append("AND timestamp >= ? ");
            params.add(startDate);
        }
        if (endDate != null) {
            sql.append("AND timestamp <= ? ");
            params.add(endDate);
        }
        if (minAmount != null) {
            sql.append("AND amount >= ? ");
            params.add(minAmount);
        }
        if (maxAmount != null) {
            sql.append("AND amount <= ? ");
            params.add(maxAmount);
        }
        if (type != null && !type.isEmpty()) {
            sql.append("AND type = ? ");
            params.add(type);
        }
        sql.append("ORDER BY timestamp DESC");

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapResultSetToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving filtered transactions: " + e.getMessage());
        }
        return transactions;
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("transaction_id"),
                rs.getInt("sender_id"),
                rs.getInt("receiver_id"),
                rs.getDouble("amount"),
                rs.getString("type"),
                rs.getString("status"),
                rs.getTimestamp("timestamp"));
    }
}
