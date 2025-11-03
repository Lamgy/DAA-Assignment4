import com.google.gson.*;
import graph.Edge;
import graph.Graph;
import graph.scc.KosarajuSCC;
import graph.topo.KahnTopo;
import graph.dagsp.DAGShortestPath;
import util.Metrics;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    private static final String CSV_PATH = "results/results.csv";

    public static void main(String[] args) throws Exception {
        String dataDir = "data";
        System.out.println("=== DAG & SCC Analysis ===");

        if (!Files.exists(Path.of(CSV_PATH))) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_PATH))) {
                pw.println("dataset,totalTimeMs,elapsedMs,dfsVisits,dfsEdges,kahnPushes,kahnPops,relaxations");
            }
        }

        Files.list(Path.of(dataDir))
                .filter(p -> p.toString().endsWith(".json"))
                .sorted()
                .forEach(path -> {
                    System.out.println("\n--- Processing " + path.getFileName() + " ---");
                    try {
                        Graph g = readGraph(path.toString());
                        Metrics m = new Metrics();

                        long tStart = System.nanoTime();
                        m.startTimer();
                        runAlgorithms(g, m);
                        m.stopTimer();
                        long tEnd = System.nanoTime();
                        double totalMs = (tEnd - tStart) / 1_000_000.0;

                        appendMetricsCSV(path.getFileName().toString(), totalMs, m);

                    } catch (Exception e) {
                        System.err.println("Failed: " + e.getMessage());
                    }
                });
    }

    private static Graph readGraph(String path) throws Exception {
        try (FileReader r = new FileReader(path)) {
            JsonObject obj = JsonParser.parseReader(r).getAsJsonObject();
            int n = obj.get("n").getAsInt();
            List<String> nodes = new ArrayList<>();
            for (int i = 0; i < n; i++) nodes.add(String.valueOf(i));

            List<Edge> edges = new ArrayList<>();
            for (JsonElement e : obj.getAsJsonArray("edges")) {
                JsonObject eo = e.getAsJsonObject();
                edges.add(new Edge(
                        String.valueOf(eo.get("u").getAsInt()),
                        String.valueOf(eo.get("v").getAsInt()),
                        eo.get("w").getAsDouble()
                ));
            }
            return new Graph(0, nodes, edges);
        }
    }

    private static void runAlgorithms(Graph g, Metrics m) {
        KosarajuSCC scc = new KosarajuSCC(m);

        Map<String, List<String>> adj = new HashMap<>();
        for (String node : g.getNodes()) adj.put(node, new ArrayList<>());
        for (Edge e : g.getEdges()) adj.get(e.getFrom()).add(e.getTo());

        List<List<String>> comps = scc.compute(adj);
        Map<String, List<String>> dag = scc.buildCondensation(adj, comps);
        System.out.println("Condensation DAG: " + dag);

        KahnTopo topo = new KahnTopo(m);
        List<String> topoOrder = topo.sort(dag);
        System.out.println("Topological Order: " + topoOrder);

        Map<String, List<Edge>> weighted = new HashMap<>();
        for (String node : dag.keySet()) weighted.put(node, new ArrayList<>());
        for (Edge e : g.getEdges()) {
            String cu = findComp(comps, e.getFrom());
            String cv = findComp(comps, e.getTo());
            if (!cu.equals(cv)) weighted.get(cu).add(new Edge(cu, cv, e.getWeight()));
        }

        DAGShortestPath dsp = new DAGShortestPath(m);
        String source = findComp(comps, g.getNodes().get(0));

        Map<String, String> shortestParent = new HashMap<>();
        Map<String, Double> shortest = dsp.shortestPath(weighted, source, shortestParent);

        Map<String, String> longestParent = new HashMap<>();
        Map<String, Double> longest = dsp.longestPath(weighted, source, longestParent);

        String criticalEnd = longest.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(source);

        List<String> criticalPath = dsp.reconstructPath(longestParent, criticalEnd);

        System.out.println("\n=== Output ===");
        System.out.println("Critical path: " + String.join(" -> ", criticalPath));
        System.out.printf("Length: %.2f%n", longest.get(criticalEnd));

        System.out.println("\nShortest distances from " + source + ":");
        shortest.forEach((v, d) -> System.out.printf("  %-5s : %.2f%n", v, d));

        String target = shortest.entrySet()
                .stream()
                .filter(e -> !e.getKey().equals(source))
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (target != null) {
            List<String> sp = dsp.reconstructPath(shortestParent, target);
            System.out.println("\nOne optimal shortest path: " + String.join(" -> ", sp));
            System.out.printf("Total length: %.2f%n", shortest.get(target));
        }
    }

    private static String findComp(List<List<String>> comps, String node) {
        for (int i = 0; i < comps.size(); i++)
            if (comps.get(i).contains(node)) return "C" + i;
        return null;
    }

    private static void appendMetricsCSV(String dataset, double totalMs, Metrics m) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_PATH, true))) {
            pw.printf(Locale.US,
                    "%s,%.3f,%d,%d,%d,%d,%d,%d%n",
                    dataset, totalMs, m.elapsedMs(), m.dfsVisits, m.dfsEdges,
                    m.kahnPushes, m.kahnPops, m.relaxations
            );
        } catch (IOException e) {
            System.err.println("Failed to write CSV: " + e.getMessage());
        }
    }
}
