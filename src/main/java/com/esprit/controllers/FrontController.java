package com.esprit.controllers;

import com.esprit.models.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class FrontController {

    @FXML
    private Text txtWelcome;

    @FXML
    private Text txtRole;

    @FXML
    private Button btnLogout;

    private Utilisateur currentUser;

    public void setUser(Utilisateur user) {
        this.currentUser = user;
        txtWelcome.setText("Welcome, " + user.getPrenom() + " " + user.getNom());
        txtRole.setText("Role: " + (user.getRole() == 1 ? "Coach" : "Member"));
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGetStarted(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Get Started");
        alert.setHeaderText(null);
        alert.setContentText("Welcome to SmartGym! (You can customize this action.)");
        alert.showAndWait();
    }
} 