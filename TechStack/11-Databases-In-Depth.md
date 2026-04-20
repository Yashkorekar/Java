# Databases In-Depth Interview Prep and Deep Guide

## 1. What this guide covers
- This is not about learning SQL syntax or basic CRUD. The existing MongoDB and Elasticsearch files cover those specific databases.
- This guide covers the deep database concepts that interviewers ask about: ACID, CAP theorem, indexing internals, replication, sharding, transactions, locking, MVCC, storage engines, connection pooling, query optimization, and how to choose the right database.
- These concepts apply across PostgreSQL, MySQL, MongoDB, Cassandra, DynamoDB, and any other database you will encounter.

## 2. SQL vs NoSQL — the real answer interviewers want

### What they are
- **SQL (relational)**: data in tables with rows and columns. Fixed schema. Relationships via foreign keys. Query via SQL. Examples: PostgreSQL, MySQL, Oracle, SQL Server.
- **NoSQL**: a broad category covering any non-relational database. Four main types:
  - **Document stores**: flexible JSON-like documents. Examples: MongoDB, CouchDB.
  - **Key-value stores**: simple key → value. Examples: Redis, DynamoDB (simple mode), Riak.
  - **Wide-column stores**: rows with dynamic columns, organized by column families. Examples: Cassandra, HBase, Bigtable.
  - **Graph databases**: nodes and edges for relationship-heavy data. Examples: Neo4j, Amazon Neptune.

### When to use what
- **SQL is better when**: you need ACID transactions, complex joins, strict schema enforcement, well-understood relational patterns, reporting/analytics, or your data is naturally relational.
- **NoSQL is better when**: you need flexible schema, horizontal scaling, high write throughput, denormalized read-optimized data, specific data models (documents, graphs, time-series), or your scale exceeds what a single relational server can handle.

### The real-world truth
- Most systems use BOTH. SQL for transactional core data (orders, payments, users) and NoSQL for specific needs (caching with Redis, search with Elasticsearch, event logs with Kafka).
- Choosing only one type for everything is almost always wrong.
- Senior answer: "It depends on the access pattern." Then explain the specific trade-offs.

## 3. ACID properties — deep explanation

### Atomicity
- A transaction is all-or-nothing. Either all operations succeed, or none of them take effect.
- Implementation: databases use a **write-ahead log (WAL)** or **undo log**. Before making changes, the database writes what it plans to do to the log. If a crash happens mid-transaction, the database uses the log to undo partial changes on recovery.
- Example: transferring money from account A to account B. Debit A and credit B must both happen, or neither happens.

### Consistency
- A transaction moves the database from one valid state to another. All constraints (unique keys, foreign keys, check constraints) are satisfied after the transaction.
- This is about application-level correctness, not distributed systems consistency.
- The database enforces schema rules. The application defines business rules.

### Isolation
- Concurrent transactions behave as if they ran sequentially. One transaction does not see the intermediate state of another.
- In practice, full isolation (serializable) is expensive. Databases offer isolation levels with different trade-offs.
- Isolation levels (from weakest to strongest):

| Level | Dirty read | Non-repeatable read | Phantom read | Performance |
| --- | --- | --- | --- | --- |
| Read Uncommitted | Possible | Possible | Possible | Fastest |
| Read Committed | Prevented | Possible | Possible | Good |
| Repeatable Read | Prevented | Prevented | Possible (varies) | Moderate |
| Serializable | Prevented | Prevented | Prevented | Slowest |

- **Dirty read**: seeing uncommitted changes from another transaction.
- **Non-repeatable read**: reading the same row twice and getting different values because another transaction committed in between.
- **Phantom read**: running the same query twice and getting different rows because another transaction inserted/deleted rows.
- Most production systems use **Read Committed** (PostgreSQL default) or **Repeatable Read** (MySQL InnoDB default).

### Durability
- Once a transaction is committed, it stays committed even if the system crashes.
- Implementation: WAL ensures committed transactions are on disk. After a crash, the database replays the WAL to recover committed transactions and undo uncommitted ones.

## 4. CAP theorem — the real explanation

### What it says
- In a distributed system, you can have at most two of three properties:
  - **Consistency (C)**: every read receives the most recent write or an error.
  - **Availability (A)**: every request receives a response (not necessarily the most recent data).
  - **Partition tolerance (P)**: the system continues operating despite network partitions between nodes.

### Why it matters
- Network partitions WILL happen in distributed systems. So you must choose between C and A during a partition.
- **CP systems**: during a partition, the system returns an error or blocks rather than returning stale data. Examples: etcd, ZooKeeper, HBase, MongoDB (with `w: majority`, `readConcern: majority`).
- **AP systems**: during a partition, the system continues serving requests but may return stale data. Examples: Cassandra, DynamoDB, Eureka, CouchDB.

### Common misconceptions
- CAP does not mean you permanently sacrifice one property. It is about behavior DURING a partition.
- When there is no partition (normal operation), you can have all three.
- CAP is a simplification. Real systems offer tunable consistency (e.g., Cassandra's consistency levels, DynamoDB's strong vs eventual consistency reads).
- **PACELC theorem** is a more nuanced model: "if Partition, choose Availability or Consistency; Else, choose Latency or Consistency." This captures the fact that even without partitions, there is a latency-consistency trade-off.

