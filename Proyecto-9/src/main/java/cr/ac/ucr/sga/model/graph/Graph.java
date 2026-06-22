package cr.ac.ucr.sga.model.graph;

import java.util.*;

/**
 * Simple weighted graph implementation using adjacency lists.
 */
public class Graph {
    private final Map<String, Vertex> vertices = new HashMap<>();
    private final Map<String, List<Edge>> adj = new HashMap<>();

    public Vertex addVertex(String id) {
        return addVertex(id, id);
    }

    public Vertex addVertex(String id, String label) {
        vertices.putIfAbsent(id, new Vertex(id, label));
        adj.putIfAbsent(id, new ArrayList<>());
        return vertices.get(id);
    }

    public void addEdge(String from, String to, double weight, boolean directed) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        addVertex(from);
        addVertex(to);
        adj.get(from).add(new Edge(from, to, weight));
        if (!directed) {
            adj.get(to).add(new Edge(to, from, weight));
        }
    }

    public void addEdge(String from, String to, double weight) {
        addEdge(from, to, weight, false);
    }

    public Collection<Vertex> getVertices() {
        return Collections.unmodifiableCollection(vertices.values());
    }

    public List<Edge> getEdgesFrom(String id) {
        return Collections.unmodifiableList(adj.getOrDefault(id, Collections.emptyList()));
    }

    public boolean containsVertex(String id) {
        return vertices.containsKey(id);
    }

    public int vertexCount() { return vertices.size(); }

    public int edgeCount() {
        int c = 0;
        for (var l : adj.values()) c += l.size();
        return c;
    }
}

