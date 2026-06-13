package cr.ac.ucr.sga.controller;

import cr.ac.ucr.sga.model.services.CurriculumService;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PrerequisiteTreeController {

    @FXML private TextField txtCourseCode;
    @FXML private Canvas canvas;
    @FXML private Label lblStatus;

    private final CurriculumService curriculum = CurriculumService.getInstance();

    private static final double NODE_RADIUS = 30;
    private static final double LEVEL_GAP   = 90;
    private static final double SIBLING_GAP = 20;

    private static class PNode {
        String code;
        String label;
        boolean isGroupConnector;
        List<PNode> children = new ArrayList<>();

        PNode(String code, String label, boolean isGroupConnector) {
            this.code = code;
            this.label = label;
            this.isGroupConnector = isGroupConnector;
        }
    }

    private Map<PNode, Double> xPositions;

    @FXML
    public void initialize() {
        clearCanvas();
    }

    @FXML
    private void onBuscar() {
        String code = txtCourseCode.getText();
        if (code == null || code.trim().isEmpty()) {
            lblStatus.setText("⚠ Ingrese un código de curso.");
            return;
        }
        code = code.trim().toUpperCase();

        if (curriculum.getCourseName(code) == null) {
            clearCanvas();
            lblStatus.setText("⚠ El curso \"" + code + "\" no existe en la malla curricular.");
            return;
        }

        PNode root = buildTree(code, new java.util.HashSet<>());
        drawTree(root);

        if (root.children.isEmpty()) {
            lblStatus.setText("✔ El curso \"" + code + "\" no tiene prerrequisitos.");
        } else {
            lblStatus.setText("✔ Árbol de prerrequisitos de \"" + code + "\" generado.");
        }
    }

    private PNode buildTree(String code, Set<String> visited) {
        String name = curriculum.getCourseName(code);
        PNode node = new PNode(code, code + (name != null ? "\n" + name : ""), false);

        if (visited.contains(code)) {
            return node;
        }
        visited.add(code);

        List<Set<String>> groups = curriculum.getPrerequisiteGroups(code);
        for (Set<String> group : groups) {
            if (group.isEmpty()) continue;
            if (group.size() == 1) {
                node.children.add(buildTree(group.iterator().next(), new java.util.HashSet<>(visited)));
            } else {
                PNode orNode = new PNode("O", "uno de:", true);
                for (String alt : group) {
                    orNode.children.add(buildTree(alt, new java.util.HashSet<>(visited)));
                }
                node.children.add(orNode);
            }
        }
        return node;
    }

    private void clearCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.web("#64748b"));
        gc.setFont(Font.font("System", 14));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Ingrese un código de curso y presione \"Buscar\".", 20, 30);
    }

    private void drawTree(PNode root) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        int leafCount = Math.max(1, countLeaves(root));
        double requiredWidth = leafCount * (NODE_RADIUS * 2 + SIBLING_GAP) + 60;
        int depth = height(root);
        double requiredHeight = depth * (NODE_RADIUS * 2 + LEVEL_GAP) + 80;

        canvas.setWidth(Math.max(requiredWidth, 1100));
        canvas.setHeight(Math.max(requiredHeight, 500));

        xPositions = new HashMap<>();
        double[] counter = {0};
        assignX(root, counter);

        gc.setLineWidth(1.5);
        gc.setStroke(Color.web("#94a3b8"));
        drawConnections(gc, root, 0);

        drawNodes(gc, root, 0);
    }

    private void assignX(PNode node, double[] counter) {
        if (node == null) return;
        if (node.children.isEmpty()) {
            double x = 40 + counter[0] * (NODE_RADIUS * 2 + SIBLING_GAP) + NODE_RADIUS;
            xPositions.put(node, x);
            counter[0]++;
            return;
        }
        for (PNode child : node.children) {
            assignX(child, counter);
        }
        double first = xPositions.get(node.children.get(0));
        double last = xPositions.get(node.children.get(node.children.size() - 1));
        xPositions.put(node, (first + last) / 2);
    }

    private void drawConnections(GraphicsContext gc, PNode node, int depth) {
        if (node == null) return;
        double x = xPositions.get(node);
        double y = 30 + depth * (NODE_RADIUS * 2 + LEVEL_GAP) + NODE_RADIUS;

        for (PNode child : node.children) {
            double cx = xPositions.get(child);
            double cy = 30 + (depth + 1) * (NODE_RADIUS * 2 + LEVEL_GAP) + NODE_RADIUS;
            gc.strokeLine(x, y + NODE_RADIUS, cx, cy - NODE_RADIUS);
            drawConnections(gc, child, depth + 1);
        }
    }

    private void drawNodes(GraphicsContext gc, PNode node, int depth) {
        if (node == null) return;

        double x = xPositions.get(node);
        double y = 30 + depth * (NODE_RADIUS * 2 + LEVEL_GAP) + NODE_RADIUS;

        Color fill = depth == 0 ? Color.web("#86efac")
                : node.isGroupConnector ? Color.web("#fde68a")
                  : Color.web("#e2e8f0");

        gc.setFill(fill);
        gc.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        gc.setStroke(Color.web("#334155"));
        gc.setLineWidth(1.2);
        gc.strokeOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

        gc.setFill(Color.web("#0f172a"));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("System", FontWeight.BOLD, 10));

        String[] lines = node.label.split("\n");
        for (int i = 0; i < lines.length; i++) {
            gc.fillText(truncate(lines[i]), x, y - (lines.length - 1) * 6 + i * 12 + 3);
        }

        for (PNode child : node.children) {
            drawNodes(gc, child, depth + 1);
        }
    }

    private String truncate(String s) {
        return s.length() > 12 ? s.substring(0, 11) + "…" : s;
    }

    private int countLeaves(PNode node) {
        if (node == null) return 0;
        if (node.children.isEmpty()) return 1;
        int total = 0;
        for (PNode child : node.children) total += countLeaves(child);
        return total;
    }

    private int height(PNode node) {
        if (node == null) return 0;
        int max = 0;
        for (PNode child : node.children) max = Math.max(max, height(child));
        return 1 + max;
    }
}