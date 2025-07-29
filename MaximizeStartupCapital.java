import java.util.*;

public class MaximizeStartupCapital {

    public static int maximizeCapital(int k, int c, int[] revenues, int[] investments) {
        int n = revenues.length;
        // Store all projects by investment needed
        PriorityQueue<int[]> minInvestmentHeap = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        // Max revenue heap for affordable projects
        PriorityQueue<Integer> maxRevenueHeap = new PriorityQueue<>(Collections.reverseOrder());

        for (int i = 0; i < n; i++) {
            minInvestmentHeap.offer(new int[]{investments[i], revenues[i]});
        }

        while (k > 0) {
            // Move all affordable projects to revenue heap
            while (!minInvestmentHeap.isEmpty() && minInvestmentHeap.peek()[0] <= c) {
                maxRevenueHeap.offer(minInvestmentHeap.poll()[1]);
            }

            if (maxRevenueHeap.isEmpty()) {
                break;
            }

            // Take the most profitable affordable project
            c += maxRevenueHeap.poll();
            k--;
        }

        return c;
    }

    public static void main(String[] args) {
        // Test Example 1
        int k1 = 2, c1 = 0;
        int[] revenues1 = {2, 5, 8};
        int[] investments1 = {0, 2, 3};
        System.out.println("Example 1 Output: " + maximizeCapital(k1, c1, revenues1, investments1));  // Should be 7

        // Test Example 2
        int k2 = 3, c2 = 1;
        int[] revenues2 = {3, 6, 10};
        int[] investments2 = {1, 3, 5};
        System.out.println("Example 2 Output: " + maximizeCapital(k2, c2, revenues2, investments2));  // Should be 19
    }
}
