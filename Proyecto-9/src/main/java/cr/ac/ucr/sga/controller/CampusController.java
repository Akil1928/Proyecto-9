/* UCR - IF-3001 - Grupo 21 */
package cr.ac.ucr.sga.controller;

import cr.ac.ucr.sga.model.entities.Building;
import cr.ac.ucr.sga.model.services.CampusService;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;

public class CampusController {

    @FXML private Canvas canvas;
    @FXML private ComboBox<Building> originComboBox;
    @FXML private ComboBox<Building> destinationComboBox;
    @FXML private VBox adminPanel;
    @FXML private HBox navigationPanel;

    private CampusService campusService;
    private GraphicsContext gc;


    @FXML
    public void initialize() {
        campusService = CampusService.getInstance();
        gc = canvas.getGraphicsContext2D();
        loadBuildingsIntoComboBoxes();
        drawGraph();
    }




    private void loadBuildingsIntoComboBoxes() {
        originComboBox.getItems().setAll(campusService.getBuildings().values());
        destinationComboBox.getItems().setAll(campusService.getBuildings().values());
    }

    private void drawGraph() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        Map<String, Building> buildings = campusService.getBuildings();
        Map<String, Map<String, Integer>> graph = campusService.getGraph();

        for (Map.Entry<String, Map<String, Integer>> entry : graph.entrySet()) {
            Building source = buildings.get(entry.getKey());
            if (source == null) continue;

            for (Map.Entry<String, Integer> neighbor : entry.getValue().entrySet()) {
                Building dest = buildings.get(neighbor.getKey());
                if (dest == null) continue;

                gc.setStroke(Color.GRAY);
                gc.setLineWidth(2);
                gc.strokeLine(source.getX(), source.getY(), dest.getX(), dest.getY());

                gc.setFill(Color.DARKSLATEGRAY);
                gc.fillText(
                        String.valueOf(neighbor.getValue()) + "m",
                        (source.getX() + dest.getX()) / 2,
                        (source.getY() + dest.getY()) / 2
                );
            }
        }

        for (Building building : buildings.values()) {
            gc.setFill(Color.ROYALBLUE);
            gc.fillOval(building.getX() - 8, building.getY() - 8, 16, 16);
            gc.setFill(Color.BLACK);
            gc.fillText(building.getName(), building.getX() + 12, building.getY() - 12);
        }
    }

    @FXML
    private void handleFindShortestPath() {
        drawGraph();
        Building origin = originComboBox.getValue();
        Building destination = destinationComboBox.getValue();

        if (origin == null || destination == null) {
            showAlert("Selección requerida", "Debe seleccionar un origen y un destino.");
            return;
        }

        List<String> path = campusService.dijkstra(origin.getId(), destination.getId());
        if (path.isEmpty()) {
            showAlert("Sin ruta", "No existe una ruta entre los edificios seleccionados.");
            return;
        }
        animatePath(path, Color.RED);
    }

    @FXML
    private void handleBFS() {
        drawGraph();
        Building start = originComboBox.getValue();
        if (start == null) {
            showAlert("Selección requerida", "Debe seleccionar un edificio de origen.");
            return;
        }
        List<String> path = campusService.bfs(start.getId());
        animatePath(path, Color.GREEN);
    }

    @FXML
    private void handleDFS() {
        drawGraph();
        Building start = originComboBox.getValue();
        if (start == null) {
            showAlert("Selección requerida", "Debe seleccionar un edificio de origen.");
            return;
        }
        List<String> path = campusService.dfs(start.getId());
        animatePath(path, Color.BLUE);
    }

    private void animatePath(List<String> path, Color color) {
        if (path == null || path.size() < 2) return;

        gc.setStroke(color);
        gc.setLineWidth(4);

        SequentialTransition sequence = new SequentialTransition();

        for (int i = 0; i < path.size() - 1; i++) {
            Building current = campusService.getBuildings().get(path.get(i));
            Building next = campusService.getBuildings().get(path.get(i + 1));

            if (current == null || next == null) continue;

            PauseTransition pause = new PauseTransition(Duration.seconds(0.4));
            pause.setOnFinished(event -> {
                gc.strokeLine(current.getX(), current.getY(), next.getX(), next.getY());

                gc.setFill(color);
                gc.fillOval(current.getX() - 8, current.getY() - 8, 16, 16);
                gc.fillOval(next.getX() - 8, next.getY() - 8, 16, 16);
            });
            sequence.getChildren().add(pause);
        }
        sequence.play();
    }



    @FXML
    private void handleAddBuilding() {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Agregar Edificio");
        dialog.setHeaderText("Formato: ID,Nombre,X,Y\nEjemplo: F,Edificio F,600,200");

        dialog.showAndWait().ifPresent(input -> {
            try {
                String[] parts = input.split(",");
                Building b = new Building.Builder()
                        .id(parts[0].trim())
                        .name(parts[1].trim())
                        .x(Double.parseDouble(parts[2].trim()))
                        .y(Double.parseDouble(parts[3].trim()))
                        .build();
                campusService.addBuilding(b);
                loadBuildingsIntoComboBoxes();
                drawGraph();
            } catch (Exception e) {
                showAlert("Error", "Formato incorrecto. Use: ID,Nombre,X,Y");
            }
        });
    }

    @FXML
    private void handleDeleteBuilding() {
        javafx.scene.control.ChoiceDialog<Building> dialog = new javafx.scene.control.ChoiceDialog<>(null, campusService.getBuildings().values());
        dialog.setTitle("Eliminar Edificio");
        dialog.setHeaderText("Seleccione el edificio a eliminar:");

        dialog.showAndWait().ifPresent(b -> {
            campusService.deleteBuilding(b.getId());
            loadBuildingsIntoComboBoxes();
            drawGraph();
        });
    }

    @FXML
    private void handleAddEdge() {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Agregar Conexión");
        dialog.setHeaderText("Formato: ID_Origen,ID_Destino,Peso\nEjemplo: A,B,50");

        dialog.showAndWait().ifPresent(input -> {
            try {
                String[] parts = input.split(",");
                campusService.addEdge(parts[0].trim(), parts[1].trim(), Double.parseDouble(parts[2].trim()));
                drawGraph();
            } catch (Exception e) {
                showAlert("Error", "Formato incorrecto. Use: ID_Origen,ID_Destino,Peso");
            }
        });
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public void setAdminMode(boolean isAdmin) {
        // Mostrar/Ocultar panel admin
        adminPanel.setVisible(isAdmin);
        adminPanel.setManaged(isAdmin);

        // Ocultar/Mostrar panel de navegación (si el HBox en el FXML tiene el id "navigationPanel")
        // Asegúrate de tener declarado: @FXML private HBox navigationPanel;
        if (navigationPanel != null) {
            navigationPanel.setVisible(!isAdmin);
            navigationPanel.setManaged(!isAdmin);
        }
    }

}