### Practical interviewer framing
- When an interviewer asks "is this CP or AP?", they want to know: during a failure, does this system prioritize giving correct data or giving any data?
- PostgreSQL (single node) is not really a "CAP" system because CAP applies to distributed systems. A single-node RDBMS is consistent and available but has no partition tolerance (it is a single point of failure).

## 5. Indexing internals — how databases find data fast

### B-tree / B+ tree
- The most common index structure in relational databases (PostgreSQL, MySQL InnoDB, Oracle).
- A balanced tree where:
  - Internal nodes store keys and pointers to child nodes.
  - Leaf nodes store keys and pointers to the actual data (or the data itself in clustered indexes).
  - In B+ trees (most common), all data/pointers are in leaf nodes, and leaf nodes are linked for efficient range scans.
- **Lookup**: O(log N). Start at root, navigate down to the leaf.
- **Range scan**: find the starting leaf, then follow leaf-to-leaf pointers.
- **Insertion**: find the correct leaf, insert. If the leaf is full, split it and propagate upward.
- Why B+ trees are good for databases: they minimize disk I/O. Each node is typically one disk page (4-16KB). A 3-4 level tree can index billions of rows with only 3-4 disk reads per lookup.

### LSM tree (Log-Structured Merge-tree)
- Used by write-heavy databases: Cassandra, RocksDB, LevelDB, HBase.
- How it works:
  1. Writes go to an in-memory buffer (memtable).
  2. When the memtable is full, it is flushed to disk as an immutable sorted file (SSTable).
  3. Over time, multiple SSTables accumulate. Background compaction merges and sorts them.
- **Write performance**: excellent because writes are sequential (append to memtable, flush to disk sequentially). No random disk I/O.
- **Read performance**: potentially slower because a read may need to check the memtable plus multiple SSTables. Bloom filters are used to skip SSTables that definitely do not contain the key.
- **Compaction cost**: background merging consumes CPU and I/O. Can cause latency spikes if not tuned well.
- Trade-off: LSM is write-optimized, B-tree is read-optimized. This is the fundamental choice in database storage engine design.

### Hash index
- Maps keys to storage locations using a hash function. O(1) lookups for exact key matches.
- Cannot do range queries (WHERE price > 100) because there is no ordering.
- Used by: memory-optimized databases, hash-based partitioning, Redis internally.

### Covering index and index-only scans
- A **covering index** includes all the columns needed by a query. The database can satisfy the query entirely from the index without reading the actual table rows.
- This eliminates random I/O to the table (the heap) and is much faster.
- Example: `CREATE INDEX idx_order_status ON orders(status, total)`. A query `SELECT total FROM orders WHERE status = 'PAID'` can be served entirely from this index.

### Clustered vs non-clustered index
- **Clustered index**: the table data is physically sorted by the index key. Only one clustered index per table (because data can only be sorted one way). In MySQL InnoDB, the primary key is always the clustered index.
- **Non-clustered (secondary) index**: a separate structure that stores the index key and a pointer to the actual row. Multiple secondary indexes per table.
- Secondary index lookups in InnoDB: the secondary index stores the primary key value (not a physical row pointer). A secondary index lookup finds the primary key, then does another B-tree lookup on the primary key index to find the actual row. This is called a "double lookup."

## 6. Replication — keeping copies of data

### Why replicate
- High availability: if the primary fails, a replica takes over.
- Read scaling: distribute read queries across replicas.
- Geographic distribution: place replicas closer to users.
- Backup: replicas provide a near-real-time backup.

### Replication modes
- **Synchronous**: the primary waits for replicas to confirm the write before acknowledging to the client. Stronger durability but higher latency.
- **Asynchronous**: the primary acknowledges immediately after writing locally. Replicas receive changes later. Faster but replicas may lag, and data can be lost if the primary crashes before replicating.
- **Semi-synchronous**: the primary waits for at least one replica to confirm, then acknowledges. Balance between durability and latency. Used by MySQL semi-sync replication.

### Replication topologies
- **Single-leader (primary-replica)**: one node accepts writes; all others replicate from it. Most common. PostgreSQL, MySQL, MongoDB replica sets.
  - Pros: simple, consistent (one source of truth for writes).
  - Cons: single write bottleneck, failover complexity.
- **Multi-leader**: multiple nodes accept writes and replicate to each other. Used for multi-datacenter setups.
  - Pros: write availability in every datacenter, lower write latency (local writes).
  - Cons: write conflicts. If two leaders accept conflicting writes (e.g., both update the same row differently), conflict resolution is needed. Strategies: last-writer-wins (LWW), custom merge logic, CRDTs.
  - Examples: CockroachDB, MySQL Group Replication, Cassandra (technically leaderless but similar conflict issues).
- **Leaderless**: any node can accept reads and writes. The client writes to multiple nodes and reads from multiple nodes. Quorum-based consistency.
  - Pros: no single point of failure, high availability.
  - Cons: complex conflict resolution, eventual consistency unless quorum is strict.
  - Examples: Cassandra, DynamoDB, Riak.

