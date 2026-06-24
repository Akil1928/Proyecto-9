package cr.ac.ucr.sga.model.services;

import cr.ac.ucr.sga.model.entities.Notification;
import cr.ac.ucr.sga.model.structures.queues.PriorityQueue;
import cr.ac.ucr.sga.model.structures.queues.QueueException;
import cr.ac.ucr.sga.view.observers.NotificationObserver;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private static NotificationService instance;

    private final List<NotificationObserver> observers = new ArrayList<>();
    private final PriorityQueue<Notification> notificationQueue = new PriorityQueue<>();

    private NotificationService() {}

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
        try {
            notificationQueue.enQueue(notification, levelToPriority(level));
        } catch (QueueException e) {
            throw new RuntimeException(e);
        }
        for (NotificationObserver observer : observers) {
            observer.onNotification(message, level);
        }
    }

    public Notification peekLastNotification() {
        try {
            return notificationQueue.peek();
        } catch (QueueException e) {
            return null;
        }
    }

    private int levelToPriority(String level) {
        if (level == null) return 3;
        return switch (level.toUpperCase()) {
            case "HIGH", "ERROR"   -> 1;
            case "MEDIUM", "WARN"  -> 2;
            default                -> 3;
        };
    }

    //agregar este campo al singleton
    private final java.util.Map<String, java.util.List<String[]>> pendingByUser = new java.util.HashMap<>();

    //agregar este método
    public void queueForUser(String targetUserId, String message, String level) {
        pendingByUser
                .computeIfAbsent(targetUserId, k -> new java.util.ArrayList<>())
                .add(new String[]{message, level});
    }

    //agregar este método
    public java.util.List<String[]> drainPending(String userId) {
        return pendingByUser.getOrDefault(userId, java.util.List.of());
        //no borramos aún — llamar clearPending después de mostrar
    }

    public void clearPending(String userId) {
        pendingByUser.remove(userId);
    }
}