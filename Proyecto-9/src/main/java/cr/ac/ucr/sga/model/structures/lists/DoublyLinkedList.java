package cr.ac.ucr.sga.model.structures.lists;

public class DoublyLinkedList<T> implements List<T> {

    private Node<T> head;//inicio de la lista
    private Node<T> tail;//final de la lista


    @Override
    public int size() throws ListException {
        if (isEmpty()) {
            return 0;
        }

        Node<T> aux = head;
        int count = 0;
        while (aux != null) {
            count++;
            aux = aux.next;
        }
        return count;
    }


    @Override
    public void clear() {
        head = tail = null;

    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public void add(T element) {
        Node<T> node = new Node<>(element);
        if(head == null){
            head = node;
            tail = node;
        }else{
            //significa que head apunta a un nodo existente
            Node<T> aux = head;
            //me muevo por la lista hasta alcanzar el ultimo elemento
            while(aux.next != null){
                //aux.next es la flecha
                aux = aux.next;//lo mueve al siguiente nodo

            }
            //cuando se sale del while, aux.next es igual a null
            aux.next = node;

            //Hacemos el doble enlace
            node.prev = aux;

            tail = node;//lo ponemos a apuntar al ultimo nodo de la lista

        }

    }

    /**
     * Elimina todos los elementos que cumplan el predicado. Retorna true si se removió al menos uno.
     */
    public boolean removeIf(java.util.function.Predicate<T> predicate) {
        if (isEmpty()) return false;

        boolean removed = false;
        Node<T> aux = head;

        while (aux != null) {
            if (predicate.test(aux.data)) {
                removed = true;
                // si es el head
                if (aux == head) {
                    head = aux.next;
                    if (head != null) head.prev = null;
                    aux = head;
                    continue;
                }

                // si es el tail
                if (aux == tail) {
                    tail = aux.prev;
                    if (tail != null) tail.next = null;
                    break; // ya no hay más después del tail
                }

                // caso general en el medio
                Node<T> toRemove = aux;
                aux = aux.next;
                toRemove.prev.next = toRemove.next;
                toRemove.next.prev = toRemove.prev;
                continue;
            }
            aux = aux.next;
        }

        return removed;
    }

    @Override
    public void addFirst(T element) {
        Node<T> node = new Node<>(element);

        if (isEmpty()) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
    }

    @Override
    public void addLast(T element) {
        add(element);

    }

    /**
     * Copia los elementos de la lista en el arreglo proporcionado (hasta su capacidad) y lo retorna.
     */
    public T[] toArray(T[] array) {
        Node<T> aux = head;
        int index = 0;
        while (aux != null && index < array.length) {
            array[index++] = aux.data;
            aux = aux.next;
        }
        return array;
    }

    @Override
    public void addInSortedList(T element) {//agregar en forma ordenada, BUSCAR

    }

    @Override
    public void remove(T element) throws ListException {
        if(isEmpty()){
            throw new ListException("Linked List is empty");

        }
        //Caso 1. Cuando el elemento a suprimir es el primero en la lista
        if(equals(head.data, element)){
            head = head.next;
            if(head != null){
                head.prev = null;
            } else {
                tail = null;
            }
        }
        //Caso general. El elemento a suprimir puede estar en el medio o al final
        else{
            Node<T> aux = head.next;
            while(aux != null){
                if(equals(aux.data, element)){
                    //Ya encontré el elemento a eliminar
                    aux.prev.next = aux.next;
                    if(aux.next != null){
                        aux.next.prev = aux.prev;
                    } else {
                        tail = aux.prev;
                    }
                    return;
                }
                aux = aux.next;
            }
        }

    }
//prueba

    @Override
    public T removeFirst() throws ListException {
        if (isEmpty()) {
            throw new ListException("Linked List is empty");
        }

        T first = head.data;
        head = head.next;

        if (head != null) {
            head.prev = null;
        } else {
            tail = null;
        }

        return first;
    }

    @Override
    public T removeLast() throws ListException {
        if (isEmpty()) {
            throw new ListException("Linked List is empty");
        }

        T last = tail.data;

        if (head == tail) {
            clear();
        } else {
            tail = tail.prev;
            tail.next = null;
        }

        return last;
    }

    @Override
    public boolean contains(T element) throws ListException {

        if (isEmpty()) {
            throw new ListException("Linked List is empty");
        }

        Node<T> aux = head;

        while (aux != null) {
            if (equals(aux.data, element)) {
                return true;
            }

            aux = aux.next;
        }

        return false;
    }

    @Override
    public void sort() throws ListException {

    }

    @Override
    public int indexOf(T element) throws ListException {
        if(isEmpty()){
            throw new ListException("Linked List is empty");
        }
        Node<T> aux = head;
        int index = 1;//el indice de la lista enlazada inicia en 1.
        while(aux!= null){
            if(equals(aux.data, element)) return index;
            index++;
            aux = aux.next;
        }
        return -1; //esto indica que no encontro el elemento
    }

    @Override
    public T getFirst() throws ListException {
        if (isEmpty()){
            throw new ListException("Linked list is empty");
        }
        return head.data;
    }

    @Override
    public T getLast() throws ListException {
        if (isEmpty()){
            throw new ListException("Linked list is empty");
        }
        return tail.data;
    }

    @Override
    public T getPrev(T element) throws ListException {
        if (isEmpty()) {
            throw new ListException("Linked list is empty");
        }

        Node<T> aux = head;

        while (aux != null) {
            if (equals(aux.data, element)) {
                return aux.prev != null ? aux.prev.data : null;
            }

            aux = aux.next;
        }

        return null;
    }

    @Override
    public T getNext(T element) throws ListException {
        if (isEmpty()) {
            throw new ListException("Linked list is empty");
        }

        Node<T> aux = head;

        while (aux != null) {
            if (equals(aux.data, element)) {
                return aux.next != null ? aux.next.data : null;
            }

            aux = aux.next;
        }

        return null;
    }

    @Override
    public T get(int index) throws ListException {
        if (isEmpty()) {
            throw new ListException("Linked list is empty");
        }

        Node<T> aux = head;
        int count = 1;

        while (aux != null) {
            if (count == index) {
                return aux.data;
            }

            count++;
            aux = aux.next;
        }

        return null;
    }


    public Node<T> getTail() {
        return tail;
    }

    public Node<T> getHead() {
        return head;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("HEAD → ");
        Node<T> aux = head;

        while (aux != null) {
            sb.append("[").append(aux.data).append("]");
            if (aux.next != null) {
                sb.append(" → ");
            }
            aux = aux.next;
        }

        sb.append(" → NULL");
        return sb.toString();
    }

    ///======================AYUDAS========================//
    /**
     * Obtiene el nodo en la posición especificada (basado en 1).
     * @param index La posición del nodo a obtener (1-based).
     * @return El nodo en la posición especificada o null si no se encuentra.
     * @throws ListException Si la lista está vacía.
     */
    public Node<T> getNodeByIndex(int index) throws ListException{
        if(isEmpty()){
            throw new ListException("Linked list is empty");
        }
        Node<T> aux = head;
        int pos = 1; //La posicion del primer nodo
        while(aux != null){
            if (pos==index )return aux;
            aux= aux.next;
            pos++;
        }
        return null;
    }

    /**
     * Compara dos elementos para verificar si son iguales, manejando correctamente los valores nulos.
     * @param a Primer elemento a comparar.
     * @param b Segundo elemento a comparar.
     * @return true si los elementos son iguales o ambos son nulos, false en caso contrario.
     */
    private boolean equals (T a, T b){
        return  a == null ? b==null : a.equals(b);
    }
}