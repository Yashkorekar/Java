package Basics;

import java.util.Locale;
import java.util.StringJoiner;

public class StringManipulationDemo {

    public static void main(String[] args) {
        System.out.println("=== String manipulation: concat, substring, reverse ===");

        demoConcatenation();
        demoSubstring();
        demoReverse();
        printInterviewChecklist();
    }

    private static void demoConcatenation() {
        System.out.println("\n--- Concatenation ---");

        String a = "Hello";
        String b = "World";

        // 1) + operator (compiler uses StringBuilder under the hood in many cases)
        String plus = a + " " + b + "!";
        System.out.println("+ : " + plus);

        // 2) concat (NPE if left side is null)
        String viaConcat = a.concat(" ").concat(b);
        System.out.println("concat: " + viaConcat);

        // 3) StringBuilder (preferred in loops)
        StringBuilder sb = new StringBuilder();
        sb.append(a).append(' ').append(b).append('!');
        System.out.println("StringBuilder: " + sb);

        // 4) StringJoiner (nice for delimited joins)
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        joiner.add("A").add("B").add("C");
        System.out.println("StringJoiner: " + joiner);

        // 5) valueOf / String.valueOf avoids NPE (turns null into "null")
        Object maybeNull = null;
        System.out.println("String.valueOf(null): " + String.valueOf(maybeNull));

        // Performance note (interview): repeated + inside loops creates many temporary objects.
        String slow = "";
        for (int i = 0; i < 5; i++) {
            slow = slow + i; // don’t do this for large loops
        }
        System.out.println("loop using + (toy): " + slow);

        StringBuilder fast = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            fast.append(i);
        }
        System.out.println("loop using StringBuilder: " + fast);
    }

    private static void demoSubstring() {
        System.out.println("\n--- Substring ---");

        String s = "interview";

        // In Java: beginIndex is inclusive, endIndex is exclusive.
        System.out.println("s.substring(0, 5) = " + s.substring(0, 5)); // inter
        System.out.println("s.substring(5) = " + s.substring(5));       // view

        // From-scratch implementation (index validation included)
        System.out.println("customSubstring(s, 0, 5) = " + customSubstring(s, 0, 5));
        System.out.println("customSubstring(s, 5, s.length()) = " + customSubstring(s, 5, s.length()));

        // Edge-case examples interviewers like
        System.out.println("empty substring: " + customSubstring(s, 3, 3));

        try {
            customSubstring(s, -1, 2);
        } catch (RuntimeException ex) {
            System.out.println("negative index throws: " + ex.getClass().getSimpleName());
        }

        try {
            customSubstring(s, 2, 50);
        } catch (RuntimeException ex) {
            System.out.println("end > length throws: " + ex.getClass().getSimpleName());
        }

        try {
            customSubstring(s, 7, 3);
        } catch (RuntimeException ex) {
            System.out.println("begin > end throws: " + ex.getClass().getSimpleName());
        }
    }

    private static void demoReverse() {
        System.out.println("\n--- Reverse ---");

        String word = "abcd";
        System.out.println("reverse(StringBuilder): " + new StringBuilder(word).reverse());
        System.out.println("reverse(chars scratch): " + reverseCharsScratch(word));

        // Unicode edge case: characters outside BMP use surrogate pairs in UTF-16.
        // Example: 😀 (U+1F600)
        String withEmoji = "A😀B";
        System.out.println("original: " + withEmoji);
        System.out.println("reverse(StringBuilder) [may break surrogate pairs]: " + new StringBuilder(withEmoji).reverse());
        System.out.println("reverse(code points) [safe]: " + reverseByCodePoints(withEmoji));

        // Another common interview point: null safety
        System.out.println("reverse(null) -> " + reverseCharsScratch(null));
    }

    // ------------------------
    // “From scratch” implementations
    // ------------------------

    /**
     * Custom substring: begin inclusive, end exclusive.
     * Mirrors String’s behavior: throws IndexOutOfBoundsException for invalid indices.
     */
    static String customSubstring(String input, int beginIndex, int endIndex) {
        if (input == null) {
            throw new NullPointerException("input");
        }
        int length = input.length();
        if (beginIndex < 0 || endIndex > length || beginIndex > endIndex) {
            throw new IndexOutOfBoundsException(
                    "begin=" + beginIndex + ", end=" + endIndex + ", length=" + length);
        }
        int newLength = endIndex - beginIndex;
        char[] out = new char[newLength];
        for (int i = 0; i < newLength; i++) {
            out[i] = input.charAt(beginIndex + i);
        }
        return new String(out);
    }

    /**
     * Reverse based on UTF-16 char units.
     * Fast and common, but can break surrogate pairs (emoji, some historic scripts).
     */
    static String reverseCharsScratch(String input) {
        if (input == null) {
            return null;
        }
        char[] chars = input.toCharArray();
        int left = 0;
        int right = chars.length - 1;
        while (left < right) {
            char tmp = chars[left];
            chars[left] = chars[right];
            chars[right] = tmp;
            left++;
            right--;
        }
        return new String(chars);
    }

    /**
     * Reverse using Unicode code points (handles surrogate pairs correctly).
     * Interview-ready answer when asked about “proper” reversing.
     */
    static String reverseByCodePoints(String input) {
        if (input == null) {
            return null;
        }
        int[] cps = input.codePoints().toArray();
        StringBuilder out = new StringBuilder(input.length());
        for (int i = cps.length - 1; i >= 0; i--) {
            out.appendCodePoint(cps[i]);
        }
        return out.toString();
    }

    private static void printInterviewChecklist() {
        System.out.println("\n--- Interview checklist (high-frequency String APIs) ---");

        // Not exhaustive: java.lang.String has many methods across Java versions.
        // This is a checklist of the ones that show up most often in interviews.
        System.out.println("Creation/literals: literal \"x\", new String(...), String.valueOf(...)");
        System.out.println("Length/inspect: length(), isEmpty(), isBlank() (Java 11+), charAt(i)");
        System.out.println("Compare: equals(), equalsIgnoreCase(), compareTo(), compareToIgnoreCase(), contentEquals()\n");

        System.out.println("Search: contains(), indexOf(), lastIndexOf(), startsWith(), endsWith(), regionMatches()");
        System.out.println("Substring/slice: substring(begin), substring(begin,end), subSequence(begin,end)");
        System.out.println("Split/join: split(regex), String.join(delim, ...), StringJoiner");
        System.out.println("Replace: replace(char,char), replace(CharSequence,CharSequence), replaceAll(regex), replaceFirst(regex)");
        System.out.println("Trim/strip: trim(), strip(), stripLeading(), stripTrailing() (Java 11+)");
        System.out.println("Case: toLowerCase(Locale), toUpperCase(Locale)");
        System.out.println("Other common: repeat(n) (Java 11+), lines() (Java 11+), format(...)");

        // Classic gotchas:
        System.out.println("\nGotchas interviewers love:");
        System.out.println("- String is immutable (every change creates a new String)");
        System.out.println("- == compares references, equals() compares contents");
        System.out.println("- null handling (NPE risks)" );
        System.out.println("- substring indices: begin inclusive, end exclusive; validate bounds");
        System.out.println("- performance: prefer StringBuilder in loops");
        System.out.println("- Unicode: charAt works on UTF-16 units; codePoints() for full Unicode characters");

        // Locale demo (case conversion can be tricky)
        String turkish = "I";
        System.out.println("\nLocale example: \"I\".toLowerCase(TR) = " + turkish.toLowerCase(new Locale("tr", "TR")));
        System.out.println("Locale example: \"I\".toLowerCase(EN) = " + turkish.toLowerCase(Locale.ENGLISH));

        System.out.println("\nIf you truly need the full list of String methods, open the JDK String javadoc for your Java version.");
    }
}
