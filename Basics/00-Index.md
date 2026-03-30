# Java Basics Track

This folder contains runnable Java basics demos. Read it as a progression from syntax and control flow to object-level reasoning.

## Suggested order
1. `DataType.java`
2. `ConditionalsDemo.java`
3. `LoopsDemo.java`
4. `ArraysDemo.java`
5. `StringManipulationDemo.java`
6. `MethodsAndPassByValueDemo.java`
7. `OOPBasicsDemo.java`
8. `StaticVsInstanceDemo.java`
9. `LambdaAndFunctionalInterfacesDemo.java`

## What each demo adds
- `DataType.java`: primitives vs references, promotions, casting, overflow, and division traps
- `ConditionalsDemo.java`: `if/else`, ternary, classic `switch`, switch expressions, and null/assignment gotchas
- `LoopsDemo.java`: choosing the right loop form, loop control, iterator-safe removal, and off-by-one traps
- `ArraysDemo.java`: fixed-size arrays, traversal, reverse patterns, `Arrays` APIs, and covariance pitfalls
- `StringManipulationDemo.java`: immutability, pooling, substring rules, Unicode basics, and regex-based `split` traps
- `MethodsAndPassByValueDemo.java`: signatures, overloading, varargs, object mutation, and the pass-by-value reality
- `OOPBasicsDemo.java`: constructors, encapsulation, aliasing, `this`, field defaults, and class vs object state
- `StaticVsInstanceDemo.java`: when static is appropriate, why static mutable state hurts design, and dispatch quirks
- `LambdaAndFunctionalInterfacesDemo.java`: lambda syntax, functional interfaces, method references, capture rules, and scope differences

## What this folder covers well
- primitive types, operators, casting, overflow
- conditionals, switch, loops, guard clauses
- arrays, strings, common API gotchas
- method basics, overloading, Java pass-by-value semantics
- classes, objects, constructors, `this`, encapsulation
- static vs instance reasoning in real code design
- lambdas, functional interfaces, method references, and effectively-final capture rules

## What is covered elsewhere in this repo
- exceptions: `ExceptionHandling/`
- deeper object-oriented design: `OOPS/`
- data structures beyond arrays: `DataStructuresBasics/`

## How to use these notes
- Run the demos, do not only read them.
- For each file, predict the output of the trap section before you execute it.
- When a demo prints a surprising result, explain the language rule behind it in one sentence.
- If you catch yourself saying "Java passes objects by reference", correct it to "Java passes the reference value by value."