### Replication lag
- In async replication, the replica may be behind the primary. The difference is replication lag.
- Problems caused by lag:
  - **Read-after-write inconsistency**: user writes data, then reads it from a replica that has not received the write yet. The user thinks their write was lost.
  - **Monotonic read inconsistency**: user reads from one replica (gets new data), then reads from another replica (gets older data). Time seems to go backward.
- Solutions: read from primary for recent writes, use sticky sessions to a single replica, use read-concern majority (MongoDB), or use logical clocks/versions.

## 7. Sharding (partitioning) — splitting data across nodes

### Why shard
- When a single database server cannot handle the data size, write throughput, or read throughput, you split data across multiple servers.
- Each shard holds a subset of the data.

### Sharding strategies
- **Range-based sharding**: divide the key space into contiguous ranges. Shard 1 gets keys A-M, shard 2 gets N-Z.
  - Pros: range queries are efficient (scan one or few shards).
  - Cons: hotspots if the workload is skewed to certain ranges. Monotonically increasing keys (timestamps, auto-increment) cause all writes to hit the last shard.
- **Hash-based sharding**: apply a hash function to the key and distribute based on hash value. Shard = hash(key) % N.
  - Pros: even distribution, no hotspots for most access patterns.
  - Cons: range queries are expensive (must hit all shards). Changing shard count redistributes most keys (unless using consistent hashing).
- **Directory-based sharding**: a lookup table maps keys to shards. Flexible but the directory is a bottleneck and single point of failure.
- **Geographic sharding**: shard by geography (e.g., US data in shard-US, EU data in shard-EU). Good for data locality requirements and compliance (GDPR).

### Challenges
- **Cross-shard queries**: queries that span multiple shards are expensive. They require scatter-gather or coordination.
- **Cross-shard transactions**: very expensive. Requires distributed transactions (2PC). Many NoSQL databases do not support them.
- **Rebalancing**: when shards become uneven (data skew), you need to move data between shards. This is operationally complex.
- **Shard key choice**: the most critical decision. A bad shard key creates hotspots, uneven distribution, or makes common queries expensive.
- **Schema changes**: must be applied to all shards, which adds deployment complexity.

### Practical interviewer answer
- "Shard only when you need to. Vertical scaling (bigger machine), read replicas, caching, and query optimization should be tried first. Sharding adds enormous operational complexity."

## 8. Transactions and distributed transactions

### Single-node transactions
- Standard ACID transactions on one database server. Well-understood, efficient.
- Use: BEGIN → operations → COMMIT (or ROLLBACK).

### Distributed transactions (two-phase commit / 2PC)
- When a transaction spans multiple databases or shards.
- Phase 1 (Prepare): the coordinator asks all participants "can you commit?" Each participant does the work, writes to its WAL, and responds YES or NO.
- Phase 2 (Commit): if all said YES, the coordinator sends COMMIT to all. If any said NO, the coordinator sends ROLLBACK to all.
- Problems with 2PC:
  - **Blocking**: if the coordinator crashes after prepare but before commit/rollback, participants are stuck holding locks. They cannot commit or rollback until the coordinator recovers.
  - **Latency**: two network round-trips plus disk writes on every participant.
  - **Availability**: if any participant is down, the transaction cannot proceed.
- This is why distributed transactions are avoided when possible.

### Saga pattern (alternative to distributed transactions)
- Instead of one big atomic transaction, break it into a sequence of local transactions.
- Each service performs its local transaction and publishes an event.
- If a step fails, compensating transactions are executed to undo previous steps.
- Two styles:
  - **Choreography**: each service listens for events and decides what to do. Decentralized but hard to track.
  - **Orchestration**: a central saga orchestrator directs the sequence. Easier to reason about but the orchestrator is a dependency.
- Example: placing an order → deduct payment → reserve inventory → confirm order. If inventory reservation fails, compensate by refunding the payment.
- Trade-off: sagas provide eventual consistency, not strict atomicity. You must design for intermediate states and compensations.

## 9. Locking strategies

### Pessimistic locking
- Lock the resource before modifying it. Other transactions must wait.
- Types:
  - **Row-level lock**: locks a single row. Fine-grained, allows high concurrency. Most modern databases default to this (PostgreSQL, MySQL InnoDB).
  - **Table-level lock**: locks the entire table. Coarse-grained, low concurrency. Older MySQL MyISAM used this.
  - **Shared (read) lock**: multiple transactions can hold a shared lock simultaneously. Prevents writes but allows reads.
  - **Exclusive (write) lock**: only one transaction can hold it. Blocks all other reads and writes to the locked resource.
- SQL: `SELECT ... FOR UPDATE` acquires an exclusive row lock.
- Pros: prevents conflicts. Cons: reduces concurrency, can cause deadlocks.

### Optimistic locking
- Do not lock. Instead, read the data, do work, and at write time, check if anyone else changed it. If yes, retry.
- Implementation: use a **version column** or **timestamp**. On update: `UPDATE ... WHERE id = ? AND version = ?`. If the row count is 0, someone else modified it — retry.
- Pros: high concurrency when conflicts are rare. Cons: retries are expensive when conflicts are frequent.
- Best fit: read-heavy workloads with infrequent write conflicts.

