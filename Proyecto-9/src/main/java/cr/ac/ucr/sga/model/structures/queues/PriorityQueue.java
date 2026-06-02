package cr.ac.ucr.sga.model.structures.queues;

import java.util.Comparator;

public class PriorityQueue<T> implements Queue<T> {
    private Object[] heap;
    private int size;
    private int capacity;
    private final Comparator<T> comparator;

    public PriorityQueue(Comparator<T> comparator) {
        this(comparator, 10);
    }

    public PriorityQueue(Comparator<T> comparator, int capacity) {
        this.comparator = comparator;
        this.capacity = Math.max(1, capacity);
        this.heap = new Object[this.capacity];
    }

    @Override
    public void enqueue(T item) {
        if (size == capacity) {
            grow();
        }
        heap[size] = item;
        heapifyUp(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T dequeue() throws QueueException {
        if (isEmpty()) {
            throw new QueueException("Queue is empty");
        }
        T root = (T) heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        if (size > 0) {
            heapifyDown(0);
        }
        return root;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T peek() throws QueueException {
        if (isEmpty()) {
            throw new QueueException("Queue is empty");
        }
        return (T) heap[0];
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
        heap = new Object[capacity];
        size = 0;
    }

    @SuppressWarnings("unchecked")
    public T[] toArray(T[] array) {
        PriorityQueue<T> copy = new PriorityQueue<>(comparator, capacity);
        for (int i = 0; i < size; i++) {
            copy.enqueue((T) heap[i]);
        }
        int index = 0;
        while (!copy.isEmpty() && index < array.length) {
            try {
                array[index++] = copy.dequeue();
            } catch (QueueException e) {
                break;
            }
        }
        return array;
    }

    private void grow() {
        capacity *= 2;
        Object[] newHeap = new Object[capacity];
        System.arraycopy(heap, 0, newHeap, 0, size);
        heap = newHeap;
    }

    @SuppressWarnings("unchecked")
    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            T current = (T) heap[index];
            T parentValue = (T) heap[parent];
            if (comparator.compare(current, parentValue) <= 0) {
                break;
            }
            heap[index] = parentValue;
            heap[parent] = current;
            index = parent;
        }
    }

    @SuppressWarnings("unchecked")
    private void heapifyDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int largest = index;

            if (left < size && comparator.compare((T) heap[left], (T) heap[largest]) > 0) {
                largest = left;
            }
            if (right < size && comparator.compare((T) heap[right], (T) heap[largest]) > 0) {
                largest = right;
            }
            if (largest == index) {
                break;
            }
            Object tmp = heap[index];
            heap[index] = heap[largest];
            heap[largest] = tmp;
            index = largest;
        }
    }
}


