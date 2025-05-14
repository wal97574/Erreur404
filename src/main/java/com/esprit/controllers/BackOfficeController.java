package com.esprit.controllers;

import com.esprit.models.Utilisateur;
import com.esprit.services.UtilisateurService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class BackOfficeController {

    @FXML
    private TableView<Utilisateur> tableView;

    @FXML
    private TableColumn<Utilisateur, String> colNom;

    @FXML
    private TableColumn<Utilisateur, String> colPrenom;

    @FXML
    private TableColumn<Utilisateur, String> colSexe;

    @FXML
    private TableColumn<Utilisateur, String> colEmail;

    @FXML
    private TableColumn<Utilisateur, String> colTelephone;

    @FXML
    private TableColumn<Utilisateur, String> colRole;

    @FXML
    private TableColumn<Utilisateur, String> colDateInscription;

    @FXML
    private TableColumn<Utilisateur, String> colSalaire;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;

    @FXML
    private Button btnLogout;

    private UtilisateurService utilisateurService = new UtilisateurService();
    private ObservableList<Utilisateur> utilisateurs = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set up table columns
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colSexe.setCellValueFactory(new PropertyValueFactory<>("sexe"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colRole.setCellValueFactory(cellData -> {
            int role = cellData.getValue().getRole();
            String roleStr;
            switch (role) {
                case 1: roleStr = "Coach"; break;
                case 3: roleStr = "Admin"; break;
                default: roleStr = "Membre";
            }
            return new javafx.beans.property.SimpleStringProperty(roleStr);
        });
        colDateInscription.setCellValueFactory(new PropertyValueFactory<>("dateInscription"));
        colSalaire.setCellValueFactory(new PropertyValueFactory<>("salaire"));

        // Load data
        refreshTable();
    }

    private void refreshTable() {
        utilisateurs.clear();
        utilisateurs.addAll(utilisateurService.rechercher());
        tableView.setItems(utilisateurs);
    }

    @FXML
    void handleAjouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserForm.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add User");
            stage.showAndWait();
            refreshTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleModifier(ActionEvent event) {
        Utilisateur selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserForm.fxml"));
                Parent root = loader.load();
                UserFormController controller = loader.getController();
                controller.setUserToEdit(selectedUser);
                
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Edit User");
                stage.showAndWait();
                refreshTable();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Please select a user to edit");
        }
    }

    @FXML
    void handleSupprimer(ActionEvent event) {
        Utilisateur selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm Delete");
            confirmDialog.setContentText("Are you sure you want to delete this user?");
            confirmDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    utilisateurService.supprimer(selectedUser);
                    refreshTable();
                }
            });
        } else {
            showAlert("Error", "Please select a user to delete");
        }
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
} 