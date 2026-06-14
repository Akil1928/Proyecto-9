package cr.ac.ucr.sga;

import cr.ac.ucr.sga.model.trees.AVL;
import cr.ac.ucr.sga.model.trees.TreeException;
import org.junit.jupiter.api.Test;

public class AVLTest {

    //─── Inserción y balanceo ───────────────────────────────────────────

    @Test
    void testInsertionAndAutoBalance() throws Exception {
        System.out.println("=== AVL: Inserción y auto-balanceo ===");
        AVL<Integer> avl = new AVL<>();

        System.out.println("Insertando en orden ascendente (caso crítico para BST simple): 10, 20, 30");
        avl.add(10);
        avl.add(20);
        avl.add(30);

        System.out.println("InOrden (esperado: 10 20 30): " + avl.inOrder());
        System.out.println("¿Está balanceado? (esperado: true): " + avl.isBalanced());
        System.out.println("Altura del árbol (esperado: 2): " + avl.height());
        System.out.println("Información de rebalanceo:\n" + avl.getRebalancingInfo());
    }

    @Test
    void testRotacionLL() throws Exception {
        System.out.println("\n=== AVL: Rotación LL (Simple Right Rotate) ===");
        AVL<Integer> avl = new AVL<>();

        System.out.println("Insertando: 30, 20, 10 — provoca rotación LL");
        avl.add(30);
        avl.add(20);
        avl.add(10);

        System.out.println("InOrden (esperado: 10 20 30): " + avl.inOrder());
        System.out.println("¿Balanceado tras rotación LL? (esperado: true): " + avl.isBalanced());
        System.out.println("Rebalanceo aplicado:\n" + avl.getRebalancingInfo());
    }

    @Test
    void testRotacionRR() throws Exception {
        System.out.println("\n=== AVL: Rotación RR (Simple Left Rotate) ===");
        AVL<Integer> avl = new AVL<>();

        System.out.println("Insertando: 10, 20, 30 — provoca rotación RR");
        avl.add(10);
        avl.add(20);
        avl.add(30);

        System.out.println("InOrden (esperado: 10 20 30): " + avl.inOrder());
        System.out.println("¿Balanceado tras rotación RR? (esperado: true): " + avl.isBalanced());
        System.out.println("Rebalanceo aplicado:\n" + avl.getRebalancingInfo());
    }

    @Test
    void testRotacionLR() throws Exception {
        System.out.println("\n=== AVL: Rotación LR (Doble Left-Right Rotate) ===");
        AVL<Integer> avl = new AVL<>();

        System.out.println("Insertando: 30, 10, 20 — provoca rotación LR");
        avl.add(30);
        avl.add(10);
        avl.add(20);

        System.out.println("InOrden (esperado: 10 20 30): " + avl.inOrder());
        System.out.println("¿Balanceado tras rotación LR? (esperado: true): " + avl.isBalanced());
        System.out.println("Rebalanceo aplicado:\n" + avl.getRebalancingInfo());
    }

    @Test
    void testRotacionRL() throws Exception {
        System.out.println("\n=== AVL: Rotación RL (Doble Right-Left Rotate) ===");
        AVL<Integer> avl = new AVL<>();

        System.out.println("Insertando: 10, 30, 20 — provoca rotación RL");
        avl.add(10);
        avl.add(30);
        avl.add(20);

        System.out.println("InOrden (esperado: 10 20 30): " + avl.inOrder());
        System.out.println("¿Balanceado tras rotación RL? (esperado: true): " + avl.isBalanced());
        System.out.println("Rebalanceo aplicado:\n" + avl.getRebalancingInfo());
    }

    @Test
    void testEliminarNodoHoja() throws Exception {
        System.out.println("\n=== AVL: Eliminar nodo hoja ===");
        AVL<Integer> avl = new AVL<>();

        avl.add(20);
        avl.add(10);
        avl.add(30);
        System.out.println("Árbol inicial - InOrden: " + avl.inOrder());

        avl.remove(10);
        System.out.println("Tras eliminar 10 - InOrden (esperado: 20 30): " + avl.inOrder());
        System.out.println("¿Balanceado? (esperado: true): " + avl.isBalanced());
    }

    @Test
    void testEliminarNodoConDosHijos() throws Exception {
        System.out.println("\n=== AVL: Eliminar nodo con dos hijos ===");
        AVL<Integer> avl = new AVL<>();

        avl.add(20);
        avl.add(10);
        avl.add(30);
        avl.add(25);
        avl.add(35);
        System.out.println("Árbol inicial - InOrden: " + avl.inOrder());

        avl.remove(30);
        System.out.println("Tras eliminar 30 - InOrden (esperado: 10 20 25 35): " + avl.inOrder());
        System.out.println("¿Balanceado? (esperado: true): " + avl.isBalanced());
    }

    @Test
    void testAlturaGarantizadaLogN() throws Exception {
        System.out.println("\n=== AVL: Altura garantizada O(log n) con 15 inserciones en orden ===");
        AVL<Integer> avl = new AVL<>();

        System.out.println("Insertando 1 al 15 en orden ascendente (peor caso para BST simple)...");
        for (int i = 1; i <= 15; i++) avl.add(i);

        System.out.println("Cantidad de nodos: 15");
        System.out.println("Altura obtenida (esperado ≤ 4): " + avl.height());
        System.out.println("¿Balanceado? (esperado: true): " + avl.isBalanced());
        System.out.println("InOrden: " + avl.inOrder());
    }

    @Test
    void testArbolVacioLanzaExcepcion() {
        System.out.println("\n=== AVL: Árbol vacío lanza excepción ===");
        AVL<Integer> avl = new AVL<>();

        try {
            System.out.println("Intentando isBalanced() en árbol vacío...");
            avl.isBalanced();
        } catch (TreeException e) {
            System.out.println("Excepción capturada correctamente: " + e.getMessage());
        }
    }

    @Test
    void testNodeHeightYFactorEquilibrio() throws Exception {
        System.out.println("\n=== AVL: Altura y factor de equilibrio por nodo ===");
        AVL<Integer> avl = new AVL<>();

        avl.add(40);
        avl.add(20);
        avl.add(60);
        avl.add(10);
        avl.add(30);

        System.out.println("Información de alturas y FE por nodo:\n" + avl.nodeHeight());
    }
}