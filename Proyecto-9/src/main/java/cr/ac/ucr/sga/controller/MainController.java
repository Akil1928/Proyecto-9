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

    private static final String VIEW_PROFESSOR   = "Panel del Profesor";
    private static final String VIEW_STUDENT    = "Expediente Académico";
    private static final String VIEW_TRAMIT     = "Gestión de Trámites";
    private static final String VIEW_ENROLLMENT = "Cola de Matrícula";
    private static final String VIEW_PREREQ_TREE = "Árbol de Prerrequisitos";
    private static final String VIEW_BST_SEARCH  = "Búsqueda de Cursos (BST)";
    // ── NUEVO ──────────────────────────────────────────────────────────
    private static final String VIEW_CAMPUS      = "Mapa del Campus";

    @FXML
    public void initialize() {
        NotificationService.getInstance().addObserver(this);

        User user = UserService.getInstance().getCurrentUser();

        if (user != null) {
            lblUserInfo.setText("Sesión: " + user.getDisplayName()
                    + "  [" + user.getRole().name() + "]");
        }

        buildMenu(user);

        if (user != null && user.getRole() == User.Role.ADMINISTRADOR) {
            loadCenter("/fxml/student-view.fxml", VIEW_STUDENT);
        } else if (user != null && user.getRole() == User.Role.PROFESOR) {
            loadCenter("/fxml/professor-view.fxml", VIEW_PROFESSOR);
        } else {
            loadCenter("/fxml/tramit-view.fxml", VIEW_TRAMIT);
        }
    }

    private void buildMenu(User user) {
        menuPanel.getChildren().clear();

        boolean isAdmin = user != null && user.getRole() == User.Role.ADMINISTRADOR;
        boolean isProfesor = user != null && user.getRole() == User.Role.PROFESOR;

        if (isAdmin) {
            addMenuButton("Expediente Académico",      this::openStudentView);
            addMenuButton("Gestión de Trámites",       this::openTramitView);
            addMenuButton("Cola de Matrícula",         this::openEnrollmentView);
            addMenuButton("Búsqueda de Cursos (BST)",  this::openCourseSearchView);
            addMenuButton("Árbol de Prerrequisitos",   this::openPrerequisiteTreeView);
            // ── NUEVO ──────────────────────────────────────────────────
            addMenuButton("🗺  Mapa del Campus",       this::openCampusView);
        } else {
            addMenuButton("Gestión de Trámites",       this::openTramitView);
            addMenuButton("Cola de Matrícula",         this::openEnrollmentView);
            addMenuButton("Expediente Académico",      this::openStudentView);
            // ── NUEVO: estudiante también puede ver el mapa ───────────
            addMenuButton("🗺  Mapa del Campus",       this::openCampusView);
        }

        javafx.scene.control.Separator sep = new javafx.scene.control.Separator();
        menuPanel.getChildren().add(sep);

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

    @FXML private void goBack() {
        String viewName = SessionHistoryService.getInstance().previousView();
        reloadViewByName(viewName);
        lblCurrentView.setText("Vista actual: " + viewName);
    }

    @FXML private void goForward() {
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
            // ── NUEVO ──────────────────────────────────────────────────
            case VIEW_CAMPUS      -> loadCenterNoHistory("/fxml/campus-view.fxml");
            case VIEW_PROFESSOR   -> loadCenterNoHistory("/fxml/professor-view.fxml");
        }
    }

    @FXML void openStudentView()         { loadCenter("/fxml/student-view.fxml",         VIEW_STUDENT);    }
    @FXML void openTramitView()          { loadCenter("/fxml/tramit-view.fxml",          VIEW_TRAMIT);     }
    @FXML void openEnrollmentView()      { loadCenter("/fxml/enrollment-view.fxml",      VIEW_ENROLLMENT); }
    @FXML void openCourseSearchView()    { loadCenter("/fxml/course-search-view.fxml",   VIEW_BST_SEARCH); }
    @FXML void openPrerequisiteTreeView(){ loadCenter("/fxml/prerequisite-tree-view.fxml",VIEW_PREREQ_TREE);}
    // ── NUEVO ──────────────────────────────────────────────────────────
    @FXML void openProfessorView()        { loadCenter("/fxml/professor-view.fxml",   VIEW_PROFESSOR);  }
    @FXML void openCampusView()          { loadCenter("/fxml/campus-view.fxml",          VIEW_CAMPUS);     }

    private void logout() {
        UserService.getInstance().logout();
        SessionHistoryService.getInstance().reset();
        Stage stage = (Stage) mainPane.getScene().getWindow();
        ViewFactory.showLoginView(stage);
    }

    @FXML
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sprint 4");
        alert.setHeaderText("Sistema de Gestión Académica");
        alert.setContentText(
                "Sprint 4: Mapa del Campus (Grafo, Dijkstra, BFS, DFS).\n" +
                        "Roles: ADMINISTRADOR / ESTUDIANTE con accesos diferenciados.\n" +
                        "Vistas en historial: " + SessionHistoryService.getInstance().size()
        );
        alert.showAndWait();
    }

    @Override
    public void onNotification(String message, String level) {
        if (lblNotificationBar != null)
            lblNotificationBar.setText("[" + level + "] " + message);
    }

    @Override
    public void onNotification(String message) { }

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