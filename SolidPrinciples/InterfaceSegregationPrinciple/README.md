# Interface Segregation Principle (ISP)

## 1. Plain English explanation
- ISP means clients should not be forced to depend on methods they do not use.
- In simple terms, make small focused interfaces instead of one huge interface for everything.

## 2. What this principle is trying to avoid
- Fat interfaces that force classes to implement irrelevant methods.
- Empty methods, dummy methods, or methods that throw exceptions because they do not make sense.
- Tight coupling to features that many implementations do not need.

## 3. Beginner-friendly example
- Think of UPI apps like Google Pay, Paytm, and PhonePe.
- All of them can make payments.
- Some apps may also support cashback credit features.
- If one interface forces every payment app to implement every extra feature, some classes will depend on methods they do not really need.

## 4. What the Java demo shows
- `UPIPayments` contains common payment behavior.
- `CashBackManager` contains the cashback-specific behavior.
- `GooglePay` can implement both when it supports both capabilities.
- `Paytm` and `Phonepe` can implement only the payment contract they actually need.

## 5. Why interviewers care
- ISP reduces unnecessary coupling.
- ISP makes APIs easier to understand and safer to implement.
- ISP often improves testability because mocks and clients depend on smaller contracts.

## 6. Common misunderstanding
- ISP is not about making lots of tiny interfaces without reason.
- The goal is meaningful separation by responsibility or capability.

## 7. Real-life impact
- A payment app should not be forced to implement cashback credit methods if that feature is not part of that app's responsibility.
- A reporting client should not depend on write operations it never uses.
- Consumer-specific interfaces are often a practical ISP solution.

## 8. Connection to system design
- ISP affects API shape.
- Smaller interfaces often become better service contracts.
- It is especially important in SDKs, gateways, and internal platform libraries.

## 9. Interview answer in one line
- ISP means do not force classes or clients to depend on methods they do not need.
