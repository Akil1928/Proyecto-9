package cr.ac.ucr.sga.view;

import cr.ac.ucr.sga.model.entities.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * ViewFactory actualizado (Sprint 2).
 *
 * Cambios:
 *  - showLoginView(Stage): carga la pantalla de login (NUEVA, es la vista inicial).
 *  - showMainView(Stage, User): recibe el usuario en sesión para que MainController
 *    pueda construir el menú según el rol.
 *  - Main.java ahora llama a showLoginView() en lugar de showMainView().
 */
public class ViewFactory {

    private ViewFactory() {}

    // ------------------------------------------------------------------
    // Vista de Login — punto de entrada de la aplicación
    // ------------------------------------------------------------------

    public static void showLoginView(Stage stage) {
        load(stage, "/fxml/login-view.fxml", "Sistema de Gestión Académica — Iniciar Sesión");
    }

    // ------------------------------------------------------------------
    // Vista principal — se carga tras autenticación exitosa
    // ------------------------------------------------------------------

    /**
     * Carga la vista principal después de que el usuario inició sesión.
     * El usuario ya fue guardado en UserService.getInstance() por LoginController,
     * así que MainController lo recupera desde ahí en su initialize().
     */
    public static void showMainView(Stage stage, User user) {
        load(stage, "/fxml/main-view.fxml", "Sistema de Gestión Académica");
    }

    // ------------------------------------------------------------------
    // Vistas individuales (acceso directo, por compatibilidad Sprint 1)
    // ------------------------------------------------------------------

    public static void showTramitView(Stage stage) {
        load(stage, "/fxml/tramit-view.fxml", "Gestión de Trámites");
    }

    public static void showEnrollmentView(Stage stage) {
        load(stage, "/fxml/enrollment-view.fxml", "Cola de Matrícula");
    }

    // ------------------------------------------------------------------
    // Helper genérico
    // ------------------------------------------------------------------

    private static void load(Stage stage, String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewFactory.class.getResource(fxml));
            Scene scene = new Scene(loader.load(), 1000, 650);
            scene.getStylesheets().add(
                    ViewFactory.class.getResource("/css/styless.css").toExternalForm()
            );
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar la vista: " + fxml, e);
        }
    }
}