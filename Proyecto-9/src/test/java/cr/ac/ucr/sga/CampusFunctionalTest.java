package cr.ac.ucr.sga;

import cr.ac.ucr.sga.model.graph.Dijkstra;
import cr.ac.ucr.sga.model.graph.Graph;
import cr.ac.ucr.sga.model.graph.Traversals;
import cr.ac.ucr.sga.model.services.CampusService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CampusFunctionalTest {

    @Test
    void testCampusLoads() {
        CampusService service = CampusService.getInstance();

        assertTrue(service.getBuildings().size() > 0, "Debe haber edificios cargados");
        assertTrue(service.getAllEdges().size() > 0,   "Debe haber aristas cargadas");
    }

    @Test
    void testCampusNodeData() {
        CampusService service = CampusService.getInstance();

        //ttomar el primer edificio y verificar que tiene id y nombre
        var first = service.getBuildings().iterator().next();
        assertNotNull(first.getId(),   "El edificio debe tener id");
        assertNotNull(first.getName(), "El edificio debe tener nombre");
        assertTrue(service.containsBuilding(first.getId()), "containsBuilding debe funcionar");
    }

    @Test
    void testDijkstraOnCampus() {
        CampusService service = CampusService.getInstance();

        if (service.getBuildings().size() < 2) {
            System.out.println("Omitiendo prueba Dijkstra: menos de 2 edificios");
            return;
        }

        var ids = service.getBuildings().stream().map(b -> b.getId()).toList();
        String start = ids.get(0);
        String end   = ids.get(1);

        CampusService.DijkstraResult result = service.dijkstra(start);
        double distance = result.dist.getOrDefault(end, Double.POSITIVE_INFINITY);

        if (!Double.isInfinite(distance)) {
            assertTrue(distance >= 0, "La distancia debe ser no negativa");

            List<String> path = result.pathTo(end);
            assertFalse(path.isEmpty(), "El camino no debe estar vacío");
            assertEquals(start, path.get(0), "El camino debe empezar en el origen");
            assertEquals(end,   path.get(path.size() - 1), "El camino debe terminar en el destino");
        }
    }

    @Test
    void testBFSOnCampus() {
        CampusService service = CampusService.getInstance();

        if (service.getBuildings().isEmpty()) {
            System.out.println("Omitiendo prueba BFS: sin edificios");
            return;
        }

        String start = service.getBuildings().iterator().next().getId();
        List<String> traversal = service.bfs(start);

        assertNotNull(traversal, "El recorrido BFS no debe ser nulo");
        assertFalse(traversal.isEmpty(), "El recorrido BFS no debe estar vacío");
        assertEquals(start, traversal.get(0), "BFS debe comenzar en el nodo origen");
    }

    @Test
    void testDFSOnCampus() {
        CampusService service = CampusService.getInstance();

        if (service.getBuildings().isEmpty()) {
            System.out.println("Omitiendo prueba DFS: sin edificios");
            return;
        }

        String start = service.getBuildings().iterator().next().getId();
        List<String> traversal = service.dfs(start);

        assertNotNull(traversal, "El recorrido DFS no debe ser nulo");
        assertFalse(traversal.isEmpty(), "El recorrido DFS no debe estar vacío");
        assertEquals(start, traversal.get(0), "DFS debe comenzar en el nodo origen");
    }

    @Test
    void testDijkstraPerformance() {
        CampusService service = CampusService.getInstance();

        if (service.getBuildings().size() < 2) {
            System.out.println("Omitiendo prueba de rendimiento: menos de 2 edificios");
            return;
        }

        String start = service.getBuildings().iterator().next().getId();

        long t0 = System.nanoTime();
        service.dijkstra(start);
        long ms = (System.nanoTime() - t0) / 1_000_000;

        System.out.println("Tiempo de ejecución de Dijkstra: " + ms + " ms");
        assertTrue(ms < 100, "Dijkstra debe ejecutarse en menos de 100ms");
    }
}