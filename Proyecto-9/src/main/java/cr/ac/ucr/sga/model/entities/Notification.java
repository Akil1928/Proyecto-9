package cr.ac.ucr.sga.model.entities;

import java.time.LocalDateTime;

public class Notification implements Comparable<Notification> {
    private String message;
    private String level;
    private int priority;
    private LocalDateTime timestamp;

    public Notification(String message, String level) {
        this.message = message;
        this.level = level;
        this.priority = priorityFor(level);
        this.timestamp = LocalDateTime.now();
    }

    private int priorityFor(String level) {
        if (level == null) return 3;
        return switch (level.toUpperCase()) {
            case "URGENTE"      -> 1;
            case "ADVERTENCIA"  -> 2;
            default             -> 3;
        };
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; this.priority = priorityFor(level); }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public int compareTo(Notification other) {
        int cmp = Integer.compare(this.priority, other.priority);
        if (cmp != 0) return cmp;
        return other.timestamp.compareTo(this.timestamp);
    }
}

