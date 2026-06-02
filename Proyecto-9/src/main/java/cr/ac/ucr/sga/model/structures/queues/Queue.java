package cr.ac.ucr.sga.model.structures.queues;

public interface Queue<T> {
    void enqueue(T item) throws QueueException;
    T dequeue() throws QueueException;
    T peek() throws QueueException;
    boolean isEmpty();
    int size();
    void clear();
}

