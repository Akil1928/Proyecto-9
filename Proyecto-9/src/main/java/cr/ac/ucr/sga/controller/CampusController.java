package cr.ac.ucr.sga.controller;

import cr.ac.ucr.sga.model.entities.Building;
import cr.ac.ucr.sga.model.entities.User;
import cr.ac.ucr.sga.model.services.CampusService;
import cr.ac.ucr.sga.model.services.CampusService.CEdge;
import cr.ac.ucr.sga.model.services.CampusService.DijkstraResult;
import cr.ac.ucr.sga.model.services.UserService;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.*;

/**
 * CampusController — MVC: conecta CampusService (Modelo) con campus-view.fxml (Vista).
 *
 * Gestiona:
 *  • Dibujo del grafo en Canvas.
 *  • Animaciones paso a paso de BFS / DFS / Dijkstra.
 *  • Controles del Administrador (CRUD de edificios y aristas).
 */
public class CampusController {

    // ── FXML Bindings ──────────────────────────────────────────────────
    @FXML private Canvas   canvas;
    @FXML private Label    lblMode;
    @FXML private Label    lblStatus;
    @FXML private Label    lblSteps;

    // Búsqueda ruta
    @FXML private ComboBox<Building> cbOrigen;
    @FXML private ComboBox<Building> cbDestino;

    // Recorridos
    @FXML private ComboBox<Building> cbStart;

    // Paneles mutuamente excluyentes según rol
    @FXML private VBox panelEstudiante;   // Dijkstra + BFS + DFS (solo ESTUDIANTE)
    @FXML private VBox panelAdmin;        // CRUD edificios/aristas (solo ADMINISTRADOR)

    // Campos del panel Admin
    @FXML private TextField txtBuildingId;
    @FXML private TextField txtBuildingName;
    @FXML private TextField txtBX;
    @FXML private TextField txtBY;
    @FXML private ComboBox<Building> cbDeleteBuilding;
    @FXML private ComboBox<Building> cbEdgeFrom;
    @FXML private ComboBox<Building> cbEdgeTo;
    @FXML private TextField txtEdgeWeight;

    // ── Estado interno ─────────────────────────────────────────────────
    private final CampusService service = CampusService.getInstance();
    private boolean isAdmin = false;

    /** Nodos resaltados para animación */
    private final Set<String>   highlightedNodes = new HashSet<>();
    /** Aristas resaltadas para Dijkstra */
    private final List<String[]> highlightedEdges = new ArrayList<>();
    /** Color actual de resaltado */
    private Color highlightColor = Color.web("#F0A500");

    // ── Colores ────────────────────────────────────────────────────────
    private static final Color COL_BG        = Color.web("#1A1C22");
    private static final Color COL_EDGE      = Color.web("#2E3150");
    private static final Color COL_NODE      = Color.web("#60A5FA");
    private static final Color COL_NODE_RING = Color.web("#3B82F6");
    private static final Color COL_LABEL     = Color.web("#E8EBF2");
    private static final Color COL_WEIGHT    = Color.web("#6B7280");
    private static final Color COL_DIJKSTRA  = Color.web("#F0A500");
    private static final Color COL_BFS       = Color.web("#34D399");
    private static final Color COL_DFS       = Color.web("#F28B82");
    private static final Color COL_START     = Color.web("#A78BFA");

    private static final double NODE_R = 18;

    // ── Inicialización ─────────────────────────────────────────────────
    @FXML
    public void initialize() {
        User user = UserService.getInstance().getCurrentUser();
        isAdmin = user != null && user.getRole() == User.Role.ADMINISTRADOR;

        if (isAdmin) {
            // Admin: solo panel de edición, sin recorridos ni búsqueda
            lblMode.setText("Modo: Administrador (Edición del Grafo)");
            panelEstudiante.setVisible(false);
            panelEstudiante.setManaged(false);
            panelAdmin.setVisible(true);
            panelAdmin.setManaged(true);
        } else {
            // Estudiante: solo búsqueda y recorridos, sin edición
            lblMode.setText("Modo: Estudiante (Visualización)");
            panelEstudiante.setVisible(true);
            panelEstudiante.setManaged(true);
            panelAdmin.setVisible(false);
            panelAdmin.setManaged(false);
        }

        refreshComboBoxes();
        drawGraph();

        // Canvas clic: mostrar nombre de edificio
        canvas.setOnMouseClicked(e -> {
            Building clicked = buildingAt(e.getX(), e.getY());
            if (clicked != null) {
                lblStatus.setText("Edificio: " + clicked.getName()
                        + " (" + clicked.getX() + ", " + clicked.getY() + ")");
            }
        });
    }

