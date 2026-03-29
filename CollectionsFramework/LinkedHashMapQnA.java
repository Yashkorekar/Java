package CollectionsFramework;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Collections Framework - LinkedHashMap (Q&A + runnable demos)
 *
 * Run:
 *   javac .\CollectionsFramework\LinkedHashMapQnA.java
 *   java CollectionsFramework.LinkedHashMapQnA
 */
public class LinkedHashMapQnA {

    /*
     * =============================
     * 1) What does LinkedHashMap add?
     * =============================
     * - HashMap-style O(1) average lookup/update.
     * - Predictable iteration order.
     * - By default, preserves insertion order.
     * - Optional access-order mode is useful for LRU-style caches.
     */

    static void insertionOrderDemo() {
        System.out.println("=== LinkedHashMap insertion order ===");

        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("C", 3);
        map.put("A", 1);
        map.put("B", 2);
        map.put("A", 99); // updating value does not create a second key

        System.out.println(map);
        // output: {C=3, A=99, B=2}

        System.out.println("LinkedHashMap keeps insertion order by default.");
    }

    static void accessOrderDemo() {
        System.out.println("\n=== LinkedHashMap access order ===");

        LinkedHashMap<String, Integer> cache = new LinkedHashMap<>(16, 0.75f, true);
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);

        cache.get("A");
        cache.get("B");

        System.out.println(cache);
        // output: {C=3, A=1, B=2}

        System.out.println("In access-order mode, recently accessed entries move to the end.");
    }

    static void lruCacheDemo() {
        System.out.println("\n=== LRU cache pattern ===");

        LruCache<Integer, String> cache = new LruCache<>(3);
        cache.put(1, "A");
        cache.put(2, "B");
        cache.put(3, "C");
        cache.get(1);      // 1 becomes most recently used
        cache.put(4, "D"); // evicts 2

        System.out.println(cache);
        // output: {3=C, 1=A, 4=D}
    }

    public static void main(String[] args) {
        insertionOrderDemo();
        accessOrderDemo();
        lruCacheDemo();

        System.out.println("\nInterview notes:");
        System.out.println("- LinkedHashMap allows one null key and multiple null values, like HashMap.");
        System.out.println("- Use it when order matters but you still want near-HashMap performance.");
    }

    static final class LruCache<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;

        LruCache(int capacity) {
            super(capacity, 0.75f, true);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > capacity;
        }
    }
}