### Deadlocks
- Two transactions each hold a lock the other needs. Neither can proceed.
- Solution: databases detect deadlocks (typically via wait-for graph analysis) and abort one transaction. The aborted transaction retries.
- Prevention: always acquire locks in a consistent order. Keep transactions short.

## 10. MVCC (Multi-Version Concurrency Control)

### What it is
- Instead of locking rows for reads, the database keeps multiple versions of each row. Readers see a snapshot of the data as of their transaction's start time.
- Writers create new versions. Readers see old versions. This means readers do not block writers and writers do not block readers.

### How it works (PostgreSQL example)
- Each row has hidden columns: `xmin` (transaction ID that created this version) and `xmax` (transaction ID that deleted/updated this version, creating a new version).
- When transaction T1 reads a row, it sees the version where `xmin ≤ T1's snapshot` and `xmax` is either empty or not yet committed in T1's view.
- When transaction T2 updates a row, it does not overwrite. It creates a NEW row version with `xmin = T2` and marks the old version with `xmax = T2`.
- **Vacuum**: old row versions that are no longer visible to any transaction are garbage collected by VACUUM (PostgreSQL) or purge threads (MySQL InnoDB).

### How it works (MySQL InnoDB)
- InnoDB stores the latest version in the B-tree and keeps old versions in the **undo log** (rollback segment).
- When a transaction needs an older version, it reconstructs it from the undo log.
- Purge threads clean up old undo log entries when no transaction needs them.

### Why MVCC matters for interviews
- It explains why PostgreSQL and MySQL can handle concurrent reads and writes efficiently.
- It explains snapshot isolation and read consistency.
- It explains why long-running transactions can cause problems: they prevent old row versions from being garbage collected (table bloat in PostgreSQL, undo log growth in MySQL).

## 11. Write-ahead log (WAL)

### What it is
- Before modifying actual data pages, the database writes the change to a sequential log file.
- If the system crashes, the log is replayed on startup to recover committed changes and undo uncommitted ones.

### Why sequential writes matter
- Writing to the WAL is sequential I/O (appending), which is very fast on both HDDs and SSDs.
- Modifying data pages is random I/O (updating pages at arbitrary locations), which is slower.
- The WAL allows the database to acknowledge a commit quickly (after sequential WAL write) and apply the actual data page changes lazily in the background.

### WAL and replication
- In PostgreSQL, the WAL is also used for replication. Streaming replication sends WAL records to replicas, which replay them to stay in sync.
- This is why it is called "physical replication" — it replicates the exact byte-level changes.

## 12. Connection pooling

### The problem
- Creating a database connection is expensive: TCP handshake, authentication, session setup, memory allocation on the server.
- A busy application with many threads, each creating and closing connections, can overwhelm the database.
- Most databases have a connection limit (e.g., PostgreSQL default is 100).

### Solution: connection pool
- A pool of pre-established connections is maintained. Application threads borrow a connection from the pool, use it, and return it.
- Pool settings:
  - **Minimum pool size**: connections kept open even when idle. Avoids cold start latency.
  - **Maximum pool size**: upper bound. Prevents overloading the database.
  - **Connection timeout**: how long to wait for a connection from the pool before failing.
  - **Idle timeout**: how long an idle connection stays in the pool before being closed.
  - **Max lifetime**: how long a connection lives before being recycled (prevents stale connections due to DNS changes, firewall timeouts, etc.).

### Application-level pools
- **HikariCP**: the standard in the Java ecosystem. Very fast, reliable, well-tuned defaults. Spring Boot uses it by default.
- Key HikariCP settings: `maximumPoolSize` (usually 10-20 for most apps), `minimumIdle`, `connectionTimeout`, `idleTimeout`, `maxLifetime`.
- Sizing formula (rough): connections ≈ ((core_count * 2) + effective_spindle_count). For SSD-based systems, 10-20 connections per application instance is often optimal. More is NOT always better — too many connections cause context switching and contention on the database.

### External connection poolers
- **PgBouncer**: a lightweight connection pooler for PostgreSQL. Sits between the application and PostgreSQL. Multiplexes thousands of application connections into a small number of actual database connections.
- **ProxySQL**: a MySQL proxy that does connection pooling, read/write splitting, query routing, and more.
- Why use them: when you have many application instances, each with its own pool, the total connections can exceed the database limit. An external pooler consolidates them.

## 13. Query optimization

### How a query executes
- **Parser**: checks SQL syntax.
- **Analyzer**: resolves table and column names, checks permissions.
- **Optimizer**: generates multiple query plans, estimates their cost (using table statistics), and picks the cheapest one.
- **Executor**: runs the chosen plan.

### Understanding EXPLAIN
- `EXPLAIN` (or `EXPLAIN ANALYZE` for actual execution stats) shows the query plan the optimizer chose.
- Key things to look for:
  - **Seq Scan** (PostgreSQL) / **Full Table Scan** (MySQL): reads every row. Bad for large tables. Indicates a missing index.
  - **Index Scan** / **Index Only Scan**: good. Uses an index.
  - **Nested Loop Join**: for each row in table A, scan table B. Efficient when one side is small.
  - **Hash Join**: build a hash table from one side, probe with the other. Good for larger joins.
  - **Merge Join**: both sides sorted, merge them. Good when both sides are large and sorted.
  - **Sort**: may indicate an expensive sort operation. Check if an index can provide the order.
  - **Estimated rows vs actual rows**: if these are wildly different, table statistics are stale. Run `ANALYZE` (PostgreSQL) or `ANALYZE TABLE` (MySQL).

