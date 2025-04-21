package com.example.studentsmanaged;

import com.example.studentsmanaged.controllers.StudentController;
import com.example.studentsmanaged.util.ThreadUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;

public class StudentsManagedApp extends Application {

    private StudentController controller;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/studentsmanaged/StudentView.fxml"));
            Parent root = loader.load();

            // Get the controller
            controller = loader.getController();

            // Set up the scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/example/studentsmanaged/css/styles.css")).toExternalForm());

            // Set up the stage
            primaryStage.setTitle("Student Management System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);

            // Show the stage
            primaryStage.show();

            // Set up close request handler
            primaryStage.setOnCloseRequest(e -> {
                if (controller != null) {
                    controller.cleanup();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (controller != null) {
            controller.cleanup();
        }
        // Shutdown all thread pools
        ThreadUtil.shutdownAll();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}