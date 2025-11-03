package graph.scc;

import util.Metrics;
import java.util.*;

public class KosarajuSCC {
    private final Metrics metrics;

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

    public Map<String, List<String>> buildCondensation(Map<String, List<String>> adj, List<List<String>> comps) {
        Map<String, String> nodeToComp = new HashMap<>();
        for (int i = 0; i < comps.size(); i++)
            for (String v : comps.get(i))
                nodeToComp.put(v, "C" + i);

        Map<String, List<String>> dag = new HashMap<>();
        for (int i = 0; i < comps.size(); i++) dag.put("C" + i, new ArrayList<>());

        for (var e : adj.entrySet()) {
            String u = e.getKey();
            for (String v : e.getValue()) {
                String cu = nodeToComp.get(u);
                String cv = nodeToComp.get(v);
                if (!cu.equals(cv) && !dag.get(cu).contains(cv)) {
                    dag.get(cu).add(cv);
                }
            }
        }
        return dag;
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
        for (String u : adj.keySet()) rev.put(u, new ArrayList<>());
        for (var e : adj.entrySet()) {
            String u = e.getKey();
            for (String v : e.getValue())
                rev.get(v).add(u);
        }
        return rev;
    }
}
