package DataStructuresBasics;

import java.util.Arrays;

/**
 * Beginner-first array drills.
 *
 * Run:
 *   javac .\DataStructuresBasics\ArraysBasicsDemo.java
 *   java DataStructuresBasics.ArraysBasicsDemo
 */
public class ArraysBasicsDemo {

    public static void main(String[] args) {
        declarationAndInitialization();
        printingWays();
        traversalWays();
        commonOperations();
        twoDimensionalArrayDemo();
        commonInterviewChecklist();
    }

    private static void declarationAndInitialization() {
        System.out.println("=== Arrays: declaration and initialization ===");

        int[] numbers = new int[5];
        int[] marks = {90, 80, 70, 60};

        System.out.println("Default int array: " + Arrays.toString(numbers));
        System.out.println("Initialized array: " + Arrays.toString(marks));
        System.out.println("Length of marks array: " + marks.length);
        System.out.println();
    }

    private static void printingWays() {
        System.out.println("=== Arrays: ways to print ===");

        int[] values = {10, 20, 30, 40};

        System.out.println("1) Arrays.toString: " + Arrays.toString(values));

        System.out.print("2) Manual print using enhanced for loop: ");
        boolean first = true;
        for (int value : values) {
            if (!first) {
                System.out.print(" ");
            }
            System.out.print(value);
            first = false;
        }
        System.out.println();
        System.out.println();
    }

    private static void traversalWays() {
        System.out.println("=== Arrays: ways to iterate ===");

        int[] array = {5, 10, 15, 20};

        System.out.println("1) Direct access by index");
        System.out.println("index=0, value=" + array[0]);
        System.out.println("index=1, value=" + array[1]);
        System.out.println("index=2, value=" + array[2]);
        System.out.println("index=3, value=" + array[3]);
        System.out.println();

        System.out.println("2) Enhanced for loop");
        for (int value : array) {
            System.out.println("value=" + value);
        }
        System.out.println();

        System.out.println("3) Reverse traversal");
        for (int index = array.length - 1; index >= 0; index--) {
            System.out.println("index=" + index + ", value=" + array[index]);
        }
        System.out.println();
    }

    private static void commonOperations() {
        System.out.println("=== Arrays: common operations ===");

        int[] numbers = {4, 2, 9, 1, 7};
        System.out.println("Original array: " + Arrays.toString(numbers));

        int sum = 0;
        int max = numbers[0];
        for (int number : numbers) {
            sum += number;
            if (number > max) {
                max = number;
            }
        }
        System.out.println("Sum: " + sum);
        System.out.println("Max: " + max);

        int[] copied = Arrays.copyOf(numbers, numbers.length);
        Arrays.sort(copied);
        System.out.println("Sorted copy: " + Arrays.toString(copied));
        System.out.println("binarySearch(7): index=" + Arrays.binarySearch(copied, 7));

        reverseInPlace(numbers);
        System.out.println("Reversed original array: " + Arrays.toString(numbers));
        System.out.println();
    }

    private static void twoDimensionalArrayDemo() {
        System.out.println("=== Arrays: 2D array basics ===");

        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6}
        };

        System.out.println("Using Arrays.deepToString: " + Arrays.deepToString(matrix));

        System.out.println("Nested loop traversal:");
        for (int[] row : matrix) {
            for (int value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void reverseInPlace(int[] numbers) {
        int left = 0;
        int right = numbers.length - 1;
        while (left < right) {
            int temp = numbers[left];
            numbers[left] = numbers[right];
            numbers[right] = temp;
            left++;
            right--;
        }
    }

    private static void commonInterviewChecklist() {
        System.out.println("=== Arrays interview checklist ===");
        System.out.println("- Arrays are fixed-size.");
        System.out.println("- Array length is a field: arr.length");
        System.out.println("- Arrays.toString(...) is the easiest way to print 1D arrays.");
        System.out.println("- Arrays.deepToString(...) is used for 2D arrays.");
        System.out.println("- Arrays.sort(...) sorts the array.");
        System.out.println("- Arrays.binarySearch(...) should be used on sorted arrays.");
    }
}