### Common optimization techniques
- **Add appropriate indexes**: the single most impactful optimization.
- **Use covering indexes**: avoid table lookups.
- **Avoid SELECT ***: fetch only needed columns.
- **Fix N+1 queries**: fetching a list and then querying each item individually. Use JOINs or batch fetches instead.
- **Use LIMIT**: if you only need a few rows, limit the result set.
- **Partition large tables**: split by date, region, or other criteria so queries only scan relevant partitions.
- **Denormalize for read performance**: store pre-computed data to avoid expensive joins (trade write complexity for read speed).
- **Use materialized views**: pre-computed query results that refresh periodically. Good for expensive aggregate queries.

## 14. The N+1 query problem

### What it is
- You run 1 query to fetch N parent items, then N additional queries to fetch related data for each parent.
- Example: `SELECT * FROM orders` (1 query, returns 100 orders). Then for each order: `SELECT * FROM order_items WHERE order_id = ?` (100 queries). Total: 101 queries instead of 1-2.

### Why it is bad
- Each query has overhead: network round-trip to the database, parsing, execution, result transfer.
- 100 round-trips take 100x longer than 1 round-trip.
- Under load, this can overwhelm the database connection pool.

### Solutions
- **JOIN**: `SELECT * FROM orders JOIN order_items ON orders.id = order_items.order_id`.
- **Batch fetch**: `SELECT * FROM order_items WHERE order_id IN (1, 2, 3, ..., 100)`.
- **ORM features**: JPA/Hibernate `@BatchSize`, `JOIN FETCH` in JPQL, eager loading with `@EntityGraph`.
- **DataLoader pattern**: used in GraphQL. Batches multiple individual fetches into one query per batch cycle.

## 15. Normalization vs denormalization

### Normalization
- Organizing data to reduce redundancy. Each fact is stored once.
- Normal forms: 1NF → 2NF → 3NF → BCNF (each eliminates more redundancy).
- Pros: less storage waste, consistent updates (change one place), cleaner data model.
- Cons: queries often need JOINs, which can be slow on large datasets.

### Denormalization
- Intentionally introducing redundancy for read performance.
- Example: storing the customer name directly in the order table instead of joining with the customers table.
- Pros: faster reads, simpler queries, fewer JOINs.
- Cons: data can become inconsistent (customer name updated in one place but not another), more complex writes.

### When to denormalize
- When read performance is critical and the data is read much more than it is written.
- When the JOINs required for normalized data are too expensive at scale.
- In NoSQL databases, denormalization is often the default approach because JOINs are not natively supported.

## 16. Database-specific deep notes

### PostgreSQL
- MVCC with row versioning (xmin/xmax). Vacuum is critical for health.
- WAL-based replication.
- Rich index types: B-tree, hash, GiST, GIN (for full-text, JSONB, arrays), BRIN (for large sequential data).
- Supports JSON/JSONB columns natively, making it a viable "relational + document" hybrid.
- Extensions: PostGIS (geospatial), pg_trgm (trigram similarity), TimescaleDB (time-series), Citus (sharding).
- Connection pooling: use HikariCP in app + PgBouncer externally for large-scale deployments.
- Common interview fact: PostgreSQL default isolation level is Read Committed.

### MySQL (InnoDB)
- MVCC with undo logs. Purge threads clean old versions.
- Clustered index on primary key. Secondary indexes store primary key values.
- Replication: binary log (binlog) based. Supports statement-based, row-based, or mixed replication.
- Group Replication for multi-primary setups.
- Common interview fact: MySQL InnoDB default isolation level is Repeatable Read (uses gap locking to prevent phantom reads).
- Connection pooling: HikariCP in app + ProxySQL externally.

### Cassandra
- Wide-column store. LSM tree storage engine (SSTables + memtables).
- Leaderless replication. Any node can accept reads and writes.
- Tunable consistency: `ONE`, `QUORUM`, `ALL`, `LOCAL_QUORUM`, etc.
- Partition key determines data distribution. Clustering key determines sort order within a partition.
- Designed for: high write throughput, time-series data, IoT, event logging, geo-distributed deployments.
- Anti-pattern: ad-hoc queries, heavy updates/deletes, strong transactions.
- No JOINs. Design your table around your query. One table per query pattern.

### DynamoDB
- Managed key-value and document database by AWS.
- Partition key (hash key) + optional sort key (range key).
- Provisioned or on-demand capacity modes.
- Global tables for multi-region replication.
- DynamoDB Streams for CDC (change data capture) events.
- Single-digit millisecond reads and writes at any scale.
- Limitations: 400KB item size limit, limited query flexibility (primary key + sort key + optional secondary indexes), complex data modeling.

## 17. Choosing the right database — decision framework

