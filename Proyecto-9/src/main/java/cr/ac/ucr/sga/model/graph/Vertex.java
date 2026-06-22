package cr.ac.ucr.sga.model.graph;

import java.util.Objects;

/**
 * Simple vertex representation identified by an id and optional label.
 */
public final class Vertex {
    private final String id;
    private final String label;

    public Vertex(String id) {
        this(id, id);
    }

    public Vertex(String id, String label) {
        this.id = Objects.requireNonNull(id);
        this.label = label == null ? id : label;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return id.equals(vertex.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Vertex{" + id + '}';
    }
}

