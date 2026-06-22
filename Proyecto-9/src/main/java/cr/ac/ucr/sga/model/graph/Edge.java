package cr.ac.ucr.sga.model.graph;

/**
 * Edge in a weighted graph. For undirected graphs the edge may be added both ways by the caller.
 */
public final class Edge {
    private final String from;
    private final String to;
    private final double weight;

    public Edge(String from, String to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Edge{" + from + "->" + to + ",w=" + weight + '}';
    }
}