| Requirement | Best fit | Why |
| --- | --- | --- |
| ACID transactions, complex joins, strong consistency | PostgreSQL, MySQL | Purpose-built for relational workloads |
| Flexible schema, document storage, moderate scale | MongoDB | Document model with indexing and replication |
| Very high write throughput, time-series, IoT | Cassandra, DynamoDB, TimescaleDB | Write-optimized engines, horizontal scaling |
| Full-text search, autocomplete, ranking | Elasticsearch | Inverted indexes, BM25 scoring, analyzers |
| Cache, session store, counters, rate limiting | Redis | In-memory, sub-millisecond latency |
| Graph relationships (social networks, recommendations) | Neo4j, Neptune | Native graph traversal |
| Global scale, strong consistency, SQL compatibility | CockroachDB, Spanner | NewSQL — distributed SQL with strong consistency |
| Serverless, managed, AWS-native | DynamoDB | Fully managed, scales automatically |

## 18. Common mistakes
- Choosing a database based on hype instead of access patterns.
- Not adding indexes and then blaming the database for being slow.
- Adding too many indexes and slowing down writes.
- Sharding too early when read replicas, caching, and query optimization would suffice.
- Using ORM-generated queries without ever running EXPLAIN.
- Ignoring connection pooling and overwhelming the database with connections.
- Storing everything in one database instead of using the right tool for each workload.
- Assuming NoSQL means no schema discipline.
- Using distributed transactions when the Saga pattern would be simpler and more resilient.

## 19. Tricky interview questions and answers

### Q1. What is the difference between ACID and BASE?
- ACID: Atomicity, Consistency, Isolation, Durability. Strong guarantees for transactional systems (SQL databases).
- BASE: Basically Available, Soft state, Eventually consistent. Weaker guarantees that trade consistency for availability and performance (many NoSQL systems).
- It is a spectrum, not a binary choice. Many databases offer tunable consistency.

### Q2. Can MongoDB do ACID transactions?
- Yes, since version 4.0 (single replica set) and 4.2 (sharded). But transactions have limitations (timeout, performance overhead, locking contention). If transactions dominate your workload, SQL is usually a better fit.

### Q3. What is the difference between horizontal and vertical scaling?
- Vertical: bigger machine (more CPU, RAM, disk). Simple but has limits and is expensive.
- Horizontal: more machines (sharding, replication). Complex but scales further.

### Q4. Why is sharding hard?
- Shard key choice is critical and often irreversible. Cross-shard queries and transactions are expensive. Rebalancing data is complex. Schema changes must hit all shards.

### Q5. What is a deadlock?
- Two transactions waiting for each other's locks. The database detects this and kills one. Design for consistent lock ordering and short transactions.

### Q6. What is connection pooling and why does it matter?
- Reusing database connections instead of creating new ones per request. Without pooling, connection overhead can dominate request latency and exhaust database limits.

### Q7. What is the N+1 problem?
- Fetching N items with N+1 queries instead of 1-2 optimized queries. Solved by JOINs, batch fetches, or ORM features.

### Q8. Why do long-running transactions cause problems?
- They hold locks (reducing concurrency), prevent MVCC cleanup (table bloat in PostgreSQL, undo log growth in MySQL), and increase the chance of deadlocks.

### Q9. What is the difference between a clustered and non-clustered index?
- Clustered: table data is physically sorted by the index. One per table. Non-clustered: separate structure pointing to the data. Multiple per table.

### Q10. How does a database recover from a crash?
- On startup, replay the WAL: redo committed transactions that were not flushed to data pages, undo uncommitted transactions. This is called crash recovery.

## 20. Senior-Level Deep Follow-up Questions

### DQ1. How does PostgreSQL VACUUM work and why is it critical?
- PostgreSQL's MVCC creates dead tuples (old row versions) when rows are updated or deleted. These dead tuples are not immediately removed.
- **VACUUM** reclaims space occupied by dead tuples and updates the visibility map (used for index-only scans) and the free space map.
- **Autovacuum**: a background process that runs VACUUM automatically based on configurable thresholds (default: when 20% of rows are dead or 50 dead rows, whichever is larger).
- **VACUUM FULL**: rewrites the entire table, reclaiming all space. But it acquires an exclusive lock on the table (blocking all reads and writes) for the entire duration. Very expensive.
- **What happens if VACUUM does not run**:
  - Table bloat: the table file grows as dead tuples accumulate. Queries scan more data.
  - Index bloat: indexes also grow as they reference dead tuples.
  - Transaction ID wraparound: PostgreSQL uses 32-bit transaction IDs. After ~2 billion transactions, IDs wrap around. VACUUM updates the "frozen" status of old tuples to prevent wraparound. If this does not happen, PostgreSQL enters emergency shutdown to prevent data corruption.
- Senior insight: autovacuum must be properly tuned for write-heavy tables. Many production PostgreSQL outages trace back to autovacuum not keeping up.

