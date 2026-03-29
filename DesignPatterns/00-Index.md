# Design Patterns Handbook

This folder is a dedicated design-pattern study track for Java interviews and practical backend design work.

It goes beyond memorizing names. The goal is to help you answer three things clearly:
- What problem does this pattern solve?
- When is this pattern a good fit and when is it overkill?
- How do similar patterns differ in real code?

## Read order
1. [01-Pattern-Thinking-And-Selection.md](01-Pattern-Thinking-And-Selection.md)
2. [02-Creational-Patterns.md](02-Creational-Patterns.md)
3. [03-Structural-Patterns-Part-1.md](03-Structural-Patterns-Part-1.md)
4. [04-Structural-Patterns-Part-2.md](04-Structural-Patterns-Part-2.md)
5. [05-Behavioral-Patterns-Part-1.md](05-Behavioral-Patterns-Part-1.md)
6. [06-Behavioral-Patterns-Part-2.md](06-Behavioral-Patterns-Part-2.md)
7. [07-Behavioral-Patterns-Part-3.md](07-Behavioral-Patterns-Part-3.md)
8. [08-Revision-Question-Map.md](08-Revision-Question-Map.md)

## What this folder covers
- Interview-oriented explanations plus minimal Java implementations for the most important patterns.

### Creational patterns
- Singleton
- Factory Method
- Abstract Factory
- Builder
- Prototype

### Structural patterns
- Adapter
- Bridge
- Composite
- Decorator
- Facade
- Flyweight
- Proxy

### Behavioral patterns
- Chain of Responsibility
- Command
- Interpreter
- Iterator
- Mediator
- Memento
- Observer
- State
- Strategy
- Template Method
- Visitor

## How to use these notes well
- First learn the intent of a pattern in one sentence.
- Then learn the recognition signals that tell you the pattern is useful.
- Then compare it with the two or three patterns that people commonly confuse with it.
- Finally, connect the pattern to Java and Spring examples so it stops feeling theoretical.

## Fast selection map
- Need one shared instance and controlled access: think Singleton, but be careful about global state.
- Need to hide object creation behind an API: think Factory Method or Abstract Factory.
- Too many constructor parameters or optional fields: think Builder.
- Need to convert one interface into another: think Adapter.
- Need to add behavior without changing the base type: think Decorator.
- Need a simpler entry point over a messy subsystem: think Facade.
- Need lazy access, access control, caching, or a remote placeholder: think Proxy.
- Need different algorithms for the same task: think Strategy.
- Need subscribers notified on change: think Observer.
- Need request pipelines: think Chain of Responsibility.
- Need a fixed algorithm skeleton with customizable steps: think Template Method.
- Need behavior to change based on current internal mode: think State.

## High-yield interview comparisons
- Factory Method vs Abstract Factory
- Adapter vs Facade vs Proxy
- Decorator vs Proxy
- Strategy vs State
- Strategy vs Template Method
- Composite vs Decorator
- Observer vs Mediator

## Java and Spring connection
- Spring itself uses many patterns internally: Factory, Proxy, Template Method, Observer, Strategy, Adapter, and Facade all appear frequently.
- If you understand patterns in framework code, debugging Spring becomes much easier.

## Coding-round focus
- If you are short on time, practice implementation for Singleton, Factory Method, Abstract Factory, Builder, Adapter, Decorator, Facade, Proxy, Chain of Responsibility, Observer, State, Strategy, and Template Method first.
- The remaining patterns still matter, but they are more often recognition or design-discussion topics than live-coding topics in typical backend interviews.

## Companion material already in the repo
- The OOP track already contains a runnable demo for Builder, Singleton, and Factory in [../OOPS/BuilderSingletonFactoryQnA.java](../OOPS/BuilderSingletonFactoryQnA.java).
- Treat that file as a quick executable demo and this folder as the full theory and interview handbook.
