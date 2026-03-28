# Dependency Inversion Principle (DIP)

## 1. Plain English explanation
- DIP means high-level modules should not depend directly on low-level concrete classes.
- Both should depend on abstractions.
- In simple terms, business logic should talk to an interface, not hardcode one exact implementation.

## 2. What this principle is trying to avoid
- High-level code tightly coupled to one concrete dependency.
- Code that is hard to test because dependencies are fixed inside the class.
- Designs where changing infrastructure forces changes in business logic.

## 3. Beginner-friendly example
- Think about a shopping mall billing counter.
- A customer may pay using a credit card today and a debit card tomorrow.
- If the shopping mall class directly depends on one exact card type, the business flow is tightly coupled to one implementation.
- A better design is to depend on a `BankCard` abstraction.

## 4. What the Java demo shows
- `ShoppingMall` depends on a `BankCard` abstraction.
- `CreditCard` and `DebitCard` are interchangeable implementations.
- The mall code does not need to know the low-level details of each card type.
- The high-level purchase flow stays stable while implementations can vary.

## 5. Why interviewers care
- DIP improves flexibility.
- DIP makes unit testing much easier.
- DIP reduces coupling between business logic and infrastructure.
- It is common in service classes, repositories, adapters, and framework-based applications.

## 6. Common misunderstanding
- DIP is not just dependency injection.
- Dependency injection is a technique often used to implement DIP.
- The actual principle is about depending on abstractions.

## 7. Real-life impact
- Business code should not care whether payment happens through credit card, debit card, or another future implementation.
- Purchase flow should not hardcode one provider if the business needs flexibility.
- Testing also becomes easier because abstractions are simpler to replace.

## 8. Connection to system design
- DIP helps clean boundaries between core domain logic and infrastructure.
- It supports ports-and-adapters style architecture.
- In bigger systems, it helps teams replace integrations without rewriting core business code.

## 9. Interview answer in one line
- DIP means core business logic should depend on stable abstractions instead of concrete infrastructure classes.
