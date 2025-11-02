package graph.topo;

import util.Metrics;
import java.util.*;

public class KahnTopo {
    private Metrics metrics;

    public KahnTopo(Metrics metrics) {
        this.metrics = metrics;
    }

    public List<String> sort(Map<String, List<String>> adj) {
        metrics.startTimer();

        Map<String, Integer> indeg = new HashMap<>();
        for (String u : adj.keySet()) indeg.putIfAbsent(u, 0);
        for (var e : adj.entrySet()) {
            for (String v : e.getValue()) {
                indeg.put(v, indeg.getOrDefault(v, 0) + 1);
            }
        }

        Deque<String> q = new ArrayDeque<>();
        for (var entry : indeg.entrySet()) {
            if (entry.getValue() == 0) {
                q.add(entry.getKey());
                metrics.kahnPushes++;
            }
        }

        List<String> order = new ArrayList<>();
        while (!q.isEmpty()) {
            String u = q.remove();
            metrics.kahnPops++;
            order.add(u);

            for (String v : adj.getOrDefault(u, List.of())) {
                indeg.put(v, indeg.get(v) - 1);
                if (indeg.get(v) == 0) {
                    q.add(v);
                    metrics.kahnPushes++;
                }
            }
        }

        metrics.stopTimer();

        if (order.size() != indeg.size())
            throw new IllegalStateException("Not a DAG");
        return order;
    }
}
