# Auto-Configuration, Actuator, Logging — Interview Deep Dive

## Auto-configuration internals
**Q: How does Boot decide what to configure?**
- Uses `@Conditional...` annotations:
  - `@ConditionalOnClass`
  - `@ConditionalOnMissingBean`
  - `@ConditionalOnProperty`
  - `@ConditionalOnWebApplication`

**Q: What does “back off” mean?**
- Boot provides defaults, but if you define your own bean, auto-config often does nothing.

**Debugging tip:**
- Run with `--debug` to see the Condition Evaluation Report.

## Starters and dependency management
**Q: Why don’t we specify versions for every dependency?**
- Boot uses dependency management (BOM) to align versions.

Pitfall:
- Adding explicit versions can break alignment.

## Actuator
Why it matters:
- production visibility: health, metrics, env, beans.

Common endpoints:
- `/actuator/health`
- `/actuator/info`
- `/actuator/metrics`

Tricky questions:
- How to secure actuator endpoints?
- How to expose only a subset (management exposure config)?

## Logging
- Boot defaults to Logback.

Tricks:
- log levels per package (e.g. `logging.level.org.springframework=INFO`)
- MDC usage for request correlation
- JSON logging often needed in microservices

Pitfall:
- Logging sensitive info (headers, tokens, PII) — explain masking.

## Configuration properties
- Prefer `@ConfigurationProperties` over a pile of `@Value`.

Trick:
- Validation with `@Validated` + Bean Validation annotations.

## Common “why is this not working” checklist
- Wrong profile active
- bean not created due to conditions
- component scan doesn’t include your package
- you defined a bean that caused Boot to back off
