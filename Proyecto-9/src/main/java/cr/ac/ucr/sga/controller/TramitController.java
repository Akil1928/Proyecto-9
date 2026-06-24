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

    //controles exclusivos del ADMINISTRADOR
    @FXML private ComboBox<String> comboStackType;
    @FXML private Button btnProcesar;
    @FXML private Button btnAvanzarEstado;
    @FXML private Label  lblStackSection;
    @FXML private Label  lblAdminSection;

    //labels de estado/info
    @FXML private Label lblTopeTramit;
    @FXML private Label lblNotificacion;
    @FXML private Label lblTotalTramits;
    @FXML private Label lblEstadoInfo;

    private final TramitService service = TramitService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        NotificationService.getInstance().addObserver(this);

        User currentUser = UserService.getInstance().getCurrentUser();
        boolean isAdmin = currentUser != null && currentUser.getRole() == User.Role.ADMINISTRADOR;

        //tipos de trámite disponibles para todos
        comboTipoTramit.setItems(FXCollections.observableArrayList(
                "Beca", "Revisión de Nota", "Retiro de Curso", "Certificado", "Traslado"));

        if (isAdmin) {
            //ADMINISTRADOR
            //puede seleccionar cualquier estudiante
            comboStackType.setItems(FXCollections.observableArrayList("ArrayStack", "LinkedStack"));
            comboStackType.getSelectionModel().selectFirst();

            ObservableList<String> students = FXCollections.observableArrayList();
            for (var s : StudentDirectoryService.getInstance().getStudents()) {
                students.add(s.getId() + " - " + s.getName());
            }
            comboEstudiante.setItems(students);
        } else {
            //ESTUDIANTE
            //el estudiante solo puede enviar trámites a su propio nombre (US-05).
            //el combo queda bloqueado con su usuario pre-cargado.
            String selfEntry = currentUser.getUsername() + " - " + currentUser.getDisplayName();
            comboEstudiante.setItems(FXCollections.observableArrayList(selfEntry));
            comboEstudiante.getSelectionModel().selectFirst();
            comboEstudiante.setDisable(true); // no puede cambiarlo

            //ocultar y desactivar todos los controles exclusivos del administrador
            setAdminControlsVisible(false);
        }

        //configurar columnas de la tabla
        colId.setCellValueFactory(data ->
                new javafx.beans.property.ReadOnlyStringWrapper(
                        data.getValue().getId().substring(0, 8) + "..."));
        colType.setCellValueFactory(data ->
                new javafx.beans.property.ReadOnlyStringWrapper(data.getValue().getType()));
        colStudent.setCellValueFactory(data ->
                new javafx.beans.property.ReadOnlyStringWrapper(data.getValue().getStudentName()));
        colState.setCellValueFactory(data ->
                new javafx.beans.property.ReadOnlyStringWrapper(data.getValue().getStateName()));
        colDate.setCellValueFactory(data -> {
            var dt = data.getValue().getCreatedAt();
            String formatted = dt == null ? "" :
                    dt.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            return new javafx.beans.property.ReadOnlyStringWrapper(formatted);
        });

        refreshTable();
        updateLabels();
    }

    /**
     * Oculta y desactiva todos los controles que solo debe ver/usar el administrador.
     */
    private void setAdminControlsVisible(boolean visible) {
        if (btnProcesar != null) {
            btnProcesar.setVisible(visible);
            btnProcesar.setManaged(visible);
        }
        if (btnAvanzarEstado != null) {
            btnAvanzarEstado.setVisible(visible);
            btnAvanzarEstado.setManaged(visible);
        }
        if (lblStackSection != null) {
            lblStackSection.setVisible(visible);
            lblStackSection.setManaged(visible);
        }
        if (lblAdminSection != null) {
            lblAdminSection.setVisible(visible);
            lblAdminSection.setManaged(visible);
        }
        if (comboStackType != null) {
            comboStackType.setVisible(visible);
            comboStackType.setManaged(visible);
        }
        //la tabla es de solo lectura para el estudiante (no puede interactuar con estados)
        if (tramitTable != null) {
            tramitTable.setEditable(false);
        }
    }

    /**
     * US-05: Enviar un trámite.
     * - El estado "Pendiente" se asigna automáticamente por código.
     * - El estudiante NO elige el estado.
     */
    @FXML
    private void onCrearTramit() {
        try {
            validateFields();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
            return;
        }

        String[] student = comboEstudiante.getValue().split(" - ", 2);

        //el estado se fuerza a "Pendiente" dentro del constructor de Tramit (new PendingState())
        //el estudiante nunca elige el estado manualmente.
        Tramit tramit = new Tramit(
                comboTipoTramit.getValue(),
                txtDescription.getText().trim(),
                student[0],
                student[1]
        );

        service.pushTramit(tramit);
        NotificationService.getInstance().notify(
                "Trámite " + tramit.getId().substring(0, 8) + "... enviado. Estado: Pendiente", "INFO");

        refreshTable();
        updateLabels();
        clearForm();
    }

    /**
     * US-06: Procesar el trámite del tope de la pila (LIFO).
     * SOLO disponible para el ADMINISTRADOR.
     * El admin no puede elegir qué trámite procesar; solo saca el del tope.
     */
    @FXML
    private void onProcesarTramit() {
        try {
            Tramit popped = service.popTramit();
            NotificationService.getInstance().notify(
                    "Trámite " + popped.getId().substring(0, 8) + "... procesado (retirado de pila)", "INFO");
            refreshTable();
            updateLabels();
        } catch (cr.ac.ucr.sga.model.structures.stacks.StackException e) {
            showError("La pila está vacía. No hay trámites pendientes.");
            NotificationService.getInstance().notify("La pila está vacía", "URGENTE");
        }
    }

    /**
     * Avanzar el estado del trámite en el tope (Pendiente → Procesando → Resuelto).
     * SOLO disponible para el ADMINISTRADOR.
     * Esto dispara automáticamente una notificación al sistema (US-09).
     */
    @FXML
    private void onAvanzarEstado() {
        try {
            Tramit tramit = service.peekTramit();
            String estadoAnterior = tramit.getStateName();

            try {
                tramit.nextState();
            } catch (IllegalStateException ise) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Estado final");
                alert.setHeaderText("El trámite ya fue resuelto");
                alert.setContentText("No se puede avanzar más el estado de este trámite.");
                alert.showAndWait();
                return;
            }

            String estadoNuevo = tramit.getStateName();

            //US-09: El sistema notifica automáticamente el cambio de estado.
            //ni el estudiante ni el admin disparan esto con un botón específico;
            //ocurre como consecuencia directa de que el admin avanzó el estado.
            String nivelNuevo = estadoNuevo.equalsIgnoreCase("Resuelto") ? "INFO" : "ADVERTENCIA";
            String msgVisible = "Tu trámite (" + tramit.getType() + ") cambió: "
                    + estadoAnterior + " → " + estadoNuevo;

//guardar pendiente para cuando el estudiante se loguee
            NotificationService.getInstance().queueForUser(tramit.getStudentId(), msgVisible, nivelNuevo);

//notificar a observers activos (si el admin mismo está viendo la vista)
            NotificationService.getInstance().notify(
                    "[TO:" + tramit.getStudentId() + "] " + msgVisible,
                    nivelNuevo
            );

            refreshTable();
            updateLabels();

        } catch (cr.ac.ucr.sga.model.structures.stacks.StackException e) {
            showError("La pila está vacía.");
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
        User currentUser = UserService.getInstance().getCurrentUser();
        boolean isAdmin = currentUser != null && currentUser.getRole() == User.Role.ADMINISTRADOR;
        boolean isProfesor = currentUser != null && currentUser.getRole() == User.Role.PROFESOR;

        int n = service.getAllTramits().size();
        if (n == 0) return;
        Tramit[] arr = new Tramit[n];
        service.getAllTramits().toArray(arr);

        for (int i = n - 1; i >= 0; i--) {
            if (arr[i] == null) continue;
            //admin y Profesor ven todos; Estudiante solo los suyos
            if (isAdmin || isProfesor ||
                    arr[i].getStudentId().equals(currentUser.getUsername())) {
                tramitTable.getItems().add(arr[i]);
            }
        }
    }

    private void updateLabels() {
        lblTotalTramits.setText("Total en pila: " + service.size());
        try {
            var top = service.peekTramit();
            lblTopeTramit.setText("Tope: " + top.getType()
                    + " | " + top.getStudentName()
                    + " | " + top.getStateName());
        } catch (Exception e) {
            lblTopeTramit.setText("Tope: vacío");
        }
    }

    private void clearForm() {
        comboTipoTramit.getSelectionModel().clearSelection();
        txtDescription.clear();
    }

    private void validateFields() {
        if (comboTipoTramit.getValue() == null)
            throw new IllegalArgumentException("Seleccione el tipo de trámite.");
        if (comboEstudiante.getValue() == null)
            throw new IllegalArgumentException("Seleccione un estudiante.");
        if (txtDescription.getText() == null || txtDescription.getText().isBlank())
            throw new IllegalArgumentException("Ingrese una descripción del trámite.");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operación no permitida");
        alert.setContentText(message);
        alert.showAndWait();
    }

    //Observer recibe notificaciones automáticas del sistema

    @Override
    public void onNotification(String message, String level) {
        String text = "[" + level + "] " + message;
        lblNotificacion.setText(text);
        lblNotificacion.getStyleClass().removeIf(s -> s.startsWith("notif-"));
        if (level == null) level = "INFO";
        switch (level.toUpperCase()) {
            case "URGENTE"      -> lblNotificacion.getStyleClass().add("notif-urgent");
            case "ADVERTENCIA"  -> lblNotificacion.getStyleClass().add("notif-warning");
            default             -> lblNotificacion.getStyleClass().add("notif-info");
        }
    }

    @Override
    public void onNotification(String message) { }
}