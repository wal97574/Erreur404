package tn.esprit.controller;

import com.esprit.models.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class GymHomeController {

    private Utilisateur user;

    public void setUser(Utilisateur user) {
        this.user = user;
        // Optionally, update the UI with user details if needed
    }

    public Utilisateur getUser() {
        return user;
    }

    @FXML
    private void navigateToCours(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/cours.fxml"));
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
            Parent root = FXMLLoader.load(getClass().getResource("/emploi.fxml"));
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gym Schedules");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToAbonnement(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/abonnement.fxml"));
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gym Abonnement");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void navigateToSalles(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Salles Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
