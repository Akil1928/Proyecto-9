package cr.ac.ucr.sga.model.entities;

/**
 * Entidad que representa un edificio en el campus.
 * Aplica el patrón Builder para su construcción.
 */
public class Building {

    private final String id;
    private final String name;
    private final double x;
    private final double y;

    private Building(Builder builder) {
        this.id   = builder.id;
        this.name = builder.name;
        this.x    = builder.x;
        this.y    = builder.y;
    }

    public String getId()   { return id; }
    public String getName() { return name; }
    public double getX()    { return x; }
    public double getY()    { return y; }

    @Override
    public String toString() { return name; }

    // ── Builder ──────────────────────────────────────────────────────────
    public static class Builder {
        private final String id;
        private String name;
        private double x;
        private double y;

        public Builder(String id) { this.id = id; }

        public Builder name(String name) { this.name = name; return this; }
        public Builder x(double x)       { this.x = x;       return this; }
        public Builder y(double y)       { this.y = y;       return this; }

        public Building build() {
            if (id == null || id.isBlank())
                throw new IllegalStateException("Building requires an id");
            if (name == null || name.isBlank()) name = id;
            return new Building(this);
        }
    }
}
