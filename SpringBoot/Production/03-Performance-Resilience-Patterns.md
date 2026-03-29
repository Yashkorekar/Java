# Performance / Resilience / Patterns — Interview Notes

## Performance
High-value answers:
- avoid N+1 queries
- use pagination instead of loading large result sets
- keep controllers thin and avoid serializing entities directly
- tune connection pools and thread pools based on actual workload
- measure first, then optimize

Interview trap:
- People jump to caching or async work before they have even found the bottleneck.

## Caching
Core annotations:
- `@EnableCaching`
- `@Cacheable`
- `@CachePut`
- `@CacheEvict`

What interviewers want to hear:
- cache key design matters
- cache invalidation is the hard part
- stale data tolerance must be explicit

Good tradeoff answer:
- Caching helps read-heavy paths, but it adds consistency and invalidation complexity.

## Threading, async, and scheduling
- `@EnableAsync` + `@Async`
- `@EnableScheduling` + `@Scheduled`

Important traps:
- `@Async` is proxy-based, so self-invocation still breaks it.
- Async work without bounded executors can create uncontrolled pressure.
- Scheduled jobs need idempotency and coordination in clustered deployments.

## Timeouts
Strong practical rule:
- Every network call should have a timeout.

Why it matters:
- Without timeouts, one failing dependency can pin threads and cause cascading failures.

## Retries
Good answer:
- Use retries with backoff and jitter.
- Retry only when the failure is transient and the operation is safe to retry.

Critical warning:
- Retrying non-idempotent operations blindly can duplicate side effects.

## Circuit breakers and bulkheads
- Circuit breaker: stop hammering a failing dependency.
- Bulkhead: isolate resources so one failing path does not consume everything.

Library commonly mentioned:
- Resilience4j

## API design for resilience
- use idempotency keys for retryable POST operations when needed
- return consistent errors
- make timeouts and retry behavior visible to callers where possible

## Database and transaction performance
- keep transactions short
- avoid remote calls inside database transactions
- use projections when full entities are unnecessary
- monitor slow queries and pool exhaustion

## Virtual threads awareness
Modern Java point:
- Virtual threads can help with some blocking workloads, but they do not remove the need for sensible database, timeout, and backpressure design.

## Common microservice failure patterns
- cascading failures
- noisy-neighbor thread pool starvation
- distributed transaction assumptions
- retry storms during outages
- cache stampedes on hot keys

## Strong interview close
If asked how to make a Spring Boot service reliable:
- define timeouts everywhere
- use retries carefully
- add circuit breakers where justified
- keep transactions short
- instrument the system well
- prefer simple, measurable optimizations over fashionable complexity
