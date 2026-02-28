package com.revpay.repository;

import com.revpay.model.User;
import com.revpay.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Optional;

public class UserRepository {
    private static final Logger logger = LoggerUtil.getLogger(UserRepository.class);

    public boolean registerUser(User user) {

        String sql = "INSERT INTO users (full_name, username, email, phone, hashed_password, " +
                "security_answer1, security_answer2, security_answer3, transaction_pin, account_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getHashedPassword());
            stmt.setString(6, user.getSecurityAnswer1());
            stmt.setString(7, user.getSecurityAnswer2());
            stmt.setString(8, user.getSecurityAnswer3());
            stmt.setString(9, user.getTransactionPin());
            stmt.setString(10, user.getAccountType());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
                    }
                }
                logger.info("User registered successfully: " + user.getEmail());
                return true;
            }

        } catch (SQLException e) {
            logger.error("Error registering user: " + e.getMessage());
        }

        return false;
    }

    public Optional<User> findByEmailPhoneOrUsername(String identifier) {
        String sql = "SELECT * FROM users WHERE email = ? OR phone = ? OR username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            stmt.setString(3, identifier);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding user: " + e.getMessage());
        }

        return Optional.empty();
    }

    public Optional<User> findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding user by ID: " + e.getMessage());
        }

        return Optional.empty();
    }

    public void updateFailedAttempts(int userId, int attempts) {
        String sql = "UPDATE users SET failed_attempts = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, attempts);
            stmt.setInt(2, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error updating failed attempts: " + e.getMessage());
        }
    }

    public void lockAccount(int userId) {
        String sql = "UPDATE users SET account_locked = TRUE WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();
            logger.warn("Account locked for user ID: " + userId);

        } catch (SQLException e) {
            logger.error("Error locking account: " + e.getMessage());
        }
    }

    public void unlockAccount(int userId) {
        String sql = "UPDATE users SET account_locked = FALSE, failed_attempts = 0 WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();
            logger.info("Account unlocked for user ID: " + userId);

        } catch (SQLException e) {
            logger.error("Error unlocking account: " + e.getMessage());
        }
    }

    public void updatePassword(int userId, String newHashedPassword) {
        String sql = "UPDATE users SET hashed_password = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newHashedPassword);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            logger.info("Password updated for user ID: " + userId);

        } catch (SQLException e) {
            logger.error("Error updating password: " + e.getMessage());
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {

        User user = new User(
                rs.getInt("user_id"),
                rs.getString("full_name"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("hashed_password"),
                rs.getInt("failed_attempts"),
                rs.getBoolean("account_locked"),
                rs.getString("transaction_pin"),
                rs.getString("account_type"),
                rs.getTimestamp("created_at")
        );

        user.setSecurityAnswer1(rs.getString("security_answer1"));
        user.setSecurityAnswer2(rs.getString("security_answer2"));
        user.setSecurityAnswer3(rs.getString("security_answer3"));

        return user;
    }
}