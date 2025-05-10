package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import tn.esprit.entity.Emploi;
import tn.esprit.interfaces.IEmploiService;
import tn.esprit.service.EmploiService;

import java.time.LocalDate;

public class EmploiController {
    private IEmploiService emploiService = new EmploiService();

    @FXML private TextField coursIdField;
    @FXML private TextField userIdField;
    @FXML private TextField startTimeField; // Format: yyyy-MM-dd
    @FXML private TextField endTimeField;  // Format: yyyy-MM-dd
    @FXML private TextField statusField;
    @FXML private TableView<Emploi> emploiTable;

    @FXML
    private void initialize() {
        refreshEmploiList();
    }

    @FXML
    private void handleAddEmploi() {
        try {
            Emploi emploi = new Emploi(
                    0,
                    Integer.parseInt(coursIdField.getText()),
                    Integer.parseInt(userIdField.getText()),
                    LocalDate.parse(startTimeField.getText()), // Parse as LocalDate
                    LocalDate.parse(endTimeField.getText()),   // Parse as LocalDate
                    statusField.getText()
            );
            emploiService.addEmploi(emploi);
            refreshEmploiList();
        } catch (Exception e) {
            showError("Invalid input. Please check your data and try again.");
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
}
