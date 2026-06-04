package cr.ac.ucr.sga.controller;

import cr.ac.ucr.sga.model.entities.Student;
import cr.ac.ucr.sga.model.entities.User;
import cr.ac.ucr.sga.model.services.EnrollmentService;
import cr.ac.ucr.sga.model.services.NotificationService;
import cr.ac.ucr.sga.model.services.StudentDirectoryService;
import cr.ac.ucr.sga.model.services.UserService;
import cr.ac.ucr.sga.view.observers.NotificationObserver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class EnrollmentController implements Initializable, NotificationObserver {

    @FXML private TableView<Student>          enrollmentTable;
    @FXML private TableColumn<Student, String> colNombre;
    @FXML private TableColumn<Student, String> colCreditos;
    @FXML private TableColumn<Student, String> colPrioridad;

    // Controles informativos del estudiante (visibles para todos)
    @FXML private Label lblNombreEstudiante;
    @FXML private Label lblCreditosAprobados;
    @FXML private Button btnSolicitarMatricula;
    @FXML private Label lblMiPosicion;

    // Controles exclusivos del ADMINISTRADOR
    @FXML private Label      lblAdminSection;
    @FXML private ComboBox<String> comboEstudiante;
    @FXML private Label      lblCreditosSeleccionado;
    @FXML private Button     btnAgregarACola;
    @FXML private Button     btnMatricularSiguiente;
    @FXML private Label      lblTipoCola;
    @FXML private ComboBox<String> comboQueueType;
    @FXML private Label      lblSiguienteEstudiante;

    // Labels compartidos
    @FXML private Label lblNotificacion;
    @FXML private Label lblTotalEnCola;

    private final EnrollmentService service = EnrollmentService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        NotificationService.getInstance().addObserver(this);

        User currentUser = UserService.getInstance().getCurrentUser();
        boolean isAdmin = currentUser != null && currentUser.getRole() == User.Role.ADMINISTRADOR;

        // Configurar columnas (siempre)
        colNombre.setCellValueFactory(data ->
                new javafx.beans.property.ReadOnlyStringWrapper(data.getValue().getName()));
        colCreditos.setCellValueFactory(data ->
                new javafx.beans.property.ReadOnlyStringWrapper(
                        String.valueOf(data.getValue().getApprovedCredits())));
        colPrioridad.setCellValueFactory(data -> {
            int credits = data.getValue().getApprovedCredits();
            String priority = credits >= 160 ? "ALTA" : (credits >= 80 ? "MEDIA" : "BAJA");
            return new javafx.beans.property.ReadOnlyStringWrapper(priority);
        });

        // La tabla no es reordenable manualmente (US-08)
        enrollmentTable.setEditable(false);

        if (isAdmin) {
            // ── ADMINISTRADOR ──
            setupAdminControls();
        } else {
            // ── ESTUDIANTE ──
            setupStudentView(currentUser);
        }

        refreshTable();
        updateLabels();
    }

    // ─── Configuración de la vista del ADMINISTRADOR ──────────────────────────

    private void setupAdminControls() {
        // El admin selecciona el tipo de cola
        comboQueueType.setItems(
                FXCollections.observableArrayList("ArrayQueue", "LinkedQueue", "PriorityQueue"));
        comboQueueType.getSelectionModel().select("PriorityQueue");
        service.setQueueType("priority"); // Por defecto usa PriorityQueue para matrícula

        // Llenar combo de estudiantes
        ObservableList<String> students = FXCollections.observableArrayList();
        for (var s : StudentDirectoryService.getInstance().getStudents()) {
            students.add(s.getId() + " - " + s.getName());
        }
        comboEstudiante.setItems(students);

        // Actualizar créditos al seleccionar un estudiante
        comboEstudiante.setOnAction(evt -> {
            String val = comboEstudiante.getValue();
            if (val == null) {
                if (lblCreditosSeleccionado != null)
                    lblCreditosSeleccionado.setText("Créditos: 0");
                return;
            }
            String id = val.split(" - ", 2)[0];
            for (Student s : StudentDirectoryService.getInstance().getStudents()) {
                if (s.getId().equals(id)) {
                    if (lblCreditosSeleccionado != null)
                        lblCreditosSeleccionado.setText("Créditos: " + s.getApprovedCredits());
                    break;
                }
            }
        });

        // Ocultar el botón "Solicitar Matrícula" (es del estudiante)
        setVisible(btnSolicitarMatricula, false);
        setVisible(lblMiPosicion, false);
    }

    // ─── Configuración de la vista del ESTUDIANTE ─────────────────────────────

    private void setupStudentView(User currentUser) {
        // Mostrar nombre y créditos del estudiante actual
        if (lblNombreEstudiante != null)
            lblNombreEstudiante.setText("Estudiante: " + currentUser.getDisplayName());

        // Buscar los créditos del estudiante actual en el directorio
        int creditos = 0;
        for (Student s : StudentDirectoryService.getInstance().getStudents()) {
            if (s.getId().equals(currentUser.getUsername())) {
                creditos = s.getApprovedCredits();
                break;
            }
        }
        if (lblCreditosAprobados != null)
            lblCreditosAprobados.setText("Créditos aprobados: " + creditos);

        // Ocultar todos los controles exclusivos del administrador
        setVisible(lblAdminSection, false);
        setVisible(comboEstudiante, false);
        setVisible(lblCreditosSeleccionado, false);
        setVisible(btnAgregarACola, false);
        setVisible(btnMatricularSiguiente, false);
        setVisible(lblTipoCola, false);
        setVisible(comboQueueType, false);
        setVisible(lblSiguienteEstudiante, false);
    }

    // ─── Acciones ─────────────────────────────────────────────────────────────

    /**
     * US-07: El estudiante solicita su propia matrícula.
     * El sistema calcula la prioridad en base a los créditos aprobados del estudiante.
     * El estudiante NO elige su prioridad; el sistema la asigna automáticamente.
     */
    @FXML
    private void onSolicitarMatricula() {
        User currentUser = UserService.getInstance().getCurrentUser();
        if (currentUser == null) return;

        // Buscar al estudiante en el directorio por su username/id
        Student estudianteActual = null;
        for (Student s : StudentDirectoryService.getInstance().getStudents()) {
            if (s.getId().equals(currentUser.getUsername())) {
                estudianteActual = s;
                break;
            }
        }

        if (estudianteActual == null) {
            showError("No se encontró tu perfil de estudiante en el sistema.");
            return;
        }

        // Verificar que no esté duplicado
        for (Student s : service.getCurrentQueueAsList().toArray(new Student[0])) {
            if (s != null && s.getId().equals(estudianteActual.getId())) {
                showError("Ya tienes una solicitud de matrícula en espera.");
                return;
            }
        }

        // El sistema calcula la prioridad automáticamente (US-07)
        service.enqueueStudent(estudianteActual);

        NotificationService.getInstance().notify(
                currentUser.getDisplayName() + " solicitó matrícula (prioridad calculada por créditos)", "INFO");

        refreshTable();
        updateLabels();
        actualizarPosicionEstudiante(estudianteActual);
    }

    /**
     * US-08 (admin): Agrega un estudiante seleccionado manualmente a la cola.
     * SOLO para ADMINISTRADOR.
     */
    @FXML
    private void onAgregarACola() {
        if (comboEstudiante.getValue() == null) {
            showError("Seleccione un estudiante.");
            return;
        }
        String[] parts = comboEstudiante.getValue().split(" - ", 2);
        Student selected = null;
        for (Student s : StudentDirectoryService.getInstance().getStudents()) {
            if (s.getId().equals(parts[0])) {
                selected = s;
                break;
            }
        }
        if (selected == null) return;

        // Verificar duplicado
        for (Student s : service.getCurrentQueueAsList().toArray(new Student[0])) {
            if (s != null && s.getId().equals(selected.getId())) {
                showError("El estudiante ya está en la cola.");
                return;
            }
        }

        service.enqueueStudent(selected);
        NotificationService.getInstance().notify(
                selected.getName() + " agregado a la cola de matrícula", "INFO");
        refreshTable();
        updateLabels();
    }

    /**
     * US-08 (admin): Matricula al estudiante al frente de la cola de prioridad (Dequeue).
     * SOLO para ADMINISTRADOR. El admin no puede saltarse el orden.
     */
    @FXML
    private void onMatricularSiguiente() {
        try {
            Student student = service.dequeueStudent();
            NotificationService.getInstance().notify(
                    student.getName() + " ha sido matriculado exitosamente", "INFO");
            refreshTable();
            updateLabels();
        } catch (Exception e) {
            showError("La cola de matrícula está vacía.");
            NotificationService.getInstance().notify("Cola vacía, sin estudiantes pendientes", "URGENTE");
        }
    }

    @FXML
    private void onCambiarTipoCola() {
        String type = comboQueueType.getValue();
        if ("LinkedQueue".equals(type)) {
            service.setQueueType("linked");
        } else if ("PriorityQueue".equals(type)) {
            service.setQueueType("priority");
        } else {
            service.setQueueType("array");
        }
        refreshTable();
        updateLabels();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void refreshTable() {
        enrollmentTable.getItems().clear();
        var list = service.getCurrentQueueAsList();
        for (int i = 0; i < list.size(); i++) {
            Student s = list.get(i);
            if (s != null) enrollmentTable.getItems().add(s);
        }
    }

    private void updateLabels() {
        lblTotalEnCola.setText("Total en cola: " + service.size());
        try {
            Student next = service.peekStudent();
            if (lblSiguienteEstudiante != null)
                lblSiguienteEstudiante.setText("Siguiente: " + next.getName()
                        + " - " + next.getApprovedCredits() + " créditos");
        } catch (Exception e) {
            if (lblSiguienteEstudiante != null)
                lblSiguienteEstudiante.setText("Siguiente: vacío");
        }
    }

    private void actualizarPosicionEstudiante(Student estudianteActual) {
        if (lblMiPosicion == null) return;
        var list = service.getCurrentQueueAsList();
        int pos = 0;
        for (int i = 0; i < list.size(); i++) {
            Student s = list.get(i);
            if (s != null && s.getId().equals(estudianteActual.getId())) {
                pos = i + 1;
                break;
            }
        }
        lblMiPosicion.setText("Tu posición en la cola: #" + pos);
    }

    /** Oculta un control y también lo elimina del layout (setManaged false). */
    private void setVisible(javafx.scene.Node node, boolean visible) {
        if (node != null) {
            node.setVisible(visible);
            node.setManaged(visible);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operación no permitida");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ── Observer (US-09) ──────────────────────────────────────────────────────

    @Override
    public void onNotification(String message, String level) {
        String text = "[" + level + "] " + message;
        lblNotificacion.setText(text);
        lblNotificacion.getStyleClass().removeIf(s -> s.startsWith("notif-"));
        if (level == null) level = "INFO";
        switch (level.toUpperCase()) {
            case "URGENTE"     -> lblNotificacion.getStyleClass().add("notif-urgent");
            case "ADVERTENCIA" -> lblNotificacion.getStyleClass().add("notif-warning");
            default            -> lblNotificacion.getStyleClass().add("notif-info");
        }
    }

    @Override
    public void onNotification(String message) { }
}