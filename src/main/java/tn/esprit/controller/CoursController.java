package tn.esprit.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
    @FXML private TextField searchField;
    @FXML private TableView<Cours> coursTable;
    @FXML private TableColumn<Cours, Integer> idColumn;
    @FXML private TableColumn<Cours, String> descriptionColumn;
    @FXML private TableColumn<Cours, Integer> trainerIdColumn;
    @FXML private TableColumn<Cours, Integer> maxParticipantsColumn;
    @FXML private TableColumn<Cours, Integer> durationColumn;
    @FXML private TableColumn<Cours, Void> pdfColumn;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        trainerIdColumn.setCellValueFactory(new PropertyValueFactory<>("trainerId"));
        maxParticipantsColumn.setCellValueFactory(new PropertyValueFactory<>("maxParticipants"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));

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

        ObservableList<Cours> list = FXCollections.observableArrayList(coursService.getAllCours());
        FilteredList<Cours> filteredList = new FilteredList<>(list, b -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(cours -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return cours.getDescription().toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(cours.getTrainerId()).contains(lowerCaseFilter)
                        || String.valueOf(cours.getMaxParticipants()).contains(lowerCaseFilter)
                        || String.valueOf(cours.getDurationMinutes()).contains(lowerCaseFilter);
            });
        });

        SortedList<Cours> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(coursTable.comparatorProperty());
        coursTable.setItems(sortedList);
    }

    @FXML
    private void handleAddCours() {
        try {
            if (!isInputValid()) return;

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
            showError("An error occurred while adding the course.");
        }
    }

    @FXML
    private void handleUpdateCours() {
        try {
            Cours selectedCours = coursTable.getSelectionModel().getSelectedItem();
            if (selectedCours == null) {
                showError("No course selected. Please select a course to update.");
                return;
            }

            if (!isInputValid()) return;

            selectedCours.setDescription(descriptionField.getText());
            selectedCours.setTrainerId(Integer.parseInt(trainerIdField.getText()));
            selectedCours.setMaxParticipants(Integer.parseInt(maxParticipantsField.getText()));
            selectedCours.setDurationMinutes(Integer.parseInt(durationField.getText()));
            coursService.updateCours(selectedCours);
            refreshCoursList();
        } catch (Exception e) {
            showError("An error occurred while updating the course.");
        }
    }

    @FXML
    private void handleDeleteCours() {
        try {
            Cours selectedCours = coursTable.getSelectionModel().getSelectedItem();
            if (selectedCours == null) {
                showError("No course selected. Please select a course to delete.");
                return;
            }
            coursService.deleteCours(selectedCours.getId());
            refreshCoursList();
        } catch (Exception e) {
            showError("An error occurred while deleting the course.");
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (descriptionField.getText() == null || descriptionField.getText().trim().isEmpty()) {
            errorMessage += "Description cannot be empty.\n";
        }
        if (trainerIdField.getText() == null || !trainerIdField.getText().matches("\\d+")) {
            errorMessage += "Trainer ID must be a valid integer.\n";
        }
        if (maxParticipantsField.getText() == null || !maxParticipantsField.getText().matches("\\d+")) {
            errorMessage += "Max Participants must be a valid integer.\n";
        }
        if (durationField.getText() == null || !durationField.getText().matches("\\d+")) {
            errorMessage += "Duration must be a valid integer.\n";
        }

        if (!errorMessage.isEmpty()) {
            showError(errorMessage);
            return false;
        }
        return true;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Parent homeRoot = loader.load();
            Stage stage = (Stage) coursTable.getScene().getWindow();
            stage.setScene(new Scene(homeRoot));
        } catch (IOException e) {
            showError("Failed to load the home screen.");
        }
    }
}
