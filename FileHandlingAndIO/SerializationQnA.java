package FileHandlingAndIO;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

/**
 * File Handling & IO - Object serialization/deserialization (Q&A + runnable demos)
 *
 * Topics covered:
 * - Object serialization/deserialization using ObjectOutputStream/ObjectInputStream
 * - transient fields
 * - NotSerializableException trap
 * - reference identity preservation in Object streams
 *
 * Run:
 *   javac .\FileHandlingAndIO\SerializationQnA.java
 *   java FileHandlingAndIO.SerializationQnA
 */
public class SerializationQnA {

    /*
     * Q: What is serialization?
     * A: Converting an object graph into a byte stream (e.g., to file/network).
     *
     * Q: What is deserialization?
     * A: Restoring objects from that byte stream.
     *
     * Q: What must a class do to be serializable?
     * A: Implement java.io.Serializable (a marker interface).
     *
     * Q: What does transient do?
     * A: Excludes a field from default serialization.
     *
     * Q: What is serialVersionUID?
     * A: A version identifier used during deserialization to check class compatibility.
     */

    static final class UserProfile implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String username;
        private final int age;
        private final LocalDate createdOn;

        // transient fields are not serialized
        private transient String password;

        UserProfile(String username, int age, String password) {
            this.username = username;
            this.age = age;
            this.createdOn = LocalDate.now();
            this.password = password;
        }

        @Override
        public String toString() {
            return "UserProfile{username='" + username + "', age=" + age + ", createdOn=" + createdOn + ", password=" + password + "}";
        }
    }

    static final class NonSerializableDependency {
        private final String name;

        NonSerializableDependency(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "NonSerializableDependency{" + name + "}";
        }
    }

    static final class BadSession implements Serializable {
        private static final long serialVersionUID = 1L;

        private final UserProfile user;

        // TRAP: This field is not Serializable, so default serialization will fail.
        private final NonSerializableDependency dependency;

        BadSession(UserProfile user, NonSerializableDependency dependency) {
            this.user = user;
            this.dependency = dependency;
        }
    }

    static final class FixedSession implements Serializable {
        private static final long serialVersionUID = 1L;

        private final UserProfile user;

        // Fix option: make it transient if it should not be serialized.
        private transient NonSerializableDependency dependency;

        FixedSession(UserProfile user, NonSerializableDependency dependency) {
            this.user = user;
            this.dependency = dependency;
        }
    }

    private static void serializeDeserializeBasic(Path file) throws IOException, ClassNotFoundException {
        System.out.println("=== Basic serialize/deserialize + transient demo ===");

        UserProfile before = new UserProfile("yash", 25, "secret-password");
        System.out.println("before => " + before);

        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file))) {
            oos.writeObject(before);
        }

        UserProfile after;
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file))) {
            after = (UserProfile) ois.readObject();
        }

        System.out.println("after  => " + after);
        // output: password becomes null because it's transient
    }

    private static void referenceIdentityDemo(Path file) throws IOException, ClassNotFoundException {
        System.out.println("\n=== Object stream preserves reference identity ===");

        UserProfile u = new UserProfile("asha", 30, "p@ss");

        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file))) {
            oos.writeObject(u);
            oos.writeObject(u); // same reference written twice
        }

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file))) {
            Object a = ois.readObject();
            Object b = ois.readObject();

            System.out.println("a == b => " + (a == b));
            // output: true (same object instance after deserialization)
        }

        // Interview note:
        // - If you want a "fresh copy" when writing the same object again after mutating it,
        //   you may need ObjectOutputStream.reset().
    }

    private static void notSerializableTrap(Path file) throws IOException {
        System.out.println("\n=== NotSerializableException trap ===");

        UserProfile user = new UserProfile("meera", 28, "pw");
        BadSession bad = new BadSession(user, new NonSerializableDependency("db-conn"));

        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file))) {
            oos.writeObject(bad);
            System.out.println("unexpected: wrote BadSession successfully");
        } catch (NotSerializableException e) {
            System.out.println("caught => " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        FixedSession fixed = new FixedSession(user, new NonSerializableDependency("db-conn"));
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file))) {
            oos.writeObject(fixed);
            System.out.println("fixed session serialized (dependency transient)");
        }
    }

    public static void main(String[] args) throws Exception {
        Path baseDir = Files.createTempDirectory("java_serialization_demo_");
        System.out.println("Working directory: " + baseDir.toAbsolutePath());

        Path file1 = baseDir.resolve("userprofile.bin");
        Path file2 = baseDir.resolve("identity.bin");
        Path file3 = baseDir.resolve("session.bin");

        serializeDeserializeBasic(file1);
        referenceIdentityDemo(file2);
        notSerializableTrap(file3);

        // Cleanup
        Files.deleteIfExists(file1);
        Files.deleteIfExists(file2);
        Files.deleteIfExists(file3);
        Files.deleteIfExists(baseDir);
    }
}
