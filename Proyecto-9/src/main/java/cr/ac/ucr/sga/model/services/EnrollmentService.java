package cr.ac.ucr.sga.model.services;

import cr.ac.ucr.sga.model.entities.Student;
import cr.ac.ucr.sga.model.structures.lists.SimpleLinkedList;
import cr.ac.ucr.sga.model.structures.queues.ArrayQueue;
import cr.ac.ucr.sga.model.structures.queues.LinkedQueue;
import cr.ac.ucr.sga.model.structures.queues.PriorityQueue;
import cr.ac.ucr.sga.model.structures.queues.Queue;
import cr.ac.ucr.sga.model.structures.queues.QueueException;

import java.util.Comparator;

public class EnrollmentService {
    private static EnrollmentService instance;

    private final ArrayQueue<Student> arrayQueue = new ArrayQueue<>();
    private final LinkedQueue<Student> linkedQueue = new LinkedQueue<>();
    private final PriorityQueue<Student> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(Student::getApprovedCredits).reversed());
    private Queue<Student> activeQueue = arrayQueue;

    private EnrollmentService() {
    }

    public static EnrollmentService getInstance() {
        if (instance == null) {
            instance = new EnrollmentService();
        }
        return instance;
    }

    public void enqueueStudent(Student student) {
        try {
            activeQueue.enqueue(student);
        } catch (QueueException e) {
            throw new RuntimeException(e);
        }
    }

    public Student dequeueStudent() throws QueueException {
        return activeQueue.dequeue();
    }

    public Student peekStudent() throws QueueException {
        return activeQueue.peek();
    }

    public void setQueueType(String type) {
        if ("linked".equalsIgnoreCase(type)) {
            activeQueue = linkedQueue;
        } else if ("priority".equalsIgnoreCase(type)) {
            activeQueue = priorityQueue;
        } else {
            activeQueue = arrayQueue;
        }
    }

    public boolean isEmpty() {
        return activeQueue.isEmpty();
    }

    public int size() {
        return activeQueue.size();
    }

    public SimpleLinkedList<Student> getCurrentQueueAsList() {
        SimpleLinkedList<Student> list = new SimpleLinkedList<>();
        int n = activeQueue.size();
        if (n == 0) return list;
        Student[] array = new Student[n];
        // Los distintos tipos de queue implementan toArray(T[]), usar reflexión simple
        if (activeQueue instanceof ArrayQueue) {
            ((ArrayQueue<Student>) activeQueue).toArray(array);
        } else if (activeQueue instanceof LinkedQueue) {
            ((LinkedQueue<Student>) activeQueue).toArray(array);
        } else if (activeQueue instanceof PriorityQueue) {
            ((PriorityQueue<Student>) activeQueue).toArray(array);
        }

        for (Student s : array) {
            if (s != null) list.addLast(s);
        }

        return list;
    }
}


