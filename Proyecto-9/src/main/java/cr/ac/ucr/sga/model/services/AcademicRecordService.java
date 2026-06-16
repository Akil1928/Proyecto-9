package cr.ac.ucr.sga.model.services;

import cr.ac.ucr.sga.model.entities.AcademicRecordEntry;
import cr.ac.ucr.sga.model.structures.lists.DoublyLinkedList;
import cr.ac.ucr.sga.model.structures.lists.SimpleLinkedList;

import java.util.HashSet;
import java.util.Set;

/**
 * Singleton que maneja el historial académico del estudiante.
 * Sprint 2: se agrega validación de requisitos contra la malla curricular (CurriculumService).
 */
public class AcademicRecordService {

    private static AcademicRecordService instance;

    private final DoublyLinkedList<AcademicRecordEntry> academicHistory;
    private final SimpleLinkedList<AcademicRecordEntry> pendingEnrollment;

    private AcademicRecordService() {
        academicHistory   = new DoublyLinkedList<>();
        pendingEnrollment = new SimpleLinkedList<>();
        // Cargar expediente persistido al iniciar
        for (AcademicRecordEntry entry : JsonService.loadAcademicRecord()) {
            addRecord(entry);
        }
    }

    public static AcademicRecordService getInstance() {
        if (instance == null) {
            instance = new AcademicRecordService();
        }
        return instance;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Operaciones sobre el expediente
    // ─────────────────────────────────────────────────────────────────────────

    public void addRecord(AcademicRecordEntry entry) {
        academicHistory.addLast(entry);
        if ("En curso".equalsIgnoreCase(entry.getStatus())) {
            pendingEnrollment.addLast(entry);
        }
    }

    public boolean removeRecordByCode(String code) {
        return academicHistory.removeIf(
                entry -> entry.getCourse().getCode().equalsIgnoreCase(code));
    }

    public AcademicRecordEntry[] toArray() {
        return academicHistory.toArray(new AcademicRecordEntry[academicHistory.size()]);
    }

    public int size() {
        try {
            return academicHistory.isEmpty() ? 0 : academicHistory.size();
        } catch (cr.ac.ucr.sga.model.structures.lists.ListException e) {
            return 0;
        }
    }

    public void clear() {
        academicHistory.clear();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Validación de requisitos curriculares
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Devuelve el conjunto de códigos de cursos que el estudiante ya APROBÓ
     * en su expediente académico.
     * Solo se consideran los cursos con estado "Aprobado".
     */
    public Set<String> getApprovedCodesSet() {
        Set<String> approved = new HashSet<>();
        for (AcademicRecordEntry entry : toArray()) {
            if (entry != null && "Aprobado".equalsIgnoreCase(entry.getStatus())) {
                approved.add(entry.getCourse().getCode().toUpperCase());
            }
        }
        return approved;
    }

    /**
     * Verifica si el estudiante puede agregar el curso indicado,
     * consultando la malla curricular (CurriculumService).
     *
     * @param courseCode Código del curso que se quiere agregar (ej. "IF-0010").
     * @return ValidationResult con el resultado y los requisitos faltantes si aplica.
     */
    public CurriculumService.ValidationResult validatePrerequisites(String courseCode) {
        Set<String> approved = getApprovedCodesSet();
        return CurriculumService.getInstance().canEnroll(courseCode, approved);
    }

    /**
     * Verifica si el curso ya existe en el expediente (para evitar duplicados).
     *
     * @param courseCode Código del curso a verificar.
     * @return true si ya está registrado (en cualquier estado).
     */
    public boolean containsCourse(String courseCode) {
        for (AcademicRecordEntry entry : toArray()) {
            if (entry != null
                    && entry.getCourse().getCode().equalsIgnoreCase(courseCode)) {
                return true;
            }
        }
        return false;
    }


     //Cuenta los créditos totales de cursos con estado "Aprobado".//util para calcular prioridad en la cola de matrícula
    public int getTotalApprovedCredits() {
        int total = 0;
        for (AcademicRecordEntry entry : toArray()) {
            if (entry != null && "Aprobado".equalsIgnoreCase(entry.getStatus())) {
                total += entry.getCourse().getCredits();
            }
        }
        return total;
    }
}