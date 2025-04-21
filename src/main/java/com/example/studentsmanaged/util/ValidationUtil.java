package com.example.studentsmanaged.util;


public class ValidationUtil {

    /**
     * Validates if a string is not null or empty
     *
     * @param value the string value to validate
     * @param fieldName the name of the field (for error message)
     * @throws StudentManagementException if validation fails
     */
    public static void validateRequiredString(String value, String fieldName) throws StudentManagementException {
        if (value == null || value.trim().isEmpty()) {
            throw StudentManagementException.validationError(fieldName + " cannot be empty");
        }
    }

    /**
     * Validates if a student ID follows the required format (alphanumeric, length between 3-10)
     *
     * @param id the student ID to validate
     * @throws StudentManagementException if validation fails
     */
    public static void validateStudentId(String id) throws StudentManagementException {
        validateRequiredString(id, "Student ID");

        // Check if the ID contains only alphanumeric characters
        if (!id.matches("^[a-zA-Z0-9]+$")) {
            throw StudentManagementException.validationError("Student ID must contain only letters and numbers");
        }

        // Check if the ID length is between 3 and 10 characters
        if (id.length() < 3 || id.length() > 10) {
            throw StudentManagementException.validationError("Student ID must be between 3 and 10 characters");
        }
    }

    /**
     * Validates if a name is valid (contains only letters, spaces, and hyphens)
     *
     * @param name the name to validate
     * @throws StudentManagementException if validation fails
     */
    public static void validateName(String name) throws StudentManagementException {
        validateRequiredString(name, "Name");

        // Check if the name contains only allowed characters
        if (!name.matches("^[a-zA-Z\\s-]+$")) {
            throw StudentManagementException.validationError("Name must contain only letters, spaces, and hyphens");
        }

        // Check if the name length is valid
        if (name.length() < 2 || name.length() > 50) {
            throw StudentManagementException.validationError("Name must be between 2 and 50 characters");
        }
    }

    /**
     * Validates if a course name is valid
     *
     * @param course the course name to validate
     * @throws StudentManagementException if validation fails
     */
    public static void validateCourse(String course) throws StudentManagementException {
        validateRequiredString(course, "Course");

        // Check if the course length is valid
        if (course.length() < 2 || course.length() > 50) {
            throw StudentManagementException.validationError("Course name must be between 2 and 50 characters");
        }
    }

    /**
     * Validates if a grade is within the valid range (0-100)
     *
     * @param gradeStr the grade as a string
     * @return the validated grade as a double
     * @throws StudentManagementException if validation fails
     */
    public static double validateGrade(String gradeStr) throws StudentManagementException {
        validateRequiredString(gradeStr, "Grade");

        try {
            double grade = Double.parseDouble(gradeStr);
            return validateGrade(grade);
        } catch (NumberFormatException e) {
            throw StudentManagementException.validationError("Grade must be a number");
        }
    }

    /**
     * Validates if a grade is within the valid range (0-100)
     *
     * @param grade the grade to validate
     * @return the validated grade
     * @throws StudentManagementException if validation fails
     */
    public static double validateGrade(double grade) throws StudentManagementException {
        if (grade < 0 || grade > 100) {
            throw StudentManagementException.validationError("Grade must be between 0 and 100");
        }
        return grade;
    }
}