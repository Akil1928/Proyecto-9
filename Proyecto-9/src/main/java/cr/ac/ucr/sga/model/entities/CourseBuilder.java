package cr.ac.ucr.sga.model.entities;

public class CourseBuilder {

    private String code;
    private String name;
    private int credits;

    public CourseBuilder setCode(String code) {
        this.code = code;
        return this;
    }

    public CourseBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public CourseBuilder setCredits(int credits) {
        this.credits = credits;
        return this;
    }

    public Course build() {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("El código del curso es obligatorio.");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del curso es obligatorio.");
        }

        if (credits <= 0) {
            throw new IllegalArgumentException("Los créditos deben ser mayores a cero.");
        }

        return new Course(code, name, credits);
    }
}
