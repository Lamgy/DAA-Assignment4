package graph.scc;

import util.Metrics;
import java.util.*;

public class KosarajuSCC {
    private Map<String, List<String>> adj;
    private Metrics metrics;

    public KosarajuSCC(Map<String, List<String>> adj, Metrics metrics) {
        this.adj = adj;
        this.metrics = metrics;
    }

    public List<List<String>> computeSCCs() {
        metrics.startTimer();

        Set<String> visited = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();

        for (String v : adj.keySet()) {
            if (!visited.contains(v)) dfs1(v, visited, stack);
        }

        Map<String, List<String>> rev = reverseGraph();

        visited.clear();
        List<List<String>> components = new ArrayList<>();

        while (!stack.isEmpty()) {
            String v = stack.pop();
            if (!visited.contains(v)) {
                List<String> comp = new ArrayList<>();
                dfs2(v, rev, visited, comp);
                components.add(comp);
            }
        }

        metrics.stopTimer();
        return components;
    }

    private void dfs1(String v, Set<String> vis, Deque<String> st) {
        metrics.dfsVisits++;
        vis.add(v);
        for (String w : adj.getOrDefault(v, List.of())) {
            metrics.dfsEdges++;
            if (!vis.contains(w)) dfs1(w, vis, st);
        }
        st.push(v);
    }

    private void dfs2(String v, Map<String, List<String>> rev, Set<String> vis, List<String> comp) {
        vis.add(v);
        comp.add(v);
        for (String w : rev.getOrDefault(v, List.of())) {
            if (!vis.contains(w)) dfs2(w, rev, vis, comp);
        }
    }

    private Map<String, List<String>> reverseGraph() {
        Map<String, List<String>> rev = new HashMap<>();
        for (String u : adj.keySet()) {
            for (String v : adj.get(u)) {
                rev.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
            }
        }
        return rev;
    }
}
