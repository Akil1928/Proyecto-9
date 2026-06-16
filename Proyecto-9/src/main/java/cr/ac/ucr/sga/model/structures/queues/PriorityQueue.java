package cr.ac.ucr.sga.model.structures.queues;

import cr.ac.ucr.sga.model.structures.lists.Node;

public class PriorityQueue<T> implements Queue<T> {

    private Node<T> front;
    private Node<T> rear;
    private int size;

    public PriorityQueue() {
        front = rear = null;
        size = 0;
    }

    public Node<T> getFront() { return front; }
    public Node<T> getRear()  { return rear;  }

    @Override
    public int size() { return size; }

    @Override
    public void clear() { front = rear = null; size = 0; }

    @Override
    public boolean isEmpty() { return front == null; }

    @Override
    public int indexOf(T element) throws QueueException {
        if (isEmpty()) throw new QueueException("Priority Queue is empty");
        Node<T> aux = front;
        int index = 1;
        while (aux != null) {
            if (equals(aux.data, element)) return index;
            index++;
            aux = aux.next;
        }
        return -1;
    }

    @Override
    public void enQueue(T element) throws QueueException {
        Node<T> node = new Node<>(element);
        if (isEmpty()) front = rear = node;
        else { rear.next = node; rear = node; }
        size++;
    }

    @Override
    public T deQueue() throws QueueException {
        if (isEmpty()) throw new QueueException("Priority Queue is empty");
        T element = front.data;
        if (front == rear) {
            clear(); // clear() ya hace size = 0
        } else {
            front = front.next;
            size--;
        }
        return element;
    }

    @Override
    public void enQueue(T element, Integer priority) throws QueueException {
        Node<T> node = new Node<>(element, priority);
        if (isEmpty()) {
            front = rear = node;
        } else {
            Node<T> aux = front;
            Node<T> prev = null;
            //recorre mientras la prioridad del nodo actual sea MENOR O IGUAL (numericamente)
            //prioridad 1=ALTA, 2=MEDIA, 3=BAJA → los de menor número van primero
            while (aux != null && aux.priority <= priority) {
                prev = aux;
                aux = aux.next;
            }
            if (prev == null) {
                //el nuevo nodo tiene mayor prioridad que todos (va al frente)
                node.next = front;
                front = node;
            } else if (aux == null) {
                //el nuevo nodo va al final
                rear.next = node;
                rear = node;
            } else {
                //insertar en medio
                prev.next = node;
                node.next = aux;
            }
        }
        size++;
    }

    @Override
    public boolean contains(T element) throws QueueException {
        if (isEmpty()) throw new QueueException("Priority Queue is empty");
        Node<T> aux = front;
        while (aux != null) {
            if (equals(aux.data, element)) return true;
            aux = aux.next;
        }
        return false;
    }

    @Override
    public T peek() throws QueueException {
        if (isEmpty()) throw new QueueException("Priority Queue is empty");
        return front.data;
    }

    @Override
    public T front() throws QueueException {
        if (isEmpty()) throw new QueueException("Priority Queue is empty");
        return front.data;
    }

    @Override
    public String toString() {
        if (isEmpty()) return "Priority Queue is empty";
        StringBuilder sb = new StringBuilder("FRONT → ");
        Node<T> aux = front;
        while (aux != null) {
            sb.append("[").append(aux.data).append("(p").append(aux.priority).append(")]");
            if (aux.next != null) sb.append(" → ");
            aux = aux.next;
        }
        sb.append(" → REAR");
        return sb.toString();
    }

    private boolean equals(T a, T b) {
        return a == null ? b == null : a.equals(b);
    }
}