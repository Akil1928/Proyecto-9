package cr.ac.ucr.sga.model.graph;

import java.util.*;

/**
 * Dijkstra's algorithm for non-negative weighted graphs.
 */
public class Dijkstra {

    public static class Result {
        private final Map<String, Double> dist;
        private final Map<String, String> prev;

        Result(Map<String, Double> dist, Map<String, String> prev) {
            this.dist = dist;
            this.prev = prev;
        }

        public double distanceTo(String node) {
            return dist.getOrDefault(node, Double.POSITIVE_INFINITY);
        }

        public List<String> pathTo(String target) {
            if (!dist.containsKey(target) || dist.get(target) == Double.POSITIVE_INFINITY) return Collections.emptyList();
            LinkedList<String> path = new LinkedList<>();
            String cur = target;
            while (cur != null) {
                path.addFirst(cur);
                cur = prev.get(cur);
            }
            return path;
        }

        public Map<String, Double> distances() { return Collections.unmodifiableMap(dist); }
    }

    public static Result compute(Graph g, String source) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(source);

        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<NodeEntry> pq = new PriorityQueue<>(Comparator.comparingDouble(ne -> ne.dist));

        for (Vertex v : g.getVertices()) {
            dist.put(v.getId(), Double.POSITIVE_INFINITY);
            prev.put(v.getId(), null);
        }

        if (!g.containsVertex(source)) return new Result(dist, prev);

        dist.put(source, 0.0);
        pq.add(new NodeEntry(source, 0.0));

        while (!pq.isEmpty()) {
            NodeEntry ne = pq.poll();
            if (ne.dist > dist.getOrDefault(ne.id, Double.POSITIVE_INFINITY)) continue;

            for (Edge e : g.getEdgesFrom(ne.id)) {
                double alt = dist.get(ne.id) + e.getWeight();
                if (alt < dist.getOrDefault(e.getTo(), Double.POSITIVE_INFINITY)) {
                    dist.put(e.getTo(), alt);
                    prev.put(e.getTo(), ne.id);
                    pq.add(new NodeEntry(e.getTo(), alt));
                }
            }
        }

        return new Result(dist, prev);
    }

    private static final class NodeEntry {
        final String id;
        final double dist;

        NodeEntry(String id, double dist) { this.id = id; this.dist = dist; }
    }
}

