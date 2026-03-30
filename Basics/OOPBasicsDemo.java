package Basics;

public class OOPBasicsDemo {

    /*
     * Theory:
     * - A class is a blueprint; an object is one runtime instance of that blueprint.
     * - Constructors establish valid state when objects are created.
     * - Instance fields belong to each object; static fields belong to the class.
     * - Encapsulation means keeping invariants inside methods instead of exposing raw state.
     * - Field defaults exist; local variable defaults do not.
     */

    public static void main(String[] args) {
        System.out.println("=== OOP basics: classes, objects, constructors, this ===");

        constructorAndObjectStateDemo();
        encapsulationDemo();
        defaultFieldValuesDemo();
        referenceAliasingDemo();
        instanceVsClassStateDemo();
        interviewTakeaways();
        interviewTrapQuestions();
    }

    private static void constructorAndObjectStateDemo() {
        System.out.println("\n--- Constructors and object state ---");

        Student asha = new Student("Asha", 92);
        Student ravi = new Student("Ravi");

        System.out.println(asha.summary());
        System.out.println(ravi.summary());
    }

    private static void encapsulationDemo() {
        System.out.println("\n--- Encapsulation ---");

        Student student = new Student("Meera", 76);
        student.rename("Meera Sharma");
        student.setMarks(88);
        System.out.println(student.summary());

        try {
            student.setMarks(120);
        } catch (IllegalArgumentException ex) {
            System.out.println("invalid marks blocked: " + ex.getClass().getSimpleName());
        }
    }

    private static void defaultFieldValuesDemo() {
        System.out.println("\n--- Default field values ---");

        DefaultsBox box = new DefaultsBox();
        System.out.println("int default = " + box.count);
        System.out.println("boolean default = " + box.active);
        System.out.println("String default = " + box.label);
        System.out.println("local variables have no default value; they must be initialized before use.");
    }

    private static void referenceAliasingDemo() {
        System.out.println("\n--- Reference aliasing ---");

        Student original = new Student("Isha", 81);
        Student alias = original;

        alias.setMarks(95);

        System.out.println("original after alias mutation = " + original.summary());
        System.out.println("alias == original => " + (alias == original));
    }

    private static void instanceVsClassStateDemo() {
        System.out.println("\n--- Instance vs class state ---");

        Student first = new Student("Dev", 65);
        Student second = new Student("Nina", 70);

        first.rename("Dev Sharma");

        System.out.println("first = " + first.summary());
        System.out.println("second = " + second.summary());
        System.out.println("students created so far = " + Student.getCreatedCount());
    }

    private static void interviewTakeaways() {
        System.out.println("\n--- Interview takeaways ---");
        System.out.println("- An object has state (fields) and behavior (methods).");
        System.out.println("- Constructors initialize valid state.");
        System.out.println("- `this` refers to the current object and helps with constructor chaining and shadowed names.");
        System.out.println("- Encapsulation means protecting invariants instead of exposing fields freely.");
    }

    private static void interviewTrapQuestions() {
        System.out.println("\n--- Trap questions interviewers ask ---");
        System.out.println("Q: Does alias = original copy the whole object?");
        System.out.println("A: No. It copies the reference, so both names point to the same object.");
        System.out.println("Q: Do local variables get default values like fields?");
        System.out.println("A: No. Local variables must be initialized before use.");
        System.out.println("Q: Are constructors inherited?");
        System.out.println("A: No. Constructors belong to the class that declares them.");
        System.out.println("Q: Can a static method use this?");
        System.out.println("A: No. this exists only for an object instance.");
    }

    static final class DefaultsBox {
        int count;
        boolean active;
        String label;
    }

    static final class Student {
        private static int nextRollNumber = 1;
        private static int createdCount = 0;

        private final int rollNumber;
        private String name;
        private int marks;

        Student(String name) {
            this(name, 0);
        }

        Student(String name, int marks) {
            this.rollNumber = nextRollNumber++;
            createdCount++;
            rename(name);
            setMarks(marks);
        }

        void rename(String name) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("name must not be blank");
            }
            this.name = name.trim();
        }

        void setMarks(int marks) {
            if (marks < 0 || marks > 100) {
                throw new IllegalArgumentException("marks must be 0..100");
            }
            this.marks = marks;
        }

        String summary() {
            return "Student{roll=" + rollNumber + ", name='" + name + "', marks=" + marks + "}";
        }

        static int getCreatedCount() {
            return createdCount;
        }
    }
}