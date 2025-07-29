public class StrongPinChecker {

    public static int strongPinChanges(String pin) {
        int len = pin.length();
        boolean hasLower = false, hasUpper = false, hasDigit = false;
        int repeatChanges = 0;

        // Count character types and track repeats
        for (int i = 0; i < len; ) {
            char ch = pin.charAt(i);
            if (Character.isLowerCase(ch)) hasLower = true;
            if (Character.isUpperCase(ch)) hasUpper = true;
            if (Character.isDigit(ch)) hasDigit = true;

            // Count repeating sequences
            int j = i;
            while (j < len && pin.charAt(j) == ch) j++;
            int repeatLen = j - i;
            if (repeatLen >= 3) {
                repeatChanges += repeatLen / 3;
            }
            i = j;
        }

        int missingTypes = 0;
        if (!hasLower) missingTypes++;
        if (!hasUpper) missingTypes++;
        if (!hasDigit) missingTypes++;

        if (len < 6) {
            return Math.max(missingTypes, 6 - len);
        } else if (len <= 20) {
            return Math.max(missingTypes, repeatChanges);
        } else {
            int deleteCount = len - 20;
            int remainingRepeats = repeatChanges;
            return deleteCount + Math.max(missingTypes, remainingRepeats);
        }
    }

    // Test cases
    public static void main(String[] args) {
        System.out.println("X1! → " + strongPinChanges("X1!"));           // Output: 3
        System.out.println("123456 → " + strongPinChanges("123456"));     // Output: 2
        System.out.println("Aa1234! → " + strongPinChanges("Aa1234!"));   // Output: 0
        System.out.println("aaa111bbb → " + strongPinChanges("aaa111bbb")); // Output: 2
    }
}
// 1.b answer