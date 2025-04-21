package com.example.studentsmanaged.database;

import com.example.studentsmanaged.models.Student;
import com.example.studentsmanaged.util.LoggerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    // Database connection
    private final DatabaseConnection dbConnection;

    // Constructor
    public StudentDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    // Add a student to the database
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (id, name, course, grade) VALUES (?, ?, ?, ?)";
        LoggerUtil.debug("StudentDAO", "Adding student with ID: " + student.getId());

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getCourse());
            pstmt.setDouble(4, student.getGrade());

            int rowsAffected = pstmt.executeUpdate();
            boolean success = rowsAffected > 0;

            if (success) {
                LoggerUtil.info("StudentDAO", "Student added successfully: " + student.getId());
            } else {
                LoggerUtil.warning("StudentDAO", "No rows affected when adding student: " + student.getId());
            }

            return success;

        } catch (SQLException e) {
            LoggerUtil.error("StudentDAO", "Error adding student: " + student.getId(), e);
            return false;
        } finally {
            dbConnection.closeResources(conn, pstmt, null);
        }
    }

    // Update a student in the database
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET name = ?, course = ?, grade = ? WHERE id = ?";
        LoggerUtil.debug("StudentDAO", "Updating student with ID: " + student.getId());

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getCourse());
            pstmt.setDouble(3, student.getGrade());
            pstmt.setString(4, student.getId());

            int rowsAffected = pstmt.executeUpdate();
            boolean success = rowsAffected > 0;

            if (success) {
                LoggerUtil.info("StudentDAO", "Student updated successfully: " + student.getId());
            } else {
                LoggerUtil.warning("StudentDAO", "No rows affected when updating student: " + student.getId());
            }

            return success;

        } catch (SQLException e) {
            LoggerUtil.error("StudentDAO", "Error updating student: " + student.getId(), e);
            return false;
        } finally {
            dbConnection.closeResources(conn, pstmt, null);
        }
    }

    // Delete a student from the database
    public boolean deleteStudent(String id) {
        String sql = "DELETE FROM students WHERE id = ?";
        LoggerUtil.debug("StudentDAO", "Deleting student with ID: " + id);

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, id);

            int rowsAffected = pstmt.executeUpdate();
            boolean success = rowsAffected > 0;

            if (success) {
                LoggerUtil.info("StudentDAO", "Student deleted successfully: " + id);
            } else {
                LoggerUtil.warning("StudentDAO", "No rows affected when deleting student: " + id);
            }

            return success;

        } catch (SQLException e) {
            LoggerUtil.error("StudentDAO", "Error deleting student: " + id, e);
            return false;
        } finally {
            dbConnection.closeResources(conn, pstmt, null);
        }
    }

    // Get a student by ID
    public Student getStudentById(String id) {
        String sql = "SELECT * FROM students WHERE id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Student student = new Student();
                student.setId(rs.getString("id"));
                student.setName(rs.getString("name"));
                student.setCourse(rs.getString("course"));
                student.setGrade(rs.getDouble("grade"));

                return student;
            }

            return null;

        } catch (SQLException e) {
            System.err.println("Error getting student by ID: " + e.getMessage());
            return null;
        } finally {
            dbConnection.closeResources(conn, pstmt, rs);
        }
    }

    // Get all students
    public List<Student> getAllStudents() {
        String sql = "SELECT * FROM students";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        List<Student> students = new ArrayList<>();

        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getString("id"));
                student.setName(rs.getString("name"));
                student.setCourse(rs.getString("course"));
                student.setGrade(rs.getDouble("grade"));

                students.add(student);
            }

            return students;

        } catch (SQLException e) {
            System.err.println("Error getting all students: " + e.getMessage());
            return students;
        } finally {
            dbConnection.closeResources(conn, pstmt, rs);
        }
    }

    // Check if a student ID already exists
    public boolean studentExists(String id) {
        String sql = "SELECT COUNT(*) FROM students WHERE id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = dbConnection.getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("Error checking if student exists: " + e.getMessage());
            return false;
        } finally {
            dbConnection.closeResources(conn, pstmt, rs);
        }
    }
}