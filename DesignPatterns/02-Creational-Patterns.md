# Creational Patterns

Creational patterns solve problems around object creation. They matter when `new` is not the whole story anymore.

Common reasons creation becomes a design concern:
- the caller should not know the concrete class,
- construction has many steps or options,
- related products must be created together,
- cloning is easier than rebuilding,
- object lifecycle must be tightly controlled.

## Singleton

### Intent
- Ensure a class has exactly one accessible instance and provide a global access point to it.

### When to use it
- Shared configuration object with truly application-wide identity.
- Immutable service that is naturally singular.
- Expensive resource manager where duplicate instances would be harmful.

### When not to use it
- When callers just need one bean per container. In Spring, singleton scope already solves that.
- When state is mutable and global access will make behavior hard to test.
- When you really want dependency injection, not hidden access.

### Java notes
- Prefer enum singleton or initialization-on-demand holder for plain Java.
- Be careful about reflection, serialization, classloaders, and mutable state.

### Pros
- Controlled instance count.
- Centralized lifecycle.
- Easy access when DI is not available.

### Tradeoffs
- Hidden dependency.
- Harder testing.
- Can become global shared state.
- Often overused.

### Interview trap
- Many candidates say Singleton is always thread-safe. It is only thread-safe if implemented correctly.

### Good example
- Logger-like access pattern, though most modern systems rely on framework-managed injection instead.

### Minimal Java implementation
```java
final class AppConfig {
        private AppConfig() {
        }

        static AppConfig getInstance() {
                return Holder.INSTANCE;
        }

        String mode() {
                return "prod";
        }

        private static final class Holder {
                private static final AppConfig INSTANCE = new AppConfig();
        }
}
```

## Factory Method

### Intent
- Define an interface for creating an object, but let subclasses or specific creators decide which concrete implementation is returned.

### When to use it
- Creation logic varies by subtype.
- Clients should depend on abstraction, not concrete classes.
- A framework wants subclasses to plug in product creation.

### Structure
- Product interface or abstract type.
- Concrete products.
- Creator with a factory method.
- Concrete creators override that method.

### Pros
- Reduces direct coupling to concrete classes.
- Supports extension by adding new creators.
- Moves creation logic away from clients.

### Tradeoffs
- More classes.
- Sometimes unnecessary if a simple constructor or static factory is enough.

### Java ecosystem examples
- Parser factories.
- Spring bean creation hooks.
- Driver or client selection based on configuration.

### Factory Method vs simple static factory
- A static factory is just a factory function.
- Factory Method is a named pattern with subtype-driven creation or creator abstraction.

### Minimal Java implementation
```java
interface Notification {
        String send(String message);
}

final class EmailNotification implements Notification {
        public String send(String message) {
                return "email => " + message;
        }
}

final class SmsNotification implements Notification {
        public String send(String message) {
                return "sms => " + message;
        }
}

abstract class NotificationCreator {
        protected abstract Notification createNotification();

        String notifyUser(String message) {
                return createNotification().send(message);
        }
}

final class EmailCreator extends NotificationCreator {
        protected Notification createNotification() {
                return new EmailNotification();
        }
}
```

## Abstract Factory

### Intent
- Provide an interface for creating families of related or dependent objects without specifying their concrete classes.

### Typical use case
- You have multiple product families that must stay consistent.
- Example: UI family for desktop vs web, or cloud provider family for storage, queue, and secret clients.

### When it fits well
- Several products vary together.
- You want to swap an entire family with one configuration choice.
- Mixing families would be a correctness problem.

### Pros
- Ensures family consistency.
- Centralizes family selection.
- Makes environment-based product switching cleaner.

### Tradeoffs
- Adding a new product type is harder because all factories must change.
- Can be too heavy for small code.

### Factory Method vs Abstract Factory
- Factory Method usually creates one product through one creation hook.
- Abstract Factory creates multiple related products through one family-level abstraction.

### Minimal Java implementation
```java
interface StorageClient {
        String put(String fileName);
}

interface QueueClient {
        String publish(String eventName);
}

interface CloudFactory {
        StorageClient storage();
        QueueClient queue();
}

final class AwsFactory implements CloudFactory {
        public StorageClient storage() {
                return fileName -> "S3 stored " + fileName;
        }

        public QueueClient queue() {
                return eventName -> "SQS published " + eventName;
        }
}

final class AzureFactory implements CloudFactory {
        public StorageClient storage() {
                return fileName -> "Blob stored " + fileName;
        }

        public QueueClient queue() {
                return eventName -> "Service Bus published " + eventName;
        }
}

CloudFactory factory = new AwsFactory();
String fileResult = factory.storage().put("invoice.pdf");
String eventResult = factory.queue().publish("order-created");
```

