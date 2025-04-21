package com.example.studentsmanaged;

import com.example.studentsmanaged.controllers.StudentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class StudentsManagedApp extends Application {

    private StudentController controller;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Print for debugging
            System.out.println("Looking for FXML file...");
            URL fxmlLocation = getClass().getResource("/com/example/studentsmanaged/StudentView.fxml");
            System.out.println("FXML Location: " + fxmlLocation);
            
            if (fxmlLocation == null) {
                fxmlLocation = getClass().getResource("StudentView.fxml");
                System.out.println("Alternative FXML Location: " + fxmlLocation);
            }
            
            if (fxmlLocation == null) {
                throw new IllegalStateException("Cannot find the FXML file. Make sure the file exists in the resources directory.");
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            
            // Load the FXML file
            Parent root = loader.load();

            // Get the controller
            controller = loader.getController();
            System.out.println("Controller loaded: " + (controller != null));

            // Set up the scene
            Scene scene = new Scene(root);
            
            // Try different possible CSS locations
            URL cssLocation = getClass().getResource("/com/example/studentsmanaged/css/styles.css");
            if (cssLocation == null) {
                cssLocation = getClass().getResource("styles.css");
            }
            
            if (cssLocation != null) {
                scene.getStylesheets().add(cssLocation.toExternalForm());
            } else {
                System.out.println("Warning: CSS file not found");
            }

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
            System.out.println("Error in start method:");
            e.printStackTrace();
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        if (controller != null) {
            controller.cleanup();
        }
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}