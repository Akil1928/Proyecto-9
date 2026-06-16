package cr.ac.ucr.sga.model.entities;


public class CourseEntry implements Comparable<CourseEntry> {

    private final String code;
    private final String name;

    public CourseEntry(String code, String name) {
        this.code = code.toUpperCase();
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(CourseEntry other) {
        return this.code.compareTo(other.code);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CourseEntry)) return false;
        return this.code.equals(((CourseEntry) obj).code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return code;
    }

    public String getFullLabel() {
        return code + " - " + name;
    }
}