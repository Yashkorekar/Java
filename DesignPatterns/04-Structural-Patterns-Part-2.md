# Structural Patterns Part 2

This section covers:
- Decorator
- Facade
- Flyweight
- Proxy

## Decorator

### Intent
- Attach additional responsibilities to an object dynamically without changing its class.

### Core problem
- You want to add behavior in combinations.
- Inheritance would create too many subclasses.

### Example idea
- Base notifier sends a message.
- Decorators add logging, retry, metrics, encryption, or throttling.

### Why it works well
- Each wrapper has the same interface as the wrapped object.
- Wrappers can be stacked in different combinations.

### Pros
- Flexible composition.
- Open for extension.
- Better than subclass explosion for optional behavior layers.

### Tradeoffs
- Debugging can be harder when many wrappers are stacked.
- Object graphs become more indirect.
- Ordering of decorators may matter.

### Java ecosystem examples
- `InputStream` wrappers such as `BufferedInputStream`.
- Many middleware or filter-like wrappers.

### Minimal Java implementation
```java
interface Notifier {
	void send(String message);
}

final class EmailNotifier implements Notifier {
	public void send(String message) {
		System.out.println("Email => " + message);
	}
}

abstract class NotifierDecorator implements Notifier {
	protected final Notifier delegate;

	NotifierDecorator(Notifier delegate) {
		this.delegate = delegate;
	}
}

final class LoggingNotifier extends NotifierDecorator {
	LoggingNotifier(Notifier delegate) {
		super(delegate);
	}

	public void send(String message) {
		System.out.println("log => " + message);
		delegate.send(message);
	}
}
```

## Facade

### Intent
- Provide a simple unified interface to a larger and more complex subsystem.

### Core problem
- The subsystem is useful but too complex or noisy for most callers.

### Example idea
- `OrderFacade.placeOrder()` coordinates inventory, pricing, payment, and notification services.

### Pros
- Simplifies usage.
- Reduces coupling to subsystem details.
- Creates a cleaner service boundary.

### Tradeoffs
- Can become a god object if it absorbs too much logic.
- May hide useful subsystem capabilities from advanced callers.

### Facade vs Adapter
- Facade simplifies a subsystem.
- Adapter translates one interface into another.

### Minimal Java implementation
```java
final class InventoryService {
	boolean reserve(String itemId) {
		return true;
	}
}

final class PaymentService {
	boolean charge(String userId, int amount) {
		return true;
	}
}

final class NotificationService {
	void sendConfirmation(String userId) {
		System.out.println("confirmation sent to " + userId);
	}
}

final class OrderFacade {
	private final InventoryService inventory = new InventoryService();
	private final PaymentService payment = new PaymentService();
	private final NotificationService notification = new NotificationService();

	boolean placeOrder(String userId, String itemId, int amount) {
		if (!inventory.reserve(itemId) || !payment.charge(userId, amount)) {
			return false;
		}
		notification.sendConfirmation(userId);
		return true;
	}
}
```

## Flyweight

### Intent
- Use sharing to support many fine-grained objects efficiently.

### Core problem
- The system creates huge numbers of similar objects and memory cost matters.

### Key idea
- Separate intrinsic state that can be shared from extrinsic state that must be supplied from outside.

### Example idea
- Text editor characters sharing font metadata.
- Map tiles or game particles sharing immutable templates.

### Pros
- Reduces memory use.
- Can improve performance when object count is very high.

### Tradeoffs
- Requires careful state separation.
- Makes code harder to reason about if misapplied.
- Usually unnecessary unless scale justifies it.

### Interview note
- Flyweight is important conceptually, but it appears less often in everyday business code than Strategy, Factory, or Observer.

### Minimal Java implementation
```java
record GlyphStyle(String font, int size, boolean bold) {
}

final class GlyphStyleFactory {
	private final java.util.Map<String, GlyphStyle> cache = new java.util.HashMap<>();

	GlyphStyle get(String font, int size, boolean bold) {
		String key = font + ":" + size + ":" + bold;
		return cache.computeIfAbsent(key, ignored -> new GlyphStyle(font, size, bold));
	}
}
```

## Proxy

### Intent
- Provide a placeholder or surrogate for another object to control access to it.

### Typical reasons to use it
- Lazy loading.
- Security or authorization checks.
- Remote access.
- Caching.
- Logging or metrics.
- Transaction boundaries.

### Important property
- Proxy usually keeps the same interface as the real subject.

### Common proxy types
- Virtual proxy: creates the real object lazily.
- Protection proxy: checks access permissions.
- Remote proxy: represents an object in another process or network location.
- Caching proxy: stores results to reduce repeated work.

### Java and Spring relevance
- Spring AOP and transactional beans are classic proxy-heavy territory.
- Hibernate lazy loading also relies on proxy ideas.

### Decorator vs Proxy
- Decorator adds responsibilities with emphasis on behavior extension.
- Proxy controls access or indirection while preserving the same API.
- In practice the structures can look similar, but the intent differs.

### Minimal Java implementation
```java
interface ReportService {
	String getReport(String reportId);
}

final class RealReportService implements ReportService {
	public String getReport(String reportId) {
		return "report=" + reportId;
	}
}

final class CachingReportProxy implements ReportService {
	private final ReportService target;
	private final java.util.Map<String, String> cache = new java.util.HashMap<>();

	CachingReportProxy(ReportService target) {
		this.target = target;
	}

	public String getReport(String reportId) {
		return cache.computeIfAbsent(reportId, target::getReport);
	}
}
```

## Part 2 comparison map

### Decorator vs Facade
- Decorator wraps one object and preserves its interface.
- Facade provides a new, simpler interface over many subsystem pieces.

### Proxy vs Adapter
- Proxy keeps the same interface.
- Adapter changes the interface.

### Decorator vs Proxy
- Same shape is possible.
- Different reason for existing.

### Flyweight vs Prototype
- Flyweight shares objects.
- Prototype copies objects.

## Practical advice
- Use Decorator when behavior combinations matter.
- Use Facade to improve service boundaries and readability.
- Use Proxy when access control, lazy behavior, or remoting is the concern.
- Use Flyweight only when memory scale is a real problem and you can clearly separate shared state from external state.
