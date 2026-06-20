package cr.ac.ucr.sga.controller;

import cr.ac.ucr.sga.model.entities.AcademicRecordEntry;
import cr.ac.ucr.sga.model.entities.Course;
import cr.ac.ucr.sga.model.entities.CourseBuilder;
import cr.ac.ucr.sga.model.entities.User;
import cr.ac.ucr.sga.model.services.AcademicRecordService;
import cr.ac.ucr.sga.model.services.CurriculumService;
import cr.ac.ucr.sga.model.services.JsonService;
import cr.ac.ucr.sga.model.services.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class StudentController {

    @FXML private TextField txtCode;
    @FXML private TextField txtName;
    @FXML private TextField txtCredits;
    @FXML private TextField txtGrade;
    @FXML private ComboBox<String> cbStatus;
    @FXML private ComboBox<String> cbPeriod;
    @FXML private TableView<AcademicRecordEntry> tableRecord;
    @FXML private TableColumn<AcademicRecordEntry, String> colCode;
    @FXML private TableColumn<AcademicRecordEntry, String> colName;
    @FXML private TableColumn<AcademicRecordEntry, String> colPeriod;
    @FXML private TableColumn<AcademicRecordEntry, Number> colCredits;
    @FXML private TableColumn<AcademicRecordEntry, Number> colGrade;
    @FXML private TableColumn<AcademicRecordEntry, String> colStatus;
    @FXML private Label lblStatus;
    @FXML private Button btnLoadDemo;
    @FXML private VBox panelAdminCurso;
    @FXML private Label lblNombreEstudiante;
    @FXML private Label lblCreditosAprobados;

    private final AcademicRecordService service    = AcademicRecordService.getInstance();
    private final CurriculumService     curriculum = CurriculumService.getInstance();
    private final ObservableList<AcademicRecordEntry> tableData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            if (cbStatus != null)
                cbStatus.setItems(FXCollections.observableArrayList("Aprobado", "Reprobado", "En curso"));
            if (cbPeriod != null)
                cbPeriod.setItems(FXCollections.observableArrayList("I-2025", "II-2025", "I-2026", "II-2026"));
            if (cbStatus != null) cbStatus.getSelectionModel().selectFirst();
            if (cbPeriod != null) cbPeriod.getSelectionModel().selectFirst();

            colCode.setCellValueFactory(data -> data.getValue().getCourse().codeProperty());
            colName.setCellValueFactory(data -> data.getValue().getCourse().nameProperty());
            colCredits.setCellValueFactory(data -> data.getValue().getCourse().creditsProperty());
            colGrade.setCellValueFactory(data -> data.getValue().gradeProperty());
            colStatus.setCellValueFactory(data -> data.getValue().statusProperty());
            colPeriod.setCellValueFactory(data -> data.getValue().periodProperty());

            tableRecord.setItems(tableData);

            User currentUser = UserService.getInstance().getCurrentUser();
            boolean isAdmin = currentUser != null && currentUser.getRole() == User.Role.ADMINISTRADOR;

            if (isAdmin) {
                if (panelAdminCurso != null) panelAdminCurso.setVisible(true);
                if (lblNombreEstudiante != null) lblNombreEstudiante.setText("(Administrador)");
                refreshCreditos();
                addDemoCourses();
            } else {
                // ESTUDIANTE: ocultar formulario de agregar cursos
                if (panelAdminCurso != null) {
                    panelAdminCurso.setVisible(false);
                    panelAdminCurso.setManaged(false);
                }
                if (btnLoadDemo != null) {
                    btnLoadDemo.setVisible(false);
                    btnLoadDemo.setManaged(false);
                }
                if (lblNombreEstudiante != null && currentUser != null) {
                    lblNombreEstudiante.setText(currentUser.getDisplayName());
                }
                refreshTable();
                refreshCreditos();
                setStatus(tableData.isEmpty()
                        ? "Tu expediente académico está vacío."
                        : "Expediente cargado con " + tableData.size() + " curso(s).");
            }
        } catch (Exception ex) {
            // Evitar que una excepción en initialize provoque InvocationTargetException en FXMLLoader.
            ex.printStackTrace();
            // Mostrar un mensaje en la UI si es posible
            if (lblStatus != null) {
                lblStatus.setText("Error al inicializar la vista: " + ex.getMessage());
            } else {
                // Si lblStatus no está inicializado, usar un alert modal
                showError("Error al inicializar la vista", ex.getMessage());
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Acciones disponibles SOLO para el ADMINISTRADOR
    // ─────────────────────────────────────────────────────────────────────────

    @FXML
    private void addCourse() {
        try {
            validateFields();
        } catch (IllegalArgumentException ex) {
            showError("Datos inválidos", ex.getMessage());
            return;
        }

        String code = txtCode.getText().trim().toUpperCase();

        // 1. Verificar duplicado en el expediente
        if (service.containsCourse(code)) {
            showError("Curso duplicado",
                    "El curso " + code + " ya está registrado en el expediente.");
            return;
        }
        //verificar si el curso está en la malla curricular
        boolean inCurriculum = curriculum.isInCurriculum(code);

        //validar requisitos SOLO si el curso está en la malla
        //y SOLO si el estado que se va a agregar es "En curso" o "Aprobado"
        //si se agrega como "Reprobado" también se valida, porque igual hubo intento).
        if (inCurriculum) {
            CurriculumService.ValidationResult result = service.validatePrerequisites(code);
            if (!result.isValid()) {
                String nombreOficial = curriculum.getCourseName(code);
                String header = "Requisitos no cumplidos para \""
                        + code + (nombreOficial != null ? " - " + nombreOficial : "") + "\"";
                showError(header, result.buildErrorMessage(code));
                return;
            }
        }

        //Todo bien agregar al expediente
        try {
            Course course = new CourseBuilder()
                    .setCode(code)
                    .setName(txtName.getText().trim())
                    .setCredits(Integer.parseInt(txtCredits.getText().trim()))
                    .build();

            AcademicRecordEntry entry = new AcademicRecordEntry(
                    course,
                    cbPeriod.getValue(),
                    Double.parseDouble(txtGrade.getText().trim()),
                    cbStatus.getValue()
            );

            service.addRecord(entry);
            refreshTable();
            refreshCreditos();
            clearFields();
            JsonService.saveAcademicRecord(service.toArray(), "src/main/resources/data/courses.json");
            setStatus("✔ Curso \"" + course.getName() + "\" agregado. Total: " + service.size() + " curso(s).");

        } catch (IllegalArgumentException ex) {
            showError("Error al crear el curso", ex.getMessage());
        }
    }

    @FXML
    private void deleteSelectedCourse() {
        AcademicRecordEntry selected = tableRecord.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Sin selección", "Seleccione un curso en la tabla para eliminarlo.");
            return;
        }

        //advertir si otros cursos en el expediente dependen de este
        String code = selected.getCourse().getCode();
        List<String> dependants = findDependantCourses(code);

        String content = "Curso: " + selected.getCourse().getName()
                + " (" + code + ")";
        if (!dependants.isEmpty()) {
            content += "\n\n⚠ Atención: los siguientes cursos del expediente "
                    + "tienen este curso como requisito:\n  • "
                    + String.join("\n  • ", dependants);
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar el curso seleccionado?");
        confirm.setContentText(content);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String nombre = selected.getCourse().getName();
                service.removeRecordByCode(code);
                refreshTable();
                refreshCreditos();
                JsonService.saveAcademicRecord(service.toArray(),
                        "src/main/resources/data/courses.json");
                setStatus("✔ Curso \"" + nombre + "\" eliminado. Quedan: "
                        + service.size() + " curso(s).");
            }
        });
    }

    @FXML
    private void addDemoCourses() {
        service.clear();

        //cursos del I y II ciclo (sin requisitos) → se pueden agregar directamente
        service.addRecord(new AcademicRecordEntry(
                new CourseBuilder().setCode("IF-0001")
                        .setName("Desarrollo de Software I").setCredits(4).build(),
                "I-2024", 85, "Aprobado"));

        service.addRecord(new AcademicRecordEntry(
                new CourseBuilder().setCode("IF-0003")
                        .setName("Matemática Básica para Informática Empresarial").setCredits(3).build(),
                "I-2024", 78, "Aprobado"));

        //IF-0004 requiere IF-0001 o IF-2000 → ya está aprobado IF-0001 ✔
        service.addRecord(new AcademicRecordEntry(
                new CourseBuilder().setCode("IF-0004")
                        .setName("Desarrollo de Software II").setCredits(4).build(),
                "II-2024", 90, "Aprobado"));

        //IF-3001 requiere IF-0004 o IF-2000 → ya está aprobado IF-0004 ✔
        service.addRecord(new AcademicRecordEntry(
                new CourseBuilder().setCode("IF-3001")
                        .setName("Algoritmos y Estructuras de Datos").setCredits(4).build(),
                "I-2025", 95, "Aprobado"));

        //IF-0007 requiere IF-0004 → ya está aprobado ✔
        service.addRecord(new AcademicRecordEntry(
                new CourseBuilder().setCode("IF-0007")
                        .setName("Bases de Datos I").setCredits(4).build(),
                "I-2025", 82, "Aprobado"));

        refreshTable();
        refreshCreditos();
        JsonService.saveAcademicRecord(service.toArray(), "src/main/resources/data/courses.json");
        setStatus("✔ 5 cursos demo cargados (respetando la malla curricular).");
    }
    //helpers
    private java.util.List<String> findDependantCourses(String removedCode) {
        java.util.List<String> dependants = new java.util.ArrayList<>();
        for (AcademicRecordEntry entry : service.toArray()) {
            if (entry == null) continue;
            String entryCode = entry.getCourse().getCode();
            if (entryCode.equalsIgnoreCase(removedCode)) continue;
            // ¿Este curso tiene al eliminado como requisito?
            for (java.util.Set<String> group : curriculum.getPrerequisiteGroups(entryCode)) {
                if (group.contains(removedCode.toUpperCase())) {
                    dependants.add(entryCode + " - " + entry.getCourse().getName());
                    break;
                }
            }
        }
        return dependants;
    }

    private void refreshTable() {
        tableData.clear();
        for (AcademicRecordEntry entry : service.toArray()) {
            tableData.add(entry);
        }
    }

    private void refreshCreditos() {
        int total = service.getTotalApprovedCredits();
        if (lblCreditosAprobados != null) {
            lblCreditosAprobados.setText(String.valueOf(total));
        }
    }

    private void validateFields() {
        if (txtCode.getText() == null || txtCode.getText().trim().isEmpty())
            throw new IllegalArgumentException("El código del curso es obligatorio.");
        if (txtName.getText() == null || txtName.getText().trim().isEmpty())
            throw new IllegalArgumentException("El nombre del curso es obligatorio.");
        if (!txtCredits.getText().trim().matches("\\d+"))
            throw new IllegalArgumentException("Los créditos deben ser un número entero positivo.");
        if (!txtGrade.getText().trim().matches("\\d+(\\.\\d+)?"))
            throw new IllegalArgumentException("La nota debe ser numérica.");
        double grade = Double.parseDouble(txtGrade.getText().trim());
        if (grade < 0 || grade > 100)
            throw new IllegalArgumentException("La nota debe estar entre 0 y 100.");
    }

    private void clearFields() {
        txtCode.clear();
        txtName.clear();
        txtCredits.clear();
        txtGrade.clear();
        if (cbStatus != null) cbStatus.getSelectionModel().selectFirst();
        if (cbPeriod != null) cbPeriod.getSelectionModel().selectFirst();
    }

    private void setStatus(String msg) {
        if (lblStatus != null) lblStatus.setText(msg);
    }

    private void showError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validación");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}