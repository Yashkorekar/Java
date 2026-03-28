# SOLID Principles Study Folder

This folder contains original Java examples and beginner-friendly explanations for the five SOLID principles:
- SRP: Single Responsibility Principle
- OCP: Open/Closed Principle
- LSP: Liskov Substitution Principle
- ISP: Interface Segregation Principle
- DIP: Dependency Inversion Principle

## Important note
- The folders now use full principle names instead of short forms.
- The file names inside each principle folder are aligned with the external repo structure you pointed to.
- The code and explanations in this folder are still original study material created for this workspace.
- The explanation notes are concept summaries, not copied source code or a transcript.

## Why this matters for interviews
- SOLID is more about low-level design than big distributed system design.
- But interviewers often use SOLID to judge how you structure classes, services, interfaces, and dependencies.
- If you can explain SOLID clearly, your system design answers also become cleaner because your service boundaries and responsibilities become more logical.

## Suggested order
1. `SingleResponsibilityPrinciple/README.md`
2. `OpenClosedPrinciple/README.md`
3. `LiskovSubstitutionPrinciple/README.md`
4. `InterfaceSegregationPrinciple/README.md`
5. `DependencyInversionPrinciple/README.md`

## Folder structure
- `SingleResponsibilityPrinciple/` contains `BankService`, `LoanService`, `NotificationService`, and `PrinterService`.
- `OpenClosedPrinciple/` contains notification channel classes around one notification abstraction.
- `LiskovSubstitutionPrinciple/` contains a violating social-media design plus a `solution/` folder with a safer split design.
- `InterfaceSegregationPrinciple/` contains UPI payment examples such as `GooglePay`, `Paytm`, and `Phonepe`.
- `DependencyInversionPrinciple/` contains `BankCard`, `CreditCard`, `DebitCard`, and `ShoppingMall`.

## How to use these notes
- Read the explanation note first.
- Then read the Java files and compare the bad design with the better design.
- Practice explaining each principle in one or two interview lines.
- Always connect the principle to maintenance, testing, and future changes.

## One-line memory anchors
- SRP: one class, one main responsibility.
- OCP: add new behavior by extending, not by breaking existing code repeatedly.
- LSP: child types must behave safely wherever parent types are expected.
- ISP: do not force classes to implement methods they do not need.
- DIP: high-level code should depend on abstractions, not concrete details.
