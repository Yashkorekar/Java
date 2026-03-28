# Open/Closed Principle (OCP)

## 1. Plain English explanation
- OCP means software entities should be open for extension but closed for modification.
- In simple words: when a new requirement comes, you should prefer adding new code instead of repeatedly changing old stable code.

## 2. What this principle is trying to avoid
- Huge `if-else` or `switch` chains that grow every time a new feature appears.
- Fear of breaking old logic while adding new behavior.
- Classes that need to be edited again and again for every new variation.

## 3. Beginner-friendly example
- Imagine an application that sends notifications in different ways.
- First you support email notifications.
- Then mobile or SMS notifications.
- Then WhatsApp notifications.
- If you keep editing the same old class every time a new channel is added, the code becomes risky and messy.

## 4. What the Java demo shows
- `Notificationservice` is the abstraction.
- `EmailNotificationService`, `MobileNotificationService`, and `WhatsAppNotificationService` add behavior by implementing that abstraction.
- New notification channels can be added as new classes.
- Existing caller code can keep depending on the interface instead of changing old logic branches.

## 5. Why interviewers care
- OCP reduces regression risk.
- OCP makes systems easier to extend.
- OCP encourages use of interfaces, strategies, and plugins.
- It is very common in payment, notification, pricing, and workflow systems.

## 6. Common misunderstanding
- OCP does not mean old code should never change.
- It means stable behavior should not need unnecessary repeated edits for every new variation.
- Sometimes refactoring old code is exactly what makes OCP possible.

## 7. Real-life impact
- Adding a new notification type should not require modifying every old notification class.
- Adding a new delivery channel should be possible by introducing a new implementation.
- Existing higher-level code can stay stable while behavior grows.

## 8. Connection to system design
- OCP supports pluggable architecture.
- It helps when you have different business rules for different tenants, regions, or products.
- It often appears as strategy pattern or plugin design in larger systems.

## 9. Interview answer in one line
- OCP means new behavior should be added mostly by extending the design, not by constantly modifying old logic branches.
