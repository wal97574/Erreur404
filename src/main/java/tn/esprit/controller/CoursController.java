package tn.esprit.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tn.esprit.entity.Cours;
import tn.esprit.interfaces.ICoursService;
import tn.esprit.service.CoursService;

import java.io.IOException;

public class CoursController {
    private ICoursService coursService = new CoursService();

    @FXML private TextField descriptionField;
    @FXML private TextField trainerIdField;
    @FXML private TextField maxParticipantsField;
    @FXML private TextField durationField;
    @FXML private TableView<Cours> coursTable;
    @FXML private TableColumn<Cours, Integer> idColumn;
    @FXML private TableColumn<Cours, String> descriptionColumn;
    @FXML private TableColumn<Cours, Integer> trainerIdColumn;
    @FXML private TableColumn<Cours, Integer> maxParticipantsColumn;
    @FXML private TableColumn<Cours, Integer> durationColumn;
    @FXML private TableColumn<Cours, Void> pdfColumn;

    @FXML
    private void initialize() {
        // Bind columns to Cours properties
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        trainerIdColumn.setCellValueFactory(new PropertyValueFactory<>("trainerId"));
        maxParticipantsColumn.setCellValueFactory(new PropertyValueFactory<>("maxParticipants"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));

        // Add PDF button column
        pdfColumn.setCellFactory(param -> new TableCell<>() {
            private final Button pdfButton = new Button("Generate PDF");

            {
                pdfButton.setOnAction(event -> {
                    Cours cours = getTableView().getItems().get(getIndex());
                    handleGeneratePdf(cours);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pdfButton);
                }
            }
        });

        // Load data into the TableView
        refreshCoursList();
    }

    @FXML
    private void handleAddCours() {
        try {
            Cours cours = new Cours(
                    0,
                    descriptionField.getText(),
                    Integer.parseInt(trainerIdField.getText()),
                    Integer.parseInt(maxParticipantsField.getText()),
                    Integer.parseInt(durationField.getText())
            );
            coursService.addCours(cours);
            refreshCoursList();
        } catch (Exception e) {
            showError("Invalid input. Please check your data.");
        }
    }

    @FXML
    private void handleUpdateCours() {
        try {
            Cours selectedCours = coursTable.getSelectionModel().getSelectedItem();
            if (selectedCours != null) {
                selectedCours.setDescription(descriptionField.getText());
                selectedCours.setTrainerId(Integer.parseInt(trainerIdField.getText()));
                selectedCours.setMaxParticipants(Integer.parseInt(maxParticipantsField.getText()));
                selectedCours.setDurationMinutes(Integer.parseInt(durationField.getText()));
                coursService.updateCours(selectedCours);
                refreshCoursList();
            } else {
                showError("No course selected. Please select a course to update.");
            }
        } catch (Exception e) {
            showError("Invalid input. Please check your data.");
        }
    }

    @FXML
    private void handleDeleteCours() {
        try {
            Cours selectedCours = coursTable.getSelectionModel().getSelectedItem();
            if (selectedCours != null) {
                coursService.deleteCours(selectedCours.getId());
                refreshCoursList();
            } else {
                showError("No course selected. Please select a course to delete.");
            }
        } catch (Exception e) {
            showError("An error occurred while deleting the course.");
        }
    }

    private void handleGeneratePdf(Cours cours) {
        String filePath = "Cours_" + cours.getId() + ".pdf";
        coursService.generateCoursPdf(cours, filePath);
        showInfo("PDF generated successfully at: " + filePath);
    }

    private void refreshCoursList() {
        ObservableList<Cours> list = FXCollections.observableArrayList(coursService.getAllCours());
        coursTable.setItems(list);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBackToHome() {
        try {
            // Load the home screen FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Parent homeRoot = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) coursTable.getScene().getWindow();
            stage.setScene(new Scene(homeRoot));
        } catch (IOException e) {
            showError("Failed to load the home screen.");
        }
    }
}
