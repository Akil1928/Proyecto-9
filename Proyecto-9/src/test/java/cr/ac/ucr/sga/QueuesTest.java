package cr.ac.ucr.sga;

import cr.ac.ucr.sga.model.entities.Student;
import cr.ac.ucr.sga.model.entities.StudentBuilder;
import cr.ac.ucr.sga.model.structures.queues.ArrayQueue;
import cr.ac.ucr.sga.model.structures.queues.LinkedQueue;
import cr.ac.ucr.sga.model.structures.queues.PriorityQueue;
import cr.ac.ucr.sga.model.structures.queues.QueueException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QueuesTest {

    private Student student(String id, int credits) {
        return new StudentBuilder().setId(id).setName(id).setEmail(id + "@ucr.ac.cr").withCreditosAprobados(credits).build();
    }

    @Test
    void testArrayQueueEnqueueDequeue() throws Exception {
        ArrayQueue<Integer> queue = new ArrayQueue<>();
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        Assertions.assertEquals(1, queue.dequeue());
        Assertions.assertEquals(2, queue.dequeue());
        Assertions.assertEquals(3, queue.dequeue());
    }

    @Test
    void testArrayQueueCircular() throws Exception {
        ArrayQueue<Integer> queue = new ArrayQueue<>(5);
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        queue.enqueue(4);
        queue.enqueue(5);
        Assertions.assertEquals(1, queue.dequeue());
        Assertions.assertEquals(2, queue.dequeue());
        Assertions.assertEquals(3, queue.dequeue());
        queue.enqueue(6);
        queue.enqueue(7);
        queue.enqueue(8);
        Assertions.assertEquals(4, queue.dequeue());
        Assertions.assertEquals(5, queue.dequeue());
        Assertions.assertEquals(6, queue.dequeue());
        Assertions.assertEquals(7, queue.dequeue());
        Assertions.assertEquals(8, queue.dequeue());
    }

    @Test
    void testArrayQueueEmptyThrowsException() {
        ArrayQueue<Integer> queue = new ArrayQueue<>();
        Assertions.assertThrows(QueueException.class, queue::dequeue);
    }

    @Test
    void testArrayQueueOneElement() throws Exception {
        ArrayQueue<String> queue = new ArrayQueue<>();
        queue.enqueue("A");
        Assertions.assertEquals("A", queue.dequeue());
        Assertions.assertTrue(queue.isEmpty());
    }

    @Test
    void testArrayQueuePeekNoRemove() throws Exception {
        ArrayQueue<String> queue = new ArrayQueue<>();
        queue.enqueue("A");
        Assertions.assertEquals("A", queue.peek());
        Assertions.assertEquals(1, queue.size());
    }

    @Test
    void testLinkedQueueEnqueueDequeue() throws Exception {
        LinkedQueue<Integer> queue = new LinkedQueue<>();
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        Assertions.assertEquals(1, queue.dequeue());
        Assertions.assertEquals(2, queue.dequeue());
        Assertions.assertEquals(3, queue.dequeue());
    }

    @Test
    void testLinkedQueueEmptyThrowsException() {
        LinkedQueue<Integer> queue = new LinkedQueue<>();
        Assertions.assertThrows(QueueException.class, queue::dequeue);
    }

    @Test
    void testLinkedQueueOneElement() throws Exception {
        LinkedQueue<String> queue = new LinkedQueue<>();
        queue.enqueue("A");
        Assertions.assertEquals("A", queue.dequeue());
        Assertions.assertTrue(queue.isEmpty());
    }

    @Test
    void testPriorityQueueOrderByCredits() throws Exception {
        PriorityQueue<Student> queue = new PriorityQueue<>((a, b) -> Integer.compare(a.getApprovedCredits(), b.getApprovedCredits()));
        queue.enqueue(student("S1", 10));
        queue.enqueue(student("S2", 50));
        queue.enqueue(student("S3", 30));
        queue.enqueue(student("S4", 80));
        queue.enqueue(student("S5", 20));
        Assertions.assertEquals(80, queue.dequeue().getApprovedCredits());
        Assertions.assertEquals(50, queue.dequeue().getApprovedCredits());
        Assertions.assertEquals(30, queue.dequeue().getApprovedCredits());
        Assertions.assertEquals(20, queue.dequeue().getApprovedCredits());
        Assertions.assertEquals(10, queue.dequeue().getApprovedCredits());
    }

    @Test
    void testPriorityQueueEmptyThrowsException() {
        PriorityQueue<Student> queue = new PriorityQueue<>((a, b) -> Integer.compare(a.getApprovedCredits(), b.getApprovedCredits()));
        Assertions.assertThrows(QueueException.class, queue::dequeue);
    }

    @Test
    void testPriorityQueueOneElement() throws Exception {
        PriorityQueue<Student> queue = new PriorityQueue<>((a, b) -> Integer.compare(a.getApprovedCredits(), b.getApprovedCredits()));
        queue.enqueue(student("S1", 10));
        Assertions.assertEquals(10, queue.dequeue().getApprovedCredits());
        Assertions.assertTrue(queue.isEmpty());
    }

    @Test
    void testPriorityQueueSameCredits() throws Exception {
        PriorityQueue<Student> queue = new PriorityQueue<>((a, b) -> Integer.compare(a.getApprovedCredits(), b.getApprovedCredits()));
        Student a = student("A", 50);
        Student b = student("B", 50);
        queue.enqueue(a);
        queue.enqueue(b);
        Assertions.assertEquals(50, queue.dequeue().getApprovedCredits());
        Assertions.assertEquals(50, queue.dequeue().getApprovedCredits());
    }
}