## Builder

### Intent
- Separate complex object construction from the final object representation so the same construction process can create readable, valid objects.

### When to use it
- Many optional parameters exist.
- Constructor overloading becomes unreadable.
- You want immutable objects with validation before creation.
- Some fields are required and others are optional.

### Why Builder is so common in Java
- Java lacks named parameters.
- Long constructors are hard to read and easy to misuse.
- Builders improve readability and reduce parameter-order bugs.

### Common builder shape
```java
OrderRequest request = new OrderRequest.Builder("user-1", "item-9")
        .couponCode("NEW50")
        .priority(true)
        .timeoutMs(2_000)
        .build();
```

### Pros
- Readable object creation.
- Good support for immutability.
- Centralized validation during `build()`.
- Easier to evolve when optional fields grow.

### Tradeoffs
- Extra boilerplate.
- Overkill for tiny classes.
- Mutability can still leak into the builder if used carelessly.

### Interview trap
- Builder is not only for huge objects. It is for clarity and safe construction when the constructor API becomes awkward.

### Minimal Java implementation
```java
final class OrderRequest {
        private final String userId;
        private final String itemId;
        private final boolean priority;
        private final int timeoutMs;

        private OrderRequest(Builder builder) {
                this.userId = builder.userId;
                this.itemId = builder.itemId;
                this.priority = builder.priority;
                this.timeoutMs = builder.timeoutMs;
        }

        static final class Builder {
                private final String userId;
                private final String itemId;
                private boolean priority;
                private int timeoutMs = 1_000;

                Builder(String userId, String itemId) {
                        this.userId = userId;
                        this.itemId = itemId;
                }

                Builder priority(boolean priority) {
                        this.priority = priority;
                        return this;
                }

                Builder timeoutMs(int timeoutMs) {
                        this.timeoutMs = timeoutMs;
                        return this;
                }

                OrderRequest build() {
                        return new OrderRequest(this);
                }
        }
}
```

## Prototype

### Intent
- Create new objects by copying an existing prototype instead of building from scratch.

### When to use it
- Object creation is expensive.
- You want template-like preconfigured instances.
- The system needs to duplicate existing objects with some modifications.

### Key issue
- Deep copy vs shallow copy.
- Many real bugs come from copying references when you meant to copy state.

### Pros
- Can be faster than rebuilding complex objects.
- Useful when creation involves many preset values.
- Reduces subclass explosion in some cases.

### Tradeoffs
- Copy logic can become tricky.
- Cycles and nested mutable objects complicate deep copies.
- Java `Cloneable` is historically awkward and often avoided.

### Better Java approach
- Prefer explicit copy constructors, `copy()` methods, or immutable data structures over raw `clone()`.

### Minimal Java implementation
```java
final class DocumentTemplate {
        private final String title;
        private final java.util.List<String> sections;

        DocumentTemplate(String title, java.util.List<String> sections) {
                this.title = title;
                this.sections = new java.util.ArrayList<>(sections);
        }

        DocumentTemplate(DocumentTemplate other) {
                this(other.title, other.sections);
        }

        DocumentTemplate copy() {
                return new DocumentTemplate(this);
        }
}
```

## Creational pattern comparison map

### Singleton vs Builder
- Singleton controls how many instances exist.
- Builder controls how one instance is assembled.

### Factory Method vs Builder
- Factory chooses which concrete object to create.
- Builder manages step-by-step construction of one object.

### Factory Method vs Abstract Factory
- Factory Method: one product creation decision.
- Abstract Factory: related product family creation.

### Builder vs Prototype
- Builder creates by assembling.
- Prototype creates by copying.

## High-yield interview examples
- Payment provider selected by config: Factory Method.
- AWS vs Azure client families: Abstract Factory.
- HTTP request or complex DTO construction: Builder.
- Cloning a document template or game unit config: Prototype.
- One shared in-memory registry with strict lifecycle: Singleton.

## Practical advice
- In modern Spring applications, prefer dependency injection before reaching for Singleton manually.
- Use Builder very freely when constructors become noisy.
- Use Factory when creation logic is branching or when callers should not know implementation details.
- Use Abstract Factory only when product families are real, not hypothetical.
