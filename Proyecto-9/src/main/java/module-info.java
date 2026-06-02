module cr.ac.ucr.sga {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens cr.ac.ucr.sga to javafx.fxml;
    opens cr.ac.ucr.sga.controller to javafx.fxml;
    opens cr.ac.ucr.sga.model.entities to javafx.base, com.google.gson;
    opens cr.ac.ucr.sga.model.states to javafx.fxml, com.google.gson;
    opens cr.ac.ucr.sga.view.observers to javafx.fxml;

    exports cr.ac.ucr.sga;
    exports cr.ac.ucr.sga.controller;
    exports cr.ac.ucr.sga.model.entities;
    exports cr.ac.ucr.sga.model.services;
    exports cr.ac.ucr.sga.model.structures.lists;
    exports cr.ac.ucr.sga.model.structures.stacks;
    exports cr.ac.ucr.sga.model.structures.queues;
    exports cr.ac.ucr.sga.model.states;
    exports cr.ac.ucr.sga.view.observers;
    exports cr.ac.ucr.sga.view;
}
