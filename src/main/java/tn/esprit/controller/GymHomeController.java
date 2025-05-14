package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class GymHomeController {

    @FXML
    private void navigateToCours(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/cours.fxml")); // Corrected path
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gym Courses");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToEmploi(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/emploi.fxml")); // Corrected path
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gym Schedules");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