### DQ2. How does MySQL InnoDB's buffer pool work and why is sizing critical?
- The **buffer pool** is InnoDB's main memory area. It caches data pages and index pages.
- When a query needs data, InnoDB first checks the buffer pool. If the page is there (buffer pool hit), no disk I/O is needed. If not (miss), the page is loaded from disk.
- The buffer pool hit rate should be 99%+ in a healthy system. `SHOW ENGINE INNODB STATUS` reports this.
- Sizing: set `innodb_buffer_pool_size` to 60-80% of total RAM on a dedicated database server. Too small = excessive disk I/O. Too large = OS runs out of memory for filesystem cache and other processes.
- **Change buffer**: a part of the buffer pool that caches changes to secondary indexes. When a secondary index page is not in the buffer pool, the change is recorded in the change buffer instead of reading the page from disk. The changes are merged later when the page is eventually read. This dramatically improves write performance for tables with many secondary indexes.
- **Adaptive hash index**: InnoDB automatically builds hash indexes in memory for frequently accessed pages, providing O(1) lookups for hot data.

### DQ3. How does a database optimizer decide between a sequential scan and an index scan?
- The optimizer maintains **statistics** about tables: row count, data distribution (histograms), null percentage, distinct value count per column.
- For each query, the optimizer generates multiple plans and estimates the cost of each based on these statistics.
- **When a sequential scan is better**: if the query needs a large fraction of the table (e.g., >10-20% of rows), sequential scan is cheaper because it reads pages sequentially (benefiting from disk read-ahead) while an index scan does random I/O (jump to index → jump to table row → repeat).
- **When an index scan is better**: if the query is highly selective (fetches a small fraction of rows), the few random I/O operations are cheaper than scanning the entire table.
- **Stale statistics** can cause the optimizer to choose the wrong plan. This is why `ANALYZE` (PostgreSQL) and `ANALYZE TABLE` (MySQL) must run periodically.
- **Index hints**: you can force a specific index (`USE INDEX`, `FORCE INDEX` in MySQL; `SET enable_seqscan = off` in PostgreSQL), but this is usually a sign of a problem, not a solution.

### DQ4. What is a write-ahead log (WAL) and how does crash recovery work step by step?
- WAL records every change BEFORE it is applied to data pages.
- On normal operation:
  1. Transaction modifies data.
  2. The change is written to the WAL (sequential write, very fast).
  3. The transaction is committed (WAL is fsynced to disk).
  4. The actual data page modification happens later (checkpoint, background writer).
- On crash recovery:
  1. Database starts and reads the WAL.
  2. **Redo phase**: replays WAL entries for committed transactions whose changes were not yet applied to data pages.
  3. **Undo phase**: rolls back any changes from transactions that were in progress but not committed at crash time.
  4. After recovery, the database is in a consistent state.
- **Checkpoints**: periodically, the database writes all dirty pages to disk and records a checkpoint in the WAL. During recovery, only WAL entries after the last checkpoint need to be replayed. This bounds recovery time.
- Senior insight: WAL is the foundation of durability, replication, and point-in-time recovery (PITR). Understanding it is essential for any database work.

### DQ5. How does database sharding handle cross-shard JOIN queries?
- Short answer: badly. This is why sharding is hard.
- If you need to JOIN data from shard A and shard B:
  1. Query shard A for the relevant rows.
  2. Query shard B for the relevant rows.
  3. The application (or a middleware layer) performs the join in memory.
- This is expensive: multiple network round-trips, data transfer, memory overhead for the join.
- Strategies to avoid cross-shard joins:
  - **Denormalize**: store all the data you need for a query on the same shard. Trade storage for query efficiency.
  - **Shard key co-location**: design the shard key so that related data lands on the same shard. For example, shard by `tenant_id` so all of a tenant's orders, items, and payments are on one shard.
  - **Materialized views or CQRS**: pre-compute joined data into a read-optimized store (Elasticsearch, Redis, a read replica with denormalized tables).
  - **Application-level joins**: accept the overhead for rare cross-shard queries.
- NewSQL databases (CockroachDB, YugabyteDB, Spanner) handle distributed JOINs more gracefully because they have built-in distributed query execution, but it still has a performance cost.

### DQ6. What is the difference between optimistic and pessimistic locking in practice?
- **Pessimistic**: `SELECT ... FOR UPDATE`. Locks the rows immediately. Other transactions must wait. Good when conflicts are frequent (e.g., high-contention inventory updates).
  - Risk: if the transaction takes too long, other transactions queue up. Can cause deadlocks if two transactions lock rows in different orders.
- **Optimistic**: read with version number, update with `WHERE version = ?`. If version changed, retry. Good when conflicts are rare (e.g., user profile updates — unlikely two users update the same profile simultaneously).
  - Risk: if conflicts are frequent, retries waste resources and can cause livelock.
- In practice:
  - E-commerce inventory (limited stock, many concurrent buyers): pessimistic locking (or `SELECT ... FOR UPDATE SKIP LOCKED` for queue-like processing).
  - User settings update: optimistic locking.
  - Distributed systems without shared database: optimistic locking with version/timestamp is the default because you cannot take a row lock across separate databases.

### DQ7. How does Cassandra's consistency model work? What is tunable consistency?
- Cassandra is leaderless with configurable consistency levels per operation.
- **Replication factor (RF)**: number of copies. RF=3 means every row is stored on 3 nodes.
- **Write consistency**: how many replicas must acknowledge a write:
  - `ONE`: fastest, least durable. Only 1 replica confirms.
  - `QUORUM`: majority (RF/2 + 1). For RF=3, 2 replicas must confirm.
  - `ALL`: all replicas must confirm. Slowest, strongest.