    // ── Refrescar ComboBoxes ───────────────────────────────────────────
    private void refreshComboBoxes() {
        List<Building> list = new ArrayList<>(service.getBuildings());

        if (isAdmin) {
            // Solo los combos del panel de edición
            for (ComboBox<Building> cb : List.of(cbDeleteBuilding, cbEdgeFrom, cbEdgeTo)) {
                Building sel = cb.getValue();
                cb.getItems().setAll(list);
                if (sel != null && list.contains(sel)) cb.setValue(sel);
            }
        } else {
            // Solo los combos del panel de búsqueda/recorridos
            for (ComboBox<Building> cb : List.of(cbOrigen, cbDestino, cbStart)) {
                Building sel = cb.getValue();
                cb.getItems().setAll(list);
                if (sel != null && list.contains(sel)) cb.setValue(sel);
            }
        }
    }

    // ── Dibujo del Grafo ──────────────────────────────────────────────
    public void drawGraph() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double W = canvas.getWidth(), H = canvas.getHeight();

        // Fondo
        gc.setFill(COL_BG);
        gc.fillRect(0, 0, W, H);

        // Aristas
        gc.setLineWidth(1.5);
        for (CEdge e : service.getAllEdges()) {
            Building a = service.getBuilding(e.from);
            Building b = service.getBuilding(e.to);
            if (a == null || b == null) continue;

            boolean hilite = isEdgeHighlighted(e.from, e.to);
            gc.setStroke(hilite ? highlightColor : COL_EDGE);
            gc.setLineWidth(hilite ? 3.0 : 1.5);
            gc.strokeLine(a.getX(), a.getY(), b.getX(), b.getY());

            // Peso en el centro de la arista
            double mx = (a.getX() + b.getX()) / 2;
            double my = (a.getY() + b.getY()) / 2;
            gc.setFill(COL_WEIGHT);
            gc.setFont(Font.font("Consolas", 10));
            gc.fillText((int) e.weight + "m", mx + 4, my - 3);
        }

