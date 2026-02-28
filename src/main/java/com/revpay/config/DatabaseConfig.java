package com.revpay.config;

import com.revpay.util.LoggerUtil;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database configuration and connection management
 * Centralizes database connectivity for the application
 */
public class DatabaseConfig {
    private static final Logger logger = LoggerUtil.getLogger(DatabaseConfig.class);

    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/revpay_db";
    private static final String USER = "revpay_user";
    private static final String PASSWORD = "revpay123";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    private static Connection connection = null;

    /**
     * Get database connection (singleton pattern)
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load JDBC driver
                Class.forName(DRIVER_CLASS);
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                logger.info("Database connected successfully.");
            }
        } catch (ClassNotFoundException e) {
            logger.error("MySQL JDBC Driver not found: " + e.getMessage());
            throw new RuntimeException("Database driver not found", e);
        } catch (SQLException e) {
            logger.error("Database connection failed: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
        return connection;
    }

    /**
     * Close the database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed.");
            } catch (SQLException e) {
                logger.error("Error closing database connection: " + e.getMessage());
            }
        }
    }

    /**
     * Test database connectivity
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            logger.error("Connection test failed: " + e.getMessage());
            return false;
        }
    }

    // Getters for configuration (useful for testing or migration)
    public static String getUrl() {
        return URL;
    }

    public static String getUser() {
        return USER;
    }
}
