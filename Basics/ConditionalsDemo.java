package Basics;

public class ConditionalsDemo {

    /*
     * Theory:
     * - if/else checks conditions from top to bottom and runs the first matching branch.
     * - The ternary operator is an expression, so it produces a value.
     * - switch is best for discrete equality checks, not arbitrary ranges.
     * - Classic switch can fall through; switch expressions use -> and do not fall through.
     * - A switch expression should be exhaustive so every input path yields a result.
     */

    public static void main(String[] args) {
        System.out.println("=== Conditionals: if-else, switch-case ===");

        ifElseBasics();
        ifElseCommonPatterns();
        switchClassic();
        switchExpressionModern();
        interviewGotchas();
        interviewTrapQuestions();
    }

    private static void ifElseBasics() {
        System.out.println("\n--- if / else basics ---");

        int score = 76;
        if (score >= 90) {
            System.out.println("grade = A");
        } else if (score >= 80) {
            System.out.println("grade = B");
        } else if (score >= 70) {
            System.out.println("grade = C");
        } else {
            System.out.println("grade = D");
        }

        boolean isAdult = true;
        boolean hasTicket = false;

        if (isAdult && hasTicket) {
            System.out.println("allow entry");
        } else {
            System.out.println("deny entry");
        }

        // Ternary operator (expression form of if/else)
        int a = 10;
        int b = 20;
        int max = (a > b) ? a : b;
        System.out.println("max via ternary = " + max);
    }

    private static void ifElseCommonPatterns() {
        System.out.println("\n--- Common interview patterns ---");

        // 1) Guard clause (fail fast)
        String maybeNull = valueFromCaller(null);
        if (maybeNull == null) {
            System.out.println("input is null");
        }

        // 2) Validation style
        String input = valueFromCaller("  hello  ");
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            System.out.println("input is empty after trim");
        } else {
            System.out.println("normalized input = '" + trimmed + "'");
        }

        // 3) Avoiding deep nesting by early returns
        int value = -5;
        System.out.println("abs(" + value + ") = " + abs(value));
    }

    private static int abs(int x) {
        if (x >= 0) {
            return x;
        }
        return -x;
    }

    private static void switchClassic() {
        System.out.println("\n--- switch (classic) ---");

        int day = 6;
        String dayName;

        switch (day) {
            case 1:
                dayName = "Mon";
                break;
            case 2:
                dayName = "Tue";
                break;
            case 3:
                dayName = "Wed";
                break;
            case 4:
                dayName = "Thu";
                break;
            case 5:
                dayName = "Fri";
                break;
            case 6:
                dayName = "Sat";
                break;
            case 7:
                dayName = "Sun";
                break;
            default:
                dayName = "Invalid";
        }

        System.out.println("dayName = " + dayName);

        // String switch (supported since Java 7)
        String role = "ADMIN";
        switch (role) {
            case "ADMIN":
            case "OWNER":
                System.out.println("full access");
                break;
            case "USER":
                System.out.println("limited access");
                break;
            default:
                System.out.println("unknown role");
        }

        // Demonstrate fall-through
        int n = 2;
        System.out.print("fall-through demo: ");
        switch (n) {
            case 1:
                System.out.print("one ");
                // fall-through
            case 2:
                System.out.print("two ");
                // fall-through
            case 3:
                System.out.print("three ");
                break;
            default:
                System.out.print("other ");
        }
        System.out.println();
    }

    private static void switchExpressionModern() {
        System.out.println("\n--- switch expression (modern Java) ---");

        // Switch expression (Java 14+) is an expression that returns a value.
        // Uses '->' labels, no fall-through.
        int month = 2;
        int days = switch (month) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11 -> 30;
            case 2 -> 28;
            default -> throw new IllegalArgumentException("invalid month: " + month);
        };
        System.out.println("days in month " + month + " = " + days);

        // Using 'yield' (when you need a block)
        int score = 85;
        String category = switch (score / 10) {
            case 10, 9 -> "excellent";
            case 8 -> {
                String msg = "great";
                yield msg;
            }
            case 7 -> "good";
            default -> "needs improvement";
        };
        System.out.println("score category = " + category);
    }

    private static void interviewGotchas() {
        System.out.println("\n--- Interview gotchas ---");

        System.out.println("if condition must be boolean (no 0/1 like C)");
        System.out.println("switch classic requires break to avoid fall-through");
        System.out.println("switch on String uses equals() semantics (case-sensitive)");

        boolean approved;
        if (approved = true) {
            System.out.println("assignment inside if compiles for booleans and is usually a bug");
        }
        System.out.println("approved after assignment-in-condition = " + approved);

        // Null handling in switch
        try {
            String s = valueFromCaller(null);
            switch (s) {
                case "A":
                    System.out.println("A");
                    break;
                default:
                    System.out.println("default");
            }
        } catch (NullPointerException ex) {
            System.out.println("switch on null throws NullPointerException");
        }

        // Prefer equals with constant on left to avoid NPE
        String maybeNull = null;
        System.out.println("safe equals: " + "x".equals(maybeNull));
    }

    private static void interviewTrapQuestions() {
        System.out.println("\n--- Trap questions interviewers ask ---");
        System.out.println("Q: Does switch on null go to default?");
        System.out.println("A: No. It throws NullPointerException before matching any case.");
        System.out.println("Q: Can switch use boolean or long in basic Java syntax?");
        System.out.println("A: No. Common switch types are byte, short, char, int, enum, String, and some wrappers.");
        System.out.println("Q: Which if does an else bind to?");
        System.out.println("A: The nearest unmatched if.");
        System.out.println("Q: Does assignment inside an if condition compile?");
        System.out.println("A: It does for boolean assignments, which is why it is a classic bug source.");
    }

    private static String valueFromCaller(String value) {
        return value;
    }
}
