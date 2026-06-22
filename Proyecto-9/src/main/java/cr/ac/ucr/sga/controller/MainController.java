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

public class MainController implements NotificationObserver {

    @FXML private BorderPane mainPane;
    @FXML private VBox       menuPanel;
    @FXML private Label      lblCurrentView;
    @FXML private Label      lblNotificationBar;
    @FXML private Label      lblUserInfo;

    // Constantes para las vistas
    private static final String VIEW_STUDENT       = "Expediente Académico";
    private static final String VIEW_TRAMIT        = "Gestión de Trámites";
    private static final String VIEW_ENROLLMENT    = "Cola de Matrícula";
    private static final String VIEW_PREREQ_TREE   = "Árbol de Prerrequisitos";
    private static final String VIEW_CAMPUS_MAP    = "Mapa del Campus";
    private static final String VIEW_PROFESSOR     = "Panel del Profesor (AVL)";
    private static final String VIEW_BST_SEARCH    = "Búsqueda de Cursos (BST)";

    // --- Inicialización ---
    @FXML
    public void initialize() {
        NotificationService.getInstance().addObserver(this);
        User user = UserService.getInstance().getCurrentUser();
        if (user != null) {
            lblUserInfo.setText("Sesión: " + user.getDisplayName() + "  [" + user.getRole().name() + "]");
        }
        buildMenu(user);
        if (user != null) {
            switch (user.getRole()) {
                case ADMINISTRADOR -> loadCenter("/fxml/student-view.fxml",    VIEW_STUDENT);
                case PROFESOR      -> loadCenter("/fxml/professor-view.fxml",  VIEW_PROFESSOR);
                case ESTUDIANTE    -> loadCenter("/fxml/tramit-view.fxml",     VIEW_TRAMIT);
            }
        }
    }

    // --- Construcción del Menú Lateral ---
    private void buildMenu(User user) {
        menuPanel.getChildren().clear();

        if (user == null) return;

        switch (user.getRole()) {
            case ADMINISTRADOR -> {
                addMenuButton("Expediente Académico",     this::openStudentView);
                addMenuButton("Gestión de Trámites",      this::openTramitView);
                addMenuButton("Cola de Matrícula",        this::openEnrollmentView);
                addMenuButton("Búsqueda de Cursos (BST)", this::openCourseSearchView);
                addMenuButton("Árbol de Prerrequisitos",  this::openPrerequisiteTreeView);
                addMenuButton("Mirar Campus",              this::openCampusMapView); // Botón nuevo
            }
            case PROFESOR -> {
                addMenuButton("Panel del Profesor (AVL)", this::openProfessorView);
                addMenuButton("Árbol de Prerrequisitos",  this::openPrerequisiteTreeView);
                addMenuButton("Búsqueda de Cursos (BST)", this::openCourseSearchView);
                addMenuButton("Mirar Campus",              this::openCampusMapView); // Botón nuevo
            }
            case ESTUDIANTE -> {
                addMenuButton("Gestión de Trámites",  this::openTramitView);
                addMenuButton("Cola de Matrícula",    this::openEnrollmentView);
                addMenuButton("Expediente Académico", this::openStudentView);
                addMenuButton("Mirar Campus",          this::openCampusMapView); // Botón nuevo
            }
        }

        // Separador y botón Cerrar Sesión
        javafx.scene.control.Separator sep = new javafx.scene.control.Separator();
        menuPanel.getChildren().add(sep);

        Button btnLogout = new Button("Cerrar Sesión");
        btnLogout.getStyleClass().add("btn-ghost");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.setOnAction(e -> logout());
        menuPanel.getChildren().add(btnLogout);
    }

    // --- Método para agregar botones al menú ---
    private void addMenuButton(String text, Runnable action) {
        Button btn = new Button(text);
        btn.getStyleClass().add("btn-dark");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> action.run());
        menuPanel.getChildren().add(btn);
    }

    // --- Navegación entre vistas ---
    @FXML
    void openProfessorView()    { loadCenter("/fxml/professor-view.fxml",    VIEW_PROFESSOR);    }
    @FXML
    void openStudentView()     { loadCenter("/fxml/student-view.fxml",     VIEW_STUDENT);     }
    @FXML
    void openTramitView()      { loadCenter("/fxml/tramit-view.fxml",      VIEW_TRAMIT);      }
    @FXML
    void openEnrollmentView()  { loadCenter("/fxml/enrollment-view.fxml",  VIEW_ENROLLMENT);  }
    @FXML
    void openCourseSearchView() { loadCenter("/fxml/course-search-view.fxml", VIEW_BST_SEARCH); }
    @FXML
    void openPrerequisiteTreeView() { loadCenter("/fxml/prerequisite-tree-view.fxml", VIEW_PREREQ_TREE); }
    @FXML
    void openCampusMapView()   { loadCenter("/fxml/campus-view.fxml",      VIEW_CAMPUS_MAP); } // Vista del mapa del campus

    // --- Métodos para navegación con historial ---
    @FXML
    private void goBack() {
        String viewName = SessionHistoryService.getInstance().previousView();
        reloadViewByName(viewName);
        lblCurrentView.setText("Vista actual: " + viewName);
    }

    @FXML
    private void goForward() {
        String viewName = SessionHistoryService.getInstance().nextView();
        reloadViewByName(viewName);
        lblCurrentView.setText("Vista actual: " + viewName);
    }

    private void reloadViewByName(String viewName) {
        if (viewName == null || viewName.equals("Sin historial")) return;
        switch (viewName) {
            case VIEW_STUDENT     -> loadCenterNoHistory("/fxml/student-view.fxml");
            case VIEW_TRAMIT      -> loadCenterNoHistory("/fxml/tramit-view.fxml");
            case VIEW_ENROLLMENT  -> loadCenterNoHistory("/fxml/enrollment-view.fxml");
            case VIEW_BST_SEARCH  -> loadCenterNoHistory("/fxml/course-search-view.fxml");
            case VIEW_PREREQ_TREE -> loadCenterNoHistory("/fxml/prerequisite-tree-view.fxml");
            case VIEW_CAMPUS_MAP  -> loadCenterNoHistory("/fxml/campus-view.fxml");
            case VIEW_PROFESSOR   -> loadCenterNoHistory("/fxml/professor-view.fxml");
        }
    }

    // --- Cerrar sesión ---
    private void logout() {
        UserService.getInstance().logout();
        SessionHistoryService.getInstance().reset();
        Stage stage = (Stage) mainPane.getScene().getWindow();
        ViewFactory.showLoginView(stage);
    }

    // --- Mostrar información de la aplicación ---
    @FXML
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sprint 4");
        alert.setHeaderText("Sistema de Gestión Académica");
        alert.setContentText(
                "Sprint 4: Mapa del Campus con grafo, Dijkstra, BFS y DFS.\n" +
                        "Roles: ADMINISTRADOR / PROFESOR / ESTUDIANTE con accesos diferenciados.\n" +
                        "Vistas en historial: " + SessionHistoryService.getInstance().size()
        );
        alert.showAndWait();
    }

    // --- Notificaciones ---
    @Override
    public void onNotification(String message, String level) {
        if (lblNotificationBar != null)
            lblNotificationBar.setText("[" + level + "] " + message);
    }

    @Override
    public void onNotification(String message) { }

    // --- Cargar vistas en el centro ---
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