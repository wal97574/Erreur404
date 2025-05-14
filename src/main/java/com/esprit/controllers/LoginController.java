package com.esprit.controllers;

import com.esprit.models.Utilisateur;
import com.esprit.services.UtilisateurService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class LoginController {

    @FXML
    private TextField tfEmail;

    @FXML
    private TextField tfPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Hyperlink linkRegister;

    @FXML
    private Hyperlink linkForgotPassword;

    private UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    void handleLogin(ActionEvent event) {
        String email = tfEmail.getText();
        String password = tfPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        Utilisateur user = utilisateurService.rechercher().stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (user != null) {
            try {
                FXMLLoader loader;
                if (user.getRole() == 3) { // Admin
                    loader = new FXMLLoader(getClass().getResource("/BackOffice.fxml"));
                } else {
                    loader = new FXMLLoader(getClass().getResource("/Front.fxml"));
                }
                
                Parent root = loader.load();
                
                if (user.getRole() != 3) { // If not admin, set the user in FrontController
                    FrontController frontController = loader.getController();
                    frontController.setUser(user);
                }
                
                Stage stage = (Stage) btnLogin.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Invalid email or password");
        }
    }

    @FXML
    void handleRegisterLink(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Register.fxml"));
            Stage stage = (Stage) linkRegister.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleForgotPassword(ActionEvent event) {
        String email = tfEmail.getText();
        
        if (email.isEmpty()) {
            showAlert("Error", "Please enter your email address");
            return;
        }

        Utilisateur user = utilisateurService.rechercher().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);

        if (user != null) {
            // Generate a new random password
            String newPassword = generateRandomPassword();
            
            // Update user's password in database
            user.setPassword(newPassword);
            utilisateurService.modifier(user);
            
            // Send email with new password
            if (sendPasswordResetEmail(email, newPassword)) {
                showSuccessAlert("Success", "A new password has been sent to your email address");
            } else {
                showAlert("Error", "Failed to send password reset email");
            }
        } else {
            showAlert("Error", "No account found with this email address");
        }
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }

    private boolean sendPasswordResetEmail(String toEmail, String newPassword) {
        final String username = "ghofranhajjej@gmail.com";
        final String password = "kdyz gptg rsva oprz";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Password Reset");
            message.setText("Your new password is: " + newPassword + "\n\nPlease login with this password and change it immediately.");

            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
} 