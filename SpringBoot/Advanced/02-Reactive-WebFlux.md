# Reactive WebFlux — Practical Interview Notes

## What WebFlux is
Spring WebFlux is Spring's reactive web stack for asynchronous, non-blocking request handling.

Core reactive types:
- `Mono<T>`: zero or one item
- `Flux<T>`: zero to many items

## MVC vs WebFlux
### Spring MVC
- traditionally servlet-based
- request-per-thread model
- simple and very common

### Spring WebFlux
- designed for reactive pipelines and non-blocking I/O
- commonly uses Netty in Boot setups
- fits streaming or high-concurrency I/O-heavy workloads well

Good interview answer:
- WebFlux is not automatically better than MVC. It is a different model with different tradeoffs.

## When WebFlux makes sense
- streaming responses
- server-sent events
- many concurrent I/O-bound operations
- end-to-end reactive stack requirements

## When MVC is still the better answer
- normal CRUD apps
- teams without reactive experience
- workloads dominated by blocking JDBC and blocking SDKs
- cases where simpler debugging and lower cognitive load matter more

## The big trap: blocking calls in reactive code
Bad pattern:
- using blocking database or HTTP calls on event-loop threads

Why it matters:
- You lose the main benefit of reactive execution and can create severe performance problems.

## WebClient
`WebClient` is the modern Spring client for reactive/non-blocking HTTP calls.

Good answer:
- It is generally preferred over older client styles when you need reactive composition.

## Backpressure
High-level idea:
- consumers should be able to signal how much data they can handle.

Interview-safe answer:
- Backpressure helps avoid overwhelming downstream consumers in reactive streams.

## Error handling mindset
Reactive errors are part of the pipeline, not just thrown in the usual imperative way.

Common operators to know at a high level:
- `onErrorResume`
- `onErrorReturn`
- `retryWhen`

## Common interview pitfalls
- Saying WebFlux is faster for everything.
- Mixing JPA-heavy blocking code with a reactive stack and expecting ideal scalability.
- Ignoring the debugging and cognitive complexity of reactive flows.
- Using both MVC and WebFlux starters without understanding which stack Boot will favor.

## Strong interview close
If asked whether you should use WebFlux:
- choose it when the workload and team justify reactive complexity
- stay with MVC when the system is mostly straightforward blocking CRUD
- optimize for correctness and operability, not just trendiness