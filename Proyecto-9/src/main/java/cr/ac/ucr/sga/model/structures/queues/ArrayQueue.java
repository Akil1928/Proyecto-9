package cr.ac.ucr.sga.model.structures.queues;

public class ArrayQueue<T> implements Queue<T> {

    private int n;
    private T[] data;
    private int front;   // índice del primer elemento
    private int size;    // cantidad de elementos actuales

    public ArrayQueue(int capacity) {
        this.n = capacity;
        data = (T[]) new Object[capacity];
        front = 0;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        data = (T[]) new Object[n];
        front = 0;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void enQueue(T element) throws QueueException {
        if (size == n)
            throw new QueueException("Array Queue is full");
        int rear = (front + size) % n;
        data[rear] = element;
        size++;
    }

    @Override
    public T deQueue() throws QueueException {
        if (isEmpty()) throw new QueueException("Array Queue is empty");
        T element = data[front];
        data[front] = null;
        front = (front + 1) % n;
        size--;
        return element;
    }

    @Override
    public T peek() throws QueueException {
        if (isEmpty()) throw new QueueException("Array Queue is empty");
        return data[front];
    }

    @Override
    public T front() throws QueueException {
        return peek();
    }

    @Override
    public int indexOf(T element) throws QueueException {
        if (isEmpty()) throw new QueueException("Array Queue is empty");
        for (int i = 0; i < size; i++) {
            T val = data[(front + i) % n];
            if (equals(val, element)) return i + 1;
        }
        return -1;
    }

    @Override
    public boolean contains(T element) throws QueueException {
        if (isEmpty()) throw new QueueException("Array Queue is empty");
        return indexOf(element) != -1;
    }

    @Override
    public void enQueue(T element, Integer priority) throws QueueException {
        enQueue(element);
    }

    @Override
    public String toString() {
        if (isEmpty()) return "Array Queue is empty";
        StringBuilder sb = new StringBuilder(" FRONT → ");
        for (int i = 0; i < size; i++) {
            sb.append("[").append(data[(front + i) % n]).append("]");
            if (i < size - 1) sb.append(", ");
        }
        return sb.toString();
    }

    private boolean equals(T a, T b) {
        return a == null ? b == null : a.equals(b);
    }
}