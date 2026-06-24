package cr.ac.ucr.sga.model.services;

import cr.ac.ucr.sga.model.entities.Student;
import cr.ac.ucr.sga.model.entities.StudentBuilder;
import cr.ac.ucr.sga.model.structures.lists.SimpleLinkedList;

/**
 * Directorio de estudiantes del sistema.
 *
 * REGLA IMPORTANTE: el campo `id` de cada Student debe ser idéntico
 * al `username` del User correspondiente en UserService.
 * Esto permite que el sistema encuentre el perfil del estudiante
 * cuando inicia sesión (por currentUser.getUsername()).
 *
 * Créditos aprobados de ejemplo basados en la malla 600002-07:
 *   - Cada curso aprobado de la malla vale entre 2 y 4 créditos.
 *   - I año completo ≈ 32 créditos, II año ≈ 36, III año ≈ 39, IV año ≈ 37.
 */
public class StudentDirectoryService {

    private static StudentDirectoryService instance;

    private final SimpleLinkedList<Student> students = new SimpleLinkedList<>();

    private StudentDirectoryService() {
        // id = username del login, nombre = displayName del login
        students.addLast(new StudentBuilder()
                .setId("estudiante")
                .setName("Héctor Sandoval")
                .setEmail("hector.sandoval@ucr.ac.cr")
                .withCreditosAprobados(18)   //Aprobó I año completo (≈ I y II ciclo)
                .build());

        students.addLast(new StudentBuilder()
                .setId("maria")
                .setName("María González")
                .setEmail("maria.gonzalez@ucr.ac.cr")
                .withCreditosAprobados(20)   //Aprobó I año + parte del II año
                .build());

        students.addLast(new StudentBuilder()
                .setId("carlos")
                .setName("Carlos Gutierrez")
                .setEmail("carlos.gutierrez@ucr.ac.cr")
                .withCreditosAprobados(14)  //Aprobó hasta el III ciclo
                .build());

        //Estudiantes adicionales que solo aparecen en la cola de matrícula
        students.addLast(new StudentBuilder()
                .setId("ana")
                .setName("Ana Sibaja")
                .setEmail("ana.sibaja@ucr.ac.cr")
                .withCreditosAprobados(12)  //Aprobó hasta el IV ciclo
                .build());

        students.addLast(new StudentBuilder()
                .setId("jose")
                .setName("José Mesén")
                .setEmail("jose.mesen@ucr.ac.cr")
                .withCreditosAprobados(22)  // Bachillerato casi completo
                .build());
    }

    public static StudentDirectoryService getInstance() {
        if (instance == null) instance = new StudentDirectoryService();
        return instance;
    }

    public Student[] getStudents() {
        return students.toArray(new Student[students.size()]);
    }

    /**
     * Busca un estudiante por su id (= username de login).
     * Devuelve null si no existe.
     */
    public Student findById(String id) {
        if (id == null) return null;
        for (Student s : getStudents()) {
            if (s != null && s.getId().equalsIgnoreCase(id)) return s;
        }
        return null;
    }
}