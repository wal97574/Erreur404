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
    @FXML private TextField searchField;
    @FXML private TableView<Emploi> emploiTable;
    @FXML private TableColumn<Emploi, Integer> idColumn;
    @FXML private TableColumn<Emploi, Integer> coursIdColumn;
    @FXML private TableColumn<Emploi, Integer> userIdColumn;
    @FXML private TableColumn<Emploi, LocalDate> startTimeColumn;
    @FXML private TableColumn<Emploi, LocalDate> endTimeColumn;
    @FXML private TableColumn<Emploi, String> statusColumn;

    public EmploiController() {
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

        ObservableList<Emploi> list = FXCollections.observableArrayList(emploiService.getAllEmplois());
        FilteredList<Emploi> filteredList = new FilteredList<>(list, b -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(emploi -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return String.valueOf(emploi.getId()).toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(emploi.getCoursId()).toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(emploi.getUserId()).toLowerCase().contains(lowerCaseFilter)
                        || emploi.getStartTime().toString().toLowerCase().contains(lowerCaseFilter)
                        || emploi.getEndTime().toString().toLowerCase().contains(lowerCaseFilter)
                        || emploi.getStatus().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<Emploi> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(emploiTable.comparatorProperty());
        emploiTable.setItems(sortedList);
    }

    @FXML
    private void handleAddEmploi() {
        try {
            if (!isInputValid()) return;

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
            showError("An error occurred while adding the emploi.");
        }
    }

    @FXML
    private void handleUpdateEmploi() {
        try {
            Emploi selectedEmploi = emploiTable.getSelectionModel().getSelectedItem();
            if (selectedEmploi == null) {
                showError("No emploi selected. Please select an emploi to update.");
                return;
            }

            if (!isInputValid()) return;

            selectedEmploi.setCoursId(Integer.parseInt(coursIdField.getText()));
            selectedEmploi.setUserId(Integer.parseInt(userIdField.getText()));
            selectedEmploi.setStartTime(LocalDate.parse(startTimeField.getText()));
            selectedEmploi.setEndTime(LocalDate.parse(endTimeField.getText()));
            selectedEmploi.setStatus(statusField.getText());
            emploiService.updateEmploi(selectedEmploi);
            refreshEmploiList();
        } catch (Exception e) {
            showError("An error occurred while updating the emploi.");
        }
    }

    @FXML
    private void handleDeleteEmploi() {
        try {
            Emploi selectedEmploi = emploiTable.getSelectionModel().getSelectedItem();
            if (selectedEmploi == null) {
                showError("No emploi selected. Please select an emploi to delete.");
                return;
            }
            emploiService.deleteEmploi(selectedEmploi.getId());
            refreshEmploiList();
        } catch (Exception e) {
            showError("An error occurred while deleting the emploi.");
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (coursIdField.getText() == null || !coursIdField.getText().matches("\\d+")) {
            errorMessage += "Cours ID must be a valid integer.\n";
        }
        if (userIdField.getText() == null || !userIdField.getText().matches("\\d+")) {
            errorMessage += "User ID must be a valid integer.\n";
        }
        if (startTimeField.getText() == null || !isValidDate(startTimeField.getText())) {
            errorMessage += "Start Date must be in the format yyyy-MM-dd.\n";
        }
        if (endTimeField.getText() == null || !isValidDate(endTimeField.getText())) {
            errorMessage += "End Date must be in the format yyyy-MM-dd.\n";
        }
        if (statusField.getText() == null || statusField.getText().trim().isEmpty()) {
            errorMessage += "Status cannot be empty.\n";
        }

        if (!errorMessage.isEmpty()) {
            showError(errorMessage);
            return false;
        }
        return true;
    }

    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (Exception e) {
            return false;
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
