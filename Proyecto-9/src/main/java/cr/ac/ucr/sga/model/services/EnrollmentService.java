package cr.ac.ucr.sga.model.services;

import cr.ac.ucr.sga.model.entities.Student;
import cr.ac.ucr.sga.model.structures.lists.SimpleLinkedList;
import cr.ac.ucr.sga.model.structures.queues.ArrayQueue;
import cr.ac.ucr.sga.model.structures.queues.LinkedQueue;
import cr.ac.ucr.sga.model.structures.queues.PriorityQueue;
import cr.ac.ucr.sga.model.structures.queues.Queue;
import cr.ac.ucr.sga.model.structures.queues.QueueException;

public class EnrollmentService {
    private static EnrollmentService instance;

    private final ArrayQueue<Student> arrayQueue = new ArrayQueue<>(50);
    private final LinkedQueue<Student> linkedQueue = new LinkedQueue<>();
    private final PriorityQueue<Student> priorityQueue = new PriorityQueue<>();
    private Queue<Student> activeQueue = arrayQueue;

    private EnrollmentService() {}

    public static EnrollmentService getInstance() {
        if (instance == null) {
            instance = new EnrollmentService();
        }
        return instance;
    }

    public void enqueueStudent(Student student) {
        try {
            if (activeQueue instanceof PriorityQueue) {
                int credits = student.getApprovedCredits();
                int priority;
                if (credits >= 160) priority = 1;
                else if (credits >= 80)  priority = 2;
                else priority = 3;
                activeQueue.enQueue(student, priority);
            } else {
                activeQueue.enQueue(student);
            }
        } catch (QueueException e) {
            throw new RuntimeException(e);
        }
    }

    public void enqueueStudentWithPriority(Student student, int priority) {
        try {
            activeQueue.enQueue(student, priority);
        } catch (QueueException e) {
            throw new RuntimeException(e);
        }
    }

    public Student dequeueStudent() throws QueueException {
        return activeQueue.deQueue();
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
        if (activeQueue.isEmpty()) return list;

        //sacamos todos los elementos, los guardamos en la lista y los volvemos a encolar
        SimpleLinkedList<Student> temp = new SimpleLinkedList<>();
        try {
            while (!activeQueue.isEmpty()) {
                Student s = activeQueue.deQueue();
                list.addLast(s);
                temp.addLast(s);
            }
            //restauramos la cola
            for (int i = 0; i < temp.size(); i++) {
                activeQueue.enQueue(temp.get(i));
            }
        } catch (QueueException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}