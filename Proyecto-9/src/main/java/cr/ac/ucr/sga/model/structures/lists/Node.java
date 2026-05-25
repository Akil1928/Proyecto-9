package cr.ac.ucr.sga.model.structures.lists;


public class Node<T> {
    public T data;
    public Integer priority; //1=alta, 2=media, 3=baja
    public Node<T> next;
    public Node<T> prev;

    public Node(T data) {
        this.data = data;
        this.next = this.prev = null;
    }
    //Constructor sobrecargado
    public Node(){
        this.next = this.next = null;
    }


    public Node(T data, Integer priority) {
        this.data = data;
        this.priority = priority;
        this.next = null;
    }
}