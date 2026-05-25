package cr.ac.ucr.sga.model.entities;

import javafx.beans.property.*;

public class AcademicRecordEntry {

    private final Course course;
    private final StringProperty period;
    private final DoubleProperty grade;
    private final StringProperty status;

    public AcademicRecordEntry(Course course, String period, double grade, String status) {
        if (course == null) {
            throw new IllegalArgumentException("El curso es obligatorio.");
        }

        if (period == null || period.isBlank()) {
            throw new IllegalArgumentException("El periodo es obligatorio.");
        }

        if (grade < 0 || grade > 100) {
            throw new IllegalArgumentException("La nota debe estar entre 0 y 100.");
        }

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }

        this.course = course;
        this.period = new SimpleStringProperty(period);
        this.grade = new SimpleDoubleProperty(grade);
        this.status = new SimpleStringProperty(status);
    }

    public Course getCourse() {
        return course;
    }

    public String getPeriod() {
        return period.get();
    }

    public double getGrade() {
        return grade.get();
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty periodProperty() {
        return period;
    }

    public DoubleProperty gradeProperty() {
        return grade;
    }

    public StringProperty statusProperty() {
        return status;
    }
}
