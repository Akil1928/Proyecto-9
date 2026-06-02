package cr.ac.ucr.sga.view.observers;

public interface NotificationObserver {
    void onNotification(String message, String level);

    void onNotification(String message);
}

