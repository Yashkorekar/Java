# Schema Design, Keys, Constraints, and Normalization — Interview Q&A

## Keys
- `PRIMARY KEY`
  - uniquely identifies each row
  - cannot be `NULL`
- candidate key
  - any minimal column set that can uniquely identify a row
- super key
  - any column set that uniquely identifies a row, possibly with extra columns
- alternate key
  - candidate key not chosen as primary key
- `FOREIGN KEY`
  - references key in another table
- composite key
  - key made of multiple columns

## Surrogate key vs natural key
- surrogate key
  - generated identifier like `id`
  - stable and simple for joins
- natural key
  - meaningful business column like email or SSN
  - can change or have domain issues

Interview-safe answer:
- Surrogate keys are common for primary keys.
- Natural keys are often still enforced with `UNIQUE` constraints when business rules require them.

## Constraints matter
- Constraints protect data quality at the database layer.
- Application validation alone is not enough.

## Referential integrity
- Foreign keys prevent orphan child rows.
- Common actions:
  - `ON DELETE CASCADE`
  - `ON DELETE SET NULL`
  - `ON DELETE RESTRICT` or default restriction behavior

## One-to-one, one-to-many, many-to-many
- one-to-one: unique foreign key or shared primary key
- one-to-many: child table stores foreign key to parent
- many-to-many: junction table stores two foreign keys

Example many-to-many:
- `students(student_id, name)`
- `courses(course_id, name)`
- `student_courses(student_id, course_id, enrolled_at)`

## Normalization
Normalization reduces redundancy and update anomalies.

### 1NF
- Atomic values
- No repeating groups or arrays in classic relational modeling

### 2NF
- Must already be in 1NF
- No partial dependency on part of a composite key

### 3NF
- Must already be in 2NF
- No transitive dependency on non-key columns

### BCNF
- Stronger version of 3NF
- Every determinant should be a candidate key

## Anomalies
- insert anomaly
- update anomaly
- delete anomaly

## Denormalization
- Sometimes done intentionally for read performance, analytics, or simpler reporting.
- Tradeoff: faster reads vs more redundancy and harder writes.

## Entity design tips
- Choose precise data types.
- Avoid storing multiple values in one column.
- Add constraints wherever business rules are stable.
- Index foreign keys when queries join on them frequently.

## Views
- A view stores a query definition.
- It does not usually store data itself unless it is a materialized view.

## Materialized view
- Stores computed result physically.
- Great for expensive reporting queries.
- Needs refresh strategy.

## Stored procedures and functions
- Stored procedure: reusable SQL logic, may include control flow and transactions depending on vendor.
- Function: usually returns a value or table; rules differ by vendor.

## Triggers
- Trigger runs automatically on events like `INSERT`, `UPDATE`, `DELETE`.
- Use carefully.
- Interview angle: triggers can hide side effects and complicate debugging.

## Tricky questions
**Q: Can a table have multiple candidate keys but only one primary key?**
- Yes.

**Q: Should every table have a surrogate primary key?**
- Not always. Junction tables often use composite keys naturally.

**Q: Are foreign keys optional?**
- Technically sometimes omitted for scale or ingestion reasons, but from data integrity perspective they are valuable.

**Q: What is the difference between normalization and indexing?**
- Normalization is schema design for correctness and reduced redundancy.
- Indexing is a performance technique for access paths.

## Common mistakes
- Using comma-separated values in one column
- Ignoring unique constraints for business-critical natural keys
- Using triggers for core logic that should be explicit
- Over-normalizing without regard to read patterns

## Quick revision
- Know PK, FK, candidate key, composite key, surrogate key.
- Know 1NF, 2NF, 3NF, BCNF with examples.
- Be able to design one-to-many and many-to-many relationships.