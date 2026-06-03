package cr.ac.ucr.sga.controller;

import cr.ac.ucr.sga.model.entities.Tramit;
import cr.ac.ucr.sga.model.entities.User;
import cr.ac.ucr.sga.model.services.NotificationService;
import cr.ac.ucr.sga.model.services.StudentDirectoryService;
import cr.ac.ucr.sga.model.services.TramitService;
import cr.ac.ucr.sga.model.services.UserService;
import cr.ac.ucr.sga.view.observers.NotificationObserver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class TramitController implements Initializable, NotificationObserver {
    @FXML private TableView<Tramit> tramitTable;
    @FXML private TableColumn<Tramit, String> colId;
    @FXML private TableColumn<Tramit, String> colType;
    @FXML private TableColumn<Tramit, String> colStudent;
    @FXML private TableColumn<Tramit, String> colState;
    @FXML private TableColumn<Tramit, String> colDate;
    @FXML private ComboBox<String> comboTipoTramit;
    @FXML private ComboBox<String> comboEstudiante;
    @FXML private TextArea txtDescription;
    @FXML private ComboBox<String> comboStackType;
    @FXML private Label lblTopeTramit;
    @FXML private Label lblNotificacion;
    @FXML private Label lblTotalTramits;

    // Controles exclusivos del ADMINISTRADOR (con fx:id en FXML)
    @FXML private Button btnProcesar;
    @FXML private Button btnAvanzarEstado;
    @FXML private Label lblStackSection;

    private final TramitService service = TramitService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        NotificationService.getInstance().addObserver(this);

        User currentUser = UserService.getInstance().getCurrentUser();
        boolean isAdmin = currentUser != null && currentUser.getRole() == User.Role.ADMINISTRADOR;

        comboTipoTramit.setItems(FXCollections.observableArrayList(
                "Beca", "Revisión de Nota", "Retiro de Curso", "Certificado", "Traslado"));
        comboStackType.setItems(FXCollections.observableArrayList("ArrayStack", "LinkedStack"));
        comboStackType.getSelectionModel().selectFirst();

        if (isAdmin) {
            // Admin: puede seleccionar cualquier estudiante
            ObservableList<String> students = FXCollections.observableArrayList();
            for (var s : StudentDirectoryService.getInstance().getStudents()) {
                students.add(s.getId() + " - " + s.getName());
            }
            comboEstudiante.setItems(students);
        } else {
            // Estudiante (US-05): solo puede crear trámite a su nombre
            // Pre-selecciona el usuario actual; el combo queda deshabilitado
            String selfEntry = currentUser.getUsername() + " - " + currentUser.getDisplayName();
            comboEstudiante.setItems(FXCollections.observableArrayList(selfEntry));
            comboEstudiante.getSelectionModel().selectFirst();
            comboEstudiante.setDisable(true);

            // Ocultar controles exclusivos del administrador
            if (btnProcesar != null)     btnProcesar.setVisible(false);
            if (btnAvanzarEstado != null) btnAvanzarEstado.setVisible(false);
            if (lblStackSection != null) lblStackSection.setVisible(false);
            comboStackType.setVisible(false);
        }

        // Configurar columnas
        colId.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyStringWrapper(data.getValue().getId()));
        colType.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyStringWrapper(data.getValue().getType()));
        colStudent.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyStringWrapper(data.getValue().getStudentName()));
        colState.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyStringWrapper(data.getValue().getStateName()));
        colDate.setCellValueFactory(data -> {
            var dt = data.getValue().getCreatedAt();
            String formatted = dt == null ? "" : dt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            return new javafx.beans.property.ReadOnlyStringWrapper(formatted);
        });

        refreshTable();
        updateLabels();
    }

    @FXML
    private void onCrearTramit() {
        try {
            validateFields();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
            return;
        }

        String[] student = comboEstudiante.getValue().split(" - ", 2);
        Tramit tramit = new Tramit(comboTipoTramit.getValue(), txtDescription.getText().trim(), student[0], student[1]);
        service.pushTramit(tramit);
        NotificationService.getInstance().notify("Trámite " + tramit.getId() + " creado exitosamente", "INFO");
        refreshTable();
        updateLabels();
    }

    // Solo para ADMINISTRADOR (US-06: LIFO)
    @FXML
    private void onProcesarTramit() {
        try {
            Tramit popped = service.popTramit();
            NotificationService.getInstance().notify("Trámite " + popped.getId() + " procesado", "INFO");
            refreshTable();
            updateLabels();
        } catch (cr.ac.ucr.sga.model.structures.stacks.StackException e) {
            showError("La pila está vacía");
            NotificationService.getInstance().notify("La pila está vacía, no hay trámites que procesar", "URGENTE");
        }
    }

    // Solo para ADMINISTRADOR
    @FXML
    private void onAvanzarEstado() {
        try {
            Tramit tramit = service.peekTramit();
            try {
                tramit.nextState();
            } catch (IllegalStateException ise) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Estado");
                alert.setHeaderText("Información");
                alert.setContentText("El trámite ya fue resuelto");
                alert.showAndWait();
                return;
            }
            refreshTable();
            updateLabels();
        } catch (cr.ac.ucr.sga.model.structures.stacks.StackException e) {
            showError("La pila está vacía");
        }
    }

    @FXML
    private void onCambiarTipoStack() {
        String type = comboStackType.getValue();
        service.setStackType("LinkedStack".equals(type) ? "linked" : "array");
        refreshTable();
        updateLabels();
    }

    private void refreshTable() {
        tramitTable.getItems().clear();
        int n = service.getAllTramits().size();
        if (n == 0) return;
        Tramit[] arr = new Tramit[n];
        service.getAllTramits().toArray(arr);
        for (int i = n - 1; i >= 0; i--) {
            if (arr[i] != null) tramitTable.getItems().add(arr[i]);
        }
    }

    private void updateLabels() {
        lblTotalTramits.setText("Total de trámites en pila: " + service.size());
        try {
            var top = service.peekTramit();
            lblTopeTramit.setText("Tope de la pila: " + top.getType() + " - " + top.getStudentName() + " - " + top.getStateName());
        } catch (Exception e) {
            lblTopeTramit.setText("Tope de la pila: vacío");
        }
    }

    private void validateFields() {
        if (comboTipoTramit.getValue() == null) {
            throw new IllegalArgumentException("Seleccione el tipo de trámite");
        }
        if (comboEstudiante.getValue() == null) {
            throw new IllegalArgumentException("Seleccione un estudiante");
        }
        if (txtDescription.getText() == null || txtDescription.getText().isBlank()) {
            throw new IllegalArgumentException("Ingrese una descripción");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validación");
        alert.setHeaderText("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void onNotification(String message, String level) {
        String text = level + ": " + message;
        lblNotificacion.setText(text);
        lblNotificacion.getStyleClass().removeIf(s -> s.startsWith("notif-"));
        if (level == null) level = "INFO";
        switch (level.toUpperCase()) {
            case "URGENTE" -> lblNotificacion.getStyleClass().add("notif-urgent");
            case "ADVERTENCIA" -> lblNotificacion.getStyleClass().add("notif-warning");
            default -> lblNotificacion.getStyleClass().add("notif-info");
        }
    }

    @Override
    public void onNotification(String message) {
    }
}