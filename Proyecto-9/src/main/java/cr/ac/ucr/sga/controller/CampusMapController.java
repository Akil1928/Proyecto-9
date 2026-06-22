package cr.ac.ucr.sga.controller;

import cr.ac.ucr.sga.model.graph.Dijkstra;
import cr.ac.ucr.sga.model.graph.Graph;
import cr.ac.ucr.sga.model.graph.Traversals;
import cr.ac.ucr.sga.model.services.CampusGraphService;
import cr.ac.ucr.sga.model.services.UserService;
import cr.ac.ucr.sga.model.services.ReportService;
import cr.ac.ucr.sga.model.entities.User;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.*;

public class CampusMapController {

    @FXML private Canvas canvas;
    @FXML private Label lblStatus;
    @FXML private ComboBox<String> cbOrigin;
    @FXML private ComboBox<String> cbDestination;
    @FXML private Button btnDijkstra;
    @FXML private Button btnBFS;
    @FXML private Button btnDFS;
    @FXML private Label lblDistance;
    @FXML private Label lblPath;
    @FXML private VBox adminControlsPanel;
    @FXML private Label lblAlgorithm;
    @FXML private Button btnStep;
    @FXML private Button btnAutoPlay;
    @FXML private Button btnReset;

    private final CampusGraphService campusService = CampusGraphService.getInstance();
    private Graph campusGraph;
    private Map<String, CampusGraphService.CampusNode> campusNodes;

    private Set<String> visitedNodes = new HashSet<>();
    private List<String> traversalOrder = new ArrayList<>();
    private int traversalStep = 0;
    private boolean isAnimating = false;

    private static final double NODE_RADIUS = 20;
    private static final double EDGE_WIDTH = 2;
    private static final Color COLOR_NODE_DEFAULT = Color.LIGHTBLUE;
    private static final Color COLOR_NODE_VISITED = Color.LIGHTGREEN;
    private static final Color COLOR_NODE_CURRENT = Color.YELLOW;
    private static final Color COLOR_NODE_PATH = Color.ORANGE;
    private static final Color COLOR_EDGE_DEFAULT = Color.BLACK;
    private static final Color COLOR_EDGE_PATH = Color.RED;

    @FXML
    public void initialize() {
        campusGraph = campusService.getGraph();
        campusNodes = campusService.getCampusNodes();

        User user = UserService.getInstance().getCurrentUser();
        boolean isAdmin = user != null && user.getRole() == User.Role.ADMINISTRADOR;

        // Mostrar/ocultar panel de controles según rol
        if (adminControlsPanel != null) {
            adminControlsPanel.setVisible(isAdmin);
            adminControlsPanel.setManaged(isAdmin);
        }

        // Llenar ComboBoxes con nombres de edificios
        List<String> buildingNames = campusService.getAllNodeNames();
        if (cbOrigin != null && cbDestination != null) {
            cbOrigin.setItems(FXCollections.observableArrayList(buildingNames));
            cbDestination.setItems(FXCollections.observableArrayList(buildingNames));
            if (!buildingNames.isEmpty()) {
                cbOrigin.getSelectionModel().selectFirst();
                if (buildingNames.size() > 1) {
                    cbDestination.getSelectionModel().select(1);
                }
            }
        }

        // Dibujar mapa inicial
        drawMap();
    }

    /**
     * Dibuja el mapa del campus en el Canvas.
     */
    private void drawMap() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Dibujar aristas
        for (String from : campusGraph.getVertexIds()) {
            CampusGraphService.CampusNode fromNode = campusNodes.get(from);
            for (var edge : campusGraph.getEdgesFrom(from)) {
                CampusGraphService.CampusNode toNode = campusNodes.get(edge.getTo());
                if (fromNode != null && toNode != null) {
                    drawEdge(gc, fromNode, toNode, edge.getWeight());
                }
            }
        }

