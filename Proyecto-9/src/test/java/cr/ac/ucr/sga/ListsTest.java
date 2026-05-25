package cr.ac.ucr.sga;

import cr.ac.ucr.sga.model.structures.lists.CircularDoublyLinkedList;
import cr.ac.ucr.sga.model.structures.lists.CircularLinkedList;
import cr.ac.ucr.sga.model.structures.lists.DoublyLinkedList;
import cr.ac.ucr.sga.model.structures.lists.SimpleLinkedList;
import org.junit.jupiter.api.Test;

public class ListsTest {

//aa
    @Test
    void simpleLinkedListAddAndRemoveTest() {
        SimpleLinkedList<Integer> list = new SimpleLinkedList<>();

        list.addFirst(2);
        list.addLast(3);
        list.addFirst(1);

        System.out.println("=== SimpleLinkedList - addFirst / addLast ===");
        System.out.println("size():  " + list.size());
        System.out.println("get(0):  " + list.get(0));
        System.out.println("get(1):  " + list.get(1));
        System.out.println("get(2):  " + list.get(2));

        System.out.println("\n=== remove(2) ===");
        System.out.println("remove(2): " + list.remove(2));
        System.out.println("size():    " + list.size());
        System.out.println("get(0):    " + list.get(0));
        System.out.println("get(1):    " + list.get(1));

        System.out.println("\n=== removeFirst ===");
        System.out.println("removeFirst(): " + list.removeFirst());
        System.out.println("isEmpty():     " + list.isEmpty());
    }

    // ============================================================
    // DoubleLinkedList
    // ============================================================

    @Test
    void doubleLinkedListAddAndRemoveTest() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();

        list.addLast("A");
        list.addLast("B");
        list.addFirst("C");

        System.out.println("=== DoubleLinkedList - addFirst / addLast ===");
        System.out.println("size():  " + list.size());
        System.out.println("get(0):  " + list.get(0));
        System.out.println("get(1):  " + list.get(1));
        System.out.println("get(2):  " + list.get(2));

        System.out.println("\n=== removeFirst / removeLast ===");
        System.out.println("removeFirst(): " + list.removeFirst());
        System.out.println("removeLast():  " + list.removeLast());
        System.out.println("size():        " + list.size());
        System.out.println("get(0):        " + list.get(0));
    }

    @Test
    void doubleLinkedListRemoveIfTest() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();

        list.addLast("IF1000");
        list.addLast("IF2000");
        list.addLast("IF3001");

        System.out.println("=== DoubleLinkedList - removeIf ===");
        System.out.println("Antes:          size=" + list.size() + "  [" + list.get(0) + ", " + list.get(1) + ", " + list.get(2) + "]");
        System.out.println("removeIf(IF2000): " + list.removeIf(value -> value.equals("IF2000")));
        System.out.println("Después:        size=" + list.size() + "  [" + list.get(0) + ", " + list.get(1) + "]");
    }

    // ============================================================
    // CircularLinkedList
    // ============================================================

    @Test
    void circularLinkedListNavigationTest() {
        CircularLinkedList<String> list = new CircularLinkedList<>();

        System.out.println("=== CircularLinkedList vacía ===");
        System.out.println("next() en lista vacía: " + list.next());

        list.add("Vista 1");
        list.add("Vista 2");
        list.add("Vista 3");

        System.out.println("\n=== CircularLinkedList - navegación circular ===");
        System.out.println("size():  " + list.size());
        System.out.println("next():  " + list.next() + "  ← Vista 1");
        System.out.println("next():  " + list.next() + "  ← Vista 2");
        System.out.println("next():  " + list.next() + "  ← Vista 3");
        System.out.println("next():  " + list.next() + "  ← vuelve a Vista 1 (circular)");
        System.out.println("next():  " + list.next() + "  ← Vista 2");
    }

    // ============================================================
    // CircularDoubleLinkedList
    // ============================================================

    @Test
    void circularDoubleLinkedListNavigationTest() {
        CircularDoublyLinkedList<String> list = new CircularDoublyLinkedList<>();

        System.out.println("=== CircularDoubleLinkedList vacía ===");
        System.out.println("current() en lista vacía: " + list.current());

        list.add("Menú");
        list.add("Expediente");
        list.add("Reporte");

        System.out.println("\n=== CircularDoubleLinkedList - navegación adelante ===");
        System.out.println("size():     " + list.size());
        System.out.println("current():  " + list.current() + "  ← Menú");
        System.out.println("next():     " + list.next()    + "  ← Expediente");
        System.out.println("next():     " + list.next()    + "  ← Reporte");
        System.out.println("next():     " + list.next()    + "  ← vuelve a Menú (circular)");

        System.out.println("\n=== CircularDoubleLinkedList - navegación atrás ===");
        System.out.println("previous(): " + list.previous() + "  ← Reporte");
        System.out.println("previous(): " + list.previous() + "  ← Expediente");
        System.out.println("previous(): " + list.previous() + "  ← vuelve a Menú (circular)");
    }
}
