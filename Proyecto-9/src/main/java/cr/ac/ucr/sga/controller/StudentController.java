package cr.ac.ucr.sga.controller;

import cr.ac.ucr.sga.model.entities.AcademicRecordEntry;
import cr.ac.ucr.sga.model.entities.Course;
import cr.ac.ucr.sga.model.entities.CourseBuilder;
import cr.ac.ucr.sga.model.services.AcademicRecordService;
import cr.ac.ucr.sga.model.services.JsonService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    private final AcademicRecordService service = AcademicRecordService.getInstance();
    private final ObservableList<AcademicRecordEntry> tableData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cbStatus.setItems(FXCollections.observableArrayList("Aprobado", "Reprobado", "En curso"));//las opciones a escoger
        cbPeriod.setItems(FXCollections.observableArrayList("I-2025", "II-2025", "I-2026", "II-2026"));
        cbStatus.getSelectionModel().selectFirst();
        cbPeriod.getSelectionModel().selectFirst();

        colCode.setCellValueFactory(data -> data.getValue().getCourse().codeProperty());
        colName.setCellValueFactory(data -> data.getValue().getCourse().nameProperty());
        colCredits.setCellValueFactory(data -> data.getValue().getCourse().creditsProperty());
        colGrade.setCellValueFactory(data -> data.getValue().gradeProperty());
        colStatus.setCellValueFactory(data -> data.getValue().statusProperty());
        colPeriod.setCellValueFactory(data -> data.getValue().periodProperty());

        tableRecord.setItems(tableData);
        addDemoCourses();
    }

    @FXML
    private void addCourse() {
        try {
            validateFields();

            Course course = new CourseBuilder()
                    .setCode(txtCode.getText().trim())
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
            clearFields();
            JsonService.saveAcademicRecord(service.toArray(), "src/main/resources/data/courses.json");
            setStatus("✔ Curso \"" + course.getName() + "\" agregado. Total: " + service.size() + " curso(s).");

        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    private void deleteSelectedCourse() {
        AcademicRecordEntry selected = tableRecord.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Seleccione un curso en la tabla para eliminarlo.");
            return;
        }

        //confirmación antes de eliminar
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar el curso seleccionado?");
        confirm.setContentText("Curso: " + selected.getCourse().getName() + " (" + selected.getCourse().getCode() + ")");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String nombre = selected.getCourse().getName();
                service.removeRecordByCode(selected.getCourse().getCode());
                refreshTable();
                JsonService.saveAcademicRecord(service.toArray(), "src/main/resources/data/courses.json");
                setStatus("✔ Curso \"" + nombre + "\" eliminado. Quedan: " + service.size() + " curso(s).");
            }
        });
    }
//solo para mostrar los cursos de ejemplo
    @FXML
    private void addDemoCourses() {
        service.clear();

        service.addRecord(new AcademicRecordEntry(
                new CourseBuilder().setCode("IF3001").setName("Algoritmos y Estructuras de Datos").setCredits(4).build(),
                "I-2026", 95, "Aprobado"));

        service.addRecord(new AcademicRecordEntry(
                new CourseBuilder().setCode("IF2000").setName("Programación I").setCredits(4).build(),
                "II-2025", 88, "Aprobado"));

        service.addRecord(new AcademicRecordEntry(
                new CourseBuilder().setCode("IF1001").setName("Introducción a Informática").setCredits(3).build(),
                "I-2025", 79, "Aprobado"));

        refreshTable();
        JsonService.saveAcademicRecord(service.toArray(), "src/main/resources/data/courses.json");
        setStatus("✔ 3 cursos de demostración cargados correctamente.");
    }

    private void refreshTable() {
        tableData.clear();
        for (AcademicRecordEntry entry : service.toArray()) {
            tableData.add(entry);
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
        cbStatus.getSelectionModel().selectFirst();
        cbPeriod.getSelectionModel().selectFirst();
    }

    private void setStatus(String msg) {
        if (lblStatus != null) lblStatus.setText(msg);
    }
//validacion
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validación");
        alert.setHeaderText("Revise los datos ingresados");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
