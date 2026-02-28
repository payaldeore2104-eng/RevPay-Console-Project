package com.revpay.repository;

import com.revpay.model.Wallet;
import com.revpay.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class WalletRepository {
    private static final Logger logger = LoggerUtil.getLogger(WalletRepository.class);

    public void createWallet(int userId) {
        String sql = "INSERT INTO wallet (user_id, balance) VALUES (?, 0.00)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            logger.info("Wallet created for user ID: " + userId);
        } catch (SQLException e) {
            logger.error("Error creating wallet: " + e.getMessage());
        }
    }

    public Optional<Wallet> getWalletByUserId(int userId) {
        String sql = "SELECT * FROM wallet WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Wallet(
                            rs.getInt("wallet_id"),
                            rs.getInt("user_id"),
                            rs.getDouble("balance")));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving wallet: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean updateBalance(int userId, double amount) {
        // Amount can be positive (deposit) or negative (withdraw)
        String sql = "UPDATE wallet SET balance = balance + ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, userId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            logger.error("Error updating balance: " + e.getMessage());
        }
        return false;
    }

    // For transactional safety, this might be better handled in Service layer with
    // a single Connection,
    // but for this simple repo, separate calls are okay if we aren't doing complex
    // transfers yet.
    // Wait, transfer needs atomic debit and credit.
    // We will handle transaction atomicity in Service layer by passing a
    // Connection?
    // For now, let's keep it simple. If we need transaction support, we might need
    // a method that accepts a Connection.

    public boolean updateBalance(Connection conn, int userId, double amount) throws SQLException {
        String sql = "UPDATE wallet SET balance = balance + ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, userId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        }
    }
}
