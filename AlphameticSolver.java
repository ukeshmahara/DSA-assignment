import java.util.*;

public class AlphameticSolver {
    private String word1, word2, result;
    private Set<Character> letters = new LinkedHashSet<>();
    private Map<Character, Integer> charDigit = new HashMap<>();
    private boolean[] usedDigits = new boolean[10];

    public AlphameticSolver(String w1, String w2, String res) {
        this.word1 = w1;
        this.word2 = w2;
        this.result = res;

        for (char c : (w1 + w2 + res).toCharArray()) {
            letters.add(c);
        }
    }

    private boolean solve(int index) {
        if (index == letters.size()) {
            // All letters assigned, check if equation is valid
            int num1 = toNumber(word1);
            int num2 = toNumber(word2);
            int resNum = toNumber(result);

            if (num1 == -1 || num2 == -1 || resNum == -1) return false; // Leading zero check

            return (num1 + num2) == resNum;
        }

        char currentChar = (Character) letters.toArray()[index];

        for (int digit = 0; digit <= 9; digit++) {
            if (!usedDigits[digit]) {
                // Leading zero check:
                if (digit == 0 && (isLeadingChar(currentChar))) continue;

                usedDigits[digit] = true;
                charDigit.put(currentChar, digit);

                if (solve(index + 1)) return true;

                usedDigits[digit] = false;
                charDigit.remove(currentChar);
            }
        }
        return false;
    }

    private boolean isLeadingChar(char c) {
        return (word1.charAt(0) == c || word2.charAt(0) == c || result.charAt(0) == c);
    }

    private int toNumber(String word) {
        int num = 0;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!charDigit.containsKey(c)) return -1;
            int digit = charDigit.get(c);
            if (digit == 0 && i == 0 && word.length() > 1) return -1; // no leading zero
            num = num * 10 + digit;
        }
        return num;
    }

    public void printSolution() {
        System.out.println("Solution:");
        for (Map.Entry<Character, Integer> entry : charDigit.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        System.out.printf("%s + %s = %s\n", toNumber(word1), toNumber(word2), toNumber(result));
    }

    public static void main(String[] args) {
        // Example 1: STAR + MOON = NIGHT (adjust inputs as you want)
        AlphameticSolver solver = new AlphameticSolver("STAR", "MOON", "NIGHT");
        if (solver.solve(0)) {
            solver.printSolution();
        } else {
            System.out.println("No solution found.");
        }
    }
}
// 2.b answer
