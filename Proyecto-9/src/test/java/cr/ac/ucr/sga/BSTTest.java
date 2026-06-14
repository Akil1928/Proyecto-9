package cr.ac.ucr.sga;

import cr.ac.ucr.sga.model.trees.BST;
import cr.ac.ucr.sga.model.trees.TreeException;
import org.junit.jupiter.api.Test;

public class BSTTest {

    //─── Inserción y búsqueda ───────────────────────────────────────────

    @Test
    void testInsertarYBuscar() throws Exception {
        System.out.println("=== BST: Insertar y buscar ===");
        BST<Integer> bst = new BST<>();

        System.out.println("Insertando: 50, 30, 70, 20, 40");
        bst.add(50);
        bst.add(30);
        bst.add(70);
        bst.add(20);
        bst.add(40);

        System.out.println("¿Contiene 30? (esperado: true): " + bst.contains(30));
        System.out.println("¿Contiene 99? (esperado: false): " + bst.contains(99));
    }

    @Test
    void testRecorridoInOrden() throws Exception {
        System.out.println("\n=== BST: Recorrido InOrden (L-N-R) ===");
        BST<Integer> bst = new BST<>();

        bst.add(50);
        bst.add(30);
        bst.add(70);
        bst.add(20);
        bst.add(40);

        System.out.println("InOrden (esperado orden ascendente: 20 30 40 50 70): " + bst.inOrder());
    }

    @Test
    void testRecorridoPreOrden() throws Exception {
        System.out.println("\n=== BST: Recorrido PreOrden (N-L-R) ===");
        BST<Integer> bst = new BST<>();

        bst.add(50);
        bst.add(30);
        bst.add(70);
        bst.add(20);
        bst.add(40);

        System.out.println("PreOrden (esperado raíz primero: 50 30 20 40 70): " + bst.preOrder());
    }

    @Test
    void testRecorridoPostOrden() throws Exception {
        System.out.println("\n=== BST: Recorrido PostOrden (L-R-N) ===");
        BST<Integer> bst = new BST<>();

        bst.add(50);
        bst.add(30);
        bst.add(70);
        bst.add(20);
        bst.add(40);

        System.out.println("PostOrden (esperado raíz al final: 20 40 30 70 50): " + bst.postOrder());
    }

    @Test
    void testMinYMax() throws Exception {
        System.out.println("\n=== BST: Mínimo y Máximo ===");
        BST<Integer> bst = new BST<>();

        bst.add(50);
        bst.add(30);
        bst.add(70);
        bst.add(20);
        bst.add(40);

        System.out.println("Mínimo (esperado: 20): " + bst.min());
        System.out.println("Máximo (esperado: 70): " + bst.max());
    }

    @Test
    void testEliminarNodoHoja() throws Exception {
        System.out.println("\n=== BST: Eliminar nodo hoja ===");
        BST<Integer> bst = new BST<>();

        bst.add(50);
        bst.add(30);
        bst.add(70);
        System.out.println("Árbol inicial - InOrden: " + bst.inOrder());

        bst.remove(30);
        System.out.println("Tras eliminar 30 - InOrden (esperado: 50 70): " + bst.inOrder());
        System.out.println("¿Contiene 30? (esperado: false): " + bst.contains(30));
    }

    @Test
    void testEliminarNodoConDosHijos() throws Exception {
        System.out.println("\n=== BST: Eliminar nodo con dos hijos ===");
        BST<Integer> bst = new BST<>();

        bst.add(50);
        bst.add(30);
        bst.add(70);
        bst.add(20);
        bst.add(40);
        System.out.println("Árbol inicial - InOrden: " + bst.inOrder());

        bst.remove(30);
        System.out.println("Tras eliminar 30 - InOrden (esperado: 20 40 50 70): " + bst.inOrder());
    }

    @Test
    void testComparacionBSTvsAVL() throws Exception {
        System.out.println("\n=== Comparación BST vs AVL: inserción en orden ascendente ===");

        BST<Integer> bst = new BST<>();
        cr.ac.ucr.sga.model.trees.AVL<Integer> avl = new cr.ac.ucr.sga.model.trees.AVL<>();

        System.out.println("Insertando 1..10 en orden ascendente en ambos árboles...");
        for (int i = 1; i <= 10; i++) {
            bst.add(i);
            avl.add(i);
        }

        System.out.println("BST - Altura resultante (esperado: 10, árbol degenerado): " + bst.height());
        System.out.println("AVL - Altura resultante (esperado: ≤ 4, balanceado): " + avl.height());
        System.out.println("AVL - ¿Balanceado? (esperado: true): " + avl.isBalanced());
        System.out.println("Conclusión: AVL garantiza O(log n) donde BST degenera a O(n).");
    }

    @Test
    void testArbolVacioLanzaExcepcion() {
        System.out.println("\n=== BST: Árbol vacío lanza excepción ===");
        BST<Integer> bst = new BST<>();

        try {
            System.out.println("Intentando contains() en árbol vacío...");
            bst.contains(5);
        } catch (TreeException e) {
            System.out.println("Excepción capturada correctamente: " + e.getMessage());
        }
    }

    @Test
    void testAlturaPorNodo() throws Exception {
        System.out.println("\n=== BST: Altura por nodo ===");
        BST<Integer> bst = new BST<>();

        bst.add(50);
        bst.add(30);
        bst.add(70);
        bst.add(20);
        bst.add(40);

        System.out.println("Altura total del árbol: " + bst.height());
        System.out.println("Alturas por nodo:\n" + bst.nodeHeight());
    }
}