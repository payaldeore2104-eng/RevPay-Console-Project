package com.revpay.repository;

import com.revpay.model.BusinessDetails;
import com.revpay.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Optional;

public class BusinessRepository {
    private static final Logger logger = LoggerUtil.getLogger(BusinessRepository.class);

    public void createBusinessDetails(BusinessDetails details) {
        String sql = "INSERT INTO business_details (user_id, business_name, business_type, tax_id, address, verification_document_path) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, details.getUserId());
            stmt.setString(2, details.getBusinessName());
            stmt.setString(3, details.getBusinessType());
            stmt.setString(4, details.getTaxId());
            stmt.setString(5, details.getAddress());
            stmt.setString(6, details.getVerificationDocumentPath());
            stmt.executeUpdate();
            logger.info("Business details created for user ID: " + details.getUserId());
        } catch (SQLException e) {
            logger.error("Error creating business details: " + e.getMessage());
        }
    }

    public Optional<BusinessDetails> getBusinessByUserId(int userId) {
        String sql = "SELECT * FROM business_details WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new BusinessDetails(
                            rs.getInt("business_id"),
                            rs.getInt("user_id"),
                            rs.getString("business_name"),
                            rs.getString("business_type"),
                            rs.getString("tax_id"),
                            rs.getString("address"),
                            rs.getString("verification_document_path")));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving business details: " + e.getMessage());
        }
        return Optional.empty();
    }
}
