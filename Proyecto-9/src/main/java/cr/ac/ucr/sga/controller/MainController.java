package cr.ac.ucr.sga.controller;

import cr.ac.ucr.sga.model.services.SessionHistoryService;
import cr.ac.ucr.sga.model.services.NotificationService;
import cr.ac.ucr.sga.view.observers.NotificationObserver;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController implements NotificationObserver {

    @FXML private BorderPane mainPane;
    @FXML private Label lblCurrentView;
    @FXML private Label lblNotificationBar;

    @FXML
    public void initialize() {
        NotificationService.getInstance().addObserver(this);
        SessionHistoryService.getInstance().addView("Menú principal");
        SessionHistoryService.getInstance().addView("Expediente Académico");
        SessionHistoryService.getInstance().addView("Reporte de cursos");
        loadCenter("/fxml/student-view.fxml", "Expediente Académico");
    }
//ir atras
    @FXML
    private void goBack() {
        String view = SessionHistoryService.getInstance().previousView();
        lblCurrentView.setText("Vista actual: " + view);
    }
//ir adelante
    @FXML
    private void goForward() {
        String view = SessionHistoryService.getInstance().nextView();
        lblCurrentView.setText("Vista actual: " + view);
    }

    //Lo que hace es para mostrar un tipo de mensaje del acerca de, incompleto porque conforme avance el proyecto
    @FXML
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sprint 1");
        alert.setHeaderText("Sistema de Gestión Académica");
        alert.setContentText(
                "Sprint 1: Maven + JavaFX 21, patrón MVC.\n" +
                        "Estructuras: SimpleLinkedList, DoublyLinkedList,\n" +
                        "CircularLinkedList, CircularDoublyLinkedList.\n" +
                        "Historial de vistas registradas: " +
                        SessionHistoryService.getInstance().size()
        );
        alert.showAndWait();
    }

    @FXML
    void openTramitView() {
        loadCenter("/fxml/tramit-view.fxml", "Gestión de Trámites");
    }

    @FXML
    void openEnrollmentView() {
        loadCenter("/fxml/enrollment-view.fxml", "Cola de Matrícula");
    }

    @FXML
    void openStudentView() {
        loadCenter("/fxml/student-view.fxml", "Expediente Académico");
    }

    @Override
    public void onNotification(String message, String level) {
        if (lblNotificationBar != null) {
            lblNotificationBar.setText(level + ": " + message);
        }
    }

    @Override
    public void onNotification(String message) {

    }

    private void loadCenter(String fxml, String viewName) {
        try {
            var resource = getClass().getResource(fxml);
            if (resource == null) {
                throw new IOException("No se encontró el recurso FXML: " + fxml);
            }
            mainPane.setCenter(FXMLLoader.load(resource));
            lblCurrentView.setText("Vista actual: " + viewName);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar la vista: " + fxml, e);
        }
    }
}