package graph;

import java.util.*;

public class Graph {
    private int id;
    private List<String> nodes;
    private List<Edge> edges;
    private boolean directed;
    private String source; // optional for DAG-SP

    // Constructor for undirected (default)
    public Graph(int id, List<String> nodes, List<Edge> edges) {
        this(id, nodes, edges, false, null);
    }

    // Constructor for directed graph
    public Graph(int id, List<String> nodes, List<Edge> edges, boolean directed, String source) {
        this.id = id;
        this.nodes = nodes;
        this.edges = edges;
        this.directed = directed;
        this.source = source;
    }

    public int getId() { return id; }
    public List<String> getNodes() { return nodes; }
    public List<Edge> getEdges() { return edges; }
    public boolean isDirected() { return directed; }
    public String getSource() { return source; }

    /**
     * Build adjacency list according to directed/undirected flag.
     */
    public Map<String, List<Edge>> getAdjacencyList() {
        Map<String, List<Edge>> adj = new HashMap<>();
        for (String node : nodes) adj.put(node, new ArrayList<>());

        for (Edge e : edges) {
            adj.get(e.getFrom()).add(e);
            if (!directed) {
                // add reverse edge for undirected
                adj.get(e.getTo()).add(new Edge(e.getTo(), e.getFrom(), e.getWeight()));
            }
        }
        return adj;
    }

    /**
     * Build adjacency list for SCC and Topological Sort (unweighted).
     */
    public Map<String, List<String>> getAdjacencySimple() {
        Map<String, List<String>> adj = new HashMap<>();
        for (String node : nodes) adj.put(node, new ArrayList<>());

        for (Edge e : edges) {
            adj.get(e.getFrom()).add(e.getTo());
            // only one direction for SCC/DAG
        }
        return adj;
    }
}
