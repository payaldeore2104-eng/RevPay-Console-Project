package com.revpay.repository;

import com.revpay.model.PaymentMethod;
import com.revpay.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentMethodRepository {
    private static final Logger logger = LoggerUtil.getLogger(PaymentMethodRepository.class);

    public void addPaymentMethod(PaymentMethod method) {
        String sql = "INSERT INTO payment_methods (user_id, card_number_encrypted, expiry, type, is_default) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, method.getUserId());
            stmt.setString(2, method.getCardNumberEncrypted());
            stmt.setString(3, method.getExpiry());
            stmt.setString(4, method.getType());
            stmt.setBoolean(5, method.isDefault());
            stmt.executeUpdate();
            logger.info("Payment method added for user ID: " + method.getUserId());
        } catch (SQLException e) {
            logger.error("Error adding payment method: " + e.getMessage());
        }
    }

    public List<PaymentMethod> getMethodsByUserId(int userId) {
        List<PaymentMethod> methods = new ArrayList<>();
        String sql = "SELECT * FROM payment_methods WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    methods.add(new PaymentMethod(
                            rs.getInt("method_id"),
                            rs.getInt("user_id"),
                            rs.getString("card_number_encrypted"),
                            rs.getString("expiry"),
                            rs.getString("type"),
                            rs.getBoolean("is_default")));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving payment methods: " + e.getMessage());
        }
        return methods;
    }

    public void deletePaymentMethod(int methodId) {
        String sql = "DELETE FROM payment_methods WHERE method_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, methodId);
            stmt.executeUpdate();
            logger.info("Payment method deleted: " + methodId);
        } catch (SQLException e) {
            logger.error("Error deleting payment method: " + e.getMessage());
        }
    }

    public void resetDefault(int userId) {
        String sql = "UPDATE payment_methods SET is_default = FALSE WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error resetting default payment method: " + e.getMessage());
        }
    }
}
