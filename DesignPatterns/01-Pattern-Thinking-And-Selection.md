# Pattern Thinking and Selection

## What a design pattern really is
- A design pattern is a reusable solution shape for a recurring design problem.
- It is not a copy-paste code template.
- It is not a rule that every codebase must use.
- It is a communication shortcut. Saying "Strategy" or "Decorator" tells experienced engineers a lot about how the design behaves.

Good interview line:
- A design pattern is a named tradeoff, not magic.

## What patterns are not
- They are not mandatory in small code.
- They are not a substitute for clean naming and simple design.
- They are not automatically better than plain classes and interfaces.
- They are not proof of seniority by themselves.

Interview trap:
- Overusing patterns creates indirection without solving a real problem.

## Why patterns matter in Java
- Java codebases often grow around interfaces, object graphs, lifecycle management, and extension points.
- That makes creation, composition, and behavior-management patterns especially relevant.
- Frameworks such as Spring, Hibernate, SLF4J, Servlet APIs, and Java Collections all expose pattern ideas in practice.

## Three questions to ask before choosing a pattern
1. What kind of change is likely in this system?
2. Where do I want flexibility: object creation, object composition, or runtime behavior?
3. Is the pattern reducing complexity or only moving complexity around?

## Category overview

### Creational
Use these when object creation itself is part of the problem.

Recognition clues:
- Construction logic is complex.
- You want to hide concrete classes.
- Families of related objects must be created consistently.
- Object creation depends on configuration or runtime input.

### Structural
Use these when the main problem is how objects are assembled or exposed.

Recognition clues:
- Two interfaces do not match.
- You need to add behavior around an object.
- A subsystem is too complicated to expose directly.
- Tree structures or wrappers appear naturally.

### Behavioral
Use these when the problem is about interaction, coordination, or algorithm choice.

Recognition clues:
- Different behaviors must be swapped at runtime.
- Requests move through multiple handlers.
- Objects need notification when state changes.
- The same process has stable overall flow but customizable steps.

## Selection by symptom

### "I keep writing large constructors"
- Prefer Builder.
- If you only have 2 to 3 fields, Builder may be unnecessary.

### "I do not want callers to know concrete classes"
- Prefer Factory Method or Abstract Factory.
- Use Abstract Factory when multiple related products must vary together.

### "I need to wrap an existing class from a third-party API"
- Prefer Adapter.

### "I need extra behavior around an existing object"
- Prefer Decorator when you want stacking and composability.
- Prefer Proxy when the wrapper exists mainly for access control, lazy loading, caching, or remoting.

### "I need to switch algorithms based on context"
- Prefer Strategy.

### "Behavior changes depending on internal status"
- Prefer State.

### "Many objects react to one event"
- Prefer Observer.

### "A complex module needs a simpler entry point"
- Prefer Facade.

### "The same request may be handled by one of several components"
- Prefer Chain of Responsibility.

## Pattern selection heuristics

### Prefer the smallest design that solves the problem
- Start simple.
- Add the pattern when the repetition or variation is real, not hypothetical.

### Prefer composition over inheritance when change is behavior-heavy
- This is why Strategy and Decorator are so useful.
- Inheritance locks decisions earlier and often widens the blast radius of change.

### Make variation explicit
- If the system clearly has interchangeable behaviors, encode that with an interface and separate implementations.
- Hidden condition chains inside one class usually age badly.

### Do not confuse convenience with correctness
- Singleton is convenient.
- Convenience does not remove testing, concurrency, and hidden-global-state risks.

## Relationship with SOLID
- Single Responsibility Principle: many patterns separate concerns cleanly.
- Open/Closed Principle: Strategy, Decorator, and Factory often support extension without modifying old code.
- Liskov Substitution Principle: inheritance-based patterns still need subtype correctness.
- Interface Segregation Principle: patterns work better with focused abstractions.
- Dependency Inversion Principle: Factory, Strategy, Observer, and Bridge often rely on abstractions instead of concrete classes.

## Common anti-patterns around patterns

### Pattern-first coding
- You decide to use Decorator or Factory before understanding the real problem.
- Result: too many classes and no real gain.

### God Factory
- One giant factory knows every class in the system.
- Result: central bottleneck and constant modification.

### Fake Strategy
- Multiple classes exist, but the caller still uses `if/else` everywhere to decide behavior.
- Result: indirection without true encapsulation.

### Singleton as hidden global variable
- Easy to access from anywhere.
- Hard to test, harder to reason about, often unsafe when mutable.

### Inheritance abuse
- Template Method or base classes are used where composition would be simpler.
- Result: rigid hierarchies and surprising side effects.

## Patterns commonly used inside Spring and enterprise Java
- Factory: `BeanFactory`, `FactoryBean`, repository creation, object creation by container.
- Proxy: AOP, transactions, security, lazy behavior, method interception.
- Template Method: `JdbcTemplate`, `RestTemplate`, many callback-based APIs.
- Strategy: `Comparator`, validation strategies, authentication providers, message converters.
- Observer: application events, listeners, reactive stream style subscriptions.
- Adapter: MVC adapters, argument resolvers, wrappers around third-party services.
- Facade: service layer over multiple repositories or remote clients.

## What interviewers usually want
- Not all 23 names from memory.
- They usually want clarity on intent, tradeoffs, and examples.
- If asked for a pattern, explain:
  1. the problem,
  2. the structure,
  3. a concrete example,
  4. one tradeoff,
  5. a similar pattern and how it differs.

## Good answer structure in interviews
1. Define the pattern in one sentence.
2. State the problem it solves.
3. Give a simple example.
4. Mention one benefit and one downside.
5. Compare it with a related pattern if useful.

## Final rule of thumb
- If a pattern makes the code easier to change and easier to explain, it is helping.
- If it mainly increases ceremony, it is probably the wrong level of abstraction.
