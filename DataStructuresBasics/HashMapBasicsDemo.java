package DataStructuresBasics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Beginner-first HashMap drills.
 *
 * Run:
 *   javac .\DataStructuresBasics\HashMapBasicsDemo.java
 *   java DataStructuresBasics.HashMapBasicsDemo
 */
public class HashMapBasicsDemo {

    public static void main(String[] args) {
        declarationAndBasicOperations();
        iterationWays();
        frequencyMapExample();
        commonInterviewChecklist();
    }

    private static void declarationAndBasicOperations() {
        System.out.println("=== HashMap: declaration and basic operations ===");

        HashMap<String, Integer> studentMarks = new HashMap<>();

        studentMarks.put("Aman", 90);
        studentMarks.put("Neha", 85);
        studentMarks.put("Riya", 95);

        System.out.println("Initial map: " + studentMarks);

        studentMarks.put("Neha", 88); // update existing value
        System.out.println("After update: " + studentMarks);

        System.out.println("Marks of Aman: " + studentMarks.get("Aman"));
        System.out.println("Marks of Missing student: " + studentMarks.get("Missing"));
        System.out.println("Marks with default: " + studentMarks.getOrDefault("Missing", -1));

        System.out.println("Contains key Riya: " + studentMarks.containsKey("Riya"));
        System.out.println("Contains value 88: " + studentMarks.containsValue(88));

        studentMarks.putIfAbsent("Karan", 70);
        studentMarks.putIfAbsent("Aman", 100);
        System.out.println("After putIfAbsent: " + studentMarks);

        studentMarks.remove("Karan");
        System.out.println("After remove(Karan): " + studentMarks);

        studentMarks.put(null, 50);
        studentMarks.put("NullValueStudent", null);
        System.out.println("HashMap allows one null key and multiple null values: " + studentMarks);
        System.out.println();
    }

    private static void iterationWays() {
        System.out.println("=== HashMap: ways to iterate and print ===");

        HashMap<String, Integer> map = new HashMap<>();
        map.put("Math", 91);
        map.put("Science", 87);
        map.put("English", 93);

        System.out.println("Whole map using println: " + map);
        System.out.println();

        System.out.println("1) Iterate using keySet() and print both key and value");
        for (String key : map.keySet()) {
            System.out.println("key=" + key + ", value=" + map.get(key));
        }
        System.out.println();

        System.out.println("2) Iterate using values() only");
        for (Integer value : map.values()) {
            System.out.println("value=" + value);
        }
        System.out.println();

        System.out.println("3) Iterate using entrySet() - best common interview answer");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
        }
        System.out.println();

        System.out.println("4) Iterate using Iterator over entrySet()");
        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        iterator.forEachRemaining(entry -> {
            System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
        });
        System.out.println();

        System.out.println("5) Iterate using forEach lambda");
        map.forEach((key, value) -> System.out.println("key=" + key + ", value=" + value));
        System.out.println();
    }

    private static void frequencyMapExample() {
        System.out.println("=== HashMap: basic frequency map example ===");

        String[] names = {"java", "python", "java", "kafka", "python", "java"};
        Map<String, Integer> frequency = new HashMap<>();

        for (String name : names) {
            frequency.put(name, frequency.getOrDefault(name, 0) + 1);
        }

        System.out.println("Frequency map: " + frequency);
        for (Map.Entry<String, Integer> entry : frequency.entrySet()) {
            System.out.println(entry.getKey() + " appears " + entry.getValue() + " times");
        }
        System.out.println();
    }

    private static void commonInterviewChecklist() {
        System.out.println("=== HashMap interview checklist ===");
        System.out.println("- HashMap stores key-value pairs.");
        System.out.println("- Keys are unique, values can repeat.");
        System.out.println("- HashMap iteration order is not guaranteed.");
        System.out.println("- entrySet() is the usual best answer when asked to print key and value together.");
        System.out.println("- get(missingKey) returns null.");
        System.out.println("- HashMap allows one null key.");
        System.out.println("- Average get/put/remove complexity is O(1).");
    }
}
