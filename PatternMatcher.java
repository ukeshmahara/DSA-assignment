public class PatternMatcher {

    public static int maxRepetitions(String p1, int t1, String p2, int t2) {
        StringBuilder seqA = new StringBuilder();
        for (int i = 0; i < t1; i++) {
            seqA.append(p1);
        }

        String fullSeqA = seqA.toString();
        int count = 0;
        int index = 0;

        while (index < fullSeqA.length()) {
            int j = 0;
            for (int i = index; i < fullSeqA.length(); i++) {
                if (fullSeqA.charAt(i) == p2.charAt(j)) {
                    j++;
                    if (j == p2.length()) {
                        count++;
                        index = i + 1;
                        break;
                    }
                }
            }
            if (j < p2.length()) {
                break; // Can't match another p2
            }
        }

        return count / t2;
    }

    public static void main(String[] args) {
        String p1 = "bca";
        int t1 = 6;
        String p2 = "ba";
        int t2 = 3;

        int result = maxRepetitions(p1, t1, p2, t2);
        System.out.println("Output: " + result);  // Output: 1
    }
}
//3.a answer