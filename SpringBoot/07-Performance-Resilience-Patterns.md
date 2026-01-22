# Performance / Resilience / Patterns — Interview Notes

## Performance
- Avoid N+1 queries
- Use pagination
- Cache hot reads
- Don’t serialize entities directly

## Caching
- `@EnableCaching`, `@Cacheable`, `@CacheEvict`

Tricks:
- Cache key design
- Stale data and eviction strategy

## Async & scheduling
- `@EnableAsync` + `@Async`
- `@EnableScheduling` + `@Scheduled`

Pitfall:
- `@Async` also uses proxies; self-invocation issue applies.

## Resilience
- timeouts everywhere
- retries with backoff
- circuit breakers
- bulkheads

Good interview answer:
- Retries can amplify load; don’t retry non-idempotent operations blindly.

## API design
- idempotency keys for POST
- proper HTTP status codes
- error response consistency

## Security
- Never log secrets
- principle of least privilege
- secure actuator endpoints

## Observability
- structured logs
- correlation IDs (trace id)
- metrics and health checks

## Microservice communication pitfalls
- cascading failures
- distributed transactions (avoid if possible)
- eventual consistency patterns
