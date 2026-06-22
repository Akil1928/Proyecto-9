package cr.ac.ucr.sga;

import cr.ac.ucr.sga.model.graph.Graph;
import cr.ac.ucr.sga.model.graph.Traversals;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    @Test
    public void testBfsAndDfsSimple() {
        Graph g = new Graph();
        g.addEdge("A","B",1);
        g.addEdge("A","C",1);
        g.addEdge("B","D",1);
        g.addEdge("C","E",1);

        List<String> bfs = Traversals.bfs(g, "A");
        // possible BFS order: A,B,C,D,E
        assertEquals(5, bfs.size());
        assertEquals("A", bfs.get(0));

        List<String> dfs = Traversals.dfs(g, "A");
        assertEquals(5, dfs.size());
        assertEquals("A", dfs.get(0));
    }
}

