package cr.ac.ucr.sga.model.services;

import cr.ac.ucr.sga.model.entities.Notification;
import cr.ac.ucr.sga.model.structures.queues.PriorityQueue;
import cr.ac.ucr.sga.view.observers.NotificationObserver;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private static NotificationService instance;

    private final List<NotificationObserver> observers = new ArrayList<>();
    private final PriorityQueue<Notification> notificationQueue = new PriorityQueue<>(Notification::compareTo);

    private NotificationService() {
    }

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public void addObserver(NotificationObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }

    public void notify(String message, String level) {
        Notification notification = new Notification(message, level);
        notificationQueue.enqueue(notification);
        for (NotificationObserver observer : observers) {
            observer.onNotification(message, level);
        }
    }

    public Notification peekLastNotification() {
        try {
            return notificationQueue.peek();
        } catch (Exception e) {
            return null;
        }
    }
}

