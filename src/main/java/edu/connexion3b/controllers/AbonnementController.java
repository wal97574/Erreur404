package edu.connexion3b.controllers;

import edu.connexion3b.entities.Abonnement;
import edu.connexion3b.services.AbonnementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AbonnementController implements Initializable {
    @FXML private TextField nomField;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField dureeField;
    @FXML private TextField prixField;
    @FXML private RadioButton etudiantRadio;
    @FXML private RadioButton nonEtudiantRadio;
    @FXML private TableView<Abonnement> abonnementTable;
    @FXML private TableColumn<Abonnement, Integer> idCol;
    @FXML private TableColumn<Abonnement, String> nomCol;
    @FXML private TableColumn<Abonnement, String> typeCol;
    @FXML private TableColumn<Abonnement, String> dureeCol;
    @FXML private TableColumn<Abonnement, Float> prixCol;
    @FXML private Button ajouterBtn;
    @FXML private Button modifierBtn;
    @FXML private Button supprimerBtn;
    @FXML private Button clearBtn;
    @FXML private Button payerBtn; // Nouveau bouton de paiement
    
    private AbonnementService service;
    private ObservableList<Abonnement> abonnementList;
    private Abonnement selectedAbonnement;
    private ToggleGroup statutGroup;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        service = new AbonnementService();
        
        // Initialiser la combobox
        typeCombo.setItems(FXCollections.observableArrayList("VIP", "Premium", "Basic"));
        
        // Configurer les colonnes
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        dureeCol.setCellValueFactory(new PropertyValueFactory<>("duree_jours"));
        prixCol.setCellValueFactory(new PropertyValueFactory<>("prix"));
        
        // Configurer les RadioButtons
        statutGroup = new ToggleGroup();
        etudiantRadio.setToggleGroup(statutGroup);
        nonEtudiantRadio.setToggleGroup(statutGroup);
        nonEtudiantRadio.setSelected(true);
        
        // Charger les données
        refreshTable();
        
        // Configurer les boutons
        ajouterBtn.setOnAction(event -> ajouterAbonnement());
        modifierBtn.setOnAction(event -> modifierAbonnement());
        supprimerBtn.setOnAction(event -> supprimerAbonnement());
        clearBtn.setOnAction(event -> clearFields());
        payerBtn.setOnAction(event -> payerAbonnement()); // Action pour le bouton de paiement
        
        // Ajouter les listeners pour le calcul automatique du prix
        typeCombo.setOnAction(event -> calculerPrix());
        dureeField.textProperty().addListener((observable, oldValue, newValue) -> calculerPrix());
        statutGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> calculerPrix());
        
        // Sélection dans la table
        abonnementTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedAbonnement = newSelection;
                nomField.setText(selectedAbonnement.getNom());
                typeCombo.setValue(selectedAbonnement.getType());
                dureeField.setText(selectedAbonnement.getDuree_jours());
                prixField.setText(String.valueOf(selectedAbonnement.getPrix()));
                
                modifierBtn.setDisable(false);
                supprimerBtn.setDisable(false);
                payerBtn.setDisable(false); // Activer le bouton de paiement
            }
        });
        
        // Désactiver les boutons au démarrage
        modifierBtn.setDisable(true);
        supprimerBtn.setDisable(true);
        payerBtn.setDisable(true); // Désactiver le bouton de paiement
    }
    
    private void calculerPrix() {
        String type = typeCombo.getValue();
        String dureeText = dureeField.getText();
        
        if (type == null || dureeText.isEmpty()) {
            return;
        }
        
        try {
            int duree = Integer.parseInt(dureeText);
            float tarifJournalier = 0;
            
            // Déterminer le tarif journalier selon le type
            switch (type) {
                case "VIP":
                    tarifJournalier = 6;
                    break;
                case "Premium":
                    tarifJournalier = 5;
                    break;
                case "Basic":
                    tarifJournalier = 4;
                    break;
            }
            
            // Calculer le prix total
            float prixTotal = tarifJournalier * duree;
            
            // Appliquer la réduction étudiant si nécessaire
            if (etudiantRadio.isSelected()) {
                prixTotal = prixTotal * 0.8f; // Réduction de 20%
            }
            
            // Afficher le prix calculé
            prixField.setText(String.format("%.2f", prixTotal));
            
        } catch (NumberFormatException e) {
            // Ne rien faire si la durée n'est pas un nombre valide
        }
    }
    
    private void refreshTable() {
        abonnementList = FXCollections.observableArrayList(service.afficherTous());
        abonnementTable.setItems(abonnementList);
    }
    
    private void ajouterAbonnement() {
        try {
            if (!validateFields()) return;
            
            String nom = nomField.getText();
            String type = typeCombo.getValue();
            String duree = dureeField.getText();
            float prix = Float.parseFloat(prixField.getText());
            
            Abonnement abonnement = new Abonnement(nom, type, duree, prix);
            service.ajouter(abonnement);
            
            clearFields();
            refreshTable();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Abonnement ajouté avec succès");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le prix doit être un nombre");
        }
    }
    
    private void modifierAbonnement() {
        try {
            if (!validateFields() || selectedAbonnement == null) return;
            
            selectedAbonnement.setNom(nomField.getText());
            selectedAbonnement.setType(typeCombo.getValue());
            selectedAbonnement.setDuree_jours(dureeField.getText());
            selectedAbonnement.setPrix(Float.parseFloat(prixField.getText()));
            
            service.modifier(selectedAbonnement);
            
            clearFields();
            refreshTable();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Abonnement modifié avec succès");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le prix doit être un nombre");
        }
    }
    
    private void supprimerAbonnement() {
        if (selectedAbonnement == null) return;
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer l'abonnement");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet abonnement ?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            service.supprimer(selectedAbonnement.getId());
            clearFields();
            refreshTable();
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Abonnement supprimé avec succès");
        }
    }
    
    private boolean validateFields() {
        String nom = nomField.getText();
        String type = typeCombo.getValue();
        String duree = dureeField.getText();
        String prix = prixField.getText();
        
        if (nom.isEmpty() || type == null || duree.isEmpty() || prix.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs");
            return false;
        }
        
        try {
            Float.parseFloat(prix);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le prix doit être un nombre");
            return false;
        }
        
        return true;
    }
    
    private void clearFields() {
        nomField.clear();
        typeCombo.setValue(null);
        dureeField.clear();
        prixField.clear();
        selectedAbonnement = null;
        modifierBtn.setDisable(true);
        supprimerBtn.setDisable(true);
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Méthode pour gérer le paiement d'un abonnement
    private void payerAbonnement() {
        if (selectedAbonnement == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", 
                    "Veuillez sélectionner un abonnement à payer");
            return;
        }
        
        try {
            // Charger l'interface de paiement
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/paiement.fxml"));
            Parent root = loader.load();
            
            // Obtenir le contrôleur et définir l'abonnement sélectionné
            PaiementController paiementController = loader.getController();
            paiementController.setAbonnement(selectedAbonnement);
            
            // Créer et afficher la fenêtre de paiement
            Stage stage = new Stage();
            stage.setTitle("Paiement - " + selectedAbonnement.getNom());
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquer l'interaction avec la fenêtre principale
            stage.showAndWait();
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Impossible d'ouvrir la fenêtre de paiement: " + e.getMessage());
        }
    }
    @FXML
    private void navigateToHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/home.fxml"));
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
