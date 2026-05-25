package cr.ac.ucr.sga.model.structures.lists;

public class SimpleLinkedList<T> {

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public void addFirst(T data) {
        Node<T> node = new Node<>(data);

        if (isEmpty()) {
            head = node;
            tail = node;
        } else {
            node.next = head;
            head = node;
        }

        size++;
    }

    public void addLast(T data) {
        Node<T> node = new Node<>(data);

        if (isEmpty()) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            tail = node;
        }

        size++;
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }

        T data = head.data;
        head = head.next;
        size--;

        if (size == 0) {
            tail = null;
        }

        return data;
    }

    public boolean remove(T data) {
        if (isEmpty()) {
            return false;
        }

        // comparar de forma segura cuando data puede ser null
        if (equals(head.data, data)) {
            removeFirst();
            return true;
        }

        Node<T> current = head;

        while (current != null && current.next != null) {
            if (equals(current.next.data, data)) {
                if (current.next == tail) {
                    tail = current;
                }

                current.next = current.next.next;
                size--;
                return true;
            }

            current = current.next;
        }

        return false;
    }

    public T get(int index) {
        validateIndex(index);
        Node<T> current = head;

        for (int i = 0; i < index; i++) {
            current = current.next;
        }

        return current.data;
    }

    

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private void validateIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Índice fuera de rango.");
        }
    }

    private boolean equals(T a, T b) {
        return a == null ? b == null : a.equals(b);
    }

    /**
     * Elimina los elementos que cumplan el predicado. Retorna true si se removió al menos uno.
     */
    public boolean removeIf(java.util.function.Predicate<T> predicate) {
        if (isEmpty()) return false;

        boolean removed = false;

        // Eliminar nodos al inicio que cumplan el predicado
        while (!isEmpty() && predicate.test(head.data)) {
            removeFirst();
            removed = true;
        }

        if (isEmpty()) return removed;

        Node<T> current = head;
        while (current != null && current.next != null) {
            if (predicate.test(current.next.data)) {
                if (current.next == tail) {
                    tail = current;
                }
                current.next = current.next.next;
                size--;
                removed = true;
                continue;
            }
            current = current.next;
        }

        return removed;
    }

    public T[] toArray(T[] array) {
        Node<T> current = head;
        int index = 0;

        while (current != null && index < array.length) {
            array[index++] = current.data;
            current = current.next;
        }

        return array;
    }
}
