package Basics;

import java.util.Arrays;

public class ArraysDemo {

    public static void main(String[] args) {
        System.out.println("=== Arrays: declaration, traversal, basic ops ===");

        declarationAndInitialization();
        traversalExamples();
        basicOperations();
        reverseExamples();
        arraysUtilityChecklist();
    }

    private static void declarationAndInitialization() {
        System.out.println("\n--- Declaration & initialization ---");

        // 1) Declaration
        int[] a;      // preferred style
        int b[];      // valid, less common

        // 2) Allocation
        a = new int[5]; // default values: 0 for int
        b = new int[] { 1, 2, 3 };

        System.out.println("a length=" + a.length + ", values=" + Arrays.toString(a));
        System.out.println("b length=" + b.length + ", values=" + Arrays.toString(b));

        // 3) Multi-dimensional (array of arrays)
        int[][] grid = new int[2][3];
        grid[0][1] = 7;
        System.out.println("grid=" + Arrays.deepToString(grid));

        // 4) Jagged arrays (rows can have different lengths)
        int[][] jagged = new int[3][];
        jagged[0] = new int[] { 1 };
        jagged[1] = new int[] { 2, 3 };
        jagged[2] = new int[] { 4, 5, 6 };
        System.out.println("jagged=" + Arrays.deepToString(jagged));
    }

    private static void traversalExamples() {
        System.out.println("\n--- Traversal ---");

        int[] nums = { 10, 20, 30, 40 };

        // 1) Index-based loop
        System.out.print("index loop: ");
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i] + (i == nums.length - 1 ? "" : ", "));
        }
        System.out.println();

        // 2) Enhanced for-loop (read-only access to elements)
        System.out.print("enhanced loop: ");
        boolean first = true;
        for (int n : nums) {
            if (!first) {
                System.out.print(", ");
            }
            System.out.print(n);
            first = false;
        }
        System.out.println();

        // 3) Reverse traversal
        System.out.print("reverse traversal: ");
        for (int i = nums.length - 1; i >= 0; i--) {
            System.out.print(nums[i] + (i == 0 ? "" : ", "));
        }
        System.out.println();
    }

    private static void basicOperations() {
        System.out.println("\n--- Basic operations (max, min, sum) ---");

        int[] nums = { 3, -2, 7, 7, 0 };
        System.out.println("nums=" + Arrays.toString(nums));

        System.out.println("max=" + max(nums));
        System.out.println("min=" + min(nums));
        System.out.println("sum (long)=" + sumAsLong(nums));

        // Edge cases
        int[] empty = {};
        System.out.println("empty=" + Arrays.toString(empty));
        System.out.println("sum(empty)=" + sumAsLong(empty));

        try {
            max(empty);
        } catch (RuntimeException ex) {
            System.out.println("max(empty) throws: " + ex.getClass().getSimpleName());
        }

        try {
            min(null);
        } catch (RuntimeException ex) {
            System.out.println("min(null) throws: " + ex.getClass().getSimpleName());
        }
    }

    private static void reverseExamples() {
        System.out.println("\n--- Reverse (in-place and copy) ---");

        int[] nums = { 1, 2, 3, 4, 5 };
        System.out.println("original=" + Arrays.toString(nums));

        int[] reversedCopy = reversedCopy(nums);
        System.out.println("reversedCopy=" + Arrays.toString(reversedCopy));
        System.out.println("after reversedCopy, original still=" + Arrays.toString(nums));

        reverseInPlace(nums);
        System.out.println("after reverseInPlace=" + Arrays.toString(nums));

        // Edge cases
        int[] one = { 42 };
        reverseInPlace(one);
        System.out.println("one element reverse=" + Arrays.toString(one));

        int[] zero = {};
        reverseInPlace(zero);
        System.out.println("empty reverse=" + Arrays.toString(zero));
    }

    // ------------------------
    // From-scratch implementations
    // ------------------------

    static int max(int[] nums) {
        if (nums == null) {
            throw new NullPointerException("nums");
        }
        if (nums.length == 0) {
            throw new IllegalArgumentException("nums is empty");
        }
        int best = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] > best) {
                best = nums[i];
            }
        }
        return best;
    }

    static int min(int[] nums) {
        if (nums == null) {
            throw new NullPointerException("nums");
        }
        if (nums.length == 0) {
            throw new IllegalArgumentException("nums is empty");
        }
        int best = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] < best) {
                best = nums[i];
            }
        }
        return best;
    }

    /**
     * Use long to avoid int overflow when summing large arrays.
     */
    static long sumAsLong(int[] nums) {
        if (nums == null) {
            throw new NullPointerException("nums");
        }
        long sum = 0;
        for (int n : nums) {
            sum += n;
        }
        return sum;
    }

    static void reverseInPlace(int[] nums) {
        if (nums == null) {
            throw new NullPointerException("nums");
        }
        int left = 0;
        int right = nums.length - 1;
        while (left < right) {
            int tmp = nums[left];
            nums[left] = nums[right];
            nums[right] = tmp;
            left++;
            right--;
        }
    }

    static int[] reversedCopy(int[] nums) {
        if (nums == null) {
            throw new NullPointerException("nums");
        }
        int[] out = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            out[i] = nums[nums.length - 1 - i];
        }
        return out;
    }

    private static void arraysUtilityChecklist() {
        System.out.println("\n--- Interview checklist (java.util.Arrays + common gotchas) ---");

        System.out.println("Common Arrays methods:");
        System.out.println("- Arrays.toString(int[]) / Arrays.deepToString(Object[][])");
        System.out.println("- Arrays.sort(...), Arrays.parallelSort(...)");
        System.out.println("- Arrays.binarySearch(sortedArray, key)");
        System.out.println("- Arrays.copyOf(...), Arrays.copyOfRange(...)");
        System.out.println("- Arrays.equals(...), Arrays.deepEquals(...)");
        System.out.println("- Arrays.fill(array, value)");
        System.out.println("- Arrays.setAll(array, i -> ...), Arrays.parallelSetAll(...)");
        System.out.println("- Arrays.asList(T...) (NOTE: for primitives, int[] becomes one element!)");

        System.out.println("\nGotchas interviewers love:");
        System.out.println("- arrays are fixed-size; cannot resize (use ArrayList for dynamic)");
        System.out.println("- length is a field: arr.length (not length())");
        System.out.println("- default values: 0/false/null depending on type");
        System.out.println("- Arrays.binarySearch requires sorted input");
        System.out.println("- multi-dimensional arrays are arrays of arrays; jagged is allowed");
        System.out.println("- Arrays.asList has fixed size view (no add/remove), backed by original array");

        System.out.println("\nIf you need the full API surface, check the Arrays javadoc for your JDK version.");
    }
}
