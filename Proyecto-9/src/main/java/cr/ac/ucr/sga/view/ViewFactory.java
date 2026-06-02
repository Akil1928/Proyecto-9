package cr.ac.ucr.sga.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewFactory {

    private ViewFactory() {
    }

    public static void showMainView(Stage stage) {
        load(stage, "/fxml/main-view.fxml", "Sistema de Gestión Académica");
    }

    public static void showTramitView(Stage stage) {
        load(stage, "/fxml/tramit-view.fxml", "Gestión de Trámites");
    }

    public static void showEnrollmentView(Stage stage) {
        load(stage, "/fxml/enrollment-view.fxml", "Cola de Matrícula");
    }

    private static void load(Stage stage, String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewFactory.class.getResource(fxml));
            Scene scene = new Scene(loader.load(), 1000, 650);
            // Usar el archivo de estilos principal `styless.css` en lugar del antiguo `styles.css`
            scene.getStylesheets().add(ViewFactory.class.getResource("/css/styless.css").toExternalForm());
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar la vista: " + fxml, e);
        }
    }
}
