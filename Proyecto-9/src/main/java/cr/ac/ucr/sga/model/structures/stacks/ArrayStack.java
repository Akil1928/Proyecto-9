package cr.ac.ucr.sga.model.structures.stacks;

public class ArrayStack<T> implements Stack<T> {

    private int n;
    private int top;
    private T[] data;

    public ArrayStack(int n) {
        if (n <= 0) System.exit(1);
        this.n = n;
        this.top = -1;
        data = (T[]) new Object[n];
    }

    @Override
    public int size() {
        return top + 1;
    }

    @Override
    public void clear() {
        this.top = -1;
        data = (T[]) new Object[n];
    }

    @Override
    public boolean isEmpty() {
        return top == -1;
    }

    @Override
    public T peek() throws StackException {
        if (isEmpty()) throw new StackException("Array Stack is empty");
        return this.data[top];
    }

    @Override
    public T top() throws StackException {
        if (isEmpty()) throw new StackException("Array Stack is empty");
        return this.data[top];
    }

    @Override
    public void push(T element) throws StackException {
        if (top == n - 1) {
            throw new StackException("Array Stack is full");
        }
        data[++top] = element;
    }

    @Override
    public T pop() throws StackException {
        if (isEmpty()) throw new StackException("Array Stack is empty");
        return this.data[top--];
    }

    @Override
    public String toString() {
        if (isEmpty()) return "ArrayStack is empty";
        StringBuilder sb = new StringBuilder("TOP → ");
        try {
            ArrayStack<T> auxStack = new ArrayStack<>(n);
            while (!isEmpty()) {
                sb.append("[").append(peek()).append("]");
                auxStack.push(pop());
                if (!isEmpty()) sb.append(", ");
            }
            while (!auxStack.isEmpty())
                push(auxStack.pop());
        } catch (StackException e) {}
        sb.append(" →");
        return sb.toString();
    }
}