- **Read consistency**: how many replicas must respond to a read:
  - `ONE`: fastest, might read stale data.
  - `QUORUM`: majority. Combined with `QUORUM` writes, this guarantees reading the latest write.
  - `ALL`: all replicas must respond. Slowest.
- **Rule for strong consistency**: `R + W > RF`. For RF=3 with QUORUM reads and QUORUM writes: 2 + 2 > 3 ✓. The read is guaranteed to see at least one replica that has the latest write.
- **Read repair**: when Cassandra reads from multiple replicas and detects inconsistency, it updates the stale replicas in the background.
- **Hinted handoff**: if a replica is temporarily down during a write, another node stores the write as a "hint" and forwards it when the replica recovers.
- Senior insight: Cassandra gives you the knob to choose between speed and consistency per query, which is powerful but requires careful design.

### DQ8. What is the difference between logical and physical replication?
- **Physical replication**: replicates the exact bytes of WAL entries. The replica applies the same low-level changes to its data files.
  - PostgreSQL streaming replication does this.
  - Pros: fast, simple, exact byte-level copy.
  - Cons: replica must be the same PostgreSQL major version. Cannot replicate selectively (specific tables or databases).
- **Logical replication**: replicates at the logical level (SQL operations or row-level changes decoded from the WAL).
  - PostgreSQL logical replication, MySQL binlog replication (row-based).
  - Pros: can replicate specific tables, can replicate between different database versions, can replicate to different database systems (e.g., PostgreSQL to Elasticsearch via Debezium).
  - Cons: slightly more overhead, more complex, may not handle DDL changes automatically.
- **Change Data Capture (CDC)**: reading the logical changes (WAL or binlog) and streaming them to external systems. Debezium is the most popular tool for this, streaming changes from PostgreSQL/MySQL/MongoDB to Kafka.

### DQ9. How do you handle database migrations without downtime?
- The challenge: ALTER TABLE operations can lock the table for the entire duration, blocking all reads and writes.
- Strategies:
  - **Online schema change tools**: `pt-online-schema-change` (Percona, for MySQL), `pg_repack` (PostgreSQL), `gh-ost` (GitHub, for MySQL). These create a new table with the desired schema, copy data in the background, then swap.
  - **Expand-contract pattern**:
    1. **Expand**: add the new column/table without removing the old one. Deploy code that writes to both old and new.
    2. **Migrate**: backfill existing data to the new column/table.
    3. **Contract**: deploy code that reads only from the new location. Remove the old column/table.
  - **PostgreSQL-specific**: many ALTER TABLE operations in PostgreSQL do NOT lock the table (e.g., adding a column with no default is instant). But adding a column with a default value used to lock in older versions (fixed in PostgreSQL 11+, which stores the default in the catalog without rewriting rows).
- Senior insight: database migrations are one of the most operationally dangerous activities. Test on a replica first, have a rollback plan, and use tools designed for online changes.

### DQ10. How does connection pool sizing actually work? What happens when you have too many connections?
- Each database connection consumes server resources: memory (work_mem per sort/hash, shared buffers), a backend process (PostgreSQL forks a process per connection), file descriptors, and CPU for context switching.
- **Too few connections**: application threads wait for a connection, increasing request latency.
- **Too many connections**: the database spends more time context-switching between connections than doing useful work. Memory usage increases, cache pressure grows, and performance degrades.
- **The HikariCP formula** (from the HikariCP wiki): `connections = ((core_count * 2) + effective_spindle_count)`. For a 4-core machine with SSD, ~10 connections is often optimal. This sounds low, but it is correct — with fast queries and efficient pooling, 10 connections can serve thousands of concurrent web requests.
- **PgBouncer transaction mode**: multiplexes many application connections into a few database connections. Clients hold a real database connection only during an active transaction, then release it. This allows thousands of application connections with only tens of real database connections.
- Senior insight: the biggest performance improvement is often REDUCING the connection pool size, not increasing it. Profile first, tune second.

## 21. Quick revision checklist
- Can you explain ACID properties with examples?
- Can you explain CAP theorem and when to choose CP vs AP?
- Can you explain B-tree vs LSM tree and their trade-offs?
- Can you explain replication modes and topologies?
- Can you explain sharding strategies and their challenges?
- Can you explain 2PC vs Saga pattern?
- Can you explain MVCC and why it matters?
- Can you explain WAL and crash recovery?
- Can you explain optimistic vs pessimistic locking?
- Can you explain the N+1 problem and solutions?
- Can you explain connection pooling and proper sizing?

## 22. One-line memory anchors
- ACID is for correctness. BASE is for scale. Most systems need both.
- CAP is about behavior during partitions, not normal operation.
- B-tree is read-optimized. LSM tree is write-optimized.
- Shard only when you have exhausted simpler scaling options.
- MVCC: readers do not block writers, writers do not block readers.
- WAL: write the plan before doing the work.
- Connection pool: fewer, faster connections beat many slow ones.
- N+1 is the most common performance bug in ORM-based applications.
