package cr.ac.ucr.sga.controller;

import cr.ac.ucr.sga.model.entities.User;
import cr.ac.ucr.sga.model.services.NotificationService;
import cr.ac.ucr.sga.model.services.SessionHistoryService;
import cr.ac.ucr.sga.model.services.UserService;
import cr.ac.ucr.sga.view.ViewFactory;
import cr.ac.ucr.sga.view.observers.NotificationObserver;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador principal (Sprint 2 - actualizado).
 *
 * Cambios respecto al Sprint 1:
 *  1. Recibe el usuario en sesión y construye el menú lateral según su rol:
 *       - ADMINISTRADOR: Expediente, Trámites, Cola de Matrícula
 *       - ESTUDIANTE:    Expediente, Trámites  (sin Cola de Matrícula)
 *  2. Los botones Atrás / Adelante ahora son realmente funcionales:
 *       - Cada vez que se carga una vista, se registra en SessionHistoryService
 *         (CircularDoublyLinkedList).
 *       - goBack() llama a previous() y recarga la vista correspondiente.
 *       - goForward() llama a next() y recarga la vista correspondiente.
 *  3. Botón "Cerrar Sesión" al final del menú.
 */
public class MainController implements NotificationObserver {

    @FXML private BorderPane mainPane;
    @FXML private VBox       menuPanel;
    @FXML private Label      lblCurrentView;
    @FXML private Label      lblNotificationBar;
    @FXML private Label      lblUserInfo;

    // Nombres de vistas que se usan tanto en el historial como para recargar
    private static final String VIEW_STUDENT    = "Expediente Académico";
    private static final String VIEW_TRAMIT     = "Gestión de Trámites";
    private static final String VIEW_ENROLLMENT = "Cola de Matrícula";

    @FXML
    public void initialize() {
        NotificationService.getInstance().addObserver(this);

        User user = UserService.getInstance().getCurrentUser();

        // Mostrar nombre e rol del usuario en el encabezado
        if (user != null) {
            lblUserInfo.setText("Sesión: " + user.getDisplayName()
                    + "  [" + user.getRole().name() + "]");
        }

        // Construir menú según rol
        buildMenu(user);

        // Cargar vista inicial según rol del usuario
        if (user != null && user.getRole() == User.Role.ADMINISTRADOR) {
            loadCenter("/fxml/student-view.fxml", VIEW_STUDENT);
        } else {
            // Estudiante: va directo a Gestión de Trámites (US-05)
            loadCenter("/fxml/tramit-view.fxml", VIEW_TRAMIT);
        }
    }

    // -------------------------------------------------------------------------
    // Construcción dinámica del menú según rol
    // -------------------------------------------------------------------------

