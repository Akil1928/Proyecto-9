package cr.ac.ucr.sga;

import javafx.application.Application;
import javafx.stage.Stage;
import cr.ac.ucr.sga.view.ViewFactory;

/**
 * Punto de entrada de la aplicación (Sprint 2).
 *
 * Cambio respecto al Sprint 1:
 *   Ahora arranca mostrando la pantalla de Login (login-view.fxml)
 *   en lugar de ir directamente al main-view.fxml.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // Sprint 2: iniciar con la pantalla de login
        ViewFactory.showLoginView(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}