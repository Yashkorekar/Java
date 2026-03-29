# Revision Question Map

Use this file as the final pass before interviews.

## The 23 GoF patterns in one table

### Creational
- Singleton: one controlled instance.
- Factory Method: defer one product creation decision.
- Abstract Factory: create related product families.
- Builder: assemble complex objects step by step.
- Prototype: create by copying.

### Structural
- Adapter: convert one interface into another.
- Bridge: separate abstraction from implementation dimension.
- Composite: treat part and whole uniformly.
- Decorator: add behavior by wrapping.
- Facade: simplify a subsystem.
- Flyweight: share intrinsic state to reduce memory.
- Proxy: control access through a surrogate.

### Behavioral
- Chain of Responsibility: pass request through handlers.
- Command: package request as an object.
- Interpreter: evaluate a small grammar.
- Iterator: separate traversal from structure.
- Mediator: centralize interactions.
- Memento: capture state for restoration.
- Observer: notify subscribers of change.
- State: behavior changes by internal mode.
- Strategy: swap algorithms or policies.
- Template Method: fixed algorithm skeleton with override points.
- Visitor: add operations across a stable object structure.

## Pattern recognition by interview question

### "How do you avoid a huge constructor?"
- Builder.

### "How do you hide which implementation gets created?"
- Factory Method.

### "How do you switch entire families together?"
- Abstract Factory.

### "How do you integrate a vendor API with a different method shape?"
- Adapter.

### "How do you add logging, retry, and metrics around an object?"
- Decorator.

### "How do you expose a simpler API over multiple services?"
- Facade.

### "How do you choose one algorithm based on context?"
- Strategy.

### "How do you model lifecycle-dependent behavior?"
- State.

### "How do you notify many listeners when something changes?"
- Observer.

### "How do you build request processing pipelines?"
- Chain of Responsibility.

### "How do you support undo?"
- Command or Memento, sometimes both together.

### "How do you add operations across a stable tree of node types?"
- Visitor.

## Most important comparisons to memorize

### Factory Method vs Abstract Factory
- One product decision vs product family decision.

### Builder vs Factory
- Object assembly clarity vs product selection.

### Adapter vs Facade vs Proxy
- Adapter changes interface.
- Facade simplifies interface.
- Proxy preserves interface and controls access.

### Decorator vs Proxy
- Same wrapper shape can appear.
- Decorator adds behavior.
- Proxy controls access, indirection, or lifecycle.

### Strategy vs State
- Strategy is usually chosen from outside.
- State usually changes from inside based on lifecycle or mode.

### Strategy vs Template Method
- Composition vs inheritance.

### Observer vs Mediator
- Notification vs coordination.

## Spring and enterprise Java mapping
- Bean creation and container wiring: Factory ideas.
- AOP and transactions: Proxy.
- `JdbcTemplate` and callback-style frameworks: Template Method.
- Request filters and interceptors: Chain of Responsibility.
- Application events: Observer.
- Multiple pluggable implementations: Strategy.
- Service layer over repositories and clients: Facade.
- Wrapping vendor SDKs: Adapter.

## What to say if the interviewer asks, "Which patterns matter most in backend Java?"
- Strategy, Factory, Builder, Proxy, Observer, Facade, and Chain of Responsibility are the highest-yield patterns in most backend code.
- Template Method still matters because frameworks use it.
- State matters when lifecycle rules are explicit.
- Flyweight, Interpreter, and Visitor are important conceptually but less common in typical CRUD-style services.

## Most likely coding-round implementations
- Singleton
- Factory Method
- Abstract Factory
- Builder
- Adapter
- Decorator
- Facade
- Proxy
- Chain of Responsibility
- Observer
- State
- Strategy
- Template Method

If an interviewer asks for code, these are the patterns you should be comfortable writing from memory in a simplified form.

## Good short answers

### Best pattern for interchangeable business rules
- Strategy.

### Best pattern for many optional fields in immutable objects
- Builder.

### Best pattern for hiding infrastructure implementation choice
- Factory Method or Abstract Factory.

### Best pattern for a simpler service-level API over many internals
- Facade.

### Best pattern for cross-cutting wrappers like logging and metrics
- Decorator or Proxy depending on intent.

## Final revision checklist
- Can you define each high-value pattern in one sentence?
- Can you give one Java or Spring example for each common pattern?
- Can you compare Strategy vs State, Decorator vs Proxy, and Adapter vs Facade clearly?
- Can you explain one benefit and one downside for every pattern you mention?
- Can you say when a pattern becomes over-engineering?

If you can do that, you are already above average in most design-pattern interviews.
