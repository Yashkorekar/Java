# Liskov Substitution Principle (LSP)

## 1. Plain English explanation
- LSP means a child class should be usable wherever its parent type is expected without breaking the program.
- If a parent type promises some behavior, child types should honor that expectation.
- It applies to inheritance in such a way that the derived classes must be completely substituable for their base classes.
- In other words, if class A is a subtype of class B, then we should be able to replace B with A without interrupting the behaviour of the program

## 2. What this principle is trying to avoid
- Inheritance that looks correct in code but breaks behavior in real use.
- Child classes that throw errors for methods the parent implies should work.
- Designs where replacing one subtype with another changes correctness unexpectedly.

## 3. Beginner-friendly example
- Suppose you create one `SocialMedia` contract and force every app to support chatting, posting, media sharing, and group video calls.
- Facebook may support all of them.
- But an app like WhatsApp may not fit the same posting model as a social feed product.
- If one subtype is forced to throw an exception for a method that the base contract promises, the design is broken.

## 4. What the Java demo shows
- In the bad design, one `SocialMedia` contract is too broad and forces unsupported behavior.
- `Whatsapp` ends up violating the expected contract by rejecting post publishing.
- In the `solution/` folder, the contract is split into smaller abstractions.
- `PostMediaManager` and `SocialVideoCallManager` separate optional capabilities.
- Each app only implements what it can truly support.

## 5. Why interviewers care
- LSP shows whether inheritance is logically correct.
- It protects designs from surprising runtime failures.
- It often separates good abstraction from inheritance misuse.

## 6. Common misunderstanding
- LSP is not only about compilation.
- Code can compile and still violate LSP badly.
- The real question is whether the subtype behaves safely as a substitute.

## 7. Real-life impact
- A `PaymentMethod` subtype should not unexpectedly reject valid operations promised by the base type.
- A `StorageService` subtype should not silently lose guarantees promised by the abstraction.
- LSP matters wherever interfaces or inheritance model behavior.

## 8. Connection to system design
- LSP affects API contracts and service contracts.
- If one implementation of an interface behaves very differently from the others, consumers become fragile.
- Clean contracts help systems scale across teams.

## 9. Interview answer in one line
- LSP means derived types must be safely replaceable for their base types without breaking expected behavior.
