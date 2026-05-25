package cr.ac.ucr.sga.model.entities;

import javafx.beans.property.*;

public class Course {

    private final StringProperty code;
    private final StringProperty name;
    private final IntegerProperty credits;

    Course(String code, String name, int credits) {
        this.code = new SimpleStringProperty(code);
        this.name = new SimpleStringProperty(name);
        this.credits = new SimpleIntegerProperty(credits);
    }

    public String getCode() {
        return code.get();
    }

    public String getName() {
        return name.get();
    }

    public int getCredits() {
        return credits.get();
    }

    public StringProperty codeProperty() {
        return code;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public IntegerProperty creditsProperty() {
        return credits;
    }
}