        // Nodos
        for (Building b : service.getBuildings()) {
            boolean hi = highlightedNodes.contains(b.getId());
            Color nodeColor = hi ? highlightColor : COL_NODE;

            // Sombra/anillo
            gc.setFill(hi ? highlightColor.deriveColor(0, 1, 0.5, 0.3) : COL_NODE_RING.deriveColor(0, 1, 1, 0.3));
            gc.fillOval(b.getX() - NODE_R - 4, b.getY() - NODE_R - 4, (NODE_R + 4) * 2, (NODE_R + 4) * 2);

            // Círculo principal
            gc.setFill(nodeColor);
            gc.fillOval(b.getX() - NODE_R, b.getY() - NODE_R, NODE_R * 2, NODE_R * 2);

            // ID dentro del nodo
            gc.setFill(Color.web("#1A1C22"));
            gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
            gc.fillText(b.getId(), b.getX() - 9, b.getY() + 4);

            // Nombre debajo
            gc.setFill(COL_LABEL);
            gc.setFont(Font.font("Segoe UI", 10));
            String name = b.getName().length() > 18
                    ? b.getName().substring(0, 15) + "…"
                    : b.getName();
            gc.fillText(name, b.getX() - (name.length() * 3), b.getY() + NODE_R + 14);
        }
    }

    private boolean isEdgeHighlighted(String a, String b) {
        for (String[] e : highlightedEdges) {
            if ((e[0].equals(a) && e[1].equals(b)) || (e[0].equals(b) && e[1].equals(a)))
                return true;
        }
        return false;
    }

    private Building buildingAt(double mx, double my) {
        for (Building b : service.getBuildings()) {
            double dx = b.getX() - mx, dy = b.getY() - my;
            if (dx * dx + dy * dy <= (NODE_R + 6) * (NODE_R + 6)) return b;
        }
        return null;
    }

    // ── Dijkstra ──────────────────────────────────────────────────────
    @FXML
    private void onDijkstra() {
        Building ori = cbOrigen.getValue();
        Building dst = cbDestino.getValue();
        if (ori == null || dst == null) {
            lblStatus.setText("Selecciona Origen y Destino.");
            return;
        }
        clearHighlights();
        highlightColor = COL_DIJKSTRA;

        DijkstraResult res = service.dijkstra(ori.getId());
        List<String> path  = res.pathTo(dst.getId());

        if (path.isEmpty()) {
            lblStatus.setText("No existe ruta entre " + ori.getName() + " y " + dst.getName());
            return;
        }

        // Resaltar nodos del camino
        highlightedNodes.addAll(path);

        // Resaltar aristas del camino
        for (int i = 0; i < path.size() - 1; i++)
            highlightedEdges.add(new String[]{ path.get(i), path.get(i + 1) });

        double dist = res.dist.getOrDefault(dst.getId(), Double.POSITIVE_INFINITY);
        lblSteps.setText(String.join(" → ", path.stream()
                .map(id -> service.getBuilding(id) != null ? service.getBuilding(id).getName() : id)
                .toList()));
        lblStatus.setText("Ruta más corta: " + (int) dist + " m");
        drawGraph();
    }

    // ── BFS ───────────────────────────────────────────────────────────
    @FXML
    private void onBFS() {
        Building start = cbStart.getValue();
        if (start == null) { lblStatus.setText("Selecciona nodo inicial."); return; }
        clearHighlights();
        highlightColor = COL_BFS;
        List<String> order = service.bfs(start.getId());
        animateTraversal(order, "BFS");
    }

    // ── DFS ───────────────────────────────────────────────────────────
    @FXML
    private void onDFS() {
        Building start = cbStart.getValue();
        if (start == null) { lblStatus.setText("Selecciona nodo inicial."); return; }
        clearHighlights();
        highlightColor = COL_DFS;
        List<String> order = service.dfs(start.getId());
        animateTraversal(order, "DFS");
    }

    /** Animación paso a paso: resalta un nodo cada 600 ms */
    private void animateTraversal(List<String> order, String algo) {
        if (order.isEmpty()) return;
        drawGraph();

        Timeline timeline = new Timeline();
        for (int i = 0; i < order.size(); i++) {
            final int idx = i;
            KeyFrame kf = new KeyFrame(Duration.millis(600 * (idx + 1)), e -> {
                highlightedNodes.add(order.get(idx));
                lblSteps.setText(algo + ": " + String.join(" → ", order.subList(0, idx + 1)
                        .stream().map(id -> service.getBuilding(id) != null
                                ? service.getBuilding(id).getName() : id).toList()));
                drawGraph();
            });
            timeline.getKeyFrames().add(kf);
        }
        timeline.setOnFinished(e ->
                lblStatus.setText(algo + " completado — " + order.size() + " edificios visitados."));
        timeline.play();
        lblStatus.setText("Ejecutando " + algo + "…");
    }

    // ── Admin: Agregar Edificio ────────────────────────────────────────
    @FXML
    private void onAddBuilding() {
        String id   = txtBuildingId.getText().trim();
        String name = txtBuildingName.getText().trim();
        String xs   = txtBX.getText().trim();
        String ys   = txtBY.getText().trim();

        if (id.isEmpty() || name.isEmpty() || xs.isEmpty() || ys.isEmpty()) {
            lblStatus.setText("Completa todos los campos del edificio.");
            return;
        }
        if (service.containsBuilding(id)) {
            lblStatus.setText("Ya existe un edificio con ID: " + id);
            return;
        }
        try {
            double x = Double.parseDouble(xs);
            double y = Double.parseDouble(ys);
            Building b = new Building.Builder(id).name(name).x(x).y(y).build();
            service.addBuilding(b);
            refreshComboBoxes();
            clearHighlights();
            drawGraph();
            service.saveToJson();
            lblStatus.setText("Edificio '" + name + "' agregado.");
            txtBuildingId.clear(); txtBuildingName.clear(); txtBX.clear(); txtBY.clear();
        } catch (NumberFormatException ex) {
            lblStatus.setText("X e Y deben ser números.");
        }
    }

    // ── Admin: Eliminar Edificio ───────────────────────────────────────
    @FXML
    private void onDeleteBuilding() {
        Building b = cbDeleteBuilding.getValue();
        if (b == null) { lblStatus.setText("Selecciona un edificio."); return; }
        service.removeBuilding(b.getId());
        refreshComboBoxes();
        clearHighlights();
        drawGraph();
        service.saveToJson();
        lblStatus.setText("Edificio '" + b.getName() + "' eliminado.");
    }

    // ── Admin: Guardar Conexión ────────────────────────────────────────
    @FXML
    private void onSaveEdge() {
        Building from = cbEdgeFrom.getValue();
        Building to   = cbEdgeTo.getValue();
        String   ws   = txtEdgeWeight.getText().trim();

        if (from == null || to == null || ws.isEmpty()) {
            lblStatus.setText("Completa todos los campos de la conexión.");
            return;
        }
        if (from.getId().equals(to.getId())) {
            lblStatus.setText("El origen y destino no pueden ser el mismo edificio.");
            return;
        }
        try {
            double w = Double.parseDouble(ws);
            if (w <= 0) { lblStatus.setText("La distancia debe ser positiva."); return; }
            service.addEdge(from.getId(), to.getId(), w);
            clearHighlights();
            drawGraph();
            service.saveToJson();
            lblStatus.setText("Conexión guardada: " + from.getName() + " ↔ " + to.getName()
                    + " (" + (int) w + " m)");
            txtEdgeWeight.clear();
        } catch (NumberFormatException ex) {
            lblStatus.setText("La distancia debe ser un número válido.");
        }
    }

    // ── Admin: Guardar JSON ───────────────────────────────────────────
    @FXML
    private void onSaveJson() {
        service.saveToJson();
        lblStatus.setText("campus.json guardado correctamente.");
    }

    // ── Utilidades ────────────────────────────────────────────────────
    private void clearHighlights() {
        highlightedNodes.clear();
        highlightedEdges.clear();
    }

    /** Llamado por MainController para indicar si el usuario es admin */
    public void setAdminMode(boolean admin) {
        this.isAdmin = admin;
    }
}
