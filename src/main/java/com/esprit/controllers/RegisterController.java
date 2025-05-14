package com.esprit.controllers;

import com.esprit.models.Utilisateur;
import com.esprit.services.UtilisateurService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class RegisterController implements Initializable {

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfPrenom;

    @FXML
    private TextField tfEmail;

    @FXML
    private PasswordField tfPassword;

    @FXML
    private TextField tfTelephone;

    @FXML
    private ComboBox<String> cbSexe;

    @FXML
    private ComboBox<String> cbRole;

    @FXML
    private Button btnRegister;

    @FXML
    private Hyperlink linkLogin;

    private UtilisateurService utilisateurService = new UtilisateurService();
    private Utilisateur userToEdit;
    private boolean isEditMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> sexeOptions = FXCollections.observableArrayList("homme", "femme");
        ObservableList<String> roleOptions = FXCollections.observableArrayList("Member", "Coach");
        
        cbSexe.setItems(sexeOptions);
        cbRole.setItems(roleOptions);

        // Add input validation listeners
        addValidationListeners();
    }

    private void addValidationListeners() {
        // Email validation
        tfEmail.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!isValidEmail(newVal)) {
                tfEmail.setStyle("-fx-border-color: #ff0000;");
            } else {
                tfEmail.setStyle("");
            }
        });

        // Password validation (minimum 6 characters)
        tfPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() < 6) {
                tfPassword.setStyle("-fx-border-color: #ff0000;");
            } else {
                tfPassword.setStyle("");
            }
        });

        // Telephone validation (Tunisian format)
        tfTelephone.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!isValidPhoneNumber(newVal)) {
                tfTelephone.setStyle("-fx-border-color: #ff0000;");
            } else {
                tfTelephone.setStyle("");
            }
        });
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isValidPhoneNumber(String phone) {
        String phoneRegex = "^[0-9]{8}$";
        return Pattern.matches(phoneRegex, phone);
    }

    public void setUserToEdit(Utilisateur user) {
        this.userToEdit = user;
        this.isEditMode = true;
        
        // Fill the form with user data
        tfNom.setText(user.getNom());
        tfPrenom.setText(user.getPrenom());
        tfEmail.setText(user.getEmail());
        tfPassword.setText(user.getPassword());
        tfTelephone.setText(user.getTelephone());
        cbSexe.setValue(user.getSexe());
        cbRole.setValue(user.getRole() == 0 ? "Member" : "Coach");
    }

    @FXML
    void handleRegister(ActionEvent event) {
        String nom = tfNom.getText();
        String prenom = tfPrenom.getText();
        String email = tfEmail.getText();
        String password = tfPassword.getText();
        String telephone = tfTelephone.getText();
        String sexe = cbSexe.getValue();
        String roleStr = cbRole.getValue();

        // Validate all fields
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || 
            telephone.isEmpty() || sexe == null || roleStr == null) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            showAlert("Error", "Please enter a valid email address");
            return;
        }

        // Validate password length
        if (password.length() < 6) {
            showAlert("Error", "Password must be at least 6 characters long");
            return;
        }

        // Validate phone number format
        if (!isValidPhoneNumber(telephone)) {
            showAlert("Error", "Please enter a valid Tunisian phone number (8 digits)");
            return;
        }

        // Check if email already exists
        if (utilisateurService.rechercher().stream().anyMatch(u -> u.getEmail().equals(email))) {
            showAlert("Error", "Email already exists");
            return;
        }

        int role = roleStr.equals("Coach") ? 1 : 0;
        String dateInscription = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        if (isEditMode) {
            // Update existing user
            userToEdit.setNom(nom);
            userToEdit.setPrenom(prenom);
            userToEdit.setEmail(email);
            userToEdit.setPassword(password);
            userToEdit.setTelephone(telephone);
            userToEdit.setSexe(sexe);
            userToEdit.setRole(role);
            utilisateurService.modifier(userToEdit);
            showAlert("Success", "User updated successfully!");
        } else {
            // Create new user
            Utilisateur newUser = new Utilisateur(nom, prenom, sexe, email, telephone, role, password, dateInscription, null);
            utilisateurService.ajouter(newUser);
            showAlert("Success", "Registration successful!");
        }

        // Redirect to login
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage stage = (Stage) btnRegister.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleLoginLink(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage stage = (Stage) linkLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
} 