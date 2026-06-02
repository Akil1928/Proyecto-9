package cr.ac.ucr.sga;

import cr.ac.ucr.sga.model.structures.stacks.ArrayStack;
import cr.ac.ucr.sga.model.structures.stacks.LinkedStack;
import cr.ac.ucr.sga.model.structures.stacks.StackException;
import org.junit.jupiter.api.Test;

public class StacksTest {

    // ─── ArrayStack ───────────────

    @Test
    void testArrayStackPushAndPop() throws Exception {
        System.out.println("=== ArrayStack: Push y Pop ===");
        ArrayStack<Integer> stack = new ArrayStack<>(10);

        System.out.println("Insertando: 1, 2, 3");
        stack.push(1);
        stack.push(2);
        stack.push(3);

        System.out.println("Desapilando (esperado: 3): " + stack.pop());
        System.out.println("Desapilando (esperado: 2): " + stack.pop());
        System.out.println("Desapilando (esperado: 1): " + stack.pop());
    }

    @Test
    void testArrayStackPeekNoRemove() throws Exception {
        System.out.println("\n=== ArrayStack: Peek sin remover ===");
        ArrayStack<String> stack = new ArrayStack<>(10);

        stack.push("A");
        System.out.println("Elemento en el tope (esperado: A): " + stack.peek());
        System.out.println("Tamaño de la pila (esperado: 1): " + stack.size());
    }

    @Test
    void testArrayStackEmptyThrowsException() {
        System.out.println("\n=== ArrayStack: Excepción al desapilar vacía ===");
        ArrayStack<Integer> stack = new ArrayStack<>(10);

        try {
            System.out.println("Intentando hacer pop en pila vacía...");
            stack.pop();
        } catch (StackException e) {
            System.out.println("Excepción capturada correctamente: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testArrayStackOneElement() throws Exception {
        System.out.println("\n=== ArrayStack: Un solo elemento ===");
        ArrayStack<String> stack = new ArrayStack<>(10);

        stack.push("X");
        System.out.println("Desapilando (esperado: X): " + stack.pop());
        System.out.println("¿Está vacía? (esperado: true): " + stack.isEmpty());
    }

    @Test
    void testArrayStackFull() throws Exception {
        System.out.println("\n=== ArrayStack: Caso borde - Pila llena ===");
        ArrayStack<Integer> stack = new ArrayStack<>(2);

        System.out.println("Insertando 1 y 2 en capacidad 2...");
        stack.push(1);
        stack.push(2);

        try {
            System.out.println("Intentando insertar un tercer elemento (esperado: StackException)...");
            stack.push(3);
        } catch (StackException e) {
            System.out.println("Excepción de pila llena capturada correctamente: " + e.getMessage());
        }
    }

    @Test
    void testArrayStackClear() throws Exception {
        System.out.println("\n=== ArrayStack: Limpiar pila ===");
        ArrayStack<Integer> stack = new ArrayStack<>(10);

        stack.push(1);
        stack.push(2);
        System.out.println("Tamaño antes de clear: " + stack.size());

        stack.clear();
        System.out.println("¿Está vacía tras clear? (esperado: true): " + stack.isEmpty());
        System.out.println("Tamaño tras clear (esperado: 0): " + stack.size());
    }

    @Test
    void testArrayStackTopSameAsPeek() throws Exception {
        System.out.println("\n=== ArrayStack: Top es igual a Peek ===");
        ArrayStack<String> stack = new ArrayStack<>(10);

        stack.push("Z");
        System.out.println("Resultado de top(): " + stack.top());
        System.out.println("Resultado de peek(): " + stack.peek());
    }

    //─── LinkedStack ───────────────

    @Test
    void testLinkedStackPushAndPop() throws Exception {
        System.out.println("\n=== LinkedStack: Push y Pop ===");
        LinkedStack<Integer> stack = new LinkedStack<>();

        System.out.println("Insertando: 1, 2, 3");
        stack.push(1);
        stack.push(2);
        stack.push(3);

        System.out.println("Desapilando (esperado: 3): " + stack.pop());
        System.out.println("Desapilando (esperado: 2): " + stack.pop());
        System.out.println("Desapilando (esperado: 1): " + stack.pop());
    }

    @Test
    void testLinkedStackPeekNoRemove() throws Exception {
        System.out.println("\n=== LinkedStack: Peek sin remover ===");
        LinkedStack<String> stack = new LinkedStack<>();

        stack.push("A");
        System.out.println("Elemento en el tope (esperado: A): " + stack.peek());
        System.out.println("Tamaño de la pila (esperado: 1): " + stack.size());
    }

    @Test
    void testLinkedStackEmptyThrowsException() {
        System.out.println("\n=== LinkedStack: Excepción al desapilar vacía ===");
        LinkedStack<Integer> stack = new LinkedStack<>();

        try {
            System.out.println("Intentando hacer pop en pila vacía...");
            stack.pop();
        } catch (StackException e) {
            System.out.println("Excepción capturada correctamente: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testLinkedStackOneElement() throws Exception {
        System.out.println("\n=== LinkedStack: Un solo elemento ===");
        LinkedStack<String> stack = new LinkedStack<>();

        stack.push("X");
        System.out.println("Desapilando (esperado: X): " + stack.pop());
        System.out.println("¿Está vacía? (esperado: true): " + stack.isEmpty());
    }

    @Test
    void testLinkedStackManyElements() throws Exception {
        System.out.println("\n=== LinkedStack: Crecimiento ilimitado (100 elementos) ===");
        LinkedStack<Integer> stack = new LinkedStack<>();

        System.out.println("Insertando 100 elementos...");
        for (int i = 0; i < 100; i++) stack.push(i);
        System.out.println("Tamaño final alcanzado (esperado: 100): " + stack.size());

        System.out.println("Desapilando consecutivamente para validar orden LIFO...");
        boolean ordenCorrecto = true;
        for (int i = 99; i >= 0; i--) {
            if (stack.pop() != i) {
                ordenCorrecto = false;
            }
        }
        System.out.println("¿El orden de salida fue el correcto? " + ordenCorrecto);
        System.out.println("¿Está vacía tras vaciarla? (esperado: true): " + stack.isEmpty());
    }

    @Test
    void testLinkedStackClear() throws Exception {
        System.out.println("\n=== LinkedStack: Limpiar pila ===");
        LinkedStack<Integer> stack = new LinkedStack<>();

        stack.push(1);
        stack.push(2);
        System.out.println("Tamaño antes de clear: " + stack.size());

        stack.clear();
        System.out.println("¿Está vacía tras clear? (esperado: true): " + stack.isEmpty());
        System.out.println("Tamaño tras clear (esperado: 0): " + stack.size());
    }
}