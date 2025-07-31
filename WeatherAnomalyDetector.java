public class WeatherAnomalyDetector {

    public static int countValidPeriods(int[] temperatureChanges, int lowThreshold, int highThreshold) {
        int count = 0;
        int n = temperatureChanges.length;

        for (int start = 0; start < n; start++) {
            int total = 0;
            for (int end = start; end < n; end++) {
                total += temperatureChanges[end];
                if (total >= lowThreshold && total <= highThreshold) {
                    count++;
                }
            }
        }

        return count;
    }

    public static void main(String[] args) {
        // Example 1
        int[] temperatureChanges1 = {3, -1, -4, 6, 2};
        int low1 = 2, high1 = 5;
        System.out.println("Output (Example 1): " + countValidPeriods(temperatureChanges1, low1, high1)); // Output: 3

        // Example 2
        int[] temperatureChanges2 = {-2, 3, 1, -5, 4};
        int low2 = -1, high2 = 2;
        System.out.println("Output (Example 2): " + countValidPeriods(temperatureChanges2, low2, high2)); // Output: 5
    }
}
// 2.a answer