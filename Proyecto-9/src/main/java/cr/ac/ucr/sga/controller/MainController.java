package cr.ac.ucr.sga.controller;

import cr.ac.ucr.sga.model.services.SessionHistoryService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private Label lblCurrentView;

    @FXML
    public void initialize() {
        SessionHistoryService.getInstance().addView("Menú principal");
        showStudentView();
    }

    @FXML
    private void showStudentView() {
        loadCenter("/fxml/student-view.fxml", "Expediente Académico");
    }

    @FXML
    private void goBack() {
        String view = SessionHistoryService.getInstance().previousView();
        lblCurrentView.setText("Vista actual: " + view);
    }

    @FXML
    private void goForward() {
        String view = SessionHistoryService.getInstance().nextView();
        lblCurrentView.setText("Vista actual: " + view);
    }

    @FXML
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sprint 1");
        alert.setHeaderText("Sistema de Gestión Académica");
        alert.setContentText("Sprint 1: Maven, JavaFX, MVC, listas enlazadas, expediente académico e historial circular.");
        alert.showAndWait();
    }

    private void loadCenter(String fxml, String viewName) {
        try {
            mainPane.setCenter(FXMLLoader.load(getClass().getResource(fxml)));
            lblCurrentView.setText("Vista actual: " + viewName);
            SessionHistoryService.getInstance().addView(viewName);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar la vista", e);
        }
    }
}
