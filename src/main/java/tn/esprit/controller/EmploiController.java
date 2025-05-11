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
import tn.esprit.entity.Emploi;
import tn.esprit.interfaces.IEmploiService;
import tn.esprit.service.CoursService;
import tn.esprit.service.EmploiService;
import tn.esprit.service.GoogleCalendarService;

import java.io.IOException;
import java.time.LocalDate;

public class EmploiController {
    private IEmploiService emploiService;

    @FXML private TextField coursIdField;
    @FXML private TextField userIdField;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private TextField statusField;
    @FXML private TableView<Emploi> emploiTable;
    @FXML private TableColumn<Emploi, Integer> idColumn;
    @FXML private TableColumn<Emploi, Integer> coursIdColumn;
    @FXML private TableColumn<Emploi, Integer> userIdColumn;
    @FXML private TableColumn<Emploi, LocalDate> startTimeColumn;
    @FXML private TableColumn<Emploi, LocalDate> endTimeColumn;
    @FXML private TableColumn<Emploi, String> statusColumn;

    public EmploiController() {
        // Initialize dependencies
        CoursService coursService = new CoursService();
        GoogleCalendarService googleCalendarService = new GoogleCalendarService(coursService);
        this.emploiService = new EmploiService(googleCalendarService);
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        coursIdColumn.setCellValueFactory(new PropertyValueFactory<>("coursId"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        refreshEmploiList();
    }

    @FXML
    private void handleAddEmploi() {
        try {
            Emploi emploi = new Emploi(
                    0,
                    Integer.parseInt(coursIdField.getText()),
                    Integer.parseInt(userIdField.getText()),
                    LocalDate.parse(startTimeField.getText()),
                    LocalDate.parse(endTimeField.getText()),
                    statusField.getText()
            );
            emploiService.addEmploi(emploi);
            refreshEmploiList();
        } catch (Exception e) {
            showError("Invalid input. Please check your data.");
        }
    }

    @FXML
    private void handleUpdateEmploi() {
        try {
            Emploi selectedEmploi = emploiTable.getSelectionModel().getSelectedItem();
            if (selectedEmploi != null) {
                selectedEmploi.setCoursId(Integer.parseInt(coursIdField.getText()));
                selectedEmploi.setUserId(Integer.parseInt(userIdField.getText()));
                selectedEmploi.setStartTime(LocalDate.parse(startTimeField.getText()));
                selectedEmploi.setEndTime(LocalDate.parse(endTimeField.getText()));
                selectedEmploi.setStatus(statusField.getText());
                emploiService.updateEmploi(selectedEmploi);
                refreshEmploiList();
            } else {
                showError("No emploi selected. Please select an emploi to update.");
            }
        } catch (Exception e) {
            showError("Invalid input. Please check your data.");
        }
    }

    @FXML
    private void handleDeleteEmploi() {
        try {
            Emploi selectedEmploi = emploiTable.getSelectionModel().getSelectedItem();
            if (selectedEmploi != null) {
                emploiService.deleteEmploi(selectedEmploi.getId());
                refreshEmploiList();
            } else {
                showError("No emploi selected. Please select an emploi to delete.");
            }
        } catch (Exception e) {
            showError("An error occurred while deleting the emploi.");
        }
    }

    private void refreshEmploiList() {
        ObservableList<Emploi> list = FXCollections.observableArrayList(emploiService.getAllEmplois());
        emploiTable.setItems(list);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBackToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Parent homeRoot = loader.load();
            Stage stage = (Stage) emploiTable.getScene().getWindow();
            stage.setScene(new Scene(homeRoot));
        } catch (IOException e) {
            showError("Failed to load the home screen.");
        }
    }
}
