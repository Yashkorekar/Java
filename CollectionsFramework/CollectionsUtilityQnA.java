package CollectionsFramework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Collections Framework - Collections utility methods (Q&A + runnable demos)
 *
 * Run:
 *   javac .\CollectionsFramework\CollectionsUtilityQnA.java
 *   java CollectionsFramework.CollectionsUtilityQnA
 */
public class CollectionsUtilityQnA {

    /*
     * =============================
     * 1) High-frequency utility methods
     * =============================
     * - sort, binarySearch
     * - reverse, shuffle, rotate
     * - min, max, frequency, disjoint
     * - fill, copy
     * - emptyList, singletonList, nCopies
     */

    static void sortAndBinarySearchDemo() {
        System.out.println("=== sort + binarySearch ===");

        List<Integer> nums = new ArrayList<>(Arrays.asList(5, 1, 3, 7));
        Collections.sort(nums);
        System.out.println("sorted => " + nums);

        int foundIndex = Collections.binarySearch(nums, 3);
        int missingIndex = Collections.binarySearch(nums, 4);
        int insertionPoint = -missingIndex - 1;

        System.out.println("binarySearch(3) => " + foundIndex);
        System.out.println("binarySearch(4) => " + missingIndex + " (insertion point " + insertionPoint + ")");

        System.out.println("binarySearch requires sorted input; otherwise results are undefined.");
    }

    static void reverseShuffleRotateDemo() {
        System.out.println("\n=== reverse + shuffle + rotate ===");

        List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

        Collections.reverse(list);
        System.out.println("reverse => " + list);

        Collections.rotate(list, 1);
        System.out.println("rotate by 1 => " + list);

        Collections.shuffle(list);
        System.out.println("shuffle => " + list + " (order varies)");
    }

    static void minMaxFrequencyDisjointDemo() {
        System.out.println("\n=== min + max + frequency + disjoint ===");

        List<String> letters = Arrays.asList("B", "A", "B", "C");
        System.out.println("min => " + Collections.min(letters));
        System.out.println("max => " + Collections.max(letters));
        System.out.println("frequency(B) => " + Collections.frequency(letters, "B"));
        System.out.println("disjoint with [X, Y] => " + Collections.disjoint(letters, List.of("X", "Y")));
        System.out.println("disjoint with [C, Z] => " + Collections.disjoint(letters, List.of("C", "Z")));
    }

    static void fillCopyAndFactoryDemo() {
        System.out.println("\n=== fill + copy + factory helpers ===");

        List<String> dest = new ArrayList<>(Arrays.asList("x", "x", "x"));
        Collections.fill(dest, "seed");
        System.out.println("after fill => " + dest);

        Collections.copy(dest, Arrays.asList("A", "B", "C"));
        System.out.println("after copy => " + dest);

        try {
            Collections.copy(new ArrayList<>(Arrays.asList("only-one")), Arrays.asList("A", "B"));
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("copy into too-small destination throws => " + ex.getClass().getSimpleName());
        }

        System.out.println("emptyList => " + Collections.emptyList());
        System.out.println("singletonList => " + Collections.singletonList("one"));
        System.out.println("nCopies => " + Collections.nCopies(3, "hi"));
    }

    public static void main(String[] args) {
        sortAndBinarySearchDemo();
        reverseShuffleRotateDemo();
        minMaxFrequencyDisjointDemo();
        fillCopyAndFactoryDemo();

        System.out.println("\nInterview notes:");
        System.out.println("- `Collections` contains algorithms and wrappers; `Collection` is the interface hierarchy.");
        System.out.println("- `binarySearch` on unsorted data is a classic trap.");
    }
}