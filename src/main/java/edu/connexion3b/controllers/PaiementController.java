package edu.connexion3b.controllers;

import com.stripe.exception.StripeException;
import edu.connexion3b.entities.Abonnement;
import edu.connexion3b.entities.Paiement;
import edu.connexion3b.services.AbonnementService;
import edu.connexion3b.services.PaiementDBService;
import edu.connexion3b.services.PaiementService;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PaiementController implements Initializable {
    @FXML private Label nomAbonnementLabel;
    @FXML private Label typeAbonnementLabel;
    @FXML private Label dureeAbonnementLabel;
    @FXML private Label prixAbonnementLabel;
    @FXML private TextField emailField;
    @FXML private Button payerButton;
    @FXML private Button annulerButton;
    @FXML private Button retourButton;
    @FXML private WebView webView;
    
    private WebEngine webEngine;
    private Abonnement abonnement;
    private PaiementService paiementService;
    private PaiementDBService paiementDBService;
    private AbonnementService abonnementService;
    private String clientSecret;
    
    // Clé publique Stripe (à remplacer par votre clé réelle en production)
    private static final String STRIPE_PUBLIC_KEY = "pk_test_51OyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paiementService = new PaiementService();
        paiementDBService = new PaiementDBService();
        abonnementService = new AbonnementService();
        
        webEngine = webView.getEngine();
        
        // Initialiser les boutons
        payerButton.setOnAction(event -> initierPaiement());
        annulerButton.setOnAction(event -> fermerFenetre());
        retourButton.setOnAction(event -> fermerFenetre());
        
        // Configurer l'icône du bouton retour en utilisant un caractère Unicode
        retourButton.setText("← Retour");
    }
    
    public void setAbonnement(Abonnement abonnement) {
        this.abonnement = abonnement;
        
        // Mettre à jour l'interface avec les détails de l'abonnement
        nomAbonnementLabel.setText(abonnement.getNom());
        typeAbonnementLabel.setText(abonnement.getType());
        dureeAbonnementLabel.setText(abonnement.getDuree_jours() + " jours");
        prixAbonnementLabel.setText(String.format("%.2f €", abonnement.getPrix()));
    }
    
    private void initierPaiement() {
        String email = emailField.getText().trim();
        
        // Validation de l'email
        if (email.isEmpty() || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer une adresse email valide");
            return;
        }
        
        try {
            // Créer l'intention de paiement avec Stripe
            clientSecret = paiementService.creerIntentionPaiement(abonnement, email);
            
            // Extraire l'ID de l'intention de paiement du client secret
            String paymentIntentId = clientSecret.split("_secret_")[0];
            
            // Enregistrer le paiement dans la base de données
            Paiement paiement = new Paiement(
                    abonnement.getId(),
                    email,
                    abonnement.getPrix(),
                    "EUR",
                    "pending",
                    paymentIntentId
            );
            paiementDBService.ajouter(paiement);
            
            // Charger le formulaire de paiement Stripe
            chargerFormulaireStripe(clientSecret);
            
        } catch (StripeException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de paiement", 
                    "Une erreur est survenue lors de l'initialisation du paiement: " + e.getMessage());
        }
    }
    
    private void chargerFormulaireStripe(String clientSecret) {
        // Désactiver le bouton de paiement pendant le chargement
        payerButton.setDisable(true);
        
        try {
            // Charger le fichier HTML depuis les ressources
            String htmlTemplate = chargerFichierHTML("/stripe-payment-form.html");
            
            // Remplacer les variables dans le template
            String htmlContent = htmlTemplate
                    .replace("${STRIPE_PUBLIC_KEY}", STRIPE_PUBLIC_KEY)
                    .replace("${CLIENT_SECRET}", clientSecret);
            
            // Charger le contenu HTML dans le WebView
            webEngine.loadContent(htmlContent);
            
            // Écouter les changements de statut pour détecter la fin du paiement
            webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == Worker.State.SUCCEEDED) {
                    // Ajouter un écouteur pour le statut de la fenêtre
                    webEngine.setOnStatusChanged(event -> {
                        String status = event.getData();
                        if (status != null && status.startsWith("payment_completed:")) {
                            // Paiement terminé, mettre à jour le statut
                            Platform.runLater(() -> traiterPaiementTermine(status));
                        }
                    });
                }
            });
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Impossible de charger le formulaire de paiement: " + e.getMessage());
            payerButton.setDisable(false);
        }
    }
    
    private String chargerFichierHTML(String chemin) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(chemin);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
    
    private void traiterPaiementTermine(String status) {
        // Extraire le client secret du statut
        String returnedClientSecret = status.substring("payment_completed:".length());
        
        try {
            // Extraire l'ID de l'intention de paiement
            String paymentIntentId = returnedClientSecret.split("_secret_")[0];
            
            // Vérifier le statut du paiement auprès de Stripe
            boolean paiementReussi = paiementService.verifierPaiement(paymentIntentId);
            
            if (paiementReussi) {
                // Mettre à jour le statut dans la base de données
                paiementDBService.mettreAJourStatut(paymentIntentId, "succeeded");
                
                // Afficher un message de succès personnalisé
                afficherSuccesPaiement();
                
                // Fermer la fenêtre après un court délai
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        Platform.runLater(this::fermerFenetre);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                // Mettre à jour le statut dans la base de données
                paiementDBService.mettreAJourStatut(paymentIntentId, "failed");
                
                // Afficher un message d'erreur
                showAlert(Alert.AlertType.ERROR, "Échec", 
                        "Le paiement n'a pas pu être complété. Veuillez réessayer.");
                
                // Réactiver le bouton de paiement
                payerButton.setDisable(false);
            }
        } catch (StripeException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Erreur lors de la vérification du paiement: " + e.getMessage());
            payerButton.setDisable(false);
        }
    }
    
    private void fermerFenetre() {
        // Récupérer la fenêtre actuelle et la fermer
        Stage stage = (Stage) annulerButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Affiche un message de succès personnalisé
     */
    private void afficherSuccesPaiement() {
        // Créer une alerte de type INFORMATION
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Paiement réussi");
        alert.setHeaderText("Félicitations!");
        alert.setContentText("Votre paiement a été traité avec succès. Votre abonnement est maintenant actif!");
        
        // Personnaliser l'apparence de l'alerte
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/abonnement.css").toExternalForm());
        dialogPane.getStyleClass().add("success-dialog");
        
        // Afficher l'alerte
        alert.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}