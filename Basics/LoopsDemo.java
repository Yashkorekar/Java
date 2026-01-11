package Basics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LoopsDemo {

    public static void main(String[] args) {
        System.out.println("=== Loops: for, while, do-while, enhanced for ===");

        forLoopBasics();
        whileLoopBasics();
        doWhileBasics();
        enhancedForBasics();
        commonInterviewGotchas();
    }

    private static void forLoopBasics() {
        System.out.println("\n--- for loop ---");

        // Classic counting loop
        System.out.print("count 0..4: ");
        for (int i = 0; i < 5; i++) {
            System.out.print(i + (i == 4 ? "" : ", "));
        }
        System.out.println();

        // Decrement loop
        System.out.print("countdown 5..1: ");
        for (int i = 5; i >= 1; i--) {
            System.out.print(i + (i == 1 ? "" : ", "));
        }
        System.out.println();

        // break / continue
        System.out.print("skip evens, stop at 7: ");
        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) {
                continue; // skip evens
            }
            if (i == 9) {
                break; // stop early
            }
            System.out.print(i + " ");
        }
        System.out.println();

        // Nested loop + labeled break (interview favorite)
        int[][] matrix = { { 1, 2, 3 }, { 4, 5, 6 } };
        int target = 5;
        boolean found = false;

        search:
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[r].length; c++) {
                if (matrix[r][c] == target) {
                    found = true;
                    System.out.println("found " + target + " at r=" + r + ", c=" + c);
                    break search; // breaks out of both loops
                }
            }
        }

        if (!found) {
            System.out.println("target not found");
        }
    }

    private static void whileLoopBasics() {
        System.out.println("\n--- while loop ---");

        // Use while when the number of iterations is not known up front.
        int n = 12345;
        int sumDigits = 0;
        int temp = n;

        while (temp > 0) {
            int digit = temp % 10;
            sumDigits += digit;
            temp /= 10;
        }

        System.out.println("sum of digits of " + n + " = " + sumDigits);

        // Typical pattern: loop until condition becomes true
        int attempts = 0;
        int value = 1;
        while (value < 100) {
            value *= 2;
            attempts++;
        }
        System.out.println("doubled until >= 100: attempts=" + attempts + ", value=" + value);

        // Infinite loop pattern (always use a clear exit)
        int i = 0;
        while (true) {
            i++;
            if (i == 3) {
                System.out.println("breaking an infinite loop at i=" + i);
                break;
            }
        }
    }

    private static void doWhileBasics() {
        System.out.println("\n--- do-while loop ---");

        // do-while executes at least once.
        int x = 5;
        do {
            System.out.println("runs once even if condition is false; x=" + x);
            x++;
        } while (x < 5);

        // Common use case: menu prompt / input validation (even with no input, runs once)
        int guess = 0;
        int tries = 0;
        do {
            // Simulate guesses (no Scanner here to keep demo non-interactive)
            guess += 2;
            tries++;
        } while (guess < 6);

        System.out.println("simulated do-while guesses: tries=" + tries + ", last guess=" + guess);
    }

    private static void enhancedForBasics() {
        System.out.println("\n--- enhanced for (for-each) ---");

        int[] nums = { 10, 20, 30 };

        int sum = 0;
        for (int v : nums) {
            sum += v;
        }
        System.out.println("sum via enhanced-for = " + sum);

        // Gotcha: changing loop variable does NOT update the array.
        for (int v : nums) {
            v = v * 10; // only changes local variable v
        }
        System.out.println("after trying to modify v, nums still = [" + nums[0] + ", " + nums[1] + ", " + nums[2] + "]");

        // Correct way to modify array elements: index-based loop
        for (int i = 0; i < nums.length; i++) {
            nums[i] = nums[i] * 10;
        }
        System.out.println("after index modification, nums = [" + nums[0] + ", " + nums[1] + ", " + nums[2] + "]");
    }

    private static void commonInterviewGotchas() {
        System.out.println("\n--- Interview gotchas ---");

        System.out.println("1) Off-by-one: i < n vs i <= n");
        System.out.println("2) Scope: 'int i' in for-loop exists only inside the loop");
        System.out.println("3) Pre vs post increment: ++i vs i++ (matters in expressions)");

        // Example: pre vs post increment
        int a = 1;
        int b = a++; // b gets 1, a becomes 2
        int c = ++a; // a becomes 3, c gets 3
        System.out.println("a after ops=" + a + ", b=" + b + ", c=" + c);

        // Enhanced-for + collections removal gotcha
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        System.out.println("\n4) Removing from List while iterating");
        System.out.println("- Wrong: removing inside enhanced-for throws ConcurrentModificationException");
        System.out.println("- Right: use Iterator.remove()\n");

        // Right way:
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            int v = it.next();
            if (v % 2 == 1) {
                it.remove();
            }
        }
        System.out.println("after removing odds with iterator: " + list);

        System.out.println("\n5) Prefer enhanced-for for read-only traversal; prefer index loop when you need indices or in-place edits.");
    }
}
