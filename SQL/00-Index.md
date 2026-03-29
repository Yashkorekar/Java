# SQL Interview Prep (Index)

This folder is a SQL interview-oriented knowledge base: theory, tricky edge cases, practical querying patterns, optimization, and practice questions.

## Suggested order
1. `01-SQL-Fundamentals.md`
2. `02-Queries-Joins-Subqueries.md`
3. `03-Aggregations-Window-Functions.md`
4. `04-Design-Keys-Normalization.md`
5. `05-Transactions-Concurrency.md`
6. `06-Indexes-Optimization.md`
7. `07-Advanced-SQL-Edge-Cases.md`
8. `08-Interview-Questions-And-Practice.md`
9. `09-Functions-DataTypes-And-Vendor-Differences.md`
10. `10-Recursive-CTEs-Advanced-Patterns-And-Case-Studies.md`
11. `11-Execution-Plans-Statistics-And-Tuning-Workflow.md`

## Scope
- SQL language basics and execution order
- DDL, DML, DQL, DCL, TCL
- joins, subqueries, CTEs, set operators
- grouping, aggregation, window functions
- keys, constraints, normalization, denormalization
- transactions, locks, isolation levels, anomalies
- indexes, query plans, performance tuning
- stored procedures, views, triggers, edge cases
- vendor differences, advanced functions, recursive patterns, tuning workflow
- interview-style questions and practice sets

## Important note
- SQL syntax differs a bit across databases such as MySQL, PostgreSQL, SQL Server, Oracle, and SQLite.
- These notes focus on common SQL concepts first, then point out common vendor-specific differences when interviewers usually care.

## How to use these notes
- Learn the logical query processing order properly.
- Practice joins, grouping, subqueries, and windows by writing queries from scratch.
- For interview prep, focus on the "Tricky questions" and "Common mistakes" sections.
- When a query works, also ask whether it is correct for duplicates, `NULL`, ties, and performance.

## High-value revision checklist
- Can you explain `WHERE` vs `HAVING`?
- Can you explain `INNER JOIN`, `LEFT JOIN`, `RIGHT JOIN`, `FULL JOIN`, `CROSS JOIN`, and self join?
- Can you explain why `NOT IN` can break when `NULL` is present?
- Can you use `ROW_NUMBER()`, `RANK()`, and `DENSE_RANK()` correctly?
- Can you explain clustered vs non-clustered index at a high level?
- Can you explain dirty read, non-repeatable read, and phantom read?
- Can you design a schema with PK, FK, unique, and check constraints?
- Can you solve top-N per group and duplicate-removal problems?
- Can you explain recursive CTEs, gaps-and-islands, and latest-row-per-group patterns?
- Can you explain estimated vs actual plan rows and why statistics matter?