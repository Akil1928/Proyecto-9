package cr.ac.ucr.sga;

import cr.ac.ucr.sga.model.entities.Student;
import cr.ac.ucr.sga.model.entities.StudentBuilder;
import cr.ac.ucr.sga.model.structures.queues.ArrayQueue;
import cr.ac.ucr.sga.model.structures.queues.LinkedQueue;
import cr.ac.ucr.sga.model.structures.queues.PriorityQueue;
import cr.ac.ucr.sga.model.structures.queues.QueueException;
import org.junit.jupiter.api.Test;

public class QueuesTest {

    private Student student(String id, int credits) {
        return new StudentBuilder()
                .setId(id)
                .setName(id)
                .setEmail(id + "@ucr.ac.cr")
                .withCreditosAprobados(credits)
                .build();
    }

    // ─── ArrayQueue ─────────────

    @Test
    void testArrayQueueEnqueueDequeue() throws Exception {
        System.out.println("=== ArrayQueue: Enqueue y Dequeue ===");
        ArrayQueue<Integer> queue = new ArrayQueue<>(10);

        System.out.println("Encolando: 1, 2, 3");
        queue.enQueue(1);
        queue.enQueue(2);
        queue.enQueue(3);

        System.out.println("Desencolando (esperado 1): " + queue.deQueue());
        System.out.println("Desencolando (esperado 2): " + queue.deQueue());
        System.out.println("Desencolando (esperado 3): " + queue.deQueue());
    }

