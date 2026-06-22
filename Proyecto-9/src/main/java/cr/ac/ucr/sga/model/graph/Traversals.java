package cr.ac.ucr.sga.model.graph;

import java.util.*;

public final class Traversals {

    public static List<String> bfs(Graph g, String start) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(start);
        List<String> order = new ArrayList<>();
        if (!g.containsVertex(start)) return order;

        Set<String> visited = new HashSet<>();
        Queue<String> q = new ArrayDeque<>();
        visited.add(start);
        q.add(start);

        while (!q.isEmpty()) {
            String u = q.poll();
            order.add(u);
            for (Edge e : g.getEdgesFrom(u)) {
                if (!visited.contains(e.getTo())) {
                    visited.add(e.getTo());
                    q.add(e.getTo());
                }
            }
        }
        return order;
    }

    public static List<String> dfs(Graph g, String start) {
        Objects.requireNonNull(g);
        Objects.requireNonNull(start);
        List<String> order = new ArrayList<>();
        if (!g.containsVertex(start)) return order;

        Set<String> visited = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            String u = stack.pop();
            if (visited.contains(u)) continue;
            visited.add(u);
            order.add(u);
            // push neighbors in reverse order to get natural order
            List<Edge> edges = g.getEdgesFrom(u);
            for (int i = edges.size() - 1; i >= 0; i--) {
                String v = edges.get(i).getTo();
                if (!visited.contains(v)) stack.push(v);
            }
        }
        return order;
    }
}

