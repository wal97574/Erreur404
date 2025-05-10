package tn.esprit.controller;

// CoursController.java
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import tn.esprit.entity.Cours;
import tn.esprit.interfaces.ICoursService;
import tn.esprit.service.CoursService;

public class CoursController {
    private ICoursService coursService = new CoursService();

    @FXML private TextField descriptionField;
    @FXML private TextField trainerIdField;
    @FXML private TextField maxParticipantsField;
    @FXML private TextField durationField;
    @FXML private TableView<Cours> coursTable;

    @FXML
    private void initialize() {
        refreshCoursList();
    }

    @FXML
    private void handleAddCours() {
        Cours cours = new Cours(
                0,
                descriptionField.getText(),
                Integer.parseInt(trainerIdField.getText()),
                Integer.parseInt(maxParticipantsField.getText()),
                Integer.parseInt(durationField.getText())
        );
        coursService.addCours(cours);
        refreshCoursList();
    }

    private void refreshCoursList() {
        ObservableList<Cours> list = FXCollections.observableArrayList(coursService.getAllCours());
        coursTable.setItems(list);
    }
}
