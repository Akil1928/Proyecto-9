package cr.ac.ucr.sga.view;

import cr.ac.ucr.sga.model.entities.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * ViewFactory — Factory Method para instanciar todas las vistas del sistema.
 *
 * Sprint 4: agrega showCampusView() para abrir campus-view.fxml.
 */
public class ViewFactory {

    private ViewFactory() {}

    //── Login ─────────────────────────────────────────────────────────
    public static void showLoginView(Stage stage) {
        load(stage, "/fxml/login-view.fxml", "Sistema de Gestión Académica — Iniciar Sesión");
    }

    //── Vista principal ───────────────────────────────────────────────
    public static void showMainView(Stage stage, User user) {
        load(stage, "/fxml/main-view.fxml", "Sistema de Gestión Académica");
    }

    //── Vistas individuales ───────────────────────────────────────────
    public static void showTramitView(Stage stage) {
        load(stage, "/fxml/tramit-view.fxml", "Gestión de Trámites");
    }

    public static void showEnrollmentView(Stage stage) {
        load(stage, "/fxml/enrollment-view.fxml", "Cola de Matrícula");
    }

    public static void showCourseSearchView(Stage stage) {
        load(stage, "/fxml/course-search-view.fxml", "Búsqueda de Cursos (BST)");
    }

    //── NUEVO: Mapa del Campus ─────────────────────────────────────────
    /**
     * Abre campus-view.fxml en una ventana modal independiente.
     * Útil si se invoca directamente desde un botón "Mirar" en el perfil.
     *
     * @param owner Stage padre (puede ser null para ventana independiente)
     */
    public static void showCampusView(Stage owner) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ViewFactory.class.getResource("/fxml/campus-view.fxml"));
            Scene scene = new Scene(loader.load(), 1100, 680);
            scene.getStylesheets().add(
                    ViewFactory.class.getResource("/css/styless.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Mapa del Campus — UCR");
            stage.setScene(scene);
            stage.setMinWidth(900);
            stage.setMinHeight(580);

            if (owner != null) {
                stage.initOwner(owner);
                stage.initModality(Modality.WINDOW_MODAL);
            }
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar campus-view.fxml", e);
        }
    }

    //── Helper genérico ───────────────────────────────────────────────
    private static void load(Stage stage, String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewFactory.class.getResource(fxml));
            Scene scene = new Scene(loader.load(), 1000, 650);
            scene.getStylesheets().add(
                    ViewFactory.class.getResource("/css/styless.css").toExternalForm());
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar la vista: " + fxml, e);
        }
    }
}
