package com.example.studentsmanaged.database;

import com.example.studentsmanaged.util.LoggerUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    // Database URL
    private static final String DB_URL = "jdbc:sqlite:students.db";

    // Singleton instance
    private static DatabaseConnection instance;

    // Get singleton instance
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Private constructor for singleton pattern
    private DatabaseConnection() {
        LoggerUtil.info("DatabaseConnection", "Initializing database connection");
        initializeDatabase();
    }

    // Initialize database and create tables if they don't exist
    private void initializeDatabase() {
        LoggerUtil.info("DatabaseConnection", "Creating/checking database tables");
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create students table if it doesn't exist
            String sql = "CREATE TABLE IF NOT EXISTS students (" +
                    "id TEXT PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "course TEXT NOT NULL," +
                    "grade REAL NOT NULL" +
                    ");";

            stmt.execute(sql);
            LoggerUtil.info("DatabaseConnection", "Database initialized successfully");

        } catch (SQLException e) {
            LoggerUtil.error("DatabaseConnection", "Error initializing database", e);
        }
    }

    // Get a database connection
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Close resources safely
    public void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            LoggerUtil.error("DatabaseConnection", "Error closing resources", e);
        }
    }
}