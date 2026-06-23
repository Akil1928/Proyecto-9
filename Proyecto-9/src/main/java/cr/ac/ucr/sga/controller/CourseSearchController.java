package cr.ac.ucr.sga.controller;

import cr.ac.ucr.sga.model.services.CurriculumService;
import cr.ac.ucr.sga.model.trees.BST;
import cr.ac.ucr.sga.model.trees.BTreeNode;
import cr.ac.ucr.sga.model.trees.TreeException;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseSearchController {

    @FXML private TextField txtSearchCode;
    @FXML private Canvas    canvas;
    @FXML private Label     lblStatus;
    @FXML private Label     lblInfo;
    @FXML private TextArea  txtTraversal;

    private final CurriculumService curriculum = CurriculumService.getInstance();
    private BST<String> bst = new BST<>();

    private String  highlightedNode  = null;  // encontrado (verde)
    private List<String> searchPath  = new ArrayList<>(); // comparados (rojo)

    private static final double NODE_RADIUS = 26;
    private static final double LEVEL_GAP   = 75;
    private static final double SIBLING_GAP = 8;

    private Map<BTreeNode<String>, Double> xPositions = new HashMap<>();

    @FXML
    public void initialize() {
        buildBST();
    }

    // ── Construir / Reconstruir árbol ─────────────────────────────────────

    private void buildBST() {
        bst = new BST<>();
        for (String code : curriculum.getAllCourseCodes()) {
            bst.add(code);
        }
        highlightedNode = null;
        searchPath.clear();
        drawTree();
        updateInfo();
        if (lblStatus != null) lblStatus.setText("✔ BST reconstruido con " + curriculum.getAllCourseCodes().size() + " cursos de la malla.");
    }

    @FXML
    private void onReconstruir() {
        buildBST();
    }

    // ── Búsqueda ──────────────────────────────────────────────────────────

    @FXML
    private void onBuscar() {
        String code = getCode();
        if (code == null) return;

        searchPath.clear();
        highlightedNode = null;

        boolean found = searchWithPath(bst.root, code);

        drawTree();

        if (found) {
            highlightedNode = code;
            drawTree();
            lblStatus.setText("✔ Curso \"" + code + "\" encontrado. Se compararon " + searchPath.size() + " nodos.");
        } else {
            lblStatus.setText("⚠ Curso \"" + code + "\" no encontrado en el BST.");
        }
    }

    private boolean searchWithPath(BTreeNode<String> node, String target) {
        if (node == null) return false;
        String data = node.data.toString();
        searchPath.add(data);
        int cmp = target.compareTo(data);
        if (cmp == 0) return true;
        else if (cmp < 0) return searchWithPath(node.left, target);
        else return searchWithPath(node.right, target);
    }

    // ── Recorridos ────────────────────────────────────────────────────────

    @FXML
    private void onInOrder() {
        try {
            txtTraversal.setText("InOrden:\n" + bst.inOrder());
        } catch (TreeException e) {
            txtTraversal.setText("⚠ " + e.getMessage());
        }
    }

    @FXML
    private void onPreOrder() {
        try {
            txtTraversal.setText("PreOrden:\n" + bst.preOrder());
        } catch (TreeException e) {
            txtTraversal.setText("⚠ " + e.getMessage());
        }
    }

    @FXML
    private void onPostOrder() {
        try {
            txtTraversal.setText("PostOrden:\n" + bst.postOrder());
        } catch (TreeException e) {
            txtTraversal.setText("⚠ " + e.getMessage());
        }
    }

    // ── Dibujo ────────────────────────────────────────────────────────────

    private void drawTree() {
        if (bst.isEmpty()) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.web("#64748b"));
            gc.fillText("Árbol vacío.", 20, 30);
            return;
        }

        BTreeNode<String> root = bst.root;
        int    leaves         = Math.max(1, countLeaves(root));
        double requiredWidth  = leaves * (NODE_RADIUS * 2 + SIBLING_GAP) + 60;
        int    depth          = height(root);
        double requiredHeight = depth * (NODE_RADIUS * 2 + LEVEL_GAP) + 80;

        canvas.setWidth(Math.max(requiredWidth, 1100));
        canvas.setHeight(Math.max(requiredHeight, 600));

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
            xPositions.put(node, 40 + counter[0] * (NODE_RADIUS * 2 + SIBLING_GAP) + NODE_RADIUS);
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
        for (BTreeNode<String> child : new BTreeNode[]{node.left, node.right}) {
            if (child != null) {
                double cx = xPositions.getOrDefault(child, 0.0);
                double cy = 30 + (depth + 1) * (NODE_RADIUS * 2 + LEVEL_GAP) + NODE_RADIUS;
                gc.strokeLine(x, y + NODE_RADIUS, cx, cy - NODE_RADIUS);
                drawEdges(gc, child, depth + 1);
            }
        }
    }

    private void drawNodes(GraphicsContext gc, BTreeNode<String> node, int depth) {
        if (node == null) return;
        double x    = xPositions.getOrDefault(node, 0.0);
        double y    = 30 + depth * (NODE_RADIUS * 2 + LEVEL_GAP) + NODE_RADIUS;
        String code = node.data != null ? node.data.toString() : "";

        Color fill;
        if (code.equals(highlightedNode)) {
            fill = Color.web("#86efac"); // verde — encontrado
        } else if (searchPath.contains(code)) {
            fill = Color.web("#fca5a5"); // rojo — comparado
        } else if (depth == 0) {
            fill = Color.web("#fde68a"); // amarillo — raíz
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
        gc.setFont(Font.font("System", FontWeight.BOLD, 9));
        gc.fillText(code, x, y + 4);

        drawNodes(gc, node.left,  depth + 1);
        drawNodes(gc, node.right, depth + 1);
    }

    // ── Utilidades ────────────────────────────────────────────────────────

    private String getCode() {
        String text = txtSearchCode.getText();
        if (text == null || text.trim().isEmpty()) {
            lblStatus.setText("⚠ Ingrese un código de curso.");
            return null;
        }
        return text.trim().toUpperCase();
    }

    private void updateInfo() {
        int count  = curriculum.getAllCourseCodes().size();
        int height = height(bst.root);
        if (lblInfo != null) lblInfo.setText("Cursos: " + count + "  |  Altura: " + height);
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