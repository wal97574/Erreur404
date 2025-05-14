package Controllers;

import Entities.Materiel;
import Entities.Salle;
import Services.ServiceMateriel;
import Services.ServiceSalle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import Services.ChatBot;
import javafx.stage.Stage;

public class GestionMain {
    private ServiceMateriel serviceMateriel=new ServiceMateriel();
    private ServiceSalle serviceSalle=new ServiceSalle();
    private Materiel materiel;
    @FXML
    private AnchorPane anchorBienvenue;
    //materiel
    @FXML
    private TabPane materielTabPane;
    //ajouter
    @FXML
    private TextField materielAjouterNom;
    @FXML
    private DatePicker materielAjouterDate;
    @FXML
    private Label materielAjouterAffichage;
    //modifier
    @FXML
    private TextField materielModifierId;
    @FXML
    private Label materielModifierAffichage;
    @FXML
    private Label materielModifierNomLabel;
    @FXML
    private Label materielModifierDateLabel;
    @FXML
    private TextField materielModifierNomTextField;
    @FXML
    private DatePicker materielModifierDate;
    //lister
    @FXML
    private TableView<Materiel> materielTable;
    @FXML
    private TableColumn<Materiel, Integer> idMaterielCol;
    @FXML
    private TableColumn<Materiel, String> nomMaterielCol;
    @FXML
    private TableColumn<Materiel, String> dateMaterielCol;
    @FXML
    private TextField materielListerId;
    //salle
    @FXML
    private TabPane salleTabPane;
    //Ajouter
    @FXML
    private TextField SalleAjouterNom;
    @FXML
    private Spinner<Integer> SalleAjouterNbrMax;
    @FXML
    private Label SalleAjouterAffichage;
    @FXML
    private TableView<Salle> salleTable;
    @FXML
    private TableColumn<Salle, Integer> idSalleCol;
    @FXML
    private TableColumn<Salle, String> nomSalleCol;
    @FXML
    private TableColumn<Salle, Integer> NbrMaxSalleCol;
    @FXML
    private TextField SalleListerId;
    private void masquer(){
        anchorBienvenue.setVisible(false);
        anchorBienvenue.setManaged(false);
        materielTabPane.setVisible(false);
        materielTabPane.setManaged(false);
        salleTabPane.setVisible(false);
        salleTabPane.setManaged(false);
        chatbox.setVisible(false);
        chatbox.setManaged(false);
    }
    @FXML
    public void initialize(){
        masquer();
        anchorBienvenue.setVisible(true);
        anchorBienvenue.setManaged(true);
        SalleAjouterNbrMax.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 20));
        SalleModifierNbrMaxSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 20));

        idMaterielCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomMaterielCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        dateMaterielCol.setCellValueFactory(cellData -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return new SimpleStringProperty(sdf.format(cellData.getValue().getDateMaintenance()));
        });
        ObservableList<Materiel> materielObservableList = FXCollections.observableArrayList(serviceMateriel.getAllMateriel());
        materielTable.setItems(materielObservableList);

        materielTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Materiel selected = materielTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    int selectedId = selected.getId();
                    materielListerId.setText(String.valueOf(selectedId));
                    materielModifierId.setText(String.valueOf(selectedId));
                }
            }
        });

        idSalleCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomSalleCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        NbrMaxSalleCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        ObservableList<Salle> salleObservableList = FXCollections.observableArrayList(serviceSalle.getAllSalle());
        salleTable.setItems(salleObservableList);

        salleTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Salle selected = salleTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    int selectedId = selected.getId();
                    SalleListerId.setText(String.valueOf(selectedId));
                    SalleModifierID.setText(String.valueOf(selectedId));
                }
            }
        });
        ChatBot.sendMessage("Tu es un assistant virtuel pour une salle de sport. Réponds uniquement aux questions posées, de manière claire, précise et utile, sans ajouter d’informations inutiles ou de commentaires personnels. Ne fais pas de bavardage. Réponds de manière concise et précise, sans détails inutiles.");
        chatboxTextArea.appendText("Assistant virtuel : " + "Bonjour, comment puis-je vous aider ?"+'\n');
    }
    @FXML
    public void afficherMateriel(ActionEvent event) {
        masquer();
        materielTabPane.setVisible(true);
        materielTabPane.setManaged(true);
    }
    public void afficherSalle(ActionEvent event) {
        masquer();
        salleTabPane.setVisible(true);
        salleTabPane.setManaged(true);
    }
    @FXML
    public void materielAjouter(ActionEvent event) {
        if(materielAjouterNom.getText().isEmpty()||materielAjouterDate.getValue()==null){
            materielAjouterAffichage.setText("Veuillez remplir tous les champs!");
            return;
        }
        Date date=Date.from(materielAjouterDate.getValue().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
        Materiel materiel=new Materiel(materielAjouterNom.getText(),date);
        if(serviceMateriel.addMateriel(materiel)){
            materielAjouterAffichage.setText("Salle ajoutée avec l'ID : "+materiel.getId());
            return;
        }
        materielAjouterAffichage.setText("Erreur lors de l'ajout de la materiel!");
    }
    @FXML
    public void materielSelect(ActionEvent event){
        try {
            materiel = serviceMateriel.getMateriel(Integer.parseInt(materielModifierId.getText()));
            if(materiel!=null){
                materielModifierAffichage.setText("Materiel N°"+materiel.getId()+" selectionné!");
                materielModifierNomLabel.setText(materiel.getNom());
                materielModifierNomTextField.setText(materiel.getNom());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String formattedDate = formatter.format(materiel.getDateMaintenance());
                materielModifierDateLabel.setText(formattedDate);
                Date date = materiel.getDateMaintenance();
                LocalDate localDate = date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                materielModifierDate.setValue(localDate);
                return;
            } else {
                materielModifierAffichage.setText("Materiel introuvable!");
            }
        } catch (NumberFormatException ex) {
            materielModifierAffichage.setText("Veuillez entrer un nombre!");
            return;
        }
    }
    @FXML
    public void materielSupprimer(ActionEvent event){
        try {
            if (materiel.getId()==Integer.parseInt(materielModifierId.getText())){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation de suppression");
                alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet élément ?");
                alert.setContentText("Cette action est irréversible.");

                ButtonType ouiButton = new ButtonType("Oui");
                ButtonType nonButton = new ButtonType("Non");

                alert.getButtonTypes().setAll(ouiButton, nonButton);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ouiButton) {
                    serviceMateriel.deleteMateriel(materiel.getId());
                    materielModifierAffichage.setText("Élément supprimé!");
                } else {
                    materielModifierAffichage.setText("Suppression annulée.");
                }
            }
        } catch (NumberFormatException e){
            materielModifierAffichage.setText("Veuillez selectionner un materiel avant de le supprimer!");
            return;
        }
    }
    @FXML
    public void materielModifier(ActionEvent event){
        try {
            if (materiel.getId()==Integer.parseInt(materielModifierId.getText())){

                if(materielModifierNomTextField.getText().isEmpty()||materielModifierDate.getValue()==null){
                    materielModifierAffichage.setText("Veuillez remplir tous les champs!");
                    return;
                }
                Date date=Date.from(materielModifierDate.getValue().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
                materiel.setNom(materielModifierNomTextField.getText());
                materiel.setDateMaintenance(date);
                System.out.println(materiel);
                if(serviceMateriel.updateMateriel(materiel)){
                    materielModifierAffichage.setText("Le materiel N°"+materiel.getId()+" a ete modifie avec succès!");
                    return;
                }
                materielModifierAffichage.setText("Erreur lors de la modification de la materiel!");
            }
        } catch (NumberFormatException e){
            materielModifierAffichage.setText("Veuillez selectionner un materiel avant de le modifier!");
            return;
        }
    }
    @FXML
    public void materielLister(ActionEvent event){
        ObservableList<Materiel> materielObservableList = FXCollections.observableArrayList(serviceMateriel.getAllMateriel());
        materielTable.setItems(materielObservableList);
    }
    @FXML
    public void ajouterSalle(ActionEvent event){
        if(SalleAjouterNom.getText().isEmpty()||SalleAjouterNbrMax.getValue()==null){
            SalleAjouterAffichage.setText("Veuillez remplir tous les champs!");
            return;
        }
        Salle salle=new Salle(SalleAjouterNom.getText(),SalleAjouterNbrMax.getValue());
        if(serviceSalle.addSalle(salle)){
            SalleAjouterAffichage.setText("Salle ajoutée avec l'ID : "+salle.getId());
            return;
        }
        SalleAjouterAffichage.setText("Erreur lors de l'ajout de la salle!");
    }
    @FXML
    private TextField SalleModifierID;
    @FXML
    private Label SalleModifierNomLabel;
    @FXML
    private Label SalleModifierNbrMaxLabel;
    @FXML
    private TextField SalleModifierNomTextField;
    @FXML
    private Spinner<Integer> SalleModifierNbrMaxSpinner;
    @FXML
    private Label SalleModifierAffichage;
    private Salle salle;
    @FXML
    public void SalleSelect(ActionEvent event){
        try {
            salle=serviceSalle.getSalle(Integer.parseInt(SalleModifierID.getText()));
            if(salle!=null){
                SalleModifierAffichage.setText("Salle N°"+salle.getId()+" selectionnée!");
                SalleModifierNomLabel.setText(salle.getNom());
                SalleModifierNbrMaxLabel.setText(String.valueOf(salle.getNbrMax()));
                SalleModifierNomTextField.setText(salle.getNom());
                SalleModifierNbrMaxSpinner.getValueFactory().setValue(salle.getNbrMax());
            } else {
                SalleModifierAffichage.setText("Salle introuvable!");
                return;
            }
        } catch (NumberFormatException ex) {
            SalleModifierAffichage.setText("Veuillez entrer un nombre!");
            return;
        }
    }
    @FXML
    public void SalleSupprimer(ActionEvent event){
        try {
            if (salle.getId()==Integer.parseInt(SalleModifierID.getText())){
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation de suppression");
                alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cet élément ?");
                alert.setContentText("Cette action est irréversible.");

                ButtonType ouiButton = new ButtonType("Oui");
                ButtonType nonButton = new ButtonType("Non");

                alert.getButtonTypes().setAll(ouiButton, nonButton);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ouiButton) {
                    serviceSalle.deleteSalle(salle.getId());
                    SalleModifierAffichage.setText("Élément supprimé!");
                } else {
                    SalleModifierAffichage.setText("Suppression annulée.");
                }
            }
        } catch (NumberFormatException e){
            SalleModifierAffichage.setText("Veuillez selectionner une salle avant de la supprimer!");
            return;
        }

    }
    public void SalleModifier(ActionEvent event){
        try {
            if (salle.getId()==Integer.parseInt(SalleModifierID.getText())){

                if(SalleModifierNomTextField.getText().isEmpty()||SalleModifierNbrMaxSpinner.getValue()==null){
                    SalleModifierAffichage.setText("Veuillez remplir tous les champs!");
                    return;
                }
                salle.setNom(SalleModifierNomTextField.getText());
                salle.setNbrMax(SalleModifierNbrMaxSpinner.getValue());
                System.out.println(salle);
                if(serviceSalle.updateSalle(salle)){
                    SalleModifierAffichage.setText("La salle N°"+salle.getId()+" a etee modifiee avec succès!");
                    return;
                }
                SalleModifierAffichage.setText("Erreur lors de la modification de la materiel!");
            }
        } catch (NumberFormatException e){
            SalleModifierAffichage.setText("Veuillez selectionner un materiel avant de le modifier!");
            return;
        }
    }
    @FXML
    public void SalleLister(ActionEvent event){
        ObservableList<Salle> salleObservableList = FXCollections.observableArrayList(serviceSalle.getAllSalle());
        salleTable.setItems(salleObservableList);
    }

    @FXML
    private AnchorPane chatbox;
    @FXML
    public void afficherAI(ActionEvent event) {
        masquer();
        chatbox.setVisible(true);
        chatbox.setManaged(true);
    }
    @FXML
    private TextArea chatboxTextArea;
    @FXML
    private TextField chatboxInput;
    @FXML
    public void envoyerMessage(ActionEvent event) {
        chatboxTextArea.appendText("Vous : " +chatboxInput.getText()+"\n");
        chatboxTextArea.appendText("Assistant virtuel : " + ChatBot.sendMessage(chatboxInput.getText())+"\n");
        System.out.println(ChatBot.sendMessage(chatboxInput.getText()));
        chatboxInput.clear();

    }
    @FXML
    private void navigateToHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/home.fxml"));
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
