package com.example.studentsmanaged.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    // Database URL for our students data
    private static final String DB_URL = "jdbc:sqlite:students.db";

    // Singleton instance of this database connection(ensyring only one instance of database connection)
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
        initializeDatabase();
    }

    // Initialize database and create tables if they don't exist
    private void initializeDatabase() {
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
            System.out.println("Database initialized successfully");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
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
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}