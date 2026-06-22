package cr.ac.ucr.sga;

import cr.ac.ucr.sga.model.graph.Dijkstra;
import cr.ac.ucr.sga.model.graph.Graph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class DijkstraTest {

    @Test
    public void testDijkstraShortestPath() {
        Graph g = new Graph();
        g.addEdge("S","A",1);
        g.addEdge("S","B",4);
        g.addEdge("A","B",2);
        g.addEdge("A","C",5);
        g.addEdge("B","C",1);
        g.addEdge("C","T",3);

        Dijkstra.Result r = Dijkstra.compute(g, "S");

        assertEquals(0.0, r.distanceTo("S"));
        // Expected shortest distance S -> A -> B -> C = 1 + 2 + 1 = 4
        assertEquals(4.0, r.distanceTo("C"));

        List<String> path = r.pathTo("T");
        // path should not be empty and should start with S
        assertFalse(path.isEmpty());
        assertEquals("S", path.get(0));
    }
}


