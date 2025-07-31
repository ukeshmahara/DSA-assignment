import java.util.*;

public class SecureTransmission {
    private List<List<int[]>> graph;

    public SecureTransmission(int n, int[][] links) {
        graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] link : links) {
            int u = link[0], v = link[1], strength = link[2];
            graph.get(u).add(new int[]{v, strength});
            graph.get(v).add(new int[]{u, strength}); // because the graph is undirected
        }
    }

    public boolean canTransmit(int sender, int receiver, int maxStrength) {
        boolean[] visited = new boolean[graph.size()];
        return dfs(sender, receiver, maxStrength, visited);
    }

    private boolean dfs(int current, int target, int maxStrength, boolean[] visited) {
        if (current == target) return true;
        visited[current] = true;

        for (int[] neighbor : graph.get(current)) {
            int next = neighbor[0], strength = neighbor[1];
            if (!visited[next] && strength < maxStrength) {
                if (dfs(next, target, maxStrength, visited)) return true;
            }
        }
        return false;
    }
}
// 4.a answer