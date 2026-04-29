# Functions, Data Types, and Vendor Differences — Interview Q&A

> See **01-SQL-Fundamentals.md** for full sample tables and data.

---

## Why this file matters
Many SQL interviews start with common syntax, then quickly move into practical functions, type conversion, and database-specific differences.

## Data type interview basics
- exact numeric: `INT`, `BIGINT`, `DECIMAL(p, s)`, `NUMERIC`
- approximate numeric: `FLOAT`, `REAL`, `DOUBLE`
- text: `CHAR`, `VARCHAR`, `TEXT`
- date/time: `DATE`, `TIME`, `TIMESTAMP`, `DATETIME`
- other common modern types: `JSON`, `UUID`, `XML`, binary/blob types

## `DECIMAL` vs `FLOAT`
- `DECIMAL` is exact and preferred for money.
- `FLOAT` is approximate and can produce rounding surprises.

Interview-safe answer:
- Use `DECIMAL` for financial values unless you have a very specific reason not to.

## Type conversion
- standard SQL often uses `CAST(value AS type)`.
- many databases also support vendor-specific conversion helpers such as `CONVERT()`.

```sql
SELECT CAST('2026-03-29' AS DATE);
```

**Result:**

| cast       |
|------------|
| 2026-03-29 |

## `COALESCE`, `NULLIF`, and defensive null logic
- `COALESCE(a, b, c)` returns the first non-null value.
- `NULLIF(a, b)` returns `NULL` if `a = b`, otherwise returns `a`.

Example: avoid divide-by-zero.

```sql
SELECT revenue / NULLIF(order_count, 0) AS avg_revenue
FROM daily_metrics;
```

**Result** (assuming daily_metrics has: revenue=10000, order_count=0):

| avg_revenue |
|-------------|
| NULL        |

*Without NULLIF, this would cause a divide-by-zero error. NULLIF(0, 0) returns NULL, and any division by NULL yields NULL safely.*

## `CASE WHEN`
Very common for categorization, conditional aggregation, and safe branching.

```sql
SELECT emp_name,
       CASE
         WHEN salary >= 150000 THEN 'high'
         WHEN salary >= 80000 THEN 'mid'
         ELSE 'low'
       END AS salary_band
FROM employees;
```

**Result:**

| emp_name | salary_band |
|----------|-------------|
| Asha     | mid         |
| Bob      | mid         |
| Chitra   | mid         |
| David    | low         |
| Eva      | mid         |
| Faisal   | mid         |
| Gita     | low         |
| Hari     | low         |

## String functions interviewers expect
- `UPPER`, `LOWER`, `TRIM`, `LTRIM`, `RTRIM`
- `SUBSTRING` / `SUBSTR`
- `LENGTH` / `LEN`
- `CONCAT`
- `REPLACE`
- `POSITION` / `CHARINDEX` / `INSTR`

Example:

```sql
SELECT customer_name,
       UPPER(TRIM(customer_name)) AS normalized_name
FROM customers;
```

**Result:**

| customer_name | normalized_name |
|---------------|----------------|
| Ravi          | RAVI           |
| Sneha         | SNEHA          |
| Amit          | AMIT           |
| Priya         | PRIYA          |
| Kiran         | KIRAN          |

## Date/time functions interviewers expect
- current date/time functions such as `CURRENT_DATE`, `CURRENT_TIMESTAMP`, `NOW()`
- extraction functions such as `EXTRACT(YEAR FROM order_date)`
- date arithmetic such as `DATEADD`, `INTERVAL`, `DATEDIFF`, `AGE`

Interview trap:
- Syntax differs a lot by vendor, so answer concept first, syntax second.

## Math functions
- `ROUND`, `CEIL` / `CEILING`, `FLOOR`, `ABS`, `MOD`, `POWER`

## String concatenation differences
- standard SQL often uses `||`
- SQL Server often uses `+`
- MySQL often uses `CONCAT()`

Interview-safe answer:
- Prefer to mention the idea and then say exact syntax varies by database.

## `LIMIT`, `TOP`, and pagination differences
- MySQL/PostgreSQL/SQLite: `LIMIT ... OFFSET ...`
- SQL Server: `TOP` or `OFFSET ... FETCH`
- Oracle: modern versions support `FETCH FIRST ... ROWS ONLY`

## Upsert differences
- PostgreSQL: `INSERT ... ON CONFLICT ...`
- MySQL: `INSERT ... ON DUPLICATE KEY UPDATE`
- SQL Server/Oracle: `MERGE` is often discussed, but with caution

## Common vendor comparison table

| Topic | PostgreSQL | MySQL | SQL Server | Oracle |
|---|---|---|---|---|
| limit rows | `LIMIT` | `LIMIT` | `TOP` / `OFFSET FETCH` | `FETCH FIRST` |
| string concat | `||` | `CONCAT()` | `+` | `||` |
| current timestamp | `CURRENT_TIMESTAMP` / `NOW()` | `NOW()` | `SYSDATETIME()` / `CURRENT_TIMESTAMP` | `SYSTIMESTAMP` |
| null helper | `COALESCE` | `COALESCE`, `IFNULL` | `COALESCE`, `ISNULL` | `COALESCE`, `NVL` |
| auto id | `GENERATED ... AS IDENTITY` / serial legacy | `AUTO_INCREMENT` | `IDENTITY` | identity / sequence |

## Interview questions
**Q: Is `VARCHAR(10)` the same in every database?**
- No. Storage, trailing spaces, Unicode handling, and limits vary.

**Q: Is `CURRENT_TIMESTAMP` always transaction time or statement time?**
- Behavior details vary by database.

**Q: Why use `COALESCE` instead of vendor-specific null helpers?**
- It is standard SQL and more portable.

## Common mistakes
- using `FLOAT` for currency
- assuming one date function syntax works everywhere
- forgetting type conversion in joins or predicates
- relying on implicit casts that hurt clarity or performance

## Quick revision
- know exact vs approximate numeric types
- know `CAST`, `COALESCE`, `NULLIF`, `CASE`
- know common string/date/math functions
- know major vendor syntax differences at a high level