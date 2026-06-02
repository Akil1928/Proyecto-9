package cr.ac.ucr.sga.model.structures.stacks;

import java.util.Arrays;

public class ArrayStack<T> implements Stack<T> {
    private static final int DEFAULT_CAPACITY = 10;

    private Object[] data;
    private int top;
    private int capacity;

    public ArrayStack() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayStack(int capacity) {
        this.capacity = Math.max(1, capacity);
        this.data = new Object[this.capacity];
        this.top = -1;
    }

    @Override
    public void push(T item) {
        if (top + 1 == capacity) {
            resize();
        }
        data[++top] = item;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T pop() throws StackException {
        if (isEmpty()) {
            throw new StackException("Stack is empty");
        }
        T item = (T) data[top];
        data[top--] = null;
        return item;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T peek() throws StackException {
        if (isEmpty()) {
            throw new StackException("Stack is empty");
        }
        return (T) data[top];
    }

    @Override
    public boolean isEmpty() {
        return top < 0;
    }

    @Override
    public int size() {
        return top + 1;
    }

    @Override
    public void clear() {
        data = new Object[capacity];
        top = -1;
    }

    private void resize() {
        capacity *= 2;
        data = Arrays.copyOf(data, capacity);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Top -> ");
        for (int i = top; i >= 0; i--) {
            sb.append('[').append(data[i]).append(']');
            if (i > 0) sb.append(" -> ");
        }
        sb.append(" -> Bottom");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public T[] toArray(T[] array) {
        int index = 0;
        for (int i = top; i >= 0 && index < array.length; i--) {
            array[index++] = (T) data[i];
        }
        return array;
    }
}


