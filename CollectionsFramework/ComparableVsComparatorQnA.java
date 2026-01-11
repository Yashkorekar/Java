package CollectionsFramework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 * Collections Framework - Comparable vs Comparator (Q&A + runnable demos)
 *
 * Run:
 *   javac .\CollectionsFramework\ComparableVsComparatorQnA.java
 *   java CollectionsFramework.ComparableVsComparatorQnA
 */
public class ComparableVsComparatorQnA {

    /*
     * =============================
     * 1) Comparable vs Comparator
     * =============================
     * Comparable<T>:
     * - Defines a "natural ordering" inside the class via compareTo.
     * - Used by TreeSet/TreeMap if no Comparator is provided.
     *
     * Comparator<T>:
     * - External ordering strategy.
     * - Can define multiple different sort orders without changing the class.
     *
     * Interview points:
     * - compareTo/compare should be consistent with equals (recommended) to avoid surprises in TreeSet/TreeMap.
     * - handle nulls explicitly using Comparator.nullsFirst/nullsLast.
     */

    static final class Employee implements Comparable<Employee> {
        private final int id;
        private final String name;
        private final int salary;

        Employee(int id, String name, int salary) {
            this.id = id;
            this.name = name;
            this.salary = salary;
        }

        int getId() {
            return id;
        }

        String getName() {
            return name;
        }

        int getSalary() {
            return salary;
        }

        @Override
        public int compareTo(Employee other) {
            return Integer.compare(this.id, other.id); // natural order by id
        }

        @Override
        public String toString() {
            return "Emp{id=" + id + ", name=" + name + ", salary=" + salary + "}";
        }
    }

    static void sortingDemo() {
        System.out.println("=== Sorting demo ===");

        List<Employee> list = new ArrayList<>(Arrays.asList(
            new Employee(3, "Asha", 900),
            new Employee(1, "Ravi", 1200),
                new Employee(2, "Meera", 1200)
        ));

        list.sort(null); // uses Comparable (natural order)
        System.out.println(list);
        // output: sorted by id

        Comparator<Employee> byName = Comparator.comparing(Employee::getName);
        list.sort(byName);
        System.out.println(list);
        // output: sorted by name

        Comparator<Employee> bySalaryThenName =
                Comparator.comparingInt(Employee::getSalary)
                        .thenComparing(Employee::getName);
        list.sort(bySalaryThenName);
        System.out.println(list);
        // output: sorted by salary asc, then name asc

        list.sort(bySalaryThenName.reversed());
        System.out.println(list);
        // output: salary desc, then name desc
    }

    static void treeSetUniquenessTrap() {
        System.out.println("\n=== TreeSet uniqueness depends on comparison ===");

        Comparator<Employee> bySalaryOnly = Comparator.comparingInt(Employee::getSalary);

        TreeSet<Employee> set = new TreeSet<>(bySalaryOnly);
        set.add(new Employee(1, "Asha", 1000));
        set.add(new Employee(2, "Ravi", 1000));
        set.add(new Employee(3, "Meera", 900));

        System.out.println(set);
        // output (salary-only comparator treats 1000 as duplicate):
        // [Emp{id=3, name=Meera, salary=900}, Emp{id=1, name=Asha, salary=1000}]

        // Interview takeaway:
        // - In TreeSet/TreeMap, compare(a,b)==0 means "same key".
        // - If you want to keep both employees, comparator must include a tie-breaker (id).
    }

    static void comparatorNullHandlingDemo() {
        System.out.println("\n=== Comparator null handling ===");

        List<String> names = new ArrayList<>(Arrays.asList("B", null, "A"));

        names.sort(Comparator.nullsLast(Comparator.naturalOrder()));
        System.out.println(names);
        // output: [A, B, null]
    }

    public static void main(String[] args) {
        sortingDemo();
        treeSetUniquenessTrap();
        comparatorNullHandlingDemo();

        // Interview note:
        // - Java's List.sort uses TimSort and is stable (equal elements keep relative order).
    }
}
