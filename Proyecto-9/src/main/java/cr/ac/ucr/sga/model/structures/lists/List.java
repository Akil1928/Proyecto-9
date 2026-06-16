package cr.ac.ucr.sga.model.structures.lists;
public interface List<T> {
    public int size() throws ListException; // Devuelve el número de elementos en la lista
    public void clear(); //Remueve todos los elementos de la lista
    public boolean isEmpty(); // true si la lista está vacía
    public void add (T element); // inserta un elemento al final de la lista
    public void addFirst(T element); //inserta un elemento al incio de la lista
    public void addLast(T element); //inserta un elemento al final de la lista
    public void addInSortedList(T element); // inserta un elemento a la lista en forma ordenada
    public void remove(T element) throws ListException; //Suprime un elemento de la lista
    public T removeFirst() throws ListException; //suprime y retorna el primer elemento de la lista
    public T removeLast() throws ListException; //suprime y retorna el último elemento de la lista
    public boolean contains(T element) throws ListException; //true si el elemento existe en la lista
    public void sort() throws ListException; //ordena la lista
    public int indexOf(T element) throws ListException; //devuelve la posición del elemento en la lista
    public T getFirst() throws ListException; //Devuelve el primer elemento de la lista
    public T getLast() throws ListException; //Devuelve el último elemento de la lista
    public T getPrev(T element) throws ListException; //Devuelve el elemento anterior al actual en la lista
    public T getNext(T element) throws ListException; //Devuelve el elemento posterior al actual en la lista
    public T get(int index) throws ListException; //Devuelve el elemento en la posición indicada

    Node<T> getNodeByIndex(int index) throws ListException;
    
    /**
     * Elimina los elementos que cumplan el predicado. Retorna true si se removió al menos uno.
     */
    boolean removeIf(java.util.function.Predicate<T> predicate);

    /**
     * Copia los elementos de la lista en el arreglo proporcionado (hasta su capacidad) y lo retorna.
     */
    T[] toArray(T[] array);

}
