package graph.scc;

import util.Metrics;
import java.util.*;

public class KosarajuSCC {
    private Metrics metrics;

    public KosarajuSCC(Metrics metrics) {
        this.metrics = metrics;
    }

    public List<List<String>> compute(Map<String, List<String>> adj) {
        metrics.startTimer();

        Set<String> visited = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        for (String v : adj.keySet()) {
            if (!visited.contains(v))
                dfs1(v, adj, visited, stack);
        }

        Map<String, List<String>> transposed = transpose(adj);

        visited.clear();
        List<List<String>> components = new ArrayList<>();

        while (!stack.isEmpty()) {
            String v = stack.pop();
            if (!visited.contains(v)) {
                List<String> comp = new ArrayList<>();
                dfs2(v, transposed, visited, comp);
                components.add(comp);
            }
        }

        metrics.stopTimer();
        return components;
    }

    private void dfs1(String u, Map<String, List<String>> adj, Set<String> visited, Deque<String> stack) {
        visited.add(u);
        metrics.dfsVisits++;
        for (String v : adj.getOrDefault(u, List.of())) {
            metrics.dfsEdges++;
            if (!visited.contains(v))
                dfs1(v, adj, visited, stack);
        }
        stack.push(u);
    }

    private void dfs2(String u, Map<String, List<String>> adj, Set<String> visited, List<String> comp) {
        visited.add(u);
        comp.add(u);
        for (String v : adj.getOrDefault(u, List.of())) {
            if (!visited.contains(v))
                dfs2(v, adj, visited, comp);
        }
    }

    private Map<String, List<String>> transpose(Map<String, List<String>> adj) {
        Map<String, List<String>> rev = new HashMap<>();
        for (String u : adj.keySet())
            rev.put(u, new ArrayList<>());

        for (var entry : adj.entrySet()) {
            String u = entry.getKey();
            for (String v : entry.getValue()) {
                rev.get(v).add(u);
            }
        }
        return rev;
    }
}
