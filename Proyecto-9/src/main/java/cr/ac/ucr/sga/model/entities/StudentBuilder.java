package cr.ac.ucr.sga.model.entities;

public class StudentBuilder {

    private String id;
    private String name;
    private String email;
    private int approvedCredits;

    public StudentBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public StudentBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public StudentBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public StudentBuilder setApprovedCredits(int approvedCredits) {
        this.approvedCredits = approvedCredits;
        return this;
    }

    public StudentBuilder withCreditosAprobados(int creditos) {
        this.approvedCredits = creditos;
        return this;
    }

    public Student build() {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("La identificación es obligatoria.");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("El correo electrónico no tiene un formato válido.");
        }

        if (approvedCredits < 0) {
            throw new IllegalArgumentException("Los créditos aprobados no pueden ser negativos.");
        }

        return new Student(id, name, email, approvedCredits);
    }
}
