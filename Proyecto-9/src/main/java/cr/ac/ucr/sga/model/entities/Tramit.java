package cr.ac.ucr.sga.model.entities;

import cr.ac.ucr.sga.model.states.PendingState;
import cr.ac.ucr.sga.model.states.TramitState;

import java.time.LocalDateTime;
import java.util.UUID;

public class Tramit {
    private String id;
    private String type;
    private String description;
    private String studentId;
    private String studentName;
    private TramitState state;
    private String stateName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Tramit() {
    }

    public Tramit(String type, String description, String studentId, String studentName) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.description = description;
        this.studentId = studentId;
        this.studentName = studentName;
        this.state = new PendingState();
        this.stateName = state.getName();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void nextState() {
        if (state != null) {
            state.handle(this);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public TramitState getState() { return state; }
    public void setState(TramitState state) { this.state = state; }
    public String getStateName() { return stateName; }
    public void setStateName(String stateName) { this.stateName = stateName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

