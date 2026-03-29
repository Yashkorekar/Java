# Actuator, Observability, and Logging — Interview Deep Dive

## Why this matters
Production interviews often move quickly from "how does it work" to "how will you operate it at 2 a.m.?" This note covers that layer.

## Actuator basics
Spring Boot Actuator adds production endpoints for monitoring and diagnostics.

Common endpoints:
- `/actuator/health`
- `/actuator/info`
- `/actuator/metrics`
- `/actuator/prometheus`
- `/actuator/env`
- `/actuator/beans`
- `/actuator/loggers`

Important rule:
- Do not expose everything publicly.

## Endpoint exposure
Typical pattern:
- expose only a small safe subset over HTTP
- keep sensitive endpoints internal or disabled

Interview trap:
- `/actuator/env` and `/actuator/beans` can reveal too much detail for public exposure.

## Health checks
Important ideas:
- Health is not just "the process is alive".
- Health often includes database, message broker, cache, and downstream dependency checks.

Terms you should know:
- liveness: should the platform restart the process?
- readiness: can the app currently serve traffic?

Good answer:
- In container platforms, readiness and liveness should be different concepts.

## Custom health indicators
Use case:
- add a custom check for a dependency or business-critical subsystem.

Interview-safe answer:
- Keep health checks meaningful and fast.
- Do not make readiness depend on slow or flaky non-critical systems unless you truly want to stop traffic.

## Metrics and Micrometer
Micrometer is the standard metrics facade in Spring Boot.

What it gives you:
- timers
- counters
- gauges
- distribution summaries
- integration with backends such as Prometheus

Common interview pattern:
- Use Micrometer to instrument business operations, external calls, queue depth, or cache hit ratio.

## Prometheus and scraping
- `/actuator/prometheus` exposes metrics in a Prometheus-friendly format.
- Prometheus pulls metrics by scraping rather than waiting for the app to push them in the common model.

## High-cardinality metric trap
Bad idea:
- put raw user IDs, request IDs, or highly unique values into metric tags.

Why it matters:
- Metrics storage cost and query performance can degrade badly.

## Tracing and correlation
What you should know:
- logs, metrics, and traces should line up around a correlation or trace ID.
- Modern setups often use OpenTelemetry-compatible tooling.

Interview answer:
- Metrics tell you something is wrong.
- Logs help explain events.
- Traces help connect work across services.

## Logging basics
- Spring Boot uses Logback by default in many setups.
- Log levels can be controlled globally or per package.

Examples:
- `logging.level.org.springframework=INFO`
- `logging.level.com.example.payment=DEBUG`

## Structured logging
Why it matters:
- JSON logs are easier to query in centralized logging systems.

Good answer:
- In distributed systems, structured logs plus correlation IDs are much more useful than free-form text logs.

## MDC and correlation IDs
- MDC stores request-scoped key-value data such as a trace ID.
- Filters or interceptors commonly populate correlation IDs.

Interview trap:
- If async execution or thread hopping is involved, MDC propagation needs attention.

## Sensitive data logging
Never log:
- passwords
- tokens
- secrets
- full PII payloads unless there is a strong compliant reason

What to say instead:
- Log identifiers, result codes, and minimal context.

## Common observability mistakes
- Exposing sensitive actuator endpoints publicly.
- No correlation IDs.
- Relying only on logs and having no metrics.
- Using high-cardinality labels in metrics.
- Logging too much at INFO in hot code paths.
- Treating readiness, liveness, and business health as the same thing.