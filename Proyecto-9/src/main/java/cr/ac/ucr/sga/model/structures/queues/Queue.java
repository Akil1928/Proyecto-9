package cr.ac.ucr.sga.model.structures.queues;

public interface Queue<T> {
    public int size();
    public void clear();
    public boolean isEmpty();
    public int indexOf(T element) throws QueueException;
    public void enQueue(T element) throws QueueException;
    public T deQueue() throws QueueException;
    public void enQueue(T element, Integer priority) throws QueueException;
    public boolean contains(T element) throws QueueException;
    public T peek() throws QueueException;
    public T front() throws QueueException;
}