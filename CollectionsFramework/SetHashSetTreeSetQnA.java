package CollectionsFramework;

import java.util.Arrays;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Collections Framework - Set: HashSet, TreeSet operations (Q&A + runnable demos)
 *
 * Run:
 *   javac .\CollectionsFramework\SetHashSetTreeSetQnA.java
 *   java CollectionsFramework.SetHashSetTreeSetQnA
 */
public class SetHashSetTreeSetQnA {

    /*
     * =============================
     * 1) What is Set?
     * =============================
     * Set is a collection that:
     * - does NOT allow duplicates
     * - has no positional index
     * - uses equals()/hashCode() (HashSet) or compareTo/Comparator (TreeSet) to decide uniqueness
     */

    /*
     * =============================
     * 2) HashSet quick facts
     * =============================
     * - Backed by a HashMap.
     * - add/contains/remove average O(1).
     * - Allows at most ONE null element.
     * - Iteration order is NOT guaranteed.
     *
     * Interview traps:
     * - If equals() is overridden, hashCode() MUST be consistent with it.
     * - Never mutate fields used by equals/hashCode while object is inside a HashSet.
     */

    /*
     * =============================
     * 3) TreeSet quick facts
     * =============================
     * - Sorted set (red-black tree).
     * - add/contains/remove O(log n).
     * - Does NOT allow null with natural ordering (throws NPE).
     * - Uniqueness is based on comparison (compareTo/Comparator returning 0).
     *
     * Interview traps:
     * - If compareTo is inconsistent with equals, TreeSet behavior can surprise you:
     *   elements that are "not equal" may still be treated as duplicates if compareTo returns 0.
     */

    static void hashSetBasics() {
        System.out.println("=== HashSet basics ===");

        Set<String> s = new HashSet<>();
        System.out.println(s.add("A"));
        System.out.println(s.add("A"));
        System.out.println(s.add("B"));
        // output:
        // true
        // false
        // true

        System.out.println(s.contains("A"));
        System.out.println(s.remove("A"));
        System.out.println(s.contains("A"));
        // output:
        // true
        // true
        // false

        s.add(null);
        System.out.println(s.contains(null));
        // output: true

        System.out.println(s);
        // output: order not guaranteed (example: [null, B])
    }

    static final class UserKey {
        private String id;

        UserKey(String id) {
            this.id = Objects.requireNonNull(id);
        }

        void setId(String id) {
            this.id = Objects.requireNonNull(id);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UserKey other)) return false;
            return id.equals(other.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public String toString() {
            return "UserKey{" + id + "}";
        }
    }

    static void hashSetMutationTrap() {
        System.out.println("\n=== HashSet key mutation trap ===");

        Set<UserKey> set = new HashSet<>();
        UserKey k = new UserKey("U1");
        set.add(k);

        System.out.println(set.contains(new UserKey("U1")));
        // output: true

        k.setId("U2"); // MUTATION AFTER INSERT (bad)

        System.out.println(set.contains(new UserKey("U1")));
        System.out.println(set.contains(new UserKey("U2")));
        System.out.println("size=" + set.size());
        // output (typical):
        // false
        // false (may be false because element is in wrong bucket)
        // size=1

        // Interview answer:
        // - Never mutate keys used in equals/hashCode while stored in HashSet/HashMap.
    }

    static void treeSetBasics() {
        System.out.println("\n=== TreeSet basics ===");

        NavigableSet<Integer> ts = new TreeSet<>(Arrays.asList(5, 1, 3, 3, 2));
        System.out.println(ts);
        // output: [1, 2, 3, 5]

        System.out.println(ts.first());
        System.out.println(ts.last());
        // output:
        // 1
        // 5

        System.out.println(ts.lower(3));
        System.out.println(ts.floor(3));
        System.out.println(ts.higher(3));
        System.out.println(ts.ceiling(3));
        // output:
        // 2
        // 3
        // 5
        // 3

        System.out.println(ts.subSet(2, true, 5, false));
        // output: [2, 3]

        try {
            ts.add(null);
        } catch (NullPointerException ex) {
            System.out.println("TreeSet null blocked: NullPointerException");
        }
        // output: TreeSet null blocked: NullPointerException
    }

    static final class PersonByName implements Comparable<PersonByName> {
        private final String name;
        private final int age;

        PersonByName(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public int compareTo(PersonByName o) {
            return this.name.compareTo(o.name); // compares ONLY by name
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PersonByName other)) return false;
            return age == other.age && Objects.equals(name, other.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }

        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }

    static void treeSetCompareToDuplicateTrap() {
        System.out.println("\n=== TreeSet duplicate trap (compareTo defines uniqueness) ===");

        TreeSet<PersonByName> people = new TreeSet<>();
        people.add(new PersonByName("Asha", 20));
        people.add(new PersonByName("Asha", 30));
        people.add(new PersonByName("Ravi", 25));

        System.out.println(people);
        // output:
        // [Asha(20), Ravi(25)]
        // Reason: compareTo returns 0 for same name -> TreeSet treats as duplicate.

        // Interview takeaway:
        // - In TreeSet/TreeMap, comparison must be consistent with equals to avoid surprises.
    }

    public static void main(String[] args) {
        hashSetBasics();
        hashSetMutationTrap();
        treeSetBasics();
        treeSetCompareToDuplicateTrap();
    }
}
