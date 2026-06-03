package cr.ac.ucr.sga.model.structures.queues;

public class ArrayQueue<T> implements Queue<T> {

    private int n;
    private T[] data;
    private int front, rear;

    public ArrayQueue(int capacity) {
        this.n = capacity;
        data = (T[]) new Object[capacity];
        rear = n - 1;
        front = rear;
    }

    @Override
    public int size() {
        return rear - front;
    }

    @Override
    public void clear() {
        data = (T[]) new Object[n];
        rear = n - 1;
        front = rear;
    }

    @Override
    public boolean isEmpty() {
        return front == rear;
    }

    @Override
    public int indexOf(T element) throws QueueException {
        if (isEmpty()) throw new QueueException("Array Queue is empty");
        HeaderLinkedQueue<T> aux = new HeaderLinkedQueue<>();
        int index = 1, pos = -1;
        while (!isEmpty()) {
            if (equals(front(), element)) pos = index;
            aux.enQueue(deQueue());
            index++;
        }
        while (!aux.isEmpty()) enQueue(aux.deQueue());
        return pos;
    }

    @Override
    public void enQueue(T element) throws QueueException {
        if (size() == data.length)
            throw new QueueException("Array Queue is full");

        //corregido: i < rear (en lugar de <=) para que data[i + 1] no se salga del arreglo
        for (int i = front; i < rear; i++) {
            data[i] = data[i + 1];
        }
        data[rear] = element;
        front--;
    }

    @Override
    public T deQueue() throws QueueException {
        if (isEmpty()) throw new QueueException("Array Queue is empty");
        return data[++front];
    }

    @Override
    public void enQueue(T element, Integer priority) throws QueueException {}

    @Override
    public boolean contains(T element) throws QueueException {
        if (isEmpty()) throw new QueueException("Array Queue is empty");
        return false;
    }

    @Override
    public T peek() throws QueueException {
        if (isEmpty()) throw new QueueException("Array Queue is empty");
        return data[front + 1];
    }

    @Override
    public T front() throws QueueException {
        if (isEmpty()) throw new QueueException("Array Queue is empty");
        return data[front + 1];
    }

    @Override
    public String toString() {
        if (isEmpty()) return "Array Queue is empty";
        StringBuilder sb = new StringBuilder(" FRONT → ");
        try {
            ArrayQueue<T> auxQueue = new ArrayQueue<>(n);
            while (!isEmpty()) {
                sb.append("[").append(peek()).append("]");
                auxQueue.enQueue(deQueue());
                if (!isEmpty()) sb.append(", ");
            }
            while (!auxQueue.isEmpty()) enQueue(auxQueue.deQueue());
        } catch (QueueException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    private boolean equals(T a, T b) {
        return a == null ? b == null : a.equals(b);
    }
}