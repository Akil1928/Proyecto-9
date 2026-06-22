package cr.ac.ucr.sga;

import cr.ac.ucr.sga.model.graph.Dijkstra;
import cr.ac.ucr.sga.model.graph.Graph;
import cr.ac.ucr.sga.model.graph.Traversals;
import cr.ac.ucr.sga.model.services.CampusGraphService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas funcionales del servicio del campus.
 */
public class CampusFunctionalTest {

    @Test
    void testCampusGraphLoads() {
        CampusGraphService service = CampusGraphService.getInstance();
        Graph graph = service.getGraph();

        assertNotNull(graph, "El grafo no debe ser nulo");
        assertTrue(graph.vertexCount() > 0, "El grafo debe contener vértices");
        assertTrue(graph.edgeCount() > 0, "El grafo debe contener aristas");
    }

    @Test
    void testCampusNodesData() {
        CampusGraphService service = CampusGraphService.getInstance();

        assertTrue(service.getAllNodeIds().size() > 0, "Debe haber al menos un nodo");
        assertTrue(service.getAllNodeNames().size() > 0, "Debe haber nombres de nodos");

        // Verificar que se puede obtener ID por nombre
        String firstName = service.getAllNodeNames().get(0);
        String firstId = service.getNodeIdByName(firstName);
        assertNotNull(firstId, "Should find node by name");
    }

    @Test
    void testDijkstraOnCampus() {
        CampusGraphService service = CampusGraphService.getInstance();
        Graph graph = service.getGraph();

        if (graph.vertexCount() < 2) {
            System.out.println("Omitiendo prueba Dijkstra: no hay suficientes nodos");
            return;
        }

        String start = service.getAllNodeIds().stream().findFirst().get();
        String end = service.getAllNodeIds().stream().skip(1).findFirst().get();

        Dijkstra.Result result = Dijkstra.compute(graph, start);
        double distance = result.distanceTo(end);

        // Si hay ruta conectada, debe tener distancia finita
        if (!Double.isInfinite(distance)) {
            assertTrue(distance >= 0, "La distancia debe ser positiva");

            List<String> path = result.pathTo(end);
            assertTrue(path.size() > 0, "El camino no debe estar vacío");
            assertEquals(start, path.get(0), "El camino debe comenzar en el nodo origen");
            assertEquals(end, path.get(path.size() - 1), "El camino debe terminar en el nodo destino");
        }
    }

    @Test
    void testBFSOnCampus() {
        CampusGraphService service = CampusGraphService.getInstance();
        Graph graph = service.getGraph();

        if (graph.vertexCount() == 0) {
            System.out.println("Omitiendo prueba BFS: no hay nodos");
            return;
        }

        String start = service.getAllNodeIds().stream().findFirst().get();
        List<String> traversal = Traversals.bfs(graph, start);

        assertNotNull(traversal, "El recorrido BFS no debe ser nulo");
        assertTrue(traversal.contains(start), "El recorrido BFS debe contener el nodo origen");
    }

    @Test
    void testDFSOnCampus() {
        CampusGraphService service = CampusGraphService.getInstance();
        Graph graph = service.getGraph();

        if (graph.vertexCount() == 0) {
            System.out.println("Omitiendo prueba DFS: no hay nodos");
            return;
        }

        String start = service.getAllNodeIds().stream().findFirst().get();
        List<String> traversal = Traversals.dfs(graph, start);

        assertNotNull(traversal, "El recorrido DFS no debe ser nulo");
        assertTrue(traversal.contains(start), "El recorrido DFS debe contener el nodo origen");
    }

    @Test
    void testCampusDijkstraPerformance() {
        CampusGraphService service = CampusGraphService.getInstance();
        Graph graph = service.getGraph();

        if (graph.vertexCount() < 2) {
            System.out.println("Omitiendo prueba de rendimiento: no hay suficientes nodos");
            return;
        }

        String start = service.getAllNodeIds().stream().findFirst().get();

        long startTime = System.nanoTime();
        Dijkstra.Result result = Dijkstra.compute(graph, start);
        long endTime = System.nanoTime();

        long durationMs = (endTime - startTime) / 1_000_000;
        System.out.println("Tiempo de ejecución de Dijkstra: " + durationMs + " ms");

        // Dijkstra en grafo pequeño debe ejecutarse muy rápido (< 100ms)
        assertTrue(durationMs < 100, "Dijkstra debe ejecutarse en un tiempo razonable");
    }
}

