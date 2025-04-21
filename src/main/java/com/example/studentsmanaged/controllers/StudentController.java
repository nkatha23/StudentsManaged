package com.example.studentsmanaged.controllers;


import com.example.studentsmanaged.models.Student;
import com.example.studentsmanaged.service.StudentService;
import com.example.studentsmanaged.util.StudentManagementException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentController {

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtCourse;
    @FXML
    private TextField txtGrade;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnExport;
    @FXML
    private Button btnImport;
    @FXML
    private TableView<Student> tableStudents;
    @FXML
    private TableColumn<Student, String> colId;
    @FXML
    private TableColumn<Student, String> colName;
    @FXML
    private TableColumn<Student, String> colCourse;
    @FXML
    private TableColumn<Student, Double> colGrade;

    private final StudentService studentService;
    private final ObservableList<Student> studentList;
    private final ExecutorService executorService;

    // Constructor
    public StudentController() {
        this.studentService = new StudentService();
        this.studentList = FXCollections.observableArrayList();
        this.executorService = Executors.newFixedThreadPool(2);
    }

    @FXML
    public void initialize() {
        // Set up table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCourse.setCellValueFactory(new PropertyValueFactory<>("course"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        // Set item list for table
        tableStudents.setItems(studentList);

        // Load students from database
        loadStudents();

        // Set up table selection listener
        tableStudents.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtId.setText(newSelection.getId());
                txtName.setText(newSelection.getName());
                txtCourse.setText(newSelection.getCourse());
                txtGrade.setText(String.valueOf(newSelection.getGrade()));
                txtId.setDisable(true); // Disable ID field when editing
                btnAdd.setDisable(true);
                btnUpdate.setDisable(false);
                btnDelete.setDisable(false);
            }
        });

        // Set button handlers
        btnClear.setOnAction(e -> clearForm());
        btnAdd.setOnAction(e -> addStudent());
        btnUpdate.setOnAction(e -> updateStudent());
        btnDelete.setOnAction(e -> deleteStudent());
        btnExport.setOnAction(e -> exportToCSV());
        btnImport.setOnAction(e -> importFromCSV());
    }

    // Load students from database
    private void loadStudents() {
        Task<List<Student>> task = new Task<>() {
            @Override
            protected List<Student> call() {
                return studentService.getAllStudents();
            }
        };

        task.setOnSucceeded(e -> {
            studentList.clear();
            studentList.addAll(task.getValue());
        });

        task.setOnFailed(e -> {
            showAlert(AlertType.ERROR, "Error", "Failed to load students", task.getException().getMessage());
        });

        executorService.submit(task);
    }

    // Clear form fields
    private void clearForm() {
        txtId.clear();
        txtName.clear();
        txtCourse.clear();
        txtGrade.clear();
        txtId.setDisable(false);
        btnAdd.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        tableStudents.getSelectionModel().clearSelection();
    }

    // Add a student
    private void addStudent() {
        try {
            // Create a student object from form fields
            Student student = new Student();
            student.setId(txtId.getText().trim());
            student.setName(txtName.getText().trim());
            student.setCourse(txtCourse.getText().trim());

            try {
                student.setGrade(Double.parseDouble(txtGrade.getText().trim()));
            } catch (NumberFormatException e) {
                showAlert(AlertType.ERROR, "Validation Error", "Invalid Grade", "Grade must be a number");
                return;
            }

            // Add student to database
            studentService.addStudent(student);

            // Add student to table
            studentList.add(student);

            // Clear form
            clearForm();

            showAlert(AlertType.INFORMATION, "Success", "Student Added", "Student was added successfully");

        } catch (StudentManagementException e) {
            showAlert(AlertType.ERROR, "Error", "Failed to add student", e.getMessage());
        }
    }

    // Update a student
    private void updateStudent() {
        try {
            // Get selected student
            Student selectedStudent = tableStudents.getSelectionModel().getSelectedItem();

            if (selectedStudent == null) {
                showAlert(AlertType.ERROR, "Error", "No Student Selected", "Please select a student to update");
                return;
            }

            // Update student object from form fields
            selectedStudent.setName(txtName.getText().trim());
            selectedStudent.setCourse(txtCourse.getText().trim());

            try {
                selectedStudent.setGrade(Double.parseDouble(txtGrade.getText().trim()));
            } catch (NumberFormatException e) {
                showAlert(AlertType.ERROR, "Validation Error", "Invalid Grade", "Grade must be a number");
                return;
            }

            // Update student in database
            studentService.updateStudent(selectedStudent);

            // Refresh table
            tableStudents.refresh();

            // Clear form
            clearForm();

            showAlert(AlertType.INFORMATION, "Success", "Student Updated", "Student was updated successfully");

        } catch (StudentManagementException e) {
            showAlert(AlertType.ERROR, "Error", "Failed to update student", e.getMessage());
        }
    }

    // Delete a student
    private void deleteStudent() {
        try {
            // Get selected student
            Student selectedStudent = tableStudents.getSelectionModel().getSelectedItem();

            if (selectedStudent == null) {
                showAlert(AlertType.ERROR, "Error", "No Student Selected", "Please select a student to delete");
                return;
            }

            // Delete student from database
            studentService.deleteStudent(selectedStudent.getId());

            // Remove student from table
            studentList.remove(selectedStudent);

            // Clear form
            clearForm();

            showAlert(AlertType.INFORMATION, "Success", "Student Deleted", "Student was deleted successfully");

        } catch (StudentManagementException e) {
            showAlert(AlertType.ERROR, "Error", "Failed to delete student", e.getMessage());
        }
    }

    // Export to CSV
    private void exportToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Students to CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(btnExport.getScene().getWindow());

        if (file != null) {
            studentService.exportToCSVAsync(file.getAbsolutePath(), success -> {
                if (success) {
                    showAlertLater(AlertType.INFORMATION, "Success", "Export Successful",
                            "Students were exported to CSV successfully");
                } else {
                    showAlertLater(AlertType.ERROR, "Error", "Export Failed",
                            "Failed to export students to CSV");
                }
            });
        }
    }

    // Import from CSV
    private void importFromCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Students from CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showOpenDialog(btnImport.getScene().getWindow());

        if (file != null) {
            studentService.importFromCSVAsync(file.getAbsolutePath(), students -> {
                javafx.application.Platform.runLater(() -> {
                    if (students != null && !students.isEmpty()) {
                        // Add imported students to database
                        for (Student student : students) {
                            try {
                                if (!studentService.getAllStudents().stream()
                                        .anyMatch(s -> s.getId().equals(student.getId()))) {
                                    studentService.addStudent(student);
                                    studentList.add(student);
                                }
                            } catch (StudentManagementException e) {
                                System.err.println("Error importing student: " + e.getMessage());
                            }
                        }

                        showAlert(AlertType.INFORMATION, "Success", "Import Successful",
                                "Imported " + students.size() + " students");
                    } else {
                        showAlert(AlertType.WARNING, "Warning", "Import Result",
                                "No students were imported");
                    }
                });
            });
        }
    }

    // Show alert on JavaFX application thread
    private void showAlertLater(AlertType type, String title, String header, String content) {
        javafx.application.Platform.runLater(() -> showAlert(type, title, header, content));
    }

    // Show alert
    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Clean up resources
    public void cleanup() {
        studentService.shutdown();
        executorService.shutdown();
    }
}