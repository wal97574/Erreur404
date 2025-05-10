package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {
    @FXML private StackPane contentPane;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Label statusLabel;

    @FXML
    private void initialize() {
        // Initialize user info
        userNameLabel.setText("Current User");
        userRoleLabel.setText("Role");
        statusLabel.setText("System ready");
    }

    @FXML
    private void handleDashboardNavigation() {
        // Clear content or load dashboard
        contentPane.getChildren().clear();
        statusLabel.setText("Dashboard loaded");
    }

    @FXML
    private void handleCoursesNavigation() {
        loadView("/cours.fxml");
        statusLabel.setText("Courses view loaded");
    }

    @FXML
    private void handleScheduleNavigation() {
        loadView("/emploi.fxml");
        statusLabel.setText("Schedule view loaded");
    }

    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error loading view: " + e.getMessage());
        }
    }
}
