# Data JPA & Transactions — Interview Traps

## Repositories
- `JpaRepository` gives CRUD, pagination, sorting, and common data access operations.

Common patterns:
- derived queries such as `findByEmail`
- `@Query` for custom JPQL or native SQL
- pagination with `Pageable`

## Persistence context and entity lifecycle
Important idea:
- JPA works with a persistence context that tracks managed entities.

Entity states worth knowing:
- transient: new object, not tracked
- managed: attached to persistence context
- detached: previously managed, no longer attached
- removed: marked for deletion

Why it matters:
- Dirty checking only applies to managed entities inside an active persistence context.

## Transactions (`@Transactional`)
Key concepts:
- Transaction support is usually proxy-based.
- The transaction boundary is typically at the service layer.

### Self-invocation pitfall
- `@Transactional` does not apply when the method is called internally within the same class.

### Propagation
Common values:
- `REQUIRED`: join existing or create a new transaction.
- `REQUIRES_NEW`: suspend existing and create a new transaction.
- `MANDATORY`: require an existing transaction.
- `SUPPORTS`: join if one exists.

Interview example:
- Audit logging may use `REQUIRES_NEW` so the audit record survives even if the main transaction rolls back.

### Read-only transactions
- `@Transactional(readOnly = true)` can help communicate intent and may enable optimizations, but it is not a magical performance switch for every database.

### Rollback rules
- By default, rollback happens for `RuntimeException` and `Error`.
- Checked exceptions do not trigger rollback unless configured.

### Isolation
Know the anomalies and tradeoffs:
- dirty read
- non-repeatable read
- phantom read

Good answer:
- Isolation is a correctness and contention tradeoff, not just a database checkbox.

## Dirty checking and `flush`
- JPA can detect changes to managed entities and generate SQL automatically at flush/commit time.
- `flush()` synchronizes pending changes to the database but does not necessarily commit the transaction.

Interview trap:
- People often confuse `flush` with `commit`. They are related, but not the same thing.

## Lazy vs eager loading
- `@ManyToOne` is EAGER by default.
- `@OneToMany` is LAZY by default.

Trap:
- EAGER can create large joins and over-fetching.
- LAZY can trigger N+1 or fail outside an open persistence context.

Fix patterns:
- fetch join queries
- entity graphs
- DTO projections
- batch fetching where appropriate

## N+1 problem
- Symptom: one query for the parent list and then one extra query per child association.

How to answer well:
- explain how to detect it from SQL logs or metrics
- explain why it happens
- explain one fix such as fetch join or projection

## Cascade and orphan removal
- `cascade = ...` controls which operations propagate to related entities.
- `orphanRemoval = true` deletes child entities removed from the parent collection.

Trap:
- Misusing cascade on large graphs can cause surprising writes or deletes.

## Locking
- Optimistic locking: typically version-based using `@Version`.
- Pessimistic locking: database-level locks when contention must be controlled aggressively.

Interview pattern:
- Optimistic locking is common for web apps with occasional conflicts.
- Pessimistic locking is more expensive and should be justified carefully.

## Pagination and sorting
- Use `Pageable` and `Sort` rather than loading entire tables.

Trap:
- Pagination with collection fetch joins can behave badly or produce duplicate-looking results depending on the query shape.

## Schema migration tools
- Flyway and Liquibase are the common answers for managed schema evolution.

Good answer:
- Do not rely on Hibernate auto-DDL alone in serious production systems.

## Common transaction traps
- Putting `@Transactional` on private methods and expecting it to work.
- Doing slow remote API calls inside a long database transaction.
- Returning entities directly to the web layer and triggering lazy loads later.
- Assuming repository methods alone are enough to define clear transaction boundaries.
