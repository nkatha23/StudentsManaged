package com.example.studentsmanaged.util;

// Custom exception class for the Student Management System
public class StudentManagementException extends Exception {

    // Error types
    public enum ErrorType {
        DATABASE_ERROR,
        VALIDATION_ERROR,
        FILE_ERROR,
        DUPLICATE_ID,
        NOT_FOUND,
        UNKNOWN_ERROR
    }

    private final ErrorType errorType;

    // Constructor with error message
    public StudentManagementException(String message) {
        super(message);
        this.errorType = ErrorType.UNKNOWN_ERROR;
    }

    // Constructor with error message and error type
    public StudentManagementException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    // Constructor with error message, cause, and error type
    public StudentManagementException(String message, Throwable cause, ErrorType errorType) {
        super(message, cause);
        this.errorType = errorType;
    }

    // Get the error type
    public ErrorType getErrorType() {
        return errorType;
    }

    // Static factory methods for common errors
    public static StudentManagementException databaseError(String message) {
        return new StudentManagementException("Database error: " + message, ErrorType.DATABASE_ERROR);
    }

    public static StudentManagementException validationError(String message) {
        return new StudentManagementException("Validation error: " + message, ErrorType.VALIDATION_ERROR);
    }

    public static StudentManagementException fileError(String message) {
        return new StudentManagementException("File error: " + message, ErrorType.FILE_ERROR);
    }

    public static StudentManagementException duplicateIdError(String id) {
        return new StudentManagementException("Student ID already exists: " + id, ErrorType.DUPLICATE_ID);
    }

    public static StudentManagementException notFoundError(String id) {
        return new StudentManagementException("Student not found with ID: " + id, ErrorType.NOT_FOUND);
    }
}
