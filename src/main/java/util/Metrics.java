package util;

public class Metrics {
    private long startNs, endNs;

    // SCC
    public long dfsVisits = 0;
    public long dfsEdges = 0;

    // Kahn
    public long kahnPushes = 0;
    public long kahnPops = 0;

    // dagsp
    public long relaxations = 0;

    public void startTimer() { startNs = System.nanoTime(); }
    public void stopTimer() { endNs = System.nanoTime(); }
    public long elapsedMs() { return (endNs - startNs) / 1_000_000; }

    @Override
    public String toString() {
        return String.format(
                "Time=%dms | DFS=%d/%d | Kahn Push/Pops=%d/%d | Relax=%d",
                elapsedMs(), dfsVisits, dfsEdges, kahnPushes, kahnPops, relaxations
        );
    }
}
