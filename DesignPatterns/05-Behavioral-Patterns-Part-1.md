# Behavioral Patterns Part 1

Behavioral patterns focus on collaboration, responsibility flow, and runtime behavior.

This section covers:
- Chain of Responsibility
- Command
- Interpreter
- Iterator

## Chain of Responsibility

### Intent
- Pass a request along a chain of handlers until one handles it or the chain ends.

### Core problem
- Several components may be able to process a request.
- You do not want the sender tightly coupled to one receiver.

### Common use cases
- Validation pipelines.
- Security filter chains.
- Approval workflows.
- HTTP middleware or request interceptors.

### Pros
- Sender does not know the final handler.
- Easy to add, remove, or reorder handlers.
- Supports pipeline-style processing.

### Tradeoffs
- Harder to trace request flow.
- A request may go unhandled if the chain is misconfigured.
- Order becomes a correctness concern.

### Java and Spring examples
- Servlet filters.
- Spring Security filter chain.
- Validation rules that short-circuit on failure.

### Chain vs Command
- Chain routes a request through possible handlers.
- Command packages a request as an object.

### Minimal Java implementation
```java
final class LoginRequest {
	final String token;
	final String role;

	LoginRequest(String token, String role) {
		this.token = token;
		this.role = role;
	}
}

abstract class LoginHandler {
	private LoginHandler next;

	LoginHandler linkWith(LoginHandler next) {
		this.next = next;
		return next;
	}

	final boolean handle(LoginRequest request) {
		if (!process(request)) {
			return false;
		}
		return next == null || next.handle(request);
	}

	protected abstract boolean process(LoginRequest request);
}

final class TokenHandler extends LoginHandler {
	protected boolean process(LoginRequest request) {
		return request.token != null && !request.token.isBlank();
	}
}

final class RoleHandler extends LoginHandler {
	protected boolean process(LoginRequest request) {
		return "ADMIN".equals(request.role);
	}
}
```

## Command

### Intent
- Encapsulate a request as an object, thereby letting you parameterize clients, queue operations, log them, or support undo.

### Core problem
- You want to decouple the object that issues a request from the object that performs it.

### Common examples
- UI button actions.
- Job queues.
- Audit-friendly actions.
- Undo and redo support.

### Shape
- Command interface.
- Concrete commands.
- Receiver that performs the actual work.
- Invoker that triggers the command.

### Pros
- Decouples sender and receiver.
- Easy to queue, retry, log, or persist actions.
- Good fit for undoable operations.

### Tradeoffs
- More classes and indirection.
- Can feel heavy for very small systems.

### Command vs Strategy
- Command represents an action to be executed.
- Strategy represents an algorithm or policy used by another object.

### Minimal Java implementation
```java
interface Command {
	void execute();
}

final class TextEditor {
	void save() {
		System.out.println("saved");
	}
}

final class SaveCommand implements Command {
	private final TextEditor editor;

	SaveCommand(TextEditor editor) {
		this.editor = editor;
	}

	public void execute() {
		editor.save();
	}
}

final class ToolbarButton {
	private final Command command;

	ToolbarButton(Command command) {
		this.command = command;
	}

	void click() {
		command.execute();
	}
}
```

## Interpreter

### Intent
- Define a representation for a grammar and an interpreter that can evaluate sentences in that language.

### Core problem
- The system needs to process a small domain-specific language or expression syntax.

### Common examples
- Rule engines with a tiny internal syntax.
- Expression trees.
- Query filters or formula evaluation.

### Why it is less common in day-to-day business code
- Full parsers are often better handled by dedicated libraries.
- The pattern becomes useful mainly when the grammar is small and controlled.

### Pros
- Clean model for small languages.
- Easy to extend expression types in some designs.

### Tradeoffs
- Can explode into many classes.
- Hard to manage for large grammars.
- Often replaced by parser generators or expression libraries.

### Interview line
- Interpreter is useful for small DSLs, not for rebuilding a full compiler by hand unless the problem really demands it.

### Minimal Java implementation
```java
interface Expression {
	int interpret();
}

record NumberExpression(int value) implements Expression {
	public int interpret() {
		return value;
	}
}

record AddExpression(Expression left, Expression right) implements Expression {
	public int interpret() {
		return left.interpret() + right.interpret();
	}
}

Expression expression = new AddExpression(new NumberExpression(2), new NumberExpression(3));
int result = expression.interpret();
```

## Iterator

### Intent
- Provide a way to access elements of an aggregate object sequentially without exposing its underlying representation.

### Core problem
- Clients need traversal, but should not depend on collection internals.

### Why it matters in Java
- The Collections Framework uses this everywhere.
- `Iterator`, enhanced `for`, and stream source traversal all reflect this pattern.

### Pros
- Hides internal structure.
- Supports multiple traversal strategies.
- Makes collection APIs cleaner.

### Tradeoffs
- Concurrent modification concerns.
- Some traversals need stateful iterators that are easy to misuse.

### Interview trap
- Iterator is not only about looping. It is about separating traversal from collection implementation.

### Minimal Java implementation
```java
final class Range implements Iterable<Integer> {
	private final int start;
	private final int end;

	Range(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public java.util.Iterator<Integer> iterator() {
		return new java.util.Iterator<>() {
			private int current = start;

			public boolean hasNext() {
				return current <= end;
			}

			public Integer next() {
				return current++;
			}
		};
	}
}
```

## Part 1 comparison map

### Chain of Responsibility vs Strategy
- Chain passes a request through handlers.
- Strategy selects one algorithm implementation.

### Command vs Chain of Responsibility
- Command packages one request.
- Chain routes handling across multiple candidates.

### Iterator vs Composite
- Iterator traverses a structure.
- Composite defines the structure itself.

## Practical advice
- Use Chain when orderable handlers and partial responsibility make sense.
- Use Command when actions need queuing, auditability, or undo semantics.
- Use Iterator constantly, even if the language hides it behind collection APIs.
- Use Interpreter sparingly unless the domain genuinely includes a small language.
