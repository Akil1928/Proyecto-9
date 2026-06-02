package cr.ac.ucr.sga.model.structures.queues;

import cr.ac.ucr.sga.model.structures.lists.Node;

public class LinkedQueue<T> implements Queue<T> {
    private Node<T> head;
    private Node<T> tail;
    private int size;

    @Override
    public void enqueue(T item) {
        Node<T> node = new Node<>(item);
        if (isEmpty()) {
            head = tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }

    @Override
    public T dequeue() throws QueueException {
        if (isEmpty()) {
            throw new QueueException("Queue is empty");
        }
        T value = head.data;
        head = head.next;
        size--;
        if (head == null) {
            tail = null;
        }
        return value;
    }

    @Override
    public T peek() throws QueueException {
        if (isEmpty()) {
            throw new QueueException("Queue is empty");
        }
        return head.data;
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        head = tail = null;
        size = 0;
    }

    @SuppressWarnings("unchecked")
    public T[] toArray(T[] array) {
        Node<T> aux = head;
        int index = 0;
        while (aux != null && index < array.length) {
            array[index++] = aux.data;
            aux = aux.next;
        }
        return array;
    }
}


