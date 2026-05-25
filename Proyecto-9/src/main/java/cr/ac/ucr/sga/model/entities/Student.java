package cr.ac.ucr.sga.model.entities;

public class Student {

    private final String id;
    private final String name;
    private final String email;
    private final int approvedCredits;

    Student(String id, String name, String email, int approvedCredits) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.approvedCredits = approvedCredits;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getApprovedCredits() {
        return approvedCredits;
    }
}
