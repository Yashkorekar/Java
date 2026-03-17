# Transactions, Locking, and Isolation — Interview Q&A

## What is a transaction?
- A transaction is a logical unit of work that should be completed fully or not at all.

## ACID properties
- Atomicity
  - all or nothing
- Consistency
  - transaction moves database from one valid state to another
- Isolation
  - concurrent transactions do not corrupt each other semantically
- Durability
  - committed data survives crashes according to system guarantees

## Basic transaction control

```sql
BEGIN;

UPDATE accounts
SET balance = balance - 1000
WHERE account_id = 1;

UPDATE accounts
SET balance = balance + 1000
WHERE account_id = 2;

COMMIT;
```

## `ROLLBACK` and `SAVEPOINT`
- `ROLLBACK` undoes uncommitted changes.
- `SAVEPOINT` allows partial rollback within a transaction.

## Concurrency problems
- dirty read
  - transaction reads uncommitted changes from another transaction
- non-repeatable read
  - same row read twice gives different committed values
- phantom read
  - same predicate query returns different sets of rows because new/deleted matching rows appear
- lost update
  - one transaction overwrites another transaction's change incorrectly

## Isolation levels
- `READ UNCOMMITTED`
  - weakest isolation
  - dirty reads possible in systems that support it
- `READ COMMITTED`
  - prevents dirty reads
  - non-repeatable reads and phantoms may still happen
- `REPEATABLE READ`
  - stronger guarantees for already read rows
  - phantom handling depends on DB implementation
- `SERIALIZABLE`
  - strongest isolation
  - highest overhead

## Lock types
- shared/read lock
- exclusive/write lock
- row-level lock
- page-level lock
- table-level lock

Exact locking behavior depends on engine and isolation implementation.

## Deadlock
- Two or more transactions wait on each other permanently.
- Database typically detects and aborts one transaction.

Interview-safe guidance:
- Keep transactions short.
- Update resources in consistent order.
- Use proper indexes so transactions touch fewer rows.

## Optimistic vs pessimistic locking
- optimistic locking
  - assume conflicts are rare
  - detect conflict with version/timestamp check
- pessimistic locking
  - lock earlier to prevent conflicts

## Example optimistic locking idea

```sql
UPDATE products
SET price = 1200,
    version = version + 1
WHERE product_id = 10
  AND version = 7;
```

If zero rows update, someone changed it first.

## Auto-commit
- Many tools default to auto-commit mode.
- Interview point: if each statement commits automatically, multi-step business operations can become inconsistent unless explicit transaction control is used.

## Tricky questions
**Q: Does `READ COMMITTED` prevent lost updates?**
- Not automatically in every pattern.
- Lost updates depend on how reads and writes are done.

**Q: Is `SERIALIZABLE` always the best choice?**
- Correctness is strongest, but throughput can suffer.
- Choose based on business need.

**Q: Are phantom reads the same as non-repeatable reads?**
- No.
- Non-repeatable read is same row changing.
- Phantom read is set of matching rows changing.

## Common mistakes
- Forgetting explicit transactions for related updates
- Keeping transactions open while waiting on user input or network calls
- Assuming one isolation level behaves identically across vendors
- Not retrying when deadlock or serialization failure occurs

## Quick revision
- Learn ACID properly.
- Know dirty read, non-repeatable read, phantom read, lost update.
- Know when to use commit, rollback, savepoint.