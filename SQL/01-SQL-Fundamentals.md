# SQL Fundamentals — Interview Q&A

## What is SQL?
- SQL stands for Structured Query Language.
- It is used to define, query, manipulate, and control relational databases.
- Interview answer: SQL is both a language for querying data and a language for managing relational schema, security, and transactions.

## Core categories
- `DDL`: Data Definition Language
  - `CREATE`, `ALTER`, `DROP`, `TRUNCATE`, `RENAME`
- `DML`: Data Manipulation Language
  - `INSERT`, `UPDATE`, `DELETE`, `MERGE`
- `DQL`: Data Query Language
  - `SELECT`
- `DCL`: Data Control Language
  - `GRANT`, `REVOKE`
- `TCL`: Transaction Control Language
  - `COMMIT`, `ROLLBACK`, `SAVEPOINT`

## Tables, rows, columns
- A table stores related data in rows and columns.
- A row is one record.
- A column is one attribute.
- A schema is the structural design of tables, relationships, constraints, and data types.

## Common data types
- Numeric: `INT`, `BIGINT`, `DECIMAL(p, s)`, `FLOAT`
- Character: `CHAR`, `VARCHAR`, `TEXT`
- Date/time: `DATE`, `TIME`, `TIMESTAMP`, `DATETIME`
- Boolean: `BOOLEAN`, `BIT`
- Binary/other: `BLOB`, `JSON`, `UUID`

## `CHAR` vs `VARCHAR`
- `CHAR(n)` is fixed length.
- `VARCHAR(n)` is variable length.
- Interview angle: `CHAR` can be efficient for fixed-size values like country code, but `VARCHAR` is more common for general strings.

## `DELETE` vs `TRUNCATE` vs `DROP`
- `DELETE`
  - removes selected rows
  - can use `WHERE`
  - usually logs row-level changes
  - can often be rolled back inside a transaction
- `TRUNCATE`
  - removes all rows quickly
  - no `WHERE`
  - often resets identity/auto-increment behavior depending on vendor
  - treated as DDL in many systems
- `DROP`
  - removes the table object itself, including metadata

## `NULL`
- `NULL` means unknown or missing, not zero and not empty string.
- Any direct comparison with `NULL` using `=` or `!=` is wrong.

```sql
SELECT *
FROM employees
WHERE manager_id IS NULL;
```

## Three-valued logic
- SQL uses `TRUE`, `FALSE`, and `UNKNOWN`.
- `NULL` in comparisons often produces `UNKNOWN`.
- Rows are returned by `WHERE` only when the predicate evaluates to `TRUE`.

## Constraints
- `PRIMARY KEY`: uniquely identifies a row; cannot be `NULL`
- `FOREIGN KEY`: enforces referential integrity
- `UNIQUE`: values must be unique; vendor rules for multiple `NULL` values differ
- `NOT NULL`: column cannot be missing
- `CHECK`: validates rule on column or row
- `DEFAULT`: provides default value when omitted

## Basic syntax pattern

```sql
SELECT column_list
FROM table_name
WHERE condition
GROUP BY grouping_columns
HAVING grouping_condition
ORDER BY sort_columns;
```

## Logical query processing order
Interviewers ask this often because it explains many SQL rules.

1. `FROM`
2. `JOIN`
3. `WHERE`
4. `GROUP BY`
5. `HAVING`
6. `SELECT`
7. `DISTINCT`
8. `ORDER BY`
9. `LIMIT` / `OFFSET` / `TOP`

## Why aliases work in `ORDER BY` but often not in `WHERE`
- Because logically `WHERE` runs before `SELECT`.
- The alias is created in `SELECT`, so it is usually not available to `WHERE`.

## `WHERE` vs `HAVING`
- `WHERE` filters rows before grouping.
- `HAVING` filters groups after aggregation.

```sql
SELECT department_id, COUNT(*) AS emp_count
FROM employees
WHERE salary > 50000
GROUP BY department_id
HAVING COUNT(*) >= 5;
```

## `DISTINCT`
- Removes duplicate rows from the final result set.
- It applies to the full selected column combination, not each column independently.

## `ORDER BY`
- Default order is ascending in most systems.
- `ASC` means ascending; `DESC` means descending.
- `NULL` sort order differs across databases.

## Common string functions
- `UPPER()`, `LOWER()`, `TRIM()`, `SUBSTRING()` / `SUBSTR()`, `LENGTH()` / `LEN()`, `CONCAT()`, `REPLACE()`

## Common numeric functions
- `ROUND()`, `CEIL()` / `CEILING()`, `FLOOR()`, `ABS()`, `MOD()`

## Common date functions
- `CURRENT_DATE`, `CURRENT_TIMESTAMP`
- vendor-specific functions such as `DATEADD`, `DATEDIFF`, `AGE`, `EXTRACT`

## Tricky questions
**Q: Is SQL case-sensitive?**
- Keywords usually are not.
- String comparison and identifiers depend on database collation and quoting rules.

**Q: Can a `UNIQUE` column store multiple `NULL` values?**
- Depends on vendor behavior and configuration.
- Interview-safe answer: do not assume identical `NULL` handling across databases.

**Q: What is the difference between schema and database?**
- High-level answer: a database is a broader container; schema is a logical structure inside it. Exact meaning varies by vendor.

## Common mistakes
- Using `= NULL` instead of `IS NULL`
- Confusing `WHERE` and `HAVING`
- Forgetting that duplicates affect aggregation and joins
- Assuming all SQL dialects support exactly the same syntax

## Quick revision
- Learn logical processing order.
- Treat `NULL` carefully.
- Know DDL, DML, DQL, DCL, TCL definitions.
- Know the difference between row filtering and group filtering.