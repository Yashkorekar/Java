package CollectionsFramework;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Collections Framework - Map: HashMap, TreeMap operations (Q&A + runnable demos)
 *
 * Run:
 *   javac .\CollectionsFramework\MapHashMapTreeMapQnA.java
 *   java CollectionsFramework.MapHashMapTreeMapQnA
 */
public class MapHashMapTreeMapQnA {

    /*
     * =============================
     * 1) What is Map?
     * =============================
     * Map stores key -> value pairs.
     * - Keys are unique.
     * - Values can be duplicated.
     *
     * Key operations:
     * - put, get, remove
     * - containsKey, containsValue
     * - keySet, values, entrySet
     * - putIfAbsent, computeIfAbsent, compute, merge
     */

    /*
     * =============================
     * 2) HashMap quick facts
     * =============================
     * - Average O(1) for get/put/remove.
     * - Allows ONE null key and multiple null values.
     * - Iteration order is not guaranteed.
     *
     * Interview traps:
     * - Keys rely on equals/hashCode.
     * - Mutating a key after insertion breaks lookups.
     */

    /*
     * =============================
     * 3) TreeMap quick facts
     * =============================
     * - Sorted by key (natural ordering or provided Comparator).
     * - O(log n) for get/put/remove.
     * - Null keys not allowed with natural ordering (NPE).
     * - NavigableMap methods: floorEntry, ceilingEntry, higherEntry, lowerEntry, subMap.
     */

    static void hashMapBasics() {
        System.out.println("=== HashMap basics ===");

        Map<String, Integer> map = new HashMap<>();
        System.out.println(map.put("A", 1));
        System.out.println(map.put("A", 2));
        // output:
        // null (no previous)
        // 1 (previous value)

        System.out.println(map.get("A"));
        System.out.println(map.get("MISSING"));
        // output:
        // 2
        // null

        map.put(null, 99);
        map.put("B", null);
        System.out.println(map.get(null));
        System.out.println(map.get("B"));
        // output:
        // 99
        // null

        System.out.println(map.containsKey("A"));
        System.out.println(map.containsValue(2));
        // output:
        // true
        // true

        System.out.println(map.remove("A"));
        System.out.println(map.containsKey("A"));
        // output:
        // 2
        // false

        System.out.println(map);
        // output: order not guaranteed
    }

    static void computeIfAbsentDemo() {
        System.out.println("\n=== putIfAbsent vs computeIfAbsent ===");

        Map<String, String> cache = new HashMap<>();

        cache.putIfAbsent("k1", "v1");
        cache.putIfAbsent("k1", "v2");
        System.out.println(cache.get("k1"));
        // output: v1

        String v = cache.computeIfAbsent("k2", k -> "gen-" + k);
        System.out.println(v);
        System.out.println(cache.get("k2"));
        // output:
        // gen-k2
        // gen-k2

        // Interview note:
        // - computeIfAbsent computes ONLY if key is absent.
        // - If mapping function returns null, no mapping is recorded.
    }

    static final class Key {
        private String id;

        Key(String id) {
            this.id = Objects.requireNonNull(id);
        }

        void setId(String id) {
            this.id = Objects.requireNonNull(id);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key other)) return false;
            return id.equals(other.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public String toString() {
            return "Key{" + id + "}";
        }
    }

    static void hashMapKeyMutationTrap() {
        System.out.println("\n=== HashMap key mutation trap ===");

        Map<Key, String> map = new HashMap<>();
        Key k = new Key("K1");
        map.put(k, "value");

        System.out.println(map.get(new Key("K1")));
        // output: value

        k.setId("K2");

        System.out.println(map.get(new Key("K1")));
        System.out.println(map.get(new Key("K2")));
        System.out.println("size=" + map.size());
        // output (typical):
        // null
        // null
        // size=1
    }

    static void treeMapBasics() {
        System.out.println("\n=== TreeMap basics ===");

        NavigableMap<Integer, String> tm = new TreeMap<>();
        tm.put(3, "C");
        tm.put(1, "A");
        tm.put(2, "B");
        System.out.println(tm);
        // output: {1=A, 2=B, 3=C}

        System.out.println(tm.firstEntry());
        System.out.println(tm.lastEntry());
        // output:
        // 1=A
        // 3=C

        System.out.println(tm.floorEntry(2));
        System.out.println(tm.ceilingEntry(2));
        System.out.println(tm.lowerEntry(2));
        System.out.println(tm.higherEntry(2));
        // output:
        // 2=B
        // 2=B
        // 1=A
        // 3=C

        System.out.println(tm.subMap(1, true, 3, false));
        // output: {1=A, 2=B}

        try {
            tm.put(null, "X");
        } catch (NullPointerException ex) {
            System.out.println("TreeMap null key blocked: NullPointerException");
        }
        // output: TreeMap null key blocked: NullPointerException
    }

    public static void main(String[] args) {
        hashMapBasics();
        computeIfAbsentDemo();
        hashMapKeyMutationTrap();
        treeMapBasics();

        // Extra interview note:
        // - Iteration over entrySet is usually the fastest way to traverse a map.
        // - Modifying map during for-each iteration causes ConcurrentModificationException.
    }
}
