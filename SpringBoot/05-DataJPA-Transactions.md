# Data JPA & Transactions — Interview Traps

## Repositories
- `JpaRepository` provides CRUD + paging/sorting.

Tricks:
- `findBy...` derived query naming
- `@Query` for custom queries

## Transactions (`@Transactional`)
Key concepts:
- Works via proxies.
- Transaction boundary typically at service layer.

### Self-invocation pitfall
- `@Transactional` won’t apply if you call the method inside the same class.

### Propagation
Common values:
- `REQUIRED` (default): join existing or create new.
- `REQUIRES_NEW`: suspend current and create new.

Interview examples:
- Audit logging: you may want `REQUIRES_NEW` so audit persists even if main tx rolls back.

### Read-only transactions
- `@Transactional(readOnly = true)` can optimize some scenarios.

### Rollback rules
- By default, rollback for RuntimeException/Error.
- Checked exceptions do not rollback unless configured.

### Isolation
- Know basic tradeoffs (dirty reads, non-repeatable reads, phantom reads).

## Lazy vs Eager loading
- Default for `@ManyToOne` is EAGER; for `@OneToMany` is LAZY.

Trick:
- EAGER can cause huge joins; LAZY can cause N+1.

Fix patterns:
- fetch join queries
- entity graphs
- DTO projections

## N+1 problem
- Symptom: 1 query for parent + N queries for children.

Good answer:
- show how to detect (logs/metrics) and fix (fetch join / batch size / DTO).
