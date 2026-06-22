package cr.ac.ucr.sga.controller;

import cr.ac.ucr.sga.model.services.CurriculumService;
import cr.ac.ucr.sga.model.trees.AVL;
import cr.ac.ucr.sga.model.trees.BTreeNode;
import cr.ac.ucr.sga.model.trees.TreeException;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfessorController {

    @FXML private TextField txtCourseCode;
    @FXML private Canvas    canvas;
    @FXML private Label     lblStatus;
    @FXML private Label     lblTraversal;
    @FXML private Button    btnAnimate;

    private final CurriculumService curriculum = CurriculumService.getInstance();
    private AVL<String> avl;

    // estado de animación
    private List<String> traversalOrder = new ArrayList<>();
    private int     traversalStep  = 0;
    private boolean isAnimating    = false;
    private String  highlightedNode = null;

    private static final double NODE_RADIUS = 28;
    private static final double LEVEL_GAP   = 80;
    private static final double SIBLING_GAP = 10;

    private Map<BTreeNode<String>, Double> xPositions = new HashMap<>();

    // ── Inicialización ────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        avl = new AVL<>();
        clearCanvas();
    }

    // ── Operaciones sobre el árbol ────────────────────────────────────────

    @FXML
    private void onInsert() {
        String code = getCode();
        if (code == null) return;
        avl.add(code);
        highlightedNode = null;
        drawTree();
        lblStatus.setText("✔ \"" + code + "\" insertado. Árbol AVL balanceado.");
    }

    @FXML
    private void onDelete() {
        String code = getCode();
        if (code == null) return;
        try {
            avl.remove(code);
            highlightedNode = null;
            drawTree();
            lblStatus.setText("✔ \"" + code + "\" eliminado. Árbol AVL rebalanceado.");
        } catch (TreeException e) {
            lblStatus.setText("⚠ " + e.getMessage());
        }
    }

    @FXML
    private void onSearch() {
        String code = getCode();
        if (code == null) return;
        try {
            boolean found = avl.contains(code);
            highlightedNode = found ? code : null;
            drawTree();
            if (found) {
                lblStatus.setText("✔ Curso \"" + code + "\" encontrado (resaltado en azul).");
            } else {
                lblStatus.setText("⚠ Curso \"" + code + "\" no se encuentra en el árbol.");
            }
        } catch (TreeException e) {
            lblStatus.setText("⚠ " + e.getMessage());
        }
    }

    @FXML
    private void onClear() {
        avl = new AVL<>();
        traversalOrder.clear();
        traversalStep   = 0;
        highlightedNode = null;
        isAnimating     = false;
        lblTraversal.setText("");
        clearCanvas();
        lblStatus.setText("✔ Árbol limpiado.");
    }

    @FXML
    private void onLoadAll() {
        avl = new AVL<>();
        highlightedNode = null;
        int count = 0;
        for (String code : curriculum.getAllCourseCodes()) {
            avl.add(code);
            count++;
        }
        drawTree();
        lblStatus.setText("✔ " + count + " cursos de la malla insertados en el árbol AVL.");
    }

    // ── Recorridos ────────────────────────────────────────────────────────

    @FXML
    private void onInorder() {
        try {
            String raw = avl.inOrder();
            loadTraversal(raw, "Inorden");
        } catch (TreeException e) {
            lblStatus.setText("⚠ " + e.getMessage());
        }
    }

    @FXML
    private void onPreorder() {
        try {
            String raw = avl.preOrder();
            loadTraversal(raw, "Preorden");
        } catch (TreeException e) {
            lblStatus.setText("⚠ " + e.getMessage());
        }
    }

    @FXML
    private void onPostorder() {
        try {
            String raw = avl.postOrder();
            loadTraversal(raw, "Posorden");
        } catch (TreeException e) {
            lblStatus.setText("⚠ " + e.getMessage());
        }
    }

    // convierte el String "A, B, C, " en una lista y actualiza la UI
    private void loadTraversal(String raw, String name) {
        traversalOrder.clear();
        traversalStep   = 0;
        highlightedNode = null;
        isAnimating     = false;
        btnAnimate.setDisable(false);

        // "IF-0001, IF-0002, " → split y trim
        String[] parts = raw.split(",");
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) traversalOrder.add(t);
        }

        lblTraversal.setText(name + ": " + String.join(" → ", traversalOrder));
        lblStatus.setText("✔ Recorrido " + name + " listo. Presione \"Animar\" o \"Paso\".");
        drawTree();
    }

    @FXML
    private void onStep() {
        if (traversalOrder.isEmpty()) {
            lblStatus.setText("⚠ Primero ejecute un recorrido.");
            return;
        }
        if (traversalStep < traversalOrder.size()) {
            highlightedNode = traversalOrder.get(traversalStep);
            traversalStep++;
            drawTree();
            lblStatus.setText("Paso " + traversalStep + "/" + traversalOrder.size()
                    + " — visitando: " + highlightedNode);
        } else {
            highlightedNode = null;
            drawTree();
            lblStatus.setText("✔ Recorrido completado.");
        }
    }

    @FXML
    private void onAnimate() {
        if (traversalOrder.isEmpty()) {
            lblStatus.setText("⚠ Primero ejecute un recorrido.");
            return;
        }
        if (isAnimating) return;
        isAnimating     = true;
        traversalStep   = 0;
        highlightedNode = null;
        btnAnimate.setDisable(true);
        animateStep();
    }

    private void animateStep() {
        if (traversalStep < traversalOrder.size()) {
            highlightedNode = traversalOrder.get(traversalStep);
            traversalStep++;
            drawTree();
            lblStatus.setText("Animando paso " + traversalStep + "/" + traversalOrder.size()
                    + " — " + highlightedNode);
            PauseTransition pause = new PauseTransition(Duration.millis(600));
            pause.setOnFinished(e -> animateStep());
            pause.play();
        } else {
            highlightedNode = null;
            drawTree();
            isAnimating = false;
            btnAnimate.setDisable(false);
            lblStatus.setText("✔ Animación de recorrido completada.");
        }
    }

    @FXML
    private void onReset() {
        traversalOrder.clear();
        traversalStep   = 0;
        highlightedNode = null;
        isAnimating     = false;
        btnAnimate.setDisable(false);
        lblTraversal.setText("");
        drawTree();
        lblStatus.setText("✔ Estado de animación reiniciado.");
    }

    // ── Dibujo en Canvas ──────────────────────────────────────────────────

    private void clearCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.web("#64748b"));
        gc.setFont(Font.font("System", 14));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Inserte cursos o cargue la malla para visualizar el árbol AVL.", 20, 30);
    }

    private void drawTree() {
        if (avl.isEmpty()) {
            clearCanvas();
            return;
        }

        BTreeNode<String> root = avl.root;

        int    leaves          = Math.max(1, countLeaves(root));
        double requiredWidth   = leaves * (NODE_RADIUS * 2 + SIBLING_GAP) + 60;
        int    depth           = height(root);
        double requiredHeight  = depth * (NODE_RADIUS * 2 + LEVEL_GAP) + 80;

        canvas.setWidth(Math.max(requiredWidth, 1100));
        canvas.setHeight(Math.max(requiredHeight, 520));

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        xPositions = new HashMap<>();
        double[] counter = {0};
        assignX(root, counter);

        gc.setStroke(Color.web("#94a3b8"));
        gc.setLineWidth(1.5);
        drawEdges(gc, root, 0);
        drawNodes(gc, root, 0);
    }

    private void assignX(BTreeNode<String> node, double[] counter) {
        if (node == null) return;
        if (node.left == null && node.right == null) {
            double x = 40 + counter[0] * (NODE_RADIUS * 2 + SIBLING_GAP) + NODE_RADIUS;
            xPositions.put(node, x);
            counter[0]++;
            return;
        }
        assignX(node.left, counter);
        assignX(node.right, counter);

        double left  = node.left  != null ? xPositions.getOrDefault(node.left,  0.0) : -1;
        double right = node.right != null ? xPositions.getOrDefault(node.right, 0.0) : -1;

        if (left >= 0 && right >= 0) xPositions.put(node, (left + right) / 2.0);
        else if (left  >= 0)          xPositions.put(node, left);
        else                           xPositions.put(node, right);
    }

    private void drawEdges(GraphicsContext gc, BTreeNode<String> node, int depth) {
        if (node == null) return;
        double x = xPositions.getOrDefault(node, 0.0);
        double y = 30 + depth * (NODE_RADIUS * 2 + LEVEL_GAP) + NODE_RADIUS;

        if (node.left != null) {
            double cx = xPositions.getOrDefault(node.left, 0.0);
            double cy = 30 + (depth + 1) * (NODE_RADIUS * 2 + LEVEL_GAP) + NODE_RADIUS;
            gc.strokeLine(x, y + NODE_RADIUS, cx, cy - NODE_RADIUS);
            drawEdges(gc, node.left, depth + 1);
        }
        if (node.right != null) {
            double cx = xPositions.getOrDefault(node.right, 0.0);
            double cy = 30 + (depth + 1) * (NODE_RADIUS * 2 + LEVEL_GAP) + NODE_RADIUS;
            gc.strokeLine(x, y + NODE_RADIUS, cx, cy - NODE_RADIUS);
            drawEdges(gc, node.right, depth + 1);
        }
    }

    private void drawNodes(GraphicsContext gc, BTreeNode<String> node, int depth) {
        if (node == null) return;

        double x    = xPositions.getOrDefault(node, 0.0);
        double y    = 30 + depth * (NODE_RADIUS * 2 + LEVEL_GAP) + NODE_RADIUS;
        String code = node.data != null ? node.data.toString() : "";

        Color fill;
        if (code.equals(highlightedNode) && isAnimating) {
            fill = Color.web("#fde68a"); // amarillo — animación
        } else if (code.equals(highlightedNode)) {
            fill = Color.web("#93c5fd"); // azul — búsqueda
        } else if (depth == 0) {
            fill = Color.web("#86efac"); // verde — raíz
        } else {
            fill = Color.web("#e2e8f0"); // blanco — normal
        }

        gc.setFill(fill);
        gc.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        gc.setStroke(Color.web("#334155"));
        gc.setLineWidth(1.5);
        gc.strokeOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

        gc.setFill(Color.web("#0f172a"));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("System", FontWeight.BOLD, 10));
        gc.fillText(code, x, y);

        // factor de balance
        int bf = avl.getBalanceFactor(node);
        gc.setFont(Font.font("System", 9));
        gc.setFill(Color.web("#64748b"));
        gc.fillText("bf:" + bf, x, y + 13);

        drawNodes(gc, node.left,  depth + 1);
        drawNodes(gc, node.right, depth + 1);
    }

    // ── Utilidades ────────────────────────────────────────────────────────

    private String getCode() {
        String text = txtCourseCode.getText();
        if (text == null || text.trim().isEmpty()) {
            lblStatus.setText("⚠ Ingrese un código de curso.");
            return null;
        }
        return text.trim().toUpperCase();
    }

    private int countLeaves(BTreeNode<String> node) {
        if (node == null) return 0;
        if (node.left == null && node.right == null) return 1;
        return countLeaves(node.left) + countLeaves(node.right);
    }

    private int height(BTreeNode<String> node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }
}