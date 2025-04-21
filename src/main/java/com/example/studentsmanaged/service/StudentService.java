package com.example.studentsmanaged.service;

import com.example.studentsmanaged.database.StudentDAO;
import com.example.studentsmanaged.models.Student;
import com.example.studentsmanaged.util.FileHandler;
import com.example.studentsmanaged.util.StudentManagementException;
import com.example.studentsmanaged.util.ThreadUtil;
import com.example.studentsmanaged.util.ValidationUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class StudentService {
    private final StudentDAO studentDAO;
    private final ExecutorService executorService;

    // Constructor
    public StudentService() {
        this.studentDAO = new StudentDAO();
        // Use the thread utility for file operations
        this.executorService = ThreadUtil.getFileExecutor();
    }

    // Validate student data
    private void validateStudent(Student student) throws StudentManagementException {
        if (student == null) {
            throw StudentManagementException.validationError("Student cannot be null");
        }

        // Use the validation utility methods
        ValidationUtil.validateStudentId(student.getId());
        ValidationUtil.validateName(student.getName());
        ValidationUtil.validateCourse(student.getCourse());
        ValidationUtil.validateGrade(student.getGrade());
    }

    // Add a student
    public void addStudent(Student student) throws StudentManagementException {
        validateStudent(student);

        if (studentDAO.studentExists(student.getId())) {
            throw StudentManagementException.duplicateIdError(student.getId());
        }

        boolean success = studentDAO.addStudent(student);

        if (!success) {
            throw StudentManagementException.databaseError("Failed to add student");
        }
    }

    // Update a student
    public void updateStudent(Student student) throws StudentManagementException {
        validateStudent(student);

        if (!studentDAO.studentExists(student.getId())) {
            throw StudentManagementException.notFoundError(student.getId());
        }

        boolean success = studentDAO.updateStudent(student);

        if (!success) {
            throw StudentManagementException.databaseError("Failed to update student");
        }
    }

    // Delete a student
    public void deleteStudent(String id) throws StudentManagementException {
        if (id == null || id.trim().isEmpty()) {
            throw StudentManagementException.validationError("Student ID cannot be empty");
        }

        if (!studentDAO.studentExists(id)) {
            throw StudentManagementException.notFoundError(id);
        }

        boolean success = studentDAO.deleteStudent(id);

        if (!success) {
            throw StudentManagementException.databaseError("Failed to delete student");
        }
    }

    // Get a student by ID
    public Student getStudentById(String id) throws StudentManagementException {
        if (id == null || id.trim().isEmpty()) {
            throw StudentManagementException.validationError("Student ID cannot be empty");
        }

        Student student = studentDAO.getStudentById(id);

        if (student == null) {
            throw StudentManagementException.notFoundError(id);
        }

        return student;
    }

    // Get all students
    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    // Export students to a CSV file asynchronously
    public Future<Boolean> exportToCSVAsync(String filePath, Consumer<Boolean> callback) {
        return executorService.submit(() -> {
            boolean result = FileHandler.exportToCSV(getAllStudents(), filePath);
            if (callback != null) {
                callback.accept(result);
            }
            return result;
        });
    }

    // Import students from a CSV file asynchronously
    public Future<List<Student>> importFromCSVAsync(String filePath, Consumer<List<Student>> callback) {
        return executorService.submit(() -> {
            List<Student> students = FileHandler.importFromCSV(filePath);
            if (callback != null) {
                callback.accept(students);
            }
            return students;
        });
    }

    // Shutdown the executor service
    public void shutdown() {
        executorService.shutdown();
    }
}