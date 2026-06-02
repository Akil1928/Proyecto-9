package cr.ac.ucr.sga.controller;

import cr.ac.ucr.sga.model.entities.Student;
import cr.ac.ucr.sga.model.services.EnrollmentService;
import cr.ac.ucr.sga.model.services.NotificationService;
import cr.ac.ucr.sga.model.services.StudentDirectoryService;
import cr.ac.ucr.sga.view.observers.NotificationObserver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class EnrollmentController implements Initializable, NotificationObserver {
    @FXML private TableView<Student> enrollmentTable;
    @FXML private TableColumn<Student, String> colNombre;
    @FXML private TableColumn<Student, String> colCreditos;
    @FXML private TableColumn<Student, String> colPrioridad;
    @FXML private ComboBox<String> comboQueueType;
    @FXML private ComboBox<String> comboEstudiante;
    @FXML private Label lblSiguienteEstudiante;
    @FXML private Label lblNotificacion;
    @FXML private Label lblTotalEnCola;
    @FXML private Label lblCreditosAprobados;

    private final EnrollmentService service = EnrollmentService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        NotificationService.getInstance().addObserver(this);
        comboQueueType.setItems(FXCollections.observableArrayList("ArrayQueue", "LinkedQueue", "PriorityQueue"));
        comboQueueType.getSelectionModel().selectFirst();
        ObservableList<String> students = FXCollections.observableArrayList();
        for (var s : StudentDirectoryService.getInstance().getStudents()) {
            students.add(s.getId() + " - " + s.getName());
        }
        comboEstudiante.setItems(students);
        // actualizar créditos aprobados cuando se seleccione un estudiante
        comboEstudiante.setOnAction(evt -> {
            String val = comboEstudiante.getValue();
            if (val == null) {
                lblCreditosAprobados.setText("Créditos aprobados: 0");
                return;
            }
            String[] parts = val.split(" - ", 2);
            String id = parts[0];
            for (Student s : StudentDirectoryService.getInstance().getStudents()) {
                if (s.getId().equals(id)) {
                    lblCreditosAprobados.setText("Créditos aprobados: " + s.getApprovedCredits());
                    break;
                }
            }
        });
        // configurar columnas
        colNombre.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyStringWrapper(data.getValue().getName()));
        colCreditos.setCellValueFactory(data -> new javafx.beans.property.ReadOnlyStringWrapper(String.valueOf(data.getValue().getApprovedCredits())));
        colPrioridad.setCellValueFactory(data -> {
            int credits = data.getValue().getApprovedCredits();
            String priority = credits >= 160 ? "ALTA" : (credits >= 120 ? "MEDIA" : "BAJA");
            return new javafx.beans.property.ReadOnlyStringWrapper(priority);
        });

        refreshTable();
        updateLabels();
    }

    @FXML
    private void onAgregarACola() {
        if (comboEstudiante.getValue() == null) {
            showError("Seleccione un estudiante");
            return;
        }
        String[] student = comboEstudiante.getValue().split(" - ", 2);
        Student selected = null;
        for (Student s : StudentDirectoryService.getInstance().getStudents()) {
            if (s.getId().equals(student[0])) {
                selected = s;
                break;
            }
        }
        if (selected != null) {
            // validar que no esté duplicado
            var current = service.getCurrentQueueAsList();
            Student[] arr = new Student[current.size()];
            current.toArray(arr);
            for (Student s : arr) {
                if (s != null && s.getId().equals(selected.getId())) {
                    showError("El estudiante ya está en la cola");
                    return;
                }
            }

            service.enqueueStudent(selected);
            NotificationService.getInstance().notify(selected.getName() + " agregado a la cola de matrícula", "INFO");
            refreshTable();
            updateLabels();
        }
    }

    @FXML
    private void onMatricularSiguiente() {
        try {
            Student student = service.dequeueStudent();
            NotificationService.getInstance().notify(student.getName() + " ha sido matriculado", "INFO");
            refreshTable();
            updateLabels();
        } catch (Exception e) {
            showError("La cola de matrícula está vacía");
            NotificationService.getInstance().notify("La cola está vacía, no hay estudiantes que matricular", "URGENTE");
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

    private void refreshTable() {
        enrollmentTable.getItems().clear();
        int n = service.size();
        if (n == 0) return;
        Student[] arr = new Student[n];
        var list = service.getCurrentQueueAsList();
        list.toArray(arr);
        for (Student s : arr) {
            if (s != null) enrollmentTable.getItems().add(s);
        }
    }

    private void updateLabels() {
        lblTotalEnCola.setText("Total en cola: " + service.size());
        try {
            lblSiguienteEstudiante.setText("Siguiente a ser atendido: " + service.peekStudent().getName() + " - " + service.peekStudent().getApprovedCredits() + " créditos");
        } catch (Exception e) {
            lblSiguienteEstudiante.setText("Siguiente a ser atendido: vacío");
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
}


