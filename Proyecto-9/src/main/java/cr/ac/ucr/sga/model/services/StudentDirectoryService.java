package cr.ac.ucr.sga.model.services;

import cr.ac.ucr.sga.model.entities.Student;
import cr.ac.ucr.sga.model.entities.StudentBuilder;
import cr.ac.ucr.sga.model.structures.lists.SimpleLinkedList;

public class StudentDirectoryService {
    private static StudentDirectoryService instance;

    private final SimpleLinkedList<Student> students = new SimpleLinkedList<>();

    private StudentDirectoryService() {
        students.addLast(new StudentBuilder().setId("1").setName("Ana Sibaja").setEmail("ana@ucr.ac.cr").withCreditosAprobados(120).build());
        students.addLast(new StudentBuilder().setId("2").setName("Carlos Gutierrez").setEmail("carlos@ucr.ac.cr").withCreditosAprobados(45).build());
        students.addLast(new StudentBuilder().setId("3").setName("María Moreno").setEmail("maria@ucr.ac.cr").withCreditosAprobados(200).build());
        students.addLast(new StudentBuilder().setId("4").setName("José Mesén").setEmail("jose@ucr.ac.cr").withCreditosAprobados(80).build());
        students.addLast(new StudentBuilder().setId("5").setName("Laura Fernandez").setEmail("laura@ucr.ac.cr").withCreditosAprobados(160).build());
    }

    public static StudentDirectoryService getInstance() {
        if (instance == null) {
            instance = new StudentDirectoryService();
        }
        return instance;
    }

    public Student[] getStudents() {
        return students.toArray(new Student[students.size()]);
    }
}

