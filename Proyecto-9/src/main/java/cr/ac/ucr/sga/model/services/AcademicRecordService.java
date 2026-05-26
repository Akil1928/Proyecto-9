package cr.ac.ucr.sga.model.services;

import cr.ac.ucr.sga.model.entities.AcademicRecordEntry;
import cr.ac.ucr.sga.model.structures.lists.DoublyLinkedList;
import cr.ac.ucr.sga.model.structures.lists.SimpleLinkedList;
//clase singleton para manejar el historial académico del estudiante
public class AcademicRecordService {

    private static AcademicRecordService instance;

    private final DoublyLinkedList<AcademicRecordEntry> academicHistory;
    private final SimpleLinkedList<AcademicRecordEntry> pendingEnrollment;

    private AcademicRecordService() {
        academicHistory = new DoublyLinkedList<>();
        pendingEnrollment = new SimpleLinkedList<>();
    }

    public static AcademicRecordService getInstance() {
        if (instance == null) {
            instance = new AcademicRecordService();
        }

        return instance;
    }

    public void addRecord(AcademicRecordEntry entry) {
        academicHistory.addLast(entry);

        if ("En curso".equalsIgnoreCase(entry.getStatus())) {
            pendingEnrollment.addLast(entry);
        }
    }

    public boolean removeRecordByCode(String code) {
        return academicHistory.removeIf(entry -> entry.getCourse().getCode().equalsIgnoreCase(code));
    }

    public AcademicRecordEntry[] toArray() {
        return academicHistory.toArray(new AcademicRecordEntry[academicHistory.size()]);
    }

    public int size() {
        return academicHistory.size();
    }

    public void clear() {
        academicHistory.clear();
    }
}
