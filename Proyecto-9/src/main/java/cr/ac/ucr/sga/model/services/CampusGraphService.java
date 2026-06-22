package cr.ac.ucr.sga.model.services;

import com.google.gson.*;
import cr.ac.ucr.sga.model.graph.Graph;
import cr.ac.ucr.sga.model.graph.Vertex;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

/**
 * Servicio singleton para cargar el grafo del campus desde un archivo JSON.
 * El JSON debe tener formato:
 * {
 *   "nodes": [{"id":"A", "name":"...", "x":100, "y":200}, ...],
 *   "edges": [{"from":"A", "to":"B", "weight":50.0}, ...]
 * }
 */
public class CampusGraphService {
    private static CampusGraphService instance;
    private Graph campusGraph;
    private Map<String, CampusNode> campusNodes;

    /**
     * Representa un nodo del campus con posición en Canvas.
     */
    public static class CampusNode {
        public String id;
        public String name;
        public double x;
        public double y;

        public CampusNode(String id, String name, double x, double y) {
            this.id = id;
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }

    private CampusGraphService() {
        loadCampusData();
    }

    public static synchronized CampusGraphService getInstance() {
        if (instance == null) {
            instance = new CampusGraphService();
        }
        return instance;
    }

    /**
     * Carga campus.json desde resources/data/.
     */
    private void loadCampusData() {
        this.campusGraph = new Graph();
        this.campusNodes = new HashMap<>();

        try {
            InputStream is = getClass().getResourceAsStream("/data/campus.json");
            if (is == null) {
                System.err.println("No se encontró campus.json en resources/data/");
                return;
            }

            Reader reader = new InputStreamReader(is);
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            // Cargar nodos
            JsonArray nodesArray = json.getAsJsonArray("nodes");
            if (nodesArray != null) {
                for (JsonElement nodeElem : nodesArray) {
                    JsonObject nodeObj = nodeElem.getAsJsonObject();
                    String id = nodeObj.get("id").getAsString();
                    String name = nodeObj.get("name").getAsString();
                    double x = nodeObj.get("x").getAsDouble();
                    double y = nodeObj.get("y").getAsDouble();

                    CampusNode campusNode = new CampusNode(id, name, x, y);
                    campusNodes.put(id, campusNode);
                    campusGraph.addVertex(id);
                }
            }

            // Cargar aristas
            JsonArray edgesArray = json.getAsJsonArray("edges");
            if (edgesArray != null) {
                for (JsonElement edgeElem : edgesArray) {
                    JsonObject edgeObj = edgeElem.getAsJsonObject();
                    String from = edgeObj.get("from").getAsString();
                    String to = edgeObj.get("to").getAsString();
                    double weight = edgeObj.get("weight").getAsDouble();

                    // Grafo no dirigido: agregar arista en ambas direcciones
                    campusGraph.addEdge(from, to, weight, false);
                }
            }

            System.out.println("Campus graph cargado: " + campusGraph.vertexCount() + " nodos, " + campusGraph.edgeCount() + " aristas.");

        } catch (Exception e) {
            System.err.println("Error cargando campus.json: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtiene el grafo del campus.
     */
    public Graph getGraph() {
        return campusGraph;
    }

    /**
     * Obtiene los datos de posición y nombre de los nodos.
     */
    public Map<String, CampusNode> getCampusNodes() {
        return campusNodes;
    }

    /**
     * Obtiene información de un nodo específico.
     */
    public CampusNode getNode(String id) {
        return campusNodes.get(id);
    }

    /**
     * Retorna lista de IDs de todos los nodos.
     */
    public List<String> getAllNodeIds() {
        return new ArrayList<>(campusNodes.keySet());
    }

    /**
     * Retorna lista de nombres de todos los nodos.
     */
    public List<String> getAllNodeNames() {
        List<String> names = new ArrayList<>();
        for (CampusNode node : campusNodes.values()) {
            names.add(node.name);
        }
        return names;
    }

    /**
     * Obtiene el ID del nodo por su nombre.
     */
    public String getNodeIdByName(String name) {
        for (CampusNode node : campusNodes.values()) {
            if (node.name.equals(name)) {
                return node.id;
            }
        }
        return null;
    }
}

