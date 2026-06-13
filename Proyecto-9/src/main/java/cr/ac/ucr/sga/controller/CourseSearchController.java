package cr.ac.ucr.sga.controller;

import cr.ac.ucr.sga.model.entities.CourseEntry;
import cr.ac.ucr.sga.model.services.CourseSearchService;
import cr.ac.ucr.sga.model.trees.BTree;
import cr.ac.ucr.sga.model.trees.BTreeNode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CourseSearchController — US-11 (Sprint 3).
 *
 * Dibuja el Árbol Binario de Búsqueda (BST) construido por
 * {@link CourseSearchService} y permite buscar un curso por código,
 * animando el camino recorrido nodo por nodo.
 */
public class CourseSearchController {

    @FXML private TextField txtSearchCode;
    @FXML private Canvas canvas;
    @FXML private Label lblInfo;
    @FXML private Label lblStatus;

    private final CourseSearchService searchService = CourseSearchService.getInstance();

    private static final double NODE_RADIUS = 28;
    private static final double LEVEL_GAP   = 80;
    private static final double SIBLING_GAP = 14;

    private Map<BTreeNode<CourseEntry>, Double> xPositions;
    private Timeline animation;

    @FXML
    public void initialize() {
        refreshInfo();
        drawTree(null, -1);
    }

    @FXML
    private void onReconstruir() {
        searchService.buildFromCurriculum();
        refreshInfo();
        drawTree(null, -1);
        lblStatus.setText("✔ Árbol reconstruido a partir de la malla curricular ("
                + searchService.size() + " cursos).");
    }

    @FXML
    private void onBuscar() {
        String code = txtSearchCode.getText();
        if (code == null || code.trim().isEmpty()) {
            lblStatus.setText("⚠ Ingrese un código de curso.");
            return;
        }
        code = code.trim().toUpperCase();

        CourseSearchService.SearchResult result = searchService.search(code);

        if (animation != null) {
            animation.stop();
        }

        if (result.getPath().isEmpty()) {
            drawTree(null, -1);
            lblStatus.setText("⚠ El árbol está vacío.");
            return;
        }

        animateSearch(result, code);
    }

    private void refreshInfo() {
        lblInfo.setText("Cursos: " + searchService.size() + "  |  Altura: " + searchService.height());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Animación del recorrido de búsqueda
    // ─────────────────────────────────────────────────────────────────────────

    private void animateSearch(CourseSearchService.SearchResult result, String code) {
        List<CourseEntry> path = result.getPath();

        animation = new Timeline();
        for (int i = 0; i < path.size(); i++) {
            final int step = i;
            KeyFrame frame = new KeyFrame(Duration.millis(500.0 * (i + 1)), e -> {
                boolean isLast = step == path.size() - 1;
                drawTree(path.subList(0, step + 1), isLast && result.isFound() ? step : -1);
            });
            animation.getKeyFrames().add(frame);
        }

        animation.setOnFinished(e -> {
            if (result.isFound()) {
                lblStatus.setText("✔ Curso \"" + code + "\" encontrado en "
                        + result.getComparisons() + " comparación(es). Camino: "
                        + pathToString(path));
            } else {
                lblStatus.setText("✘ Curso \"" + code + "\" NO encontrado. Comparaciones realizadas: "
                        + result.getComparisons() + ". Camino recorrido: " + pathToString(path));
            }
        });

        animation.play();
    }

    private String pathToString(List<CourseEntry> path) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) sb.append(" → ");
            sb.append(path.get(i).getCode());
        }
        return sb.toString();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Dibujo en Canvas
    // ─────────────────────────────────────────────────────────────────────────

    private void drawTree(List<CourseEntry> highlightPath, int foundIndex) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        BTreeNode<CourseEntry> root = searchService.getTree().root;
        if (root == null) {
            gc.setFill(Color.web("#64748b"));
            gc.setFont(Font.font("System", 14));
            gc.setTextAlign(TextAlignment.LEFT);
            gc.fillText("El árbol está vacío.", 20, 30);
            return;
        }

        int leafCount = Math.max(1, countLeaves(root));
        double requiredWidth = leafCount * (NODE_RADIUS * 2 + SIBLING_GAP) + 60;
        int depth = height(root);
        double requiredHeight = depth * (NODE_RADIUS * 2 + LEVEL_GAP) + 60;

        if (canvas.getWidth() < requiredWidth) canvas.setWidth(requiredWidth);
        if (canvas.getHeight() < requiredHeight) canvas.setHeight(requiredHeight);

        xPositions = new HashMap<>();
        double[] counter = {0};
        assignX(root, counter);

        gc.setLineWidth(1.5);
        gc.setStroke(Color.web("#94a3b8"));
        drawConnections(gc, root, 0);

        java.util.Set<String> highlightCodes = new java.util.HashSet<>();
        String foundCode = null;
        if (highlightPath != null) {
            for (CourseEntry c : highlightPath) highlightCodes.add(c.getCode());
            if (foundIndex >= 0 && foundIndex < highlightPath.size()) {
                foundCode = highlightPath.get(foundIndex).getCode();
            }
        }

        drawNodes(gc, root, 0, highlightCodes, foundCode);
    }

    private void assignX(BTreeNode<CourseEntry> node, double[] counter) {
        if (node == null) return;
        assignX(node.left, counter);
        double x = 40 + counter[0] * (NODE_RADIUS * 2 + SIBLING_GAP) + NODE_RADIUS;
        xPositions.put(node, x);
        counter[0]++;
        assignX(node.right, counter);
    }

    private void drawConnections(GraphicsContext gc, BTreeNode<CourseEntry> node, int depth) {
        if (node == null) return;
        double x = xPositions.get(node);
        double y = 30 + depth * (NODE_RADIUS * 2 + LEVEL_GAP) + NODE_RADIUS;

        if (node.left != null) {
            double cx = xPositions.get(node.left);
            double cy = 30 + (depth + 1) * (NODE_RADIUS * 2 + LEVEL_GAP) + NODE_RADIUS;
            gc.strokeLine(x, y + NODE_RADIUS, cx, cy - NODE_RADIUS);
            drawConnections(gc, node.left, depth + 1);
        }
        if (node.right != null) {
            double cx = xPositions.get(node.right);
            double cy = 30 + (depth + 1) * (NODE_RADIUS * 2 + LEVEL_GAP) + NODE_RADIUS;
            gc.strokeLine(x, y + NODE_RADIUS, cx, cy - NODE_RADIUS);
            drawConnections(gc, node.right, depth + 1);
        }
    }

    private void drawNodes(GraphicsContext gc, BTreeNode<CourseEntry> node, int depth,
                           java.util.Set<String> highlightCodes, String foundCode) {
        if (node == null) return;

        double x = xPositions.get(node);
        double y = 30 + depth * (NODE_RADIUS * 2 + LEVEL_GAP) + NODE_RADIUS;
        String code = node.data.getCode();

        Color fill;
        if (code.equals(foundCode)) {
            fill = Color.web("#86efac"); // verde - encontrado
        } else if (highlightCodes.contains(code)) {
            fill = Color.web("#fca5a5"); // rojo - comparado
        } else {
            fill = Color.web("#e2e8f0"); // gris claro - normal
        }

        gc.setFill(fill);
        gc.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        gc.setStroke(Color.web("#334155"));
        gc.setLineWidth(1.2);
        gc.strokeOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

        gc.setFill(Color.web("#0f172a"));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("System", FontWeight.BOLD, 11));
        gc.fillText(code, x, y + 4);

        drawNodes(gc, node.left, depth + 1, highlightCodes, foundCode);
        drawNodes(gc, node.right, depth + 1, highlightCodes, foundCode);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Métricas auxiliares
    // ─────────────────────────────────────────────────────────────────────────

    private int countLeaves(BTreeNode<CourseEntry> node) {
        if (node == null) return 0;
        if (node.left == null && node.right == null) return 1;
        return countLeaves(node.left) + countLeaves(node.right);
    }

    private int height(BTreeNode<CourseEntry> node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }
}