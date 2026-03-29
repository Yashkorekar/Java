# Behavioral Patterns Part 2

This section covers:
- Mediator
- Memento
- Observer

## Mediator

### Intent
- Define an object that centralizes how a set of objects interact, reducing direct dependencies between them.

### Core problem
- Many components talk to each other directly, and the dependency graph becomes tangled.

### Example idea
- A checkout workflow where pricing, shipping, coupon, and inventory widgets coordinate through a mediator instead of talking to each other directly.

### Pros
- Reduces many-to-many coupling.
- Centralizes coordination logic.
- Easier to change interaction rules in one place.

### Tradeoffs
- The mediator can become too large.
- Hidden complexity may move into the central coordinator.

### Mediator vs Observer
- Mediator centralizes interaction logic.
- Observer broadcasts change notifications to subscribers.
- Mediator is about coordination.
- Observer is about notification.

### Minimal Java implementation
```java
interface CheckoutMediator {
	void notify(String source, String value);
}

final class CheckoutPage implements CheckoutMediator {
	private String couponCode;
	private String shippingMethod;

	public void notify(String source, String value) {
		if ("coupon".equals(source)) {
			couponCode = value;
		}
		if ("shipping".equals(source)) {
			shippingMethod = value;
		}
		System.out.println("recalculate with " + couponCode + ", " + shippingMethod);
	}
}
```

## Memento

### Intent
- Capture and externalize an object's internal state so it can be restored later without violating encapsulation.

### Common use cases
- Undo and redo.
- Checkpoints.
- Draft restore.
- Snapshot-based rollback in small stateful models.

### Pros
- Supports state restoration cleanly.
- Keeps snapshot handling outside the core object logic.

### Tradeoffs
- Snapshots may consume memory.
- Large object graphs make snapshotting expensive.
- Care is needed to avoid exposing too much internal state.

### Practical note
- Immutable state objects often make Memento-like designs much simpler.

### Minimal Java implementation
```java
record EditorMemento(String text) {
}

final class TextEditor {
	private String text = "";

	void write(String value) {
		text = value;
	}

	EditorMemento save() {
		return new EditorMemento(text);
	}

	void restore(EditorMemento memento) {
		text = memento.text();
	}
}
```

## Observer

### Intent
- Define a one-to-many dependency so when one object changes state, all dependents are notified automatically.

### Core problem
- Multiple components must react to a state change, but the subject should not know their concrete details.

### Common examples
- Event listeners.
- UI updates.
- Domain event publishing.
- Cache invalidation notifications.

### Java and Spring relevance
- Spring application events.
- Message listeners.
- Reactive and event-driven styles often build on related concepts.

### Pros
- Loose coupling between publisher and subscribers.
- Easy to add new subscribers.
- Good fit for event-driven design.

### Tradeoffs
- Notification order may be unclear.
- Debugging event chains can be difficult.
- Synchronous observers can create latency and failure coupling.

### Interview trap
- Observer gives loose coupling, not guaranteed simplicity. A large event-driven flow can still be hard to reason about.

### Minimal Java implementation
```java
interface OrderListener {
	void onOrderCreated(String orderId);
}

final class EmailOrderListener implements OrderListener {
	public void onOrderCreated(String orderId) {
		System.out.println("email for order " + orderId);
	}
}

final class OrderEventPublisher {
	private final java.util.List<OrderListener> listeners = new java.util.ArrayList<>();

	void register(OrderListener listener) {
		listeners.add(listener);
	}

	void publishOrderCreated(String orderId) {
		for (OrderListener listener : listeners) {
			listener.onOrderCreated(orderId);
		}
	}
}
```

## Part 2 comparison map

### Mediator vs Facade
- Mediator coordinates peers.
- Facade simplifies access to a subsystem.

### Observer vs Chain of Responsibility
- Observer notifies all interested subscribers.
- Chain typically stops when one handler finishes or the chain ends.

### Memento vs Prototype
- Memento stores state for later restoration.
- Prototype creates a new object by copying an existing one.

## Practical advice
- Use Mediator when peer-to-peer chatter is making the system messy.
- Use Observer when state changes naturally need subscribers.
- Use Memento when rollback or undo is a true requirement and snapshot cost is acceptable.
