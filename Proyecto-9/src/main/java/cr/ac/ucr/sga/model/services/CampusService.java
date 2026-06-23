package cr.ac.ucr.sga.model.services;

import com.google.gson.*;
import cr.ac.ucr.sga.model.entities.Building;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

/**
 * Singleton que gestiona el grafo del campus.
 *
 * TDA Grafo propio (lista de adyacencia), sin librerías externas de grafos.
 * Algoritmos:  Dijkstra (cola de prioridad mínima interna),
 *              BFS (cola FIFO),
 *              DFS (pila iterativa).
 * Persistencia: campus.json (Gson).
 */
public final class CampusService {

    // ── Singleton ─────────────────────────────────────────────────────────
    private static CampusService instance;
    public static CampusService getInstance() {
        if (instance == null) instance = new CampusService();
        return instance;
    }
    private CampusService() { loadFromJson(); }

    // ── TDA Grafo (lista de adyacencia) ───────────────────────────────────
    /** Vértice: edificio + lista de aristas salientes */
    private final Map<String, Building>       buildings = new LinkedHashMap<>();
    private final Map<String, List<CEdge>>    adj       = new LinkedHashMap<>();

    public static final class CEdge {
        public final String from, to;
        public final double weight;
        public CEdge(String from, String to, double weight) {
            this.from = from; this.to = to; this.weight = weight;
        }
    }

    // ── Operaciones del grafo ─────────────────────────────────────────────

    public void addBuilding(Building b) {
        buildings.put(b.getId(), b);
        adj.putIfAbsent(b.getId(), new ArrayList<>());
    }

    public boolean removeBuilding(String id) {
        if (!buildings.containsKey(id)) return false;
        buildings.remove(id);
        adj.remove(id);
        // eliminar aristas que apunten a este nodo
        for (List<CEdge> edges : adj.values())
            edges.removeIf(e -> e.to.equals(id));
        return true;
    }

    public void addEdge(String from, String to, double weight) {
        if (!adj.containsKey(from) || !adj.containsKey(to)) return;
        // evitar duplicados
        removeEdge(from, to);
        adj.get(from).add(new CEdge(from, to, weight));
        adj.get(to).add(new CEdge(to, from, weight));   // no dirigido
    }

    public void removeEdge(String from, String to) {
        if (adj.containsKey(from)) adj.get(from).removeIf(e -> e.to.equals(to));
        if (adj.containsKey(to))   adj.get(to).removeIf(e -> e.to.equals(from));
    }

    public Collection<Building> getBuildings() {
        return Collections.unmodifiableCollection(buildings.values());
    }

    public List<CEdge> getEdgesFrom(String id) {
        return Collections.unmodifiableList(adj.getOrDefault(id, Collections.emptyList()));
    }

    public boolean containsBuilding(String id) { return buildings.containsKey(id); }
    public Building getBuilding(String id)      { return buildings.get(id); }

    /** Todas las aristas únicas (grafo no dirigido → evitar duplicados) */
    public List<CEdge> getAllEdges() {
        Set<String> seen = new HashSet<>();
        List<CEdge> result = new ArrayList<>();
        for (List<CEdge> list : adj.values()) {
            for (CEdge e : list) {
                String key = e.from.compareTo(e.to) < 0
                        ? e.from + "-" + e.to
                        : e.to   + "-" + e.from;
                if (seen.add(key)) result.add(e);
            }
        }
        return result;
    }

    // ── Strategy: Dijkstra ────────────────────────────────────────────────
    /** Resultado de Dijkstra */
    public static final class DijkstraResult {
        public final Map<String, Double> dist;
        public final Map<String, String> prev;
        DijkstraResult(Map<String, Double> dist, Map<String, String> prev) {
            this.dist = dist; this.prev = prev;
        }

        public List<String> pathTo(String target) {
            if (!dist.containsKey(target) || dist.get(target).isInfinite())
                return Collections.emptyList();
            LinkedList<String> path = new LinkedList<>();
            String cur = target;
            while (cur != null) { path.addFirst(cur); cur = prev.get(cur); }
            return path;
        }
    }

    public DijkstraResult dijkstra(String source) {
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        // Cola de prioridad mínima interna: par (id, distancia)
        PriorityQueue<double[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[1]));
        Map<String, Integer>    idxMap = new HashMap<>();

        int i = 0;
        for (String id : buildings.keySet()) {
            dist.put(id, Double.POSITIVE_INFINITY);
            prev.put(id, null);
            idxMap.put(id, i++);
        }

        dist.put(source, 0.0);
        pq.add(new double[]{ idxMap.get(source), 0.0 });
        String[] idArr = buildings.keySet().toArray(new String[0]);

