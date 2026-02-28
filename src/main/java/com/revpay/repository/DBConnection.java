package com.revpay.repository;

import com.revpay.util.LoggerUtil;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/revpay_db";
    private static final String USER = "revpay_user"; // CHANGE THIS
    private static final String PASSWORD = "revpay123"; // CHANGE THIS
    private static final Logger logger = LoggerUtil.getLogger(DBConnection.class);

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load driver class (optional for newer JDBC, but good practice)
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                logger.info("Database connected successfully.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("Database connection failed: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
        return connection;
    }

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
}
