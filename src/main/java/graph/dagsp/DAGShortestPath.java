package graph.dagsp;

import graph.Edge;
import graph.topo.KahnTopo;
import util.Metrics;

import java.util.*;

public class DAGShortestPath {
    private final Metrics metrics;

    public DAGShortestPath(Metrics metrics) {
        this.metrics = metrics;
    }

    public Map<String, Double> shortestPath(Map<String, List<Edge>> adj, String src, Map<String, String> parent) {
        Map<String, List<String>> simple = new HashMap<>();
        for (var e : adj.entrySet()) {
            List<String> vs = new ArrayList<>();
            for (Edge ed : e.getValue()) vs.add(ed.getTo());
            simple.put(e.getKey(), vs);
        }

        List<String> topoOrder = new KahnTopo(metrics).sort(simple);

        Map<String, Double> dist = new HashMap<>();
        for (String u : adj.keySet()) dist.put(u, Double.POSITIVE_INFINITY);
        dist.put(src, 0.0);

        for (String u : topoOrder) {
            if (dist.get(u) == Double.POSITIVE_INFINITY) continue;
            for (Edge e : adj.getOrDefault(u, List.of())) {
                metrics.relaxations++;
                double newDist = dist.get(u) + e.getWeight();
                if (newDist < dist.get(e.getTo())) {
                    dist.put(e.getTo(), newDist);
                    parent.put(e.getTo(), u);
                }
            }
        }
        return dist;
    }

    public Map<String, Double> longestPath(Map<String, List<Edge>> adj, String src, Map<String, String> parent) {
        Map<String, List<String>> simple = new HashMap<>();
        for (var e : adj.entrySet()) {
            List<String> vs = new ArrayList<>();
            for (Edge ed : e.getValue()) vs.add(ed.getTo());
            simple.put(e.getKey(), vs);
        }

        List<String> topoOrder = new KahnTopo(metrics).sort(simple);

        Map<String, Double> dist = new HashMap<>();
        for (String u : adj.keySet()) dist.put(u, Double.NEGATIVE_INFINITY);
        dist.put(src, 0.0);

        for (String u : topoOrder) {
            if (dist.get(u) == Double.NEGATIVE_INFINITY) continue;
            for (Edge e : adj.getOrDefault(u, List.of())) {
                metrics.relaxations++;
                double newDist = dist.get(u) + e.getWeight();
                if (newDist > dist.get(e.getTo())) {
                    dist.put(e.getTo(), newDist);
                    parent.put(e.getTo(), u);
                }
            }
        }
        return dist;
    }

    public String getCriticalEnd(Map<String, Double> longestDist) {
        return longestDist.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public List<String> reconstructPath(Map<String, String> parent, String end) {
        LinkedList<String> path = new LinkedList<>();
        String cur = end;
        while (cur != null) {
            path.addFirst(cur);
            cur = parent.get(cur);
        }
        return path;
    }
}
