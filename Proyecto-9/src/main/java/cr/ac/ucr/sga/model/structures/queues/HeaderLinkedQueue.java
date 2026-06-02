package cr.ac.ucr.sga.model.structures.queues;

import cr.ac.ucr.sga.model.structures.lists.Node;

public class HeaderLinkedQueue<T> implements Queue<T> {

    private Node<T> front;
    private Node<T> rear;
    private int size;

    public HeaderLinkedQueue() {
        front = rear = new Node<>();
        size = 0;
    }

    @Override
    public int size() { return size; }

    @Override
    public void clear() {
        front = rear = new Node<>();
        size = 0;
    }

    @Override
    public boolean isEmpty() { return front == rear; }

    @Override
    public int indexOf(T element) throws QueueException {
        if (isEmpty()) throw new QueueException("Header Linked Queue is empty");
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
        Node<T> node = new Node<>(element);
        rear.next = node;
        rear = node;
        size++;
    }

    @Override
    public T deQueue() throws QueueException {
        if (isEmpty()) throw new QueueException("Linked Queue is empty");
        T element = front.next.data;
        if (front.next == rear) clear();
        else { front.next = front.next.next; size--; }
        return element;
    }

    @Override
    public void enQueue(T element, Integer priority) throws QueueException {
        enQueue(element);
    }

    @Override
    public boolean contains(T element) throws QueueException {
        if (isEmpty()) throw new QueueException("Linked Queue is empty");
        HeaderLinkedQueue<T> aux = new HeaderLinkedQueue<>();
        boolean found = false;
        while (!isEmpty()) {
            if (equals(front(), element)) found = true;
            aux.enQueue(deQueue());
        }
        while (!aux.isEmpty()) enQueue(aux.deQueue());
        return found;
    }

    @Override
    public T peek() throws QueueException {
        if (isEmpty()) throw new QueueException("Linked Queue is empty");
        return front.next.data;
    }

    @Override
    public T front() throws QueueException {
        if (isEmpty()) throw new QueueException("Linked Queue is empty");
        return front.next.data;
    }

    @Override
    public String toString() {
        if (isEmpty()) return "Linked Queue is empty";
        StringBuilder sb = new StringBuilder(" FRONT → [] → ");
        try {
            HeaderLinkedQueue<T> auxQueue = new HeaderLinkedQueue<>();
            while (!isEmpty()) {
                sb.append("[").append(peek()).append("]");
                auxQueue.enQueue(deQueue());
                if (!isEmpty()) sb.append(", ");
            }
            while (!auxQueue.isEmpty()) enQueue(auxQueue.deQueue());
        } catch (QueueException e) {
            throw new RuntimeException(e);
        }
        sb.append(" ← REAR");
        return sb.toString();
    }

    private boolean equals(T a, T b) {
        return a == null ? b == null : a.equals(b);
    }
}