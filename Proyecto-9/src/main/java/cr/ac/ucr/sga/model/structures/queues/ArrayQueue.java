package cr.ac.ucr.sga.model.structures.queues;

import java.util.Arrays;

public class ArrayQueue<T> implements Queue<T> {
    private static final int DEFAULT_CAPACITY = 10;

    private Object[] data;
    private int front;
    private int rear;
    private int size;
    private int capacity;

    public ArrayQueue() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayQueue(int capacity) {
        this.capacity = Math.max(1, capacity);
        this.data = new Object[this.capacity];
    }

    @Override
    public void enqueue(T item) throws QueueException {
        if (size == capacity) {
            throw new QueueException("Queue is full");
        }
        data[rear] = item;
        rear = (rear + 1) % capacity;
        size++;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T dequeue() throws QueueException {
        if (isEmpty()) {
            throw new QueueException("Queue is empty");
        }
        T value = (T) data[front];
        data[front] = null;
        front = (front + 1) % capacity;
        size--;
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T peek() throws QueueException {
        if (isEmpty()) {
            throw new QueueException("Queue is empty");
        }
        return (T) data[front];
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        data = new Object[capacity];
        front = 0;
        rear = 0;
        size = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Front -> ");
        for (int i = 0; i < size; i++) {
            int index = (front + i) % capacity;
            sb.append('[').append(data[index]).append(']');
            if (i < size - 1) sb.append(" -> ");
        }
        sb.append(" -> Rear");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public T[] toArray(T[] array) {
        for (int i = 0; i < size && i < array.length; i++) {
            array[i] = (T) data[(front + i) % capacity];
        }
        return array;
    }
}


