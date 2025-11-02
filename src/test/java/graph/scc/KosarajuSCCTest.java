package graph.scc;

import graph.Edge;
import graph.Graph;
import org.junit.jupiter.api.Test;
import util.Metrics;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class KosarajuSCCTest {

    @Test
    public void testSingleSCC_AllConnected() {
        List<String> nodes = List.of("0", "1", "2");
        List<Edge> edges = List.of(
                new Edge("0", "1", 1),
                new Edge("1", "2", 1),
                new Edge("2", "0", 1)
        );

        Graph g = new Graph(1, nodes, edges, true, null);
        KosarajuSCC scc = new KosarajuSCC(new Metrics());
        List<List<String>> comps = scc.compute(g.getAdjacencySimple());

        assertEquals(1, comps.size(), "All nodes should form one SCC");
        assertTrue(comps.get(0).containsAll(List.of("0", "1", "2")));
    }

    @Test
    public void testMultipleSCCs() {
        List<String> nodes = List.of("0", "1", "2", "3", "4");
        List<Edge> edges = List.of(
                new Edge("0", "1", 1),
                new Edge("1", "2", 1),
                new Edge("2", "0", 1),
                new Edge("3", "4", 1),
                new Edge("4", "3", 1)
        );

        Graph g = new Graph(2, nodes, edges, true, null);
        KosarajuSCC scc = new KosarajuSCC(new Metrics());
        List<List<String>> comps = scc.compute(g.getAdjacencySimple());

        assertEquals(2, comps.size(), "Should detect two SCCs");
    }

    @Test
    public void testIsolatedNode() {
        List<String> nodes = List.of("A", "B");
        List<Edge> edges = List.of(new Edge("A", "B", 1));
        Graph g = new Graph(3, nodes, edges, true, null);

        KosarajuSCC scc = new KosarajuSCC(new Metrics());
        List<List<String>> comps = scc.compute(g.getAdjacencySimple());

        assertEquals(2, comps.size(), "Should detect {A,B} with A→B but B isolated");
    }

    @Test
    public void testEmptyGraph() {
        List<String> nodes = new ArrayList<>();
        List<Edge> edges = new ArrayList<>();
        Graph g = new Graph(4, nodes, edges, true, null);

        KosarajuSCC scc = new KosarajuSCC(new Metrics());
        List<List<String>> comps = scc.compute(g.getAdjacencySimple());

        assertTrue(comps.isEmpty(), "Empty graph has no SCCs");
    }
}
