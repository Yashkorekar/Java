Refer to Coding Shuttle handbooks for quick parallel reading:
https://www.codingshuttle.com/handbooks/

# Spring Boot Handbook (Interview + Practical Notes)

This folder is now organized as a standalone Spring Boot study track. If you read it in order, you should cover the core Spring Boot topics that come up in most interviews and day-to-day backend work.

## How to read this folder
1. Finish the Core section in order.
2. Read the Production section next.
3. Read the Advanced section last.
4. Revisit the cheat sheet before interviews.

## Spring Boot 3 / 2026 baseline
- Spring Boot 3.x uses `jakarta.*`, not `javax.*`.
- Spring Boot 3 requires Java 17+, and many teams are already on Java 21.
- `WebSecurityConfigurerAdapter` is gone. Security configuration is usually done with a `SecurityFilterChain` bean.
- `ProblemDetail`, Testcontainers, Micrometer, OpenTelemetry, Docker/buildpacks, and AOT/native image are common modern discussion points.

## Core
Start here if you want a strong baseline before production topics.

- [Core/00-Index.md](Core/00-Index.md)
- [Core/01-SpringBoot-Fundamentals.md](Core/01-SpringBoot-Fundamentals.md)
- [Core/02-Spring-Core-DI-Beans-Lifecycle.md](Core/02-Spring-Core-DI-Beans-Lifecycle.md)
- [Core/03-Configuration-AutoConfiguration-And-Startup.md](Core/03-Configuration-AutoConfiguration-And-Startup.md)
- [Core/04-Web-REST-Validation-ExceptionHandling.md](Core/04-Web-REST-Validation-ExceptionHandling.md)
- [Core/05-DataJPA-Transactions.md](Core/05-DataJPA-Transactions.md)

## Production
Read these when you want the "how this behaves in a real system" layer.

- [Production/00-Index.md](Production/00-Index.md)
- [Production/01-Spring-Security.md](Production/01-Spring-Security.md)
- [Production/02-Actuator-Observability-And-Logging.md](Production/02-Actuator-Observability-And-Logging.md)
- [Production/03-Performance-Resilience-Patterns.md](Production/03-Performance-Resilience-Patterns.md)
- [Production/04-Deployment-Packaging-And-Operations.md](Production/04-Deployment-Packaging-And-Operations.md)

## Advanced
These topics usually show up in stronger interviews, debugging rounds, or real projects.

- [Advanced/00-Index.md](Advanced/00-Index.md)
- [Advanced/01-Testing.md](Advanced/01-Testing.md)
- [Advanced/02-Reactive-WebFlux.md](Advanced/02-Reactive-WebFlux.md)
- [Advanced/03-Annotations-And-Mappings-CheatSheet.md](Advanced/03-Annotations-And-Mappings-CheatSheet.md)

## What this handbook now covers well
- Spring Boot startup, configuration, profiles, DI, proxies, bean lifecycle
- MVC/REST, validation, exception handling, serialization, transaction boundaries
- JPA, lazy loading, N+1, propagation, locking, schema migrations
- Security basics and modern Boot security configuration
- Actuator, metrics, health checks, logging, tracing, observability
- Testing strategy, slices, MockMvc, Testcontainers, security tests
- Performance, resilience, deployment, packaging, and operational readiness
- Reactive/WebFlux basics for roles that expect modern Spring breadth

## Topics that are still role-specific add-ons
These are useful, but not mandatory for every Spring Boot interview:

- Spring Cloud (discovery, config server, gateway)
- Messaging systems such as Kafka or RabbitMQ
- Spring Batch
- GraphQL
- Deep Kubernetes platform work

## How to use the notes well
- Read each topic as if you must explain both what happens and why.
- For every major section, be able to name 2-3 common failure modes.
- If a note says "proxy", "lifecycle", or "transaction boundary", pause and mentally trace the runtime behavior.
- When possible, reproduce one mini-scenario in a sample app.
