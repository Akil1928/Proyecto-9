package cr.ac.ucr.sga.model.structures.lists;

/**
 * Nodo doble con referencias al siguiente y al anterior.
 * Reciclado y adaptado del PG-04 (model.Node ya tenía campo prev).
 */
public class DoubleNode<T> {

    private T data;
    private DoubleNode<T> next;
    private DoubleNode<T> previous;

    public DoubleNode(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public DoubleNode<T> getNext() {
        return next;
    }

    public DoubleNode<T> getPrevious() {
        return previous;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setNext(DoubleNode<T> next) {
        this.next = next;
    }

    public void setPrevious(DoubleNode<T> previous) {
        this.previous = previous;
    }
}
