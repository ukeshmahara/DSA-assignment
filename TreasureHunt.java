import java.util.*;

public class TreasureHunt {
    
    static class State {
        int p1, p2, turn;

        State(int p1, int p2, int turn) {
            this.p1 = p1;
            this.p2 = p2;
            this.turn = turn;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof State)) return false;
            State s = (State) o;
            return p1 == s.p1 && p2 == s.p2 && turn % 2 == s.turn % 2;
        }

        @Override
        public int hashCode() {
            return Objects.hash(p1, p2, turn % 2);
        }
    }

    public static int treasureHunt(int[][] graph) {
        Queue<State> queue = new LinkedList<>();
        Set<State> visited = new HashSet<>();

        queue.add(new State(1, 2, 0));

        while (!queue.isEmpty()) {
            State current = queue.poll();

            int p1 = current.p1;
            int p2 = current.p2;
            int turn = current.turn;

            if (p1 == 0) return 1;           // Player 1 reached treasure
            if (p1 == p2) return 2;          // Player 2 caught Player 1

            State snapshot = new State(p1, p2, turn % 2);
            if (visited.contains(snapshot)) return 0;  // Draw due to loop
            visited.add(snapshot);

            if (turn > 1000) return 0;  // safety check

            if (turn % 2 == 0) {
                // Player 1's turn
                for (int neighbor : graph[p1]) {
                    queue.add(new State(neighbor, p2, turn + 1));
                }
            } else {
                // Player 2's turn (can't go to node 0)
                for (int neighbor : graph[p2]) {
                    if (neighbor != 0) {
                        queue.add(new State(p1, neighbor, turn + 1));
                    }
                }
            }
        }

        return 0;  // Draw
    }

    public static void main(String[] args) {
        int[][] graph = {
            {2, 5},       // 0
            {3},          // 1
            {0, 4, 5},    // 2
            {1, 4, 5},    // 3
            {2, 3},       // 4
            {0, 2, 3}     // 5
        };

        int result = treasureHunt(graph);
        System.out.println("Result: " + result);  // Expected: 0
    }
}
//4.b answer
