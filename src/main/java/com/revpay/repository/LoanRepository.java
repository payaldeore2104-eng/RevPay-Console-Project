package com.revpay.repository;

import com.revpay.config.DatabaseConfig;
import com.revpay.model.LoanApplication;
import com.revpay.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for loan application database operations
 */
public class LoanRepository {
    private static final Logger logger = LoggerUtil.getLogger(LoanRepository.class);

    /**
     * Create a new loan application
     */
    public boolean createLoanApplication(LoanApplication loan) {
        String sql = "INSERT INTO loan_applications (user_id, amount, purpose, interest_rate, duration_months, status, applied_date) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, loan.getUserId());
            stmt.setDouble(2, loan.getAmount());
            stmt.setString(3, loan.getPurpose());
            stmt.setDouble(4, loan.getInterestRate());
            stmt.setInt(5, loan.getDurationMonths());
            stmt.setString(6, loan.getStatus());
            stmt.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                logger.info("Loan application created for user ID: " + loan.getUserId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating loan application: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get all loan applications for a user
     */
    public List<LoanApplication> getLoansByUserId(int userId) {
        List<LoanApplication> loans = new ArrayList<>();
        String sql = "SELECT * FROM loan_applications WHERE user_id = ? ORDER BY applied_date DESC";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            logger.error("Error fetching loans for user " + userId + ": " + e.getMessage());
        }
        return loans;
    }

    /**
     * Get loan by ID
     */
    public LoanApplication getLoanById(int loanId) {
        String sql = "SELECT * FROM loan_applications WHERE loan_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, loanId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToLoan(rs);
            }
        } catch (SQLException e) {
            logger.error("Error fetching loan " + loanId + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Update loan status
     */
    public boolean updateLoanStatus(int loanId, String status) {
        String sql = "UPDATE loan_applications SET status = ?, approved_date = ? WHERE loan_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            if ("APPROVED".equals(status)) {
                stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            } else {
                stmt.setNull(2, Types.TIMESTAMP);
            }
            stmt.setInt(3, loanId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                logger.info("Loan " + loanId + " status updated to " + status);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating loan status: " + e.getMessage());
        }
        return false;
    }

    /**
     * Update loan payment
     */
    public boolean recordPayment(int loanId, double paymentAmount) {
        String sql = "UPDATE loan_applications SET amount_paid = amount_paid + ? WHERE loan_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, paymentAmount);
            stmt.setInt(2, loanId);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                logger.info("Payment of " + paymentAmount + " recorded for loan " + loanId);

                // Check if loan is fully paid
                LoanApplication loan = getLoanById(loanId);
                if (loan != null && loan.getBalanceRemaining() <= 0.01) { // Using small threshold for floating point
                    updateLoanStatus(loanId, "CLOSED");
                }
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error recording payment: " + e.getMessage());
        }
        return false;
    }

    /**
     * Map ResultSet to LoanApplication object
     */
    private LoanApplication mapResultSetToLoan(ResultSet rs) throws SQLException {
        LoanApplication loan = new LoanApplication();
        loan.setLoanId(rs.getInt("loan_id"));
        loan.setUserId(rs.getInt("user_id"));
        loan.setAmount(rs.getDouble("amount"));
        loan.setPurpose(rs.getString("purpose"));
        loan.setInterestRate(rs.getDouble("interest_rate"));
        loan.setDurationMonths(rs.getInt("duration_months"));
        loan.setStatus(rs.getString("status"));
        loan.setAppliedDate(rs.getTimestamp("applied_date"));
        loan.setApprovedDate(rs.getTimestamp("approved_date"));

        // Handle amount_paid column if it exists
        try {
            loan.setAmountPaid(rs.getDouble("amount_paid"));
        } catch (SQLException e) {
            loan.setAmountPaid(0.0);
        }

        return loan;
    }
}
