package cr.ac.ucr.sga.model.structures.stacks;

public interface Stack<T> {
    void push(T item) throws StackException;
    T pop() throws StackException;
    T peek() throws StackException;
    boolean isEmpty();
    int size();
    void clear();
}

