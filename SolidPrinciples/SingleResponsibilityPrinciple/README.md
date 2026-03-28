# Single Responsibility Principle (SRP)

## 1. Plain English explanation
- SRP means a class should have one main responsibility.
- Another common way to say it is: a class should have one reason to change.
- If one class handles banking operations, loan logic, notifications, and printing, then many unrelated changes will affect the same class.

## 2. What this principle is trying to avoid
- Large classes that do many jobs.
- Code that becomes hard to test.
- One change accidentally breaking another responsibility.
- Confusion about where business logic should live.

## 3. Beginner-friendly example
- Think about a banking app.
- One part of the system should handle account operations.
- Another part should answer loan-related questions.
- Another part should send notifications.
- Another part should print or format account details.
- If all of that is inside one class, the class becomes crowded and fragile.

## 4. What the Java demo shows
- `BankService` handles bank account operations.
- `LoanService` handles loan information only.
- `NotificationService` handles alerts and OTP-style communication.
- `PrinterService` handles printing or formatting account output.
- Each class has one clear area of responsibility.

## 5. Why interviewers care
- SRP makes code easier to test.
- SRP makes code easier to change when requirements evolve.
- SRP usually leads to better service and module boundaries.
- In low-level design interviews, SRP is one of the first signals of clean thinking.

## 6. Common misunderstanding
- SRP does not mean every class must have only one method.
- It means the class should serve one coherent responsibility.
- A repository can have multiple methods and still follow SRP if all of them are about persistence.

## 7. Real-life impact
- If withdrawal rules change, only account logic should change.
- If loan policy messaging changes, only loan-related code should change.
- If SMS or email content changes, only notification code should change.
- If output format changes, only printer logic should change.

## 8. Connection to system design
- SRP at class level often becomes better service boundaries at higher level.
- A service that does too many unrelated things is harder to scale, monitor, and maintain.

## 9. Interview answer in one line
- SRP means a class should do one main job well and should change for one primary reason.
