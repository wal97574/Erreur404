package com.esprit.controllers;

import com.esprit.models.Utilisateur;
import com.esprit.services.UtilisateurService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class UserFormController implements Initializable {

    @FXML
    private Text txtTitle;

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
    private TextField tfSalaire;

    @FXML
    private Button btnSave;

    private UtilisateurService utilisateurService = new UtilisateurService();
    private Utilisateur userToEdit;
    private boolean isEditMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> sexeOptions = FXCollections.observableArrayList("homme", "femme");
        ObservableList<String> roleOptions = FXCollections.observableArrayList("Member", "Coach");
        
        cbSexe.setItems(sexeOptions);
        cbRole.setItems(roleOptions);

        // Add listener to role ComboBox to show/hide salary field
        cbRole.valueProperty().addListener((obs, oldVal, newVal) -> {
            tfSalaire.setVisible("Coach".equals(newVal));
        });

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

        // Salary validation (only for Coach)
        tfSalaire.textProperty().addListener((obs, oldVal, newVal) -> {
            if (tfSalaire.isVisible() && !newVal.isEmpty() && !isValidSalary(newVal)) {
                tfSalaire.setStyle("-fx-border-color: #ff0000;");
            } else {
                tfSalaire.setStyle("");
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

    private boolean isValidSalary(String salary) {
        try {
            double value = Double.parseDouble(salary);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void setUserToEdit(Utilisateur user) {
        this.userToEdit = user;
        this.isEditMode = true;
        txtTitle.setText("Edit User");
        
        // Fill the form with user data
        tfNom.setText(user.getNom());
        tfPrenom.setText(user.getPrenom());
        tfEmail.setText(user.getEmail());
        tfPassword.setText(user.getPassword());
        tfTelephone.setText(user.getTelephone());
        cbSexe.setValue(user.getSexe());
        cbRole.setValue(user.getRole() == 0 ? "Member" : "Coach");
        if (user.getRole() == 1) { // Coach
            tfSalaire.setText(user.getSalaire() != null ? user.getSalaire() : "");
            tfSalaire.setVisible(true);
        } else {
            tfSalaire.setText("");
            tfSalaire.setVisible(false);
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        String nom = tfNom.getText();
        String prenom = tfPrenom.getText();
        String email = tfEmail.getText();
        String password = tfPassword.getText();
        String telephone = tfTelephone.getText();
        String sexe = cbSexe.getValue();
        String roleStr = cbRole.getValue();
        String salaire = tfSalaire.getText();

        // Validate all fields
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || 
            telephone.isEmpty() || sexe == null || roleStr == null) {
            showAlert("Error", "Please fill in all required fields");
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

        // Validate salary for Coach
        if ("Coach".equals(roleStr)) {
            if (!tfSalaire.isVisible() || salaire == null || salaire.trim().isEmpty() || !isValidSalary(salaire)) {
                showAlert("Error", "Please enter a valid salary amount");
                return;
            }
        }

        // Check if email already exists (only for new users or when email is changed)
        if (!isEditMode || !email.equals(userToEdit.getEmail())) {
            if (utilisateurService.rechercher().stream().anyMatch(u -> u.getEmail().equals(email))) {
                showAlert("Error", "Email already exists");
                return;
            }
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
            if (role == 1) { // Coach
                userToEdit.setSalaire(salaire);
            } else {
                userToEdit.setSalaire(null);
            }
            utilisateurService.modifier(userToEdit);
            showAlert("Success", "User updated successfully!");
        } else {
            // Create new user
            Utilisateur newUser = new Utilisateur(nom, prenom, sexe, email, telephone, role, password, dateInscription, 
                role == 1 ? salaire : null);
            utilisateurService.ajouter(newUser);
            showAlert("Success", "User added successfully!");
        }

        // Close the window
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleCancel(ActionEvent event) {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
} 