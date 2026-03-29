# Behavioral Patterns Part 3

This section covers:
- State
- Strategy
- Template Method
- Visitor

## State

### Intent
- Allow an object to alter its behavior when its internal state changes. The object appears to change its class.

### Core problem
- A class has many state-dependent branches, and behavior changes based on current mode.

### Example idea
- Order lifecycle: created, paid, shipped, delivered, cancelled.
- Each state supports different transitions and actions.

### Why it helps
- Replaces giant conditional logic with explicit state objects.
- Makes valid transitions clearer.

### Pros
- Cleaner than massive `if/else` or `switch` blocks.
- Encapsulates state-specific behavior.
- Makes transitions easier to reason about.

### Tradeoffs
- More classes.
- Can be unnecessary if states are trivial.

### State vs Strategy
- State changes behavior based on internal status and often drives transitions.
- Strategy is usually chosen externally to pick one algorithm or policy.

### Minimal Java implementation
```java
interface OrderState {
	void next(OrderContext context);
	String name();
}

final class CreatedState implements OrderState {
	public void next(OrderContext context) {
		context.setState(new PaidState());
	}

	public String name() {
		return "CREATED";
	}
}

final class PaidState implements OrderState {
	public void next(OrderContext context) {
		context.setState(this);
	}

	public String name() {
		return "PAID";
	}
}

final class OrderContext {
	private OrderState state = new CreatedState();

	void setState(OrderState state) {
		this.state = state;
	}

	void advance() {
		state.next(this);
	}
}
```

## Strategy

### Intent
- Define a family of algorithms, encapsulate each one, and make them interchangeable.

### Core problem
- The same task can be performed in multiple ways.
- The caller should use one abstraction instead of condition-heavy branching.

### Common examples
- Payment calculation.
- Discount rules.
- Sorting/comparison rules.
- Authentication or serialization strategies.

### Why it is high-value in interviews and real systems
- It directly supports Open/Closed Principle.
- It removes branching logic from large service methods.
- It is easy to explain with business examples.

### Pros
- Swappable behavior.
- Cleaner testing.
- Reduced conditional complexity.

### Tradeoffs
- More small classes or lambdas.
- Overkill if there are only one or two stable behaviors.

### Java examples
- `Comparator` is a classic strategy-style abstraction.
- Spring often wires different strategy implementations and selects one by type or configuration.

### Minimal Java implementation
```java
interface PricingStrategy {
	int price(int baseAmount);
}

final class RegularPricing implements PricingStrategy {
	public int price(int baseAmount) {
		return baseAmount;
	}
}

final class PremiumPricing implements PricingStrategy {
	public int price(int baseAmount) {
		return (int) (baseAmount * 0.9);
	}
}

final class CheckoutService {
	private final PricingStrategy pricingStrategy;

	CheckoutService(PricingStrategy pricingStrategy) {
		this.pricingStrategy = pricingStrategy;
	}

	int checkout(int baseAmount) {
		return pricingStrategy.price(baseAmount);
	}
}
```

## Template Method

### Intent
- Define the skeleton of an algorithm in a base class, letting subclasses redefine specific steps without changing the overall flow.

### Core problem
- Many variants share the same high-level process but differ in some steps.

### Common examples
- Import pipelines.
- Report generation.
- Framework lifecycle hooks.

### Why it matters in framework code
- Frameworks often own the outer algorithm and let application code fill specific hooks.
- This is a frequent pattern in older Java framework designs.

### Pros
- Reuses stable flow.
- Encourages consistent algorithm structure.
- Good for lifecycle hooks.

### Tradeoffs
- Inheritance-based.
- Subclass behavior can become fragile.
- Less flexible than composition-based alternatives.

### Template Method vs Strategy
- Template Method varies steps through inheritance.
- Strategy varies behavior through composition.
- In modern code, Strategy is often more flexible.

### Minimal Java implementation
```java
abstract class DataImporter {
	public final void run() {
		read();
		transform();
		save();
	}

	protected abstract void read();

	protected void transform() {
		System.out.println("default transform");
	}

	protected abstract void save();
}

final class CsvImporter extends DataImporter {
	protected void read() {
		System.out.println("read csv");
	}

	protected void save() {
		System.out.println("save rows");
	}
}
```

## Visitor

### Intent
- Represent an operation to be performed on elements of an object structure, letting you define new operations without changing the element classes.

### Core problem
- You have a stable object structure but need to add many new operations across it.

### Common examples
- AST processing.
- Compiler or rule-engine passes.
- Reporting or exporting over a stable domain tree.

### Pros
- Easy to add new operations.
- Keeps operation logic outside the element classes.

### Tradeoffs
- Harder to add new element types because all visitors must change.
- Double dispatch can feel complex.
- Often too heavy for ordinary CRUD-heavy business code.

### Visitor vs Strategy
- Visitor applies operations across a structure of element types.
- Strategy usually swaps one algorithm in one context.

### Minimal Java implementation
```java
interface Shape {
	void accept(ShapeVisitor visitor);
}

final class Circle implements Shape {
	public void accept(ShapeVisitor visitor) {
		visitor.visit(this);
	}
}

final class Rectangle implements Shape {
	public void accept(ShapeVisitor visitor) {
		visitor.visit(this);
	}
}

interface ShapeVisitor {
	void visit(Circle circle);
	void visit(Rectangle rectangle);
}

final class AreaPrinter implements ShapeVisitor {
	public void visit(Circle circle) {
		System.out.println("circle area logic");
	}

	public void visit(Rectangle rectangle) {
		System.out.println("rectangle area logic");
	}
}
```

## Part 3 comparison map

### Strategy vs Template Method
- Strategy uses composition.
- Template Method uses inheritance.

### State vs Strategy
- Same shape is possible.
- Intent differs: State models internal mode, Strategy models replaceable policy.

### Visitor vs Composite
- Composite defines the object tree.
- Visitor defines operations performed across that tree.

## Practical advice
- Use Strategy very often when branching logic keeps growing.
- Use State when lifecycle rules or mode-specific behavior are real.
- Use Template Method when the framework owns the outer flow and variation points are stable.
- Use Visitor when the object structure is stable but operations keep expanding.
