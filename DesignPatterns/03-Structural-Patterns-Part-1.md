# Structural Patterns Part 1

Structural patterns focus on how classes and objects are composed.

This section covers:
- Adapter
- Bridge
- Composite

## Adapter

### Intent
- Convert the interface of one class into another interface that the client expects.

### Core problem
- You want to reuse an existing class, but its API does not match what your code expects.

### When to use it
- Integrating third-party libraries.
- Migrating between old and new APIs.
- Wrapping infrastructure clients behind your own domain-friendly interface.

### Example idea
- Your application expects `PaymentGateway.charge()`.
- A vendor SDK exposes `VendorPayClient.makePayment()`.
- An adapter translates between them.

### Pros
- Reuses existing code.
- Isolates vendor-specific details.
- Helps with migrations and testing.

### Tradeoffs
- Adds an extra layer.
- Can hide a poor domain model if overused.

### Adapter vs Facade
- Adapter changes one interface into another.
- Facade simplifies a subsystem but does not primarily solve interface mismatch.

### Minimal Java implementation
```java
interface PaymentGateway {
	boolean charge(int amountCents);
}

final class VendorPayClient {
	String makePayment(int amountCents) {
		return "OK";
	}
}

final class VendorPayAdapter implements PaymentGateway {
	private final VendorPayClient client;

	VendorPayAdapter(VendorPayClient client) {
		this.client = client;
	}

	public boolean charge(int amountCents) {
		return "OK".equals(client.makePayment(amountCents));
	}
}
```

## Bridge

### Intent
- Decouple an abstraction from its implementation so both can vary independently.

### Core problem
- Two dimensions of variation exist, and inheritance would create a class explosion.

### Example idea
- Notification type varies: alert, reminder, report.
- Delivery mechanism varies: email, SMS, push.
- Instead of `EmailAlert`, `SmsAlert`, `PushAlert`, `EmailReminder`, and so on, Bridge separates abstraction from implementation.

### Structure
- Abstraction references an implementation interface.
- Refined abstractions extend the abstraction.
- Concrete implementations provide the platform-specific work.

### Pros
- Avoids combinatorial inheritance growth.
- Makes both sides independently extensible.
- Favors composition.

### Tradeoffs
- More abstractions and upfront design.
- Overkill if the second dimension of change is not real.

### Bridge vs Strategy
- Bridge separates abstraction and implementation dimensions in the object model.
- Strategy swaps one algorithm or behavior for a task.

### Minimal Java implementation
```java
interface MessageSender {
	void send(String subject, String body);
}

final class EmailSender implements MessageSender {
	public void send(String subject, String body) {
		System.out.println("EMAIL: " + subject + " -> " + body);
	}
}

final class SmsSender implements MessageSender {
	public void send(String subject, String body) {
		System.out.println("SMS: " + subject + " -> " + body);
	}
}

abstract class Notification {
	protected final MessageSender sender;

	Notification(MessageSender sender) {
		this.sender = sender;
	}

	abstract void notifyUser(String message);
}

final class AlertNotification extends Notification {
	AlertNotification(MessageSender sender) {
		super(sender);
	}

	void notifyUser(String message) {
		sender.send("ALERT", message);
	}
}
```

## Composite

### Intent
- Compose objects into tree structures so individual objects and groups can be treated uniformly.

### Core problem
- You have part-whole hierarchies.
- Clients want to work with both leaves and containers through the same interface.

### Common examples
- File systems with files and directories.
- UI component trees.
- Organization hierarchies.
- Expression trees.

### Shape
- Component interface.
- Leaf implementation.
- Composite that holds child components.

### Why it is useful
- Recursive structures become much easier to traverse.
- Client logic can treat one item and many items consistently.

### Pros
- Uniform API for leaf and group.
- Natural fit for recursive tree problems.
- Simplifies client code.

### Tradeoffs
- Sometimes the shared interface becomes too generic.
- Child-management methods may not make equal sense for leaves.

### Safe-design note
- Transparent Composite gives all methods to both leaf and composite for a uniform API.
- Safer Composite keeps child-management only on composite types.
- Uniformity is nice, but misleading APIs can also hurt.

### Minimal Java implementation
```java
interface MenuComponent {
	void render();
}

final class MenuItem implements MenuComponent {
	private final String label;

	MenuItem(String label) {
		this.label = label;
	}

	public void render() {
		System.out.println("Item: " + label);
	}
}

final class MenuGroup implements MenuComponent {
	private final java.util.List<MenuComponent> children = new java.util.ArrayList<>();
	private final String title;

	MenuGroup(String title) {
		this.title = title;
	}

	void add(MenuComponent component) {
		children.add(component);
	}

	public void render() {
		System.out.println("Group: " + title);
		for (MenuComponent child : children) {
			child.render();
		}
	}
}
```

## Part 1 comparison map

### Adapter vs Bridge
- Adapter usually helps after interfaces already differ.
- Bridge is usually a proactive design choice to separate dimensions of change.

### Composite vs Decorator
- Composite organizes objects into trees.
- Decorator wraps one object to add behavior.

### Adapter vs Proxy
- Adapter changes the interface.
- Proxy keeps the same interface and controls access or adds indirection.

## Java and Spring examples
- Adapter: wrapping external SDKs or repository APIs behind domain services.
- Bridge: notification system with independent message type and transport type.
- Composite: validation trees, rule trees, menu trees, ASTs, and UI structures.