        // Dibujar nodos
        for (CampusGraphService.CampusNode node : campusNodes.values()) {
            Color nodeColor = COLOR_NODE_DEFAULT;
            if (visitedNodes.contains(node.id)) {
                nodeColor = COLOR_NODE_VISITED;
            }
            if (traversalStep < traversalOrder.size() && traversalOrder.get(traversalStep - 1).equals(node.id)) {
                nodeColor = COLOR_NODE_CURRENT;
            }
            drawNode(gc, node, nodeColor);
        }
    }

    /**
     * Dibuja una arista (línea entre dos nodos).
     */
    private void drawEdge(GraphicsContext gc, CampusGraphService.CampusNode from, CampusGraphService.CampusNode to, double weight) {
        gc.setStroke(COLOR_EDGE_DEFAULT);
        gc.setLineWidth(EDGE_WIDTH);
        gc.strokeLine(from.x, from.y, to.x, to.y);

        // Dibujar la distancia en el medio de la arista
        double midX = (from.x + to.x) / 2;
        double midY = (from.y + to.y) / 2;
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(10));
        gc.fillText(String.format("%.1f", weight), midX, midY - 5);
    }

    /**
     * Dibuja un nodo (edificio) en el Canvas.
     */
    private void drawNode(GraphicsContext gc, CampusGraphService.CampusNode node, Color color) {
        gc.setFill(color);
        gc.fillOval(node.x - NODE_RADIUS, node.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(node.x - NODE_RADIUS, node.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

        // Dibujar texto
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(node.id, node.x, node.y + 5);
    }

    /**
     * Dibuja el camino más corto encontrado por Dijkstra.
     */
    private void drawPath(List<String> path) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Limpiar y redibujar
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Dibujar aristas
        for (String from : campusGraph.getVertexIds()) {
            CampusGraphService.CampusNode fromNode = campusNodes.get(from);
            for (var edge : campusGraph.getEdgesFrom(from)) {
                CampusGraphService.CampusNode toNode = campusNodes.get(edge.getTo());
                if (fromNode != null && toNode != null) {
                    // Verificar si esta arista está en el camino
                    boolean inPath = false;
                    for (int i = 0; i < path.size() - 1; i++) {
                        if ((path.get(i).equals(from) && path.get(i + 1).equals(edge.getTo())) ||
                            (path.get(i + 1).equals(from) && path.get(i).equals(edge.getTo()))) {
                            inPath = true;
                            break;
                        }
                    }

                    if (inPath) {
                        gc.setStroke(COLOR_EDGE_PATH);
                        gc.setLineWidth(4);
                    } else {
                        gc.setStroke(COLOR_EDGE_DEFAULT);
                        gc.setLineWidth(EDGE_WIDTH);
                    }
                    gc.strokeLine(fromNode.x, fromNode.y, toNode.x, toNode.y);
                    gc.setFill(Color.BLACK);
                    gc.setFont(new Font(10));
                    double midX = (fromNode.x + toNode.x) / 2;
                    double midY = (fromNode.y + toNode.y) / 2;
                    gc.fillText(String.format("%.1f", edge.getWeight()), midX, midY - 5);
                }
            }
        }

        // Dibujar nodos con colores según si están en el camino
        for (CampusGraphService.CampusNode node : campusNodes.values()) {
            Color nodeColor = path.contains(node.id) ? COLOR_NODE_PATH : COLOR_NODE_DEFAULT;
            drawNode(gc, node, nodeColor);
        }
    }

    @FXML
    private void onDijkstra() {
        if (cbOrigin == null || cbDestination == null) {
            lblStatus.setText("⚠ Seleccione origen y destino.");
            return;
        }

        String originName = cbOrigin.getSelectionModel().getSelectedItem();
        String destinationName = cbDestination.getSelectionModel().getSelectedItem();

        if (originName == null || destinationName == null) {
            lblStatus.setText("⚠ Debe seleccionar origen y destino.");
            return;
        }

        String originId = campusService.getNodeIdByName(originName);
        String destinationId = campusService.getNodeIdByName(destinationName);

        if (originId == null || destinationId == null) {
            lblStatus.setText("⚠ Edificio no encontrado.");
            return;
        }

        // Ejecutar Dijkstra
        long startTime = System.currentTimeMillis();
        Dijkstra.Result result = Dijkstra.compute(campusGraph, originId);
        long endTime = System.currentTimeMillis();

        double distance = result.distanceTo(destinationId);
        if (Double.isInfinite(distance)) {
            lblStatus.setText("⚠ No hay ruta entre " + originName + " y " + destinationName);
            lblDistance.setText("");
            lblPath.setText("");
        } else {
            List<String> path = result.pathTo(destinationId);
            drawPath(path);

            String pathStr = String.join(" → ", path);
            lblDistance.setText(String.format("Distancia: %.1f metros", distance));
            lblPath.setText("Camino: " + pathStr);
            lblStatus.setText(String.format("✔ Algoritmo ejecutado en %.3f ms", (double)(endTime - startTime)));
        }
    }

    @FXML
    private void onBFS() {
        if (cbOrigin == null) {
            lblStatus.setText("⚠ Seleccione un nodo inicial.");
            return;
        }

        String originName = cbOrigin.getSelectionModel().getSelectedItem();
        if (originName == null) {
            lblStatus.setText("⚠ Debe seleccionar un nodo inicial.");
            return;
        }

        String originId = campusService.getNodeIdByName(originName);
        if (originId == null) {
            lblStatus.setText("⚠ Edificio no encontrado.");
            return;
        }

        // Ejecutar BFS
        long startTime = System.currentTimeMillis();
        traversalOrder = Traversals.bfs(campusGraph, originId);
        long endTime = System.currentTimeMillis();

        traversalStep = 0;
        visitedNodes.clear();
        isAnimating = false;

        if (lblAlgorithm != null) {
            lblAlgorithm.setText("Algoritmo: BFS (Búsqueda en Anchura)");
        }

        lblStatus.setText(String.format("✔ BFS iniciado. Nodos encontrados: %d. Tiempo: %.3f ms",
                traversalOrder.size(), (double)(endTime - startTime)));
        lblPath.setText("Orden de visita: " + String.join(" → ", traversalOrder));

        drawMap();
    }

    @FXML
    private void onDFS() {
        if (cbOrigin == null) {
            lblStatus.setText("⚠ Seleccione un nodo inicial.");
            return;
        }

        String originName = cbOrigin.getSelectionModel().getSelectedItem();
        if (originName == null) {
            lblStatus.setText("⚠ Debe seleccionar un nodo inicial.");
            return;
        }

        String originId = campusService.getNodeIdByName(originName);
        if (originId == null) {
            lblStatus.setText("⚠ Edificio no encontrado.");
            return;
        }

        // Ejecutar DFS
        long startTime = System.currentTimeMillis();
        traversalOrder = Traversals.dfs(campusGraph, originId);
        long endTime = System.currentTimeMillis();

        traversalStep = 0;
        visitedNodes.clear();
        isAnimating = false;

        if (lblAlgorithm != null) {
            lblAlgorithm.setText("Algoritmo: DFS (Búsqueda en Profundidad)");
        }

        lblStatus.setText(String.format("✔ DFS iniciado. Nodos encontrados: %d. Tiempo: %.3f ms",
                traversalOrder.size(), (double)(endTime - startTime)));
        lblPath.setText("Orden de visita: " + String.join(" → ", traversalOrder));

        drawMap();
    }

    @FXML
    private void onStep() {
        if (traversalOrder.isEmpty()) {
            lblStatus.setText("⚠ Primero ejecute BFS o DFS.");
            return;
        }

        if (traversalStep < traversalOrder.size()) {
            String currentNode = traversalOrder.get(traversalStep);
            visitedNodes.add(currentNode);
            traversalStep++;
            lblStatus.setText("Paso " + traversalStep + ": Visitando nodo " + currentNode);
            drawMap();
        } else {
            lblStatus.setText("✔ Recorrido completado.");
        }
    }

    @FXML
    private void onAutoPlay() {
        if (traversalOrder.isEmpty()) {
            lblStatus.setText("⚠ Primero ejecute BFS o DFS.");
            return;
        }

        if (isAnimating) return;

        isAnimating = true;
        if (btnAutoPlay != null) btnAutoPlay.setDisable(true);
        if (btnStep != null) btnStep.setDisable(true);

        traversalStep = 0;
        visitedNodes.clear();

        animateStep();
    }

    private void animateStep() {
        if (traversalStep < traversalOrder.size()) {
            String currentNode = traversalOrder.get(traversalStep);
            visitedNodes.add(currentNode);
            traversalStep++;

            drawMap();

            PauseTransition pause = new PauseTransition(Duration.millis(500));
            pause.setOnFinished(e -> animateStep());
            pause.play();
        } else {
            lblStatus.setText("✔ Recorrido completado.");
            isAnimating = false;
            if (btnAutoPlay != null) btnAutoPlay.setDisable(false);
            if (btnStep != null) btnStep.setDisable(false);
        }
    }

    @FXML
    private void onReset() {
        traversalOrder.clear();
        traversalStep = 0;
        visitedNodes.clear();
        isAnimating = false;

        if (btnAutoPlay != null) btnAutoPlay.setDisable(false);
        if (btnStep != null) btnStep.setDisable(false);

        lblStatus.setText("✔ Estado reiniciado.");
        lblPath.setText("");
        lblDistance.setText("");
        lblAlgorithm.setText("");

        drawMap();
    }

    @FXML
    private void onExportMetricsReport() {
        String documentsPath = System.getProperty("user.home") + "\\Documents";
        String filename = "campus_metrics_report_" + System.currentTimeMillis() + ".csv";
        String outputPath = documentsPath + "\\" + filename;

        boolean success = ReportService.generateCampusMetricsReport(outputPath);
        if (success) {
            lblStatus.setText("✔ Reporte de métricas generado en: " + outputPath);
            showInfoAlert("Reporte Generado", "El reporte de métricas se ha guardado en:\n" + outputPath);
        } else {
            lblStatus.setText("⚠ Error al generar el reporte de métricas.");
            showErrorAlert("Error", "No se pudo generar el reporte de métricas.");
        }
    }

    @FXML
    private void onExportPathReport() {
        if (cbOrigin == null || cbDestination == null) {
            lblStatus.setText("⚠ Seleccione origen y destino antes de exportar.");
            return;
        }

        String originName = cbOrigin.getSelectionModel().getSelectedItem();
        String destinationName = cbDestination.getSelectionModel().getSelectedItem();

        if (originName == null || destinationName == null) {
            lblStatus.setText("⚠ Debe seleccionar origen y destino.");
            return;
        }

        String originId = campusService.getNodeIdByName(originName);
        String destinationId = campusService.getNodeIdByName(destinationName);

        String documentsPath = System.getProperty("user.home") + "\\Documents";
        String filename = "campus_path_report_" + System.currentTimeMillis() + ".csv";
        String outputPath = documentsPath + "\\" + filename;

        boolean success = ReportService.generateDijkstraPathReport(outputPath, originId, destinationId);
        if (success) {
            lblStatus.setText("✔ Reporte de ruta generado en: " + outputPath);
            showInfoAlert("Reporte Generado", "El reporte de ruta se ha guardado en:\n" + outputPath);
        } else {
            lblStatus.setText("⚠ Error al generar el reporte de ruta.");
            showErrorAlert("Error", "No se pudo generar el reporte de ruta.");
        }
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

