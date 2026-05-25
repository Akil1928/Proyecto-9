package cr.ac.ucr.sga;

import javafx.application.Application;
import javafx.stage.Stage;
import cr.ac.ucr.sga.view.ViewFactory;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        ViewFactory.showMainView(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
