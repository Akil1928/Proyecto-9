package cr.ac.ucr.sga.model.trees;

/**
 * Interfaz de árbol binario (clase vista en laboratorio PG-05).
 */
public interface Tree<T> {

    int size() throws TreeException;
    void clear();
    boolean isEmpty();
    boolean contains(T element) throws TreeException;
    void add(T element);
    void remove(T element) throws TreeException;
    int height(T element) throws TreeException;
    int height() throws TreeException;
    T min() throws TreeException;
    T max() throws TreeException;

    // Recorridos (US-13)
    String preOrder() throws TreeException;
    String inOrder() throws TreeException;
    String postOrder() throws TreeException;

    String nodeHeight() throws TreeException;
}