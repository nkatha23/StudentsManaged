package com.example.studentsmanaged.util;

import com.example.studentsmanaged.models.Student;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    // Export students to a CSV file
    public static boolean exportToCSV(List<Student> students, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write header
            writer.write("ID,Name,Course,Grade");
            writer.newLine();

            // Write data
            for (Student student : students) {
                writer.write(String.format("%s,%s,%s,%.2f",
                        student.getId(),
                        student.getName(),
                        student.getCourse(),
                        student.getGrade()));
                writer.newLine();
            }

            return true;
        } catch (IOException e) {
            System.err.println("Error exporting to CSV: " + e.getMessage());
            return false;
        }
    }

    // Import students from a CSV file
    public static List<Student> importFromCSV(String filePath) {
        List<Student> students = new ArrayList<>();

        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("File does not exist: " + filePath);
            return students;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            // Skip header
            String line = reader.readLine();

            // Read data
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");

                if (data.length == 4) {
                    try {
                        Student student = new Student();
                        student.setId(data[0]);
                        student.setName(data[1]);
                        student.setCourse(data[2]);
                        student.setGrade(Double.parseDouble(data[3]));

                        students.add(student);
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing grade: " + e.getMessage());
                    }
                }
            }

            return students;
        } catch (IOException e) {
            System.err.println("Error importing from CSV: " + e.getMessage());
            return students;
        }
    }
}