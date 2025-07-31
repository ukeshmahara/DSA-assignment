public class MagicalWords {

    // Check if substring from start to end is a palindrome with odd length
    public static boolean isMagical(String s, int start, int end) {
        if ((end - start + 1) % 2 == 0) return false; // even length, not magical
        while (start < end) {
            if (s.charAt(start++) != s.charAt(end--)) return false;
        }
        return true;
    }

    // Main function to find max power combination
    public static int maxMagicalProduct(String M) {
        int n = M.length();
        java.util.List<int[]> magicalWords = new java.util.ArrayList<>();

        // Step 1: Find all odd-length palindromes by expanding from center
        for (int center = 0; center < n; center++) {
            int left = center, right = center;
            while (left >= 0 && right < n && M.charAt(left) == M.charAt(right)) {
                int length = right - left + 1;
                if (length % 2 == 1) {
                    magicalWords.add(new int[]{left, right, length});
                }
                left--;
                right++;
            }
        }

        int maxProduct = 0;

        // Step 2: Check all pairs for non-overlap and find max product
        for (int i = 0; i < magicalWords.size(); i++) {
            for (int j = i + 1; j < magicalWords.size(); j++) {
                int[] a = magicalWords.get(i);
                int[] b = magicalWords.get(j);
                if (a[1] < b[0] || b[1] < a[0]) { // No overlap
                    int product = a[2] * b[2];
                    maxProduct = Math.max(maxProduct, product);
                }
            }
        }

        return maxProduct;
    }

    // Test the program
    public static void main(String[] args) {
        System.out.println("Output for 'xyzyxabc': " + maxMagicalProduct("xyzyxabc")); // Output: 5
        System.out.println("Output for 'levelwowracecar': " + maxMagicalProduct("levelwowracecar")); // Output: 35
    }
}
// 3.b answer