    @Test
    void testArrayQueueEmptyThrowsException() {
        System.out.println("\n=== ArrayQueue: Excepción al desencolar vacía ===");
        ArrayQueue<Integer> queue = new ArrayQueue<>(10);

        try {
            System.out.println("Intentando hacer deQueue en cola vacía...");
            queue.deQueue();
        } catch (QueueException e) {
            System.out.println("Excepción capturada correctamente: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testArrayQueueOneElement() throws Exception {
        System.out.println("\n=== ArrayQueue: Un solo elemento ===");
        ArrayQueue<String> queue = new ArrayQueue<>(10);

        queue.enQueue("A");
        System.out.println("Desencolando (esperado A): " + queue.deQueue());
        System.out.println("¿Está vacía? (esperado true): " + queue.isEmpty());
    }

    @Test
    void testArrayQueuePeekNoRemove() throws Exception {
        System.out.println("\n=== ArrayQueue: Peek sin remover ===");
        ArrayQueue<String> queue = new ArrayQueue<>(10);

        queue.enQueue("A");
        System.out.println("Elemento al frente (esperado A): " + queue.peek());
        System.out.println("Tamaño de la cola (esperado 1): " + queue.size());
    }

    @Test
    void testArrayQueueFull() throws Exception {
        System.out.println("\n=== ArrayQueue: Caso borde - Cola llena ===");
        ArrayQueue<Integer> queue = new ArrayQueue<>(2);

        System.out.println("Encolando 1 y 2 en capacidad 2...");
        queue.enQueue(1);
        queue.enQueue(2);

        try {
            System.out.println("Intentando encolar un tercer elemento (esperado QueueException)...");
            queue.enQueue(3);
        } catch (QueueException e) {
            System.out.println("Excepción de cola llena capturada correctamente: " + e.getMessage());
        }
    }

    @Test
    void testArrayQueueClear() throws Exception {
        System.out.println("\n=== ArrayQueue: Limpiar cola ===");
        ArrayQueue<Integer> queue = new ArrayQueue<>(10);

        queue.enQueue(1);
        queue.enQueue(2);
        System.out.println("Tamaño antes de clear: " + queue.size());

        queue.clear();
        System.out.println("¿Está vacía tras clear? (esperado true): " + queue.isEmpty());
        System.out.println("Tamaño tras clear (esperado 0): " + queue.size());
    }

    // ─── LinkedQueue ───────────────────────

    @Test
    void testLinkedQueueEnqueueDequeue() throws Exception {
        System.out.println("\n=== LinkedQueue: Enqueue y Dequeue ===");
        LinkedQueue<Integer> queue = new LinkedQueue<>();

        System.out.println("Encolando: 1, 2, 3");
        queue.enQueue(1);
        queue.enQueue(2);
        queue.enQueue(3);

        System.out.println("Desencolando (esperado 1): " + queue.deQueue());
        System.out.println("Desencolando (esperado 2): " + queue.deQueue());
        System.out.println("Desencolando (esperado 3): " + queue.deQueue());
    }

    @Test
    void testLinkedQueueEmptyThrowsException() {
        System.out.println("\n=== LinkedQueue: Excepción al desencolar vacía ===");
        LinkedQueue<Integer> queue = new LinkedQueue<>();

        try {
            System.out.println("Intentando hacer deQueue en cola vacía...");
            queue.deQueue();
        } catch (QueueException e) {
            System.out.println("Excepción capturada correctamente: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testLinkedQueueOneElement() throws Exception {
        System.out.println("\n=== LinkedQueue: Un solo elemento ===");
        LinkedQueue<String> queue = new LinkedQueue<>();

        queue.enQueue("A");
        System.out.println("Desencolando (esperado A): " + queue.deQueue());
        System.out.println("¿Está vacía? (esperado true): " + queue.isEmpty());
    }

    @Test
    void testLinkedQueuePeekNoRemove() throws Exception {
        System.out.println("\n=== LinkedQueue: Peek sin remover ===");
        LinkedQueue<String> queue = new LinkedQueue<>();

        queue.enQueue("A");
        System.out.println("Elemento al frente (esperado A): " + queue.peek());
        System.out.println("Tamaño de la cola (esperado 1): " + queue.size());
    }

    @Test
    void testLinkedQueueManyElements() throws Exception {
        System.out.println("\n=== LinkedQueue: Crecimiento dinámico (50 elementos) ===");
        LinkedQueue<Integer> queue = new LinkedQueue<>();

        System.out.println("Encolando 50 elementos...");
        for (int i = 1; i <= 50; i++) queue.enQueue(i);
        System.out.println("Tamaño final alcanzado (esperado 50): " + queue.size());

        System.out.println("Desencolando consecutivamente para validar orden FIFO...");
        boolean ordenCorrecto = true;
        for (int i = 1; i <= 50; i++) {
            if (queue.deQueue() != i) {
                ordenCorrecto = false;
            }
        }
        System.out.println("¿El orden de salida fue el correcto? " + ordenCorrecto);
        System.out.println("¿Está vacía tras vaciarla? (esperado true): " + queue.isEmpty());
    }

    // ─── PriorityQueue ───────────────────────

    @Test
    void testPriorityQueueOrderByCredits() throws Exception {
        System.out.println("\n=== PriorityQueue: Orden por créditos (Prioridad manual) ===");
        PriorityQueue<Student> queue = new PriorityQueue<>();

        System.out.println("Encolando estudiantes con diferentes prioridades (1=Alta, 2=Media, 3=Baja)...");
        queue.enQueue(student("S1", 10), 3);
        queue.enQueue(student("S2", 50), 2);
        queue.enQueue(student("S3", 30), 2);
        queue.enQueue(student("S4", 80), 1);
        queue.enQueue(student("S5", 20), 3);

        System.out.println("Desencolando en orden de prioridad:");
        System.out.println("1° Salida (esperado 80 créditos): " + queue.deQueue().getApprovedCredits());
        System.out.println("2° Salida (esperado 50 créditos): " + queue.deQueue().getApprovedCredits());
        System.out.println("3° Salida (esperado 30 créditos): " + queue.deQueue().getApprovedCredits());
        System.out.println("4° Salida (esperado 10 créditos): " + queue.deQueue().getApprovedCredits());
        System.out.println("5° Salida (esperado 20 créditos): " + queue.deQueue().getApprovedCredits());
    }

    @Test
    void testPriorityQueueEmptyThrowsException() {
        System.out.println("\n=== PriorityQueue: Excepción al desencolar vacía ===");
        PriorityQueue<Student> queue = new PriorityQueue<>();

        try {
            System.out.println("Intentando hacer deQueue en cola con prioridad vacía...");
            queue.deQueue();
        } catch (QueueException e) {
            System.out.println("Excepción capturada correctamente: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testPriorityQueueOneElement() throws Exception {
        System.out.println("\n=== PriorityQueue: Un solo elemento ===");
        PriorityQueue<Student> queue = new PriorityQueue<>();

        queue.enQueue(student("S1", 10), 1);
        System.out.println("Desencolando (esperado 10 créditos): " + queue.deQueue().getApprovedCredits());
        System.out.println("¿Está vacía? (esperado true): " + queue.isEmpty());
    }

    @Test
    void testPriorityQueueSamePriority() throws Exception {
        System.out.println("\n=== PriorityQueue: Misma prioridad ===");
        PriorityQueue<Student> queue = new PriorityQueue<>();
        Student a = student("A", 50);
        Student b = student("B", 50);

        System.out.println("Encolando dos estudiantes con misma prioridad (1)...");
        queue.enQueue(a, 1);
        queue.enQueue(b, 1);

        System.out.println("Desencolando primero (esperado 50): " + queue.deQueue().getApprovedCredits());
        System.out.println("Desencolando segundo (esperado 50): " + queue.deQueue().getApprovedCredits());
    }

    @Test
    void testPriorityQueueClear() throws Exception {
        System.out.println("\n=== PriorityQueue: Limpiar cola con prioridad ===");
        PriorityQueue<Student> queue = new PriorityQueue<>();

        queue.enQueue(student("S1", 10), 1);
        queue.enQueue(student("S2", 20), 2);
        System.out.println("Tamaño antes de clear: " + queue.size());

        queue.clear();
        System.out.println("¿Está vacía tras clear? (esperado true): " + queue.isEmpty());
        System.out.println("Tamaño tras clear (esperado 0): " + queue.size());
    }
}