package cr.ac.ucr.sga.model.structures.stacks;

import cr.ac.ucr.sga.model.structures.lists.Node;

public class LinkedStack<T> implements Stack<T> {
    private Node<T> top;
    private int size;

    @Override
    public void push(T item) {
        Node<T> node = new Node<>(item);
        node.next = top;
        top = node;
        size++;
    }

    @Override
    public T pop() throws StackException {
        if (isEmpty()) {
            throw new StackException("Stack is empty");
        }
        T value = top.data;
        top = top.next;
        size--;
        return value;
    }

    @Override
    public T peek() throws StackException {
        if (isEmpty()) {
            throw new StackException("Stack is empty");
        }
        return top.data;
    }

    @Override
    public boolean isEmpty() {
        return top == null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        top = null;
        size = 0;
    }

    @SuppressWarnings("unchecked")
    public T[] toArray(T[] array) {
        Node<T> aux = top;
        int index = 0;
        while (aux != null && index < array.length) {
            array[index++] = aux.data;
            aux = aux.next;
        }
        return array;
    }
}


