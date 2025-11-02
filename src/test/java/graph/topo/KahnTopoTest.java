package graph.topo;

import graph.Edge;
import graph.Graph;
import org.junit.jupiter.api.Test;
import util.Metrics;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class KahnTopoTest {

    @Test
    public void testSimpleDAG() {
        List<String> nodes = List.of("A", "B", "C");
        List<Edge> edges = List.of(
                new Edge("A", "B", 1),
                new Edge("B", "C", 1)
        );

        Graph g = new Graph(1, nodes, edges, true, null);
        List<String> order = new KahnTopo(new Metrics()).sort(g.getAdjacencySimple());

        assertEquals(List.of("A", "B", "C"), order, "Topo order should be linear");
    }

    @Test
    public void testMultipleValidOrders() {
        List<String> nodes = List.of("A", "B", "C");
        List<Edge> edges = List.of(
                new Edge("A", "C", 1),
                new Edge("B", "C", 1)
        );

        Graph g = new Graph(2, nodes, edges, true, null);
        List<String> order = new KahnTopo(new Metrics()).sort(g.getAdjacencySimple());

        assertEquals("C", order.get(2));
        assertTrue(order.indexOf("A") < order.indexOf("C"));
        assertTrue(order.indexOf("B") < order.indexOf("C"));
    }

    @Test
    public void testDisconnectedGraph() {
        List<String> nodes = List.of("A", "B", "C", "D");
        List<Edge> edges = List.of(
                new Edge("A", "B", 1),
                new Edge("C", "D", 1)
        );

        Graph g = new Graph(3, nodes, edges, true, null);
        List<String> order = new KahnTopo(new Metrics()).sort(g.getAdjacencySimple());

        assertEquals(4, order.size(), "Should include all nodes");
        assertTrue(order.indexOf("A") < order.indexOf("B"));
        assertTrue(order.indexOf("C") < order.indexOf("D"));
    }

    @Test
    public void testCycleGraph() {
        List<String> nodes = List.of("A", "B");
        List<Edge> edges = List.of(
                new Edge("A", "B", 1),
                new Edge("B", "A", 1)
        );

        Graph g = new Graph(4, nodes, edges, true, null);
        KahnTopo topo = new KahnTopo(new Metrics());

        assertThrows(IllegalStateException.class, () -> {
            topo.sort(g.getAdjacencySimple());
        }, "KahnTopo should throw IllegalStateException for cyclic graphs");
    }

}
