package graph.dagsp;

import graph.Edge;
import graph.Graph;
import org.junit.jupiter.api.Test;
import util.Metrics;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class DAGspTest {

    @Test
    public void testDeterministicDAG() {
        List<String> nodes = List.of("0", "1", "2", "3");
        List<Edge> edges = List.of(
                new Edge("0", "1", 1),
                new Edge("1", "2", 2),
                new Edge("0", "2", 4),
                new Edge("2", "3", 3)
        );

        Graph g = new Graph(1, nodes, edges, true, "0");
        DAGShortestPath sp = new DAGShortestPath(new Metrics());

        Map<String, Double> shortest = sp.shortestPath(g.getAdjacencyList(), "0");
        Map<String, Double> longest = sp.longestPath(g.getAdjacencyList(), "0");

        assertEquals(0.0, shortest.get("0"));
        assertEquals(1.0, shortest.get("1"));
        assertEquals(3.0, shortest.get("2"));
        assertEquals(6.0, shortest.get("3"));

        assertEquals(0.0, longest.get("0"));
        assertEquals(1.0, longest.get("1"));
        assertEquals(4.0, longest.get("2"));
        assertEquals(7.0, longest.get("3"));
    }

    @Test
    public void testUnreachableNode() {
        List<String> nodes = List.of("A", "B", "C");
        List<Edge> edges = List.of(
                new Edge("A", "B", 1)
        );

        Graph g = new Graph(2, nodes, edges, true, "A");
        DAGShortestPath sp = new DAGShortestPath(new Metrics());
        Map<String, Double> dist = sp.shortestPath(g.getAdjacencyList(), "A");

        assertEquals(Double.POSITIVE_INFINITY, dist.get("C"), "Unreachable node should remain INF");
    }

    @Test
    public void testSingleNodeGraph() {
        List<String> nodes = List.of("X");
        List<Edge> edges = List.of();
        Graph g = new Graph(3, nodes, edges, true, "X");

        DAGShortestPath sp = new DAGShortestPath(new Metrics());
        Map<String, Double> dist = sp.shortestPath(g.getAdjacencyList(), "X");

        assertEquals(0.0, dist.get("X"), "Source should have distance 0");
    }

    @Test
    public void testNegativeWeights() {
        List<String> nodes = List.of("A", "B", "C");
        List<Edge> edges = List.of(
                new Edge("A", "B", -2),
                new Edge("B", "C", 3)
        );

        Graph g = new Graph(4, nodes, edges, true, "A");
        DAGShortestPath sp = new DAGShortestPath(new Metrics());
        Map<String, Double> dist = sp.shortestPath(g.getAdjacencyList(), "A");

        assertEquals(-2.0, dist.get("B"));
        assertEquals(1.0, dist.get("C"));
    }
}