        while (!pq.isEmpty()) {
            double[] entry  = pq.poll();
            String   u      = idArr[(int) entry[0]];
            double   uDist  = entry[1];
            if (uDist > dist.get(u)) continue;

            for (CEdge e : adj.getOrDefault(u, Collections.emptyList())) {
                double alt = dist.get(u) + e.weight;
                if (alt < dist.getOrDefault(e.to, Double.POSITIVE_INFINITY)) {
                    dist.put(e.to, alt);
                    prev.put(e.to, u);
                    pq.add(new double[]{ idxMap.get(e.to), alt });
                }
            }
        }
        return new DijkstraResult(dist, prev);
    }

    // ── Strategy: BFS ─────────────────────────────────────────────────────
    /** Devuelve el orden de visita BFS desde start */
    public List<String> bfs(String start) {
        List<String> order   = new ArrayList<>();
        Set<String>  visited = new LinkedHashSet<>();
        Queue<String> queue  = new ArrayDeque<>();   // Cola FIFO

        if (!buildings.containsKey(start)) return order;
        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            String u = queue.poll();
            order.add(u);
            for (CEdge e : adj.getOrDefault(u, Collections.emptyList())) {
                if (!visited.contains(e.to)) {
                    visited.add(e.to);
                    queue.add(e.to);
                }
            }
        }
        return order;
    }

    // ── Strategy: DFS ─────────────────────────────────────────────────────
    /** Devuelve el orden de visita DFS desde start (iterativo con Pila) */
    public List<String> dfs(String start) {
        List<String>   order   = new ArrayList<>();
        Set<String>    visited = new LinkedHashSet<>();
        Deque<String>  stack   = new ArrayDeque<>();  // Pila LIFO

        if (!buildings.containsKey(start)) return order;
        stack.push(start);

        while (!stack.isEmpty()) {
            String u = stack.pop();
            if (visited.contains(u)) continue;
            visited.add(u);
            order.add(u);
            List<CEdge> edges = adj.getOrDefault(u, Collections.emptyList());
            for (int j = edges.size() - 1; j >= 0; j--) {
                String v = edges.get(j).to;
                if (!visited.contains(v)) stack.push(v);
            }
        }
        return order;
    }

    // ── Persistencia JSON ─────────────────────────────────────────────────
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String RESOURCE_PATH = "/data/campus.json";

    /** Ruta donde se guarda el archivo (en recursos o carpeta de trabajo) */
    private Path resolveWritePath() {
        try {
            URL url = getClass().getResource(RESOURCE_PATH);
            if (url != null) return Path.of(url.toURI());
        } catch (Exception ignored) {}
        return Path.of("src/main/resources/data/campus.json");
    }

    public void loadFromJson() {
        buildings.clear();
        adj.clear();
        try (InputStream is = getClass().getResourceAsStream(RESOURCE_PATH)) {
            if (is == null) { loadDefaults(); return; }
            JsonObject root = GSON.fromJson(new InputStreamReader(is), JsonObject.class);
            parseJson(root);
        } catch (Exception e) {
            System.err.println("[CampusService] Error cargando campus.json: " + e.getMessage());
            loadDefaults();
        }
    }

    private void parseJson(JsonObject root) {
        for (JsonElement el : root.getAsJsonArray("buildings")) {
            JsonObject o = el.getAsJsonObject();
            Building b = new Building.Builder(o.get("id").getAsString())
                    .name(o.get("name").getAsString())
                    .x(o.get("x").getAsDouble())
                    .y(o.get("y").getAsDouble())
                    .build();
            addBuilding(b);
        }
        for (JsonElement el : root.getAsJsonArray("edges")) {
            JsonObject o = el.getAsJsonObject();
            addEdge(o.get("from").getAsString(),
                    o.get("to").getAsString(),
                    o.get("weight").getAsDouble());
        }
    }

    public void saveToJson() {
        JsonObject root = new JsonObject();

        JsonArray bArr = new JsonArray();
        for (Building b : buildings.values()) {
            JsonObject o = new JsonObject();
            o.addProperty("id",   b.getId());
            o.addProperty("name", b.getName());
            o.addProperty("x",    b.getX());
            o.addProperty("y",    b.getY());
            bArr.add(o);
        }
        root.add("buildings", bArr);

        JsonArray eArr = new JsonArray();
        for (CEdge e : getAllEdges()) {
            JsonObject o = new JsonObject();
            o.addProperty("from",   e.from);
            o.addProperty("to",     e.to);
            o.addProperty("weight", e.weight);
            eArr.add(o);
        }
        root.add("edges", eArr);

        try {
            Path p = resolveWritePath();
            Files.createDirectories(p.getParent());
            Files.writeString(p, GSON.toJson(root));
        } catch (Exception e) {
            System.err.println("[CampusService] No se pudo guardar campus.json: " + e.getMessage());
        }
    }

    /** Campus de respaldo si no existe el archivo */
    private void loadDefaults() {
        Building[] defs = {
                new Building.Builder("B1").name("Biblioteca Central")     .x(400).y(100).build(),
                new Building.Builder("B2").name("Facultad de Ingeniería") .x(180).y(230).build(),
                new Building.Builder("B3").name("Rectoría")               .x(620).y(230).build(),
                new Building.Builder("B4").name("Comedor Universitario")  .x(180).y(400).build(),
                new Building.Builder("B5").name("Centro Deportivo")       .x(620).y(400).build(),
                new Building.Builder("B6").name("Facultad de Ciencias")   .x(400).y(300).build(),
                new Building.Builder("B7").name("Posgrado")               .x(400).y(480).build(),
                new Building.Builder("B8").name("Aulas de Idiomas")       .x(90) .y(310).build(),
                new Building.Builder("B9").name("Centro de Cómputo")      .x(710).y(310).build()
        };
        for (Building b : defs) addBuilding(b);
        addEdge("B1","B2",320); addEdge("B1","B3",280); addEdge("B1","B6",210);
        addEdge("B2","B4",190); addEdge("B2","B6",250); addEdge("B2","B8",130);
        addEdge("B3","B5",200); addEdge("B3","B6",230); addEdge("B3","B9",120);
        addEdge("B4","B7",240); addEdge("B5","B7",210); addEdge("B6","B7",185);
        addEdge("B8","B4",160); addEdge("B9","B5",145);
    }
}
