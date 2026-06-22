/* UCR - IF-3001 - Grupo 21 */
package cr.ac.ucr.sga.model.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cr.ac.ucr.sga.model.entities.Building;
import cr.ac.ucr.sga.model.graph.Dijkstra;
import cr.ac.ucr.sga.model.graph.Edge;
import cr.ac.ucr.sga.model.graph.Graph;
import cr.ac.ucr.sga.model.graph.Traversals;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CampusService {

    private static CampusService instance;
    private Graph internalGraph;
    private final Map<String, Building> buildings;
    private static final Path CAMPUS_FILE = Paths.get(System.getProperty("user.home"), "sga-data", "campus.json");

    private CampusService() {
        this.internalGraph = new Graph();
        this.buildings = new HashMap<>();
        loadCampusData();
    }

    public static CampusService getInstance() {
        if (instance == null) {
            instance = new CampusService();
        }
        return instance;
    }

    // --- Lógica de Grafos ---

    public List<String> dijkstra(String start, String end) {
        Dijkstra.Result result = Dijkstra.compute(internalGraph, start);
        return result.pathTo(end);
    }

    public List<String> bfs(String start) {
        return Traversals.bfs(internalGraph, start);
    }

    public List<String> dfs(String start) {
        return Traversals.dfs(internalGraph, start);
    }

    // --- Gestión de Datos ---

    public Map<String, Building> getBuildings() {
        return Collections.unmodifiableMap(buildings);
    }

    public Map<String, Map<String, Integer>> getGraph() {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        for (String vertexId : internalGraph.getVertexIds()) {
            Map<String, Integer> neighbors = new HashMap<>();
            for (Edge edge : internalGraph.getEdgesFrom(vertexId)) {
                neighbors.put(edge.getTo(), (int) edge.getWeight());
            }
            result.put(vertexId, neighbors);
        }
        return result;
    }

    public void addBuilding(Building b) {
        buildings.put(b.getId(), b);
        internalGraph.addVertex(b.getId(), b.getName());
        saveCampusData();
    }

    public void addEdge(String from, String to, double weight) {
        internalGraph.addEdge(from, to, weight);
        saveCampusData();
    }

    public void deleteBuilding(String id) {
        buildings.remove(id);
        // Reconstrucción del grafo sin el edificio
        List<EdgeDto> savedEdges = new ArrayList<>();
        Set<String> processedEdges = new HashSet<>();

        for (String v : internalGraph.getVertexIds()) {
            for (Edge e : internalGraph.getEdgesFrom(v)) {
                if (!e.getFrom().equals(id) && !e.getTo().equals(id)) {
                    String hash = e.getFrom() + "-" + e.getTo();
                    if (!processedEdges.contains(hash)) {
                        EdgeDto dto = new EdgeDto();
                        dto.source = e.getFrom();
                        dto.target = e.getTo();
                        dto.weight = e.getWeight();
                        savedEdges.add(dto);
                        processedEdges.add(hash);
                    }
                }
            }
        }

        this.internalGraph = new Graph();
        for (Building b : buildings.values()) internalGraph.addVertex(b.getId(), b.getName());
        for (EdgeDto e : savedEdges) internalGraph.addEdge(e.source, e.target, e.weight);
        saveCampusData();
    }

    // --- Persistencia ---

    private void loadCampusData() {
        try {
            Reader reader;
            if (Files.exists(CAMPUS_FILE)) {
                reader = new FileReader(CAMPUS_FILE.toFile());
            } else {
                InputStream is = getClass().getResourceAsStream("/data/campus.json");
                if (is == null) return;
                reader = new InputStreamReader(is);
            }

            Gson gson = new Gson();
            CampusDto dto = gson.fromJson(reader, CampusDto.class);
            reader.close();

            if (dto != null) {
                if (dto.buildings != null) {
                    for (BuildingDto b : dto.buildings) {
                        buildings.put(b.id, new Building.Builder().id(b.id).name(b.name).x(b.x).y(b.y).build());
                        internalGraph.addVertex(b.id, b.name);
                    }
                }
                if (dto.edges != null) {
                    for (EdgeDto e : dto.edges) internalGraph.addEdge(e.source, e.target, e.weight);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveCampusData() {
        try {
            Files.createDirectories(CAMPUS_FILE.getParent());
            CampusDto dto = new CampusDto();
            dto.buildings = new ArrayList<>();
            dto.edges = new ArrayList<>();

            for (Building b : buildings.values()) {
                BuildingDto bDto = new BuildingDto();
                bDto.id = b.getId(); bDto.name = b.getName(); bDto.x = b.getX(); bDto.y = b.getY();
                dto.buildings.add(bDto);
            }

            // Lógica simple para extraer aristas del grafo
            Set<String> processed = new HashSet<>();
            for (String v : internalGraph.getVertexIds()) {
                for (Edge e : internalGraph.getEdgesFrom(v)) {
                    String hash = e.getFrom() + "-" + e.getTo();
                    if (!processed.contains(hash)) {
                        EdgeDto eDto = new EdgeDto();
                        eDto.source = e.getFrom(); eDto.target = e.getTo(); eDto.weight = e.getWeight();
                        dto.edges.add(eDto);
                        processed.add(hash);
                        processed.add(e.getTo() + "-" + e.getFrom());
                    }
                }
            }

            try (Writer writer = new FileWriter(CAMPUS_FILE.toFile())) {
                new GsonBuilder().setPrettyPrinting().create().toJson(dto, writer);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // --- DTOs auxiliares ---
    private static class CampusDto { List<BuildingDto> buildings; List<EdgeDto> edges; }
    private static class BuildingDto { String id, name; double x, y; }
    private static class EdgeDto { String source, target; double weight; }
}