    private void buildMenu(User user) {
        menuPanel.getChildren().clear();

        // Expediente Académico — siempre visible
        addMenuButton("Expediente Académico", this::openStudentView);

        // Gestión de Trámites — siempre visible
        addMenuButton("Gestión de Trámites", this::openTramitView);

        // Cola de Matrícula — solo para ADMINISTRADOR
        if (user != null && user.getRole() == User.Role.ADMINISTRADOR) {
            addMenuButton("Cola de Matrícula", this::openEnrollmentView);
        }

        // Separador visual antes del cierre de sesión
        javafx.scene.control.Separator sep = new javafx.scene.control.Separator();
        menuPanel.getChildren().add(sep);

        // Cerrar Sesión — siempre visible
        Button btnLogout = new Button("Cerrar Sesión");
        btnLogout.getStyleClass().add("btn-ghost");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> logout());
        menuPanel.getChildren().add(btnLogout);
    }

    private void addMenuButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("btn-dark");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> action.run());
        menuPanel.getChildren().add(btn);
    }

    // -------------------------------------------------------------------------
    // Navegación funcional con lista circular doble
    // -------------------------------------------------------------------------

    /**
     * Retrocede al nombre de vista anterior en el historial circular
     * y recarga la vista correspondiente.
     */
    @FXML
    private void goBack() {
        String viewName = SessionHistoryService.getInstance().previousView();
        reloadViewByName(viewName);
        lblCurrentView.setText("Vista actual: " + viewName);
    }

    /**
     * Avanza al nombre de vista siguiente en el historial circular
     * y recarga la vista correspondiente.
     */
    @FXML
    private void goForward() {
        String viewName = SessionHistoryService.getInstance().nextView();
        reloadViewByName(viewName);
        lblCurrentView.setText("Vista actual: " + viewName);
    }

    /**
     * Recarga el panel central según el nombre de vista guardado en el historial.
     */
    private void reloadViewByName(String viewName) {
        if (viewName == null || viewName.equals("Sin historial")) return;
        switch (viewName) {
            case VIEW_STUDENT    -> loadCenterNoHistory("/fxml/student-view.fxml");
            case VIEW_TRAMIT     -> loadCenterNoHistory("/fxml/tramit-view.fxml");
            case VIEW_ENROLLMENT -> loadCenterNoHistory("/fxml/enrollment-view.fxml");
            default -> { /* nombre desconocido, no hacer nada */ }
        }
    }

    // -------------------------------------------------------------------------
    // Navegación desde el menú (registra en historial)
    // -------------------------------------------------------------------------

    @FXML
    void openStudentView() {
        loadCenter("/fxml/student-view.fxml", VIEW_STUDENT);
    }

    @FXML
    void openTramitView() {
        loadCenter("/fxml/tramit-view.fxml", VIEW_TRAMIT);
    }

    @FXML
    void openEnrollmentView() {
        loadCenter("/fxml/enrollment-view.fxml", VIEW_ENROLLMENT);
    }

    // -------------------------------------------------------------------------
    // Cerrar sesión
    // -------------------------------------------------------------------------

    private void logout() {
        UserService.getInstance().logout();
        // Resetear el historial de vistas para la próxima sesión
        SessionHistoryService.getInstance().reset();

        Stage stage = (Stage) mainPane.getScene().getWindow();
        ViewFactory.showLoginView(stage);
    }

    // -------------------------------------------------------------------------
    // Acerca de
    // -------------------------------------------------------------------------

    @FXML
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sprint 2");
        alert.setHeaderText("Sistema de Gestión Académica");
        alert.setContentText(
                "Sprint 2: Login con roles, menú dinámico, historial funcional.\n" +
                        "Estructuras: CircularDoublyLinkedList (navegación),\n" +
                        "             PriorityQueue (matrícula), LinkedStack (trámites LIFO).\n" +
                        "Vistas en historial: " +
                        SessionHistoryService.getInstance().size()
        );
        alert.showAndWait();
    }

    // -------------------------------------------------------------------------
    // Notificaciones (Observer)
    // -------------------------------------------------------------------------

    @Override
    public void onNotification(String message, String level) {
        if (lblNotificationBar != null) {
            lblNotificationBar.setText(level + ": " + message);
        }
    }

    @Override
    public void onNotification(String message) {
        // sobrecarga requerida por la interfaz, no usada aquí
    }

    // -------------------------------------------------------------------------
    // Helpers de carga de FXML
    // -------------------------------------------------------------------------

    /**
     * Carga una vista Y la registra en el historial circular.
     * Usar desde los botones de menú.
     */
    private void loadCenter(String fxml, String viewName) {
        try {
            var resource = getClass().getResource(fxml);
            if (resource == null) throw new IOException("No se encontró: " + fxml);
            mainPane.setCenter(FXMLLoader.load(resource));
            lblCurrentView.setText("Vista actual: " + viewName);
            SessionHistoryService.getInstance().addView(viewName);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar la vista: " + fxml, e);
        }
    }

    /**
     * Carga una vista SIN registrar en el historial.
     * Usar desde goBack() / goForward() para no generar entradas dobles.
     */
    private void loadCenterNoHistory(String fxml) {
        try {
            var resource = getClass().getResource(fxml);
            if (resource == null) throw new IOException("No se encontró: " + fxml);
            mainPane.setCenter(FXMLLoader.load(resource));
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar la vista: " + fxml, e);
        }
    }
}