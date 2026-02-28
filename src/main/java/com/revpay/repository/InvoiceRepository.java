package com.revpay.repository;

import com.revpay.model.Invoice;
import com.revpay.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceRepository {
    private static final Logger logger = LoggerUtil.getLogger(InvoiceRepository.class);

    public void createInvoice(Invoice invoice) {
        String sql = "INSERT INTO invoices (business_id, customer_name, items_description, total_amount, due_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, invoice.getBusinessId());
            stmt.setString(2, invoice.getCustomerName());
            stmt.setString(3, invoice.getItemsDescription());
            stmt.setDouble(4, invoice.getTotalAmount());
            stmt.setDate(5, invoice.getDueDate());
            stmt.setString(6, invoice.getStatus());
            stmt.executeUpdate();
            logger.info("Invoice created for business ID: " + invoice.getBusinessId());
        } catch (SQLException e) {
            logger.error("Error creating invoice: " + e.getMessage());
        }
    }

    public List<Invoice> getInvoicesByBusinessId(int businessId) {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices WHERE business_id = ? ORDER BY timestamp DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, businessId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(new Invoice(
                            rs.getInt("invoice_id"),
                            rs.getInt("business_id"),
                            rs.getString("customer_name"),
                            rs.getString("items_description"),
                            rs.getDouble("total_amount"),
                            rs.getDate("due_date"),
                            rs.getString("status"),
                            rs.getTimestamp("timestamp")));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving invoices: " + e.getMessage());
        }
        return invoices;
    }

    public void updateStatus(int invoiceId, String status) {
        String sql = "UPDATE invoices SET status = ? WHERE invoice_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, invoiceId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error updating invoice status: " + e.getMessage());
        }
    }
}
