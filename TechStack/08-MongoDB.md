# MongoDB Interview Prep and Deep Guide

## 1. What MongoDB is
- MongoDB is a document-oriented NoSQL database.
- It stores data in flexible JSON-like documents called BSON documents.
- It is often chosen when the data model is naturally document-shaped and the schema may evolve over time.

## 2. 30-second answer
- MongoDB is good for applications where records look like documents rather than heavily normalized relational rows.
- It supports indexing, replication, aggregation, and sharding.
- It is commonly used for product catalogs, user profiles, content systems, event metadata, and rapidly evolving application data.

## 3. Beginner-friendly example: ecommerce product catalog
- An ecommerce product can have different attributes.
- A phone may have `screenSize`, `battery`, and `storage`.
- A shoe may have `size`, `color`, and `material`.
- In MongoDB, both can be stored as product documents in the same collection without forcing every product to fit one rigid table structure.
- The important beginner idea is this: MongoDB is useful when records are naturally stored as documents with varying fields.

### Beginner memory trick
- MongoDB is like storing one complete product card in a folder instead of splitting that product into many rigid spreadsheet tables.

## 4. Core concepts
| Concept | Meaning |
| --- | --- |
| Document | JSON-like record |
| Collection | Group of documents, similar to a table at a high level |
| BSON | Binary JSON format used internally |
| Index | Data structure for faster queries |
| Replica set | Primary plus replicas for availability |
| Sharding | Splitting data across servers |
| Aggregation pipeline | Stage-based data processing and transformation |

## 5. Why people choose MongoDB
- Flexible schema.
- Natural fit for nested JSON-like application data.
- Good developer productivity for evolving products.
- Easy to store arrays and nested objects.
- Good scaling options with replication and sharding.

## 6. When MongoDB is a good fit
- Product catalogs.
- User profiles and preferences.
- CMS and content metadata.
- Event metadata or logs with varying structure.
- Applications where schema changes often.

## 7. When MongoDB is not the best fit
- Heavy multi-row transactional workloads with strict relational constraints.
- Systems that depend heavily on complex joins.
- Financial systems where relational consistency and mature SQL semantics are central.

## 8. Embedding vs referencing
- Embedding means storing related data inside one document.
- Referencing means storing separate documents and linking them by ID.
- Embed when the related data is small, usually read together, and belongs tightly to one parent.
- Reference when related data grows independently or becomes too large.

## 9. Indexing
- MongoDB supports single-field and compound indexes.
- It also supports text, TTL, and geospatial indexes.
- Good indexes matter as much in MongoDB as in SQL systems.
- Too many indexes slow writes and consume memory.

## 10. Replication and replica sets
- A replica set has one primary and one or more secondary nodes.
- Writes usually go to the primary.
- Secondaries replicate changes.
- If the primary fails, a new primary can be elected.

## 11. Sharding
- Sharding splits data across multiple servers.
- A shard key decides how data is distributed.
- Good shard-key choice matters a lot.
- Bad shard keys can create hotspots and uneven growth.

## 12. Read concern and write concern
- Write concern controls how safely a write must be acknowledged.
- Read concern controls what consistency level the read expects.
- These settings affect durability, latency, and consistency behavior.

## 13. Transactions
- MongoDB supports transactions, including multi-document transactions.
- They are useful but should not be used carelessly everywhere.
- If your design requires constant large relational transactions, a relational database may still be the more natural fit.

## 14. Aggregation pipeline
- Aggregation pipeline processes documents in stages.
- It is powerful for filtering, grouping, projecting, and transforming data.
- It is one of MongoDB's most important features for analytics-like operations inside the database.

## 15. Best use cases
- Catalog and content management.
- User profiles and settings.
- Nested data models.
- Rapidly changing product requirements.
- APIs that naturally produce and consume JSON.

## 16. Pros
- Flexible schema.
- Good fit for document-shaped data.
- Easy to model nested objects and arrays.
- Good developer productivity.
- Replication and sharding support.

## 17. Cons
- Less natural than SQL for relationship-heavy systems.
- Poor schema discipline can create messy data over time.
- Bad shard key choice can hurt scale badly.
- Complex joins are weaker than in relational databases.

## 18. MongoDB vs SQL
- MongoDB is usually stronger when the data is document-shaped and evolving.
- SQL is usually stronger when transactions, joins, and relational integrity are central.
- This is not about one being modern and the other being old.
- It is about the shape of data and the workload.

## 19. MongoDB vs Elasticsearch
- MongoDB is a primary database.
- Elasticsearch is usually a search engine, not the main source of truth for transactional application data.
- Many systems store primary data in MongoDB or SQL and then index searchable views into Elasticsearch.

## 20. Common mistakes
- Treating schema flexibility as if no data discipline is needed.
- Embedding huge unbounded arrays inside documents.
- Choosing a bad shard key.
- Forgetting indexes for common queries.
- Using MongoDB when the workload is deeply relational.

## 21. Tricky interview questions and answers

### Q1. Is MongoDB schema-less?
- Not exactly. It is schema-flexible, but your application still has an effective schema whether you manage it well or not.

### Q2. When should I embed instead of reference?
- Embed when data belongs together and is usually read together. Reference when the data grows independently or becomes too large.

### Q3. What is a replica set?
- A primary-replica configuration for availability and failover.

### Q4. What is sharding?
- Horizontal partitioning of data across servers.

### Q5. Can MongoDB do transactions?
- Yes, but if strong multi-row relational transactions dominate the workload, SQL may still be a better fit.

### Q6. Why can schema flexibility be dangerous?
- Because different services or developers may store inconsistent document shapes over time.

### Q7. Why is shard key choice important?
- It controls data distribution and hotspot risk.

### Q8. Is MongoDB a search engine?
- No. It can do some text search, but full-text search systems like Elasticsearch are stronger for advanced search use cases.

## 22. Quick revision checklist
- Can you explain document, collection, index, replica set, and shard?
- Can you explain embedding vs referencing?
- Can you explain when MongoDB is better than SQL?
- Can you explain why shard key choice matters?
- Can you explain why schema flexibility still needs discipline?

## 23. One-line memory anchors
- MongoDB is document-first.
- Flexible schema is useful, but not a free pass to store chaos.
- Embed for togetherness, reference for independent growth.
- Shard key choice can make or break scale.

## 24. Senior-Level Deep Follow-up Questions

### DQ1. How does the WiredTiger storage engine work internally?
- WiredTiger is MongoDB's default storage engine since version 3.2.
- It uses a **B-tree** structure for indexes and supports document-level concurrency control (not collection-level like the old MMAPv1 engine).
- Key features:
  - **Document-level locking**: multiple writes to different documents in the same collection can proceed concurrently. This is a massive improvement over collection-level locking.
  - **Compression**: WiredTiger compresses data on disk using snappy (default), zlib, or zstd. This significantly reduces storage requirements.
  - **Write-ahead log (journal)**: all write operations are first written to the journal. If MongoDB crashes, the journal is replayed on restart to recover committed writes.
  - **Checkpoint mechanism**: WiredTiger periodically writes a consistent snapshot of in-memory data to disk (every 60 seconds by default). Between checkpoints, the journal provides durability.
  - **Cache**: WiredTiger maintains an internal cache (default 50% of RAM minus 1GB). Frequently accessed data stays in this cache. This is separate from the OS filesystem cache.
- Senior insight: understanding WiredTiger explains why MongoDB performs well under concurrent workloads and why memory sizing matters. If the working set exceeds the WiredTiger cache, performance degrades because of disk reads.

### DQ2. How does MongoDB replication work internally? What is the oplog?
- MongoDB uses a **replica set** with one primary and multiple secondaries.
- The **oplog** (operations log) is a special capped collection (`local.oplog.rs`) on the primary that records all write operations.
- Replication flow:
  1. A write is applied to the primary.
  2. The write operation is recorded in the primary's oplog.
  3. Secondaries continuously tail the primary's oplog and apply the operations to their own data.
- The oplog is **idempotent**: applying the same operation multiple times produces the same result. This makes replication safe even if a secondary replays operations during recovery.
- **Oplog window**: the oplog has a fixed size. If a secondary falls too far behind and the oldest oplog entry it needs has been overwritten, it must do a full resync (copy all data from the primary). This is expensive.
- **Elections**: if the primary becomes unreachable, secondaries vote to elect a new primary. The election uses a Raft-like protocol. A majority of voting members must agree.
- **Read preferences**: you can read from secondaries (`secondaryPreferred`, `nearest`) for read scaling, but reads from secondaries may return stale data because replication is asynchronous.
- **Write concern**: controls how many replicas must acknowledge a write before it is considered successful. `w: majority` waits for a majority of replicas to confirm. `w: 1` only waits for the primary.

### DQ3. How does MongoDB handle distributed transactions? What are the limitations?
- MongoDB supports multi-document ACID transactions within a replica set (since 4.0) and across shards (since 4.2).
- How they work:
  - A session starts a transaction. All reads and writes within the transaction are isolated using snapshot isolation (MVCC).
  - Changes are not visible to other operations until the transaction commits.
  - On commit, all changes are applied atomically. On abort, all changes are discarded.
- For **sharded transactions**, MongoDB uses a two-phase commit protocol:
  1. Prepare phase: each shard prepares its changes.
  2. Commit phase: a coordinator tells all shards to commit.
- Limitations:
  - Transactions have a **60-second timeout** by default. Long-running transactions are not supported.
  - Transactions lock documents, which can cause contention in write-heavy workloads.
  - Cross-shard transactions are slower than single-shard transactions due to coordination overhead.
  - Performance degrades as the number of documents/shards in a transaction increases.
- Senior perspective: if your workload is dominated by multi-document transactions, PostgreSQL or another RDBMS might be a better fit. MongoDB transactions are best for occasional cross-document consistency needs, not as the primary interaction pattern.

### DQ4. What happens during chunk migration in a sharded cluster? How does the balancer work?
- In a sharded MongoDB cluster, data is divided into **chunks** based on the shard key. Each chunk is a contiguous range of shard key values.
- The **balancer** is a background process (runs on the config server's primary) that monitors chunk distribution across shards.
- If one shard has significantly more chunks than another, the balancer initiates **chunk migration**:
  1. The balancer selects a chunk to move and a destination shard.
  2. The destination shard requests the chunk data from the source shard.
  3. Data is copied to the destination. During this time, writes to the chunk still go to the source.
  4. Once copying is complete, the source shard stops accepting writes for that chunk (brief pause) and forwards any remaining changes.
  5. The config server metadata is updated to reflect the new chunk location.
  6. The source deletes the migrated data.
- Impact on applications:
  - During migration, reads and writes may experience slightly higher latency.
  - Jumbo chunks (chunks that cannot be split because they contain too many documents with the same shard key value) cannot be migrated. They cause hotspots.
- Senior insight: poor shard key choice (e.g., monotonically increasing like ObjectId) causes all writes to go to the last chunk on one shard, creating a permanent hotspot. Hashed shard keys distribute writes evenly but sacrifice range queries.

### DQ5. Explain MongoDB read concerns and write concerns in depth.
- **Write concern** — what durability guarantee the client wants for a write:
  - `w: 0`: fire and forget. No acknowledgement.
  - `w: 1`: acknowledged by the primary only.
  - `w: majority`: acknowledged by a majority of replica set members. This is the recommended setting for important data.
  - `w: <number>`: acknowledged by a specific number of members.
  - `j: true`: the write is acknowledged only after being written to the journal (on-disk durability).
- **Read concern** — what consistency guarantee the client wants for a read:
  - `local`: returns the most recent data on the queried node. May read data that could be rolled back.
  - `available`: similar to local but optimized for sharded clusters. May return orphaned documents during chunk migration.
  - `majority`: returns only data that has been acknowledged by a majority of replicas. Guarantees the read will not be rolled back.
  - `snapshot`: provides a consistent snapshot view for multi-document transactions.
  - `linearizable`: strongest guarantee. Reads reflect all writes that completed before the read started. Very slow because it requires a confirmation from the majority.
- The combination of write concern and read concern determines your consistency guarantees. `w: majority` + read concern `majority` is the standard recommendation for strong consistency.

### DQ6. How do MongoDB indexes work internally? What types exist?
- MongoDB indexes use **B-tree** structures (WiredTiger uses B-trees internally for its sorted file format).
- Index types:
  - **Single field**: index on one field. Most common.
  - **Compound**: index on multiple fields. Field order matters for query optimization.
  - **Multikey**: automatically created when indexing array fields. Each array element gets an index entry.
  - **Text**: full-text search index. Supports stemming and language-specific tokenization. Not as powerful as Elasticsearch.
  - **Hashed**: hashes the field value. Used for hash-based sharding. Does not support range queries.
  - **Geospatial** (2d, 2dsphere): for location-based queries.
  - **Wildcard**: indexes all fields or fields matching a pattern. Useful for dynamic schemas.
  - **TTL index**: automatically deletes documents after a specified time. Used for session data, logs, temporary records.
- **Covered queries**: if all fields needed by a query exist in the index, MongoDB returns results directly from the index without reading the full document. This is very fast.
- **Index intersection**: MongoDB can combine results from multiple single-field indexes instead of requiring a compound index. But compound indexes are usually more efficient for known query patterns.
- Senior insight: too many indexes slow down writes (every insert/update must update all relevant indexes). Use `explain()` to verify which indexes your queries actually use.

### DQ7. How does MongoDB's change streams feature work?
- Change streams allow applications to subscribe to real-time data changes on collections, databases, or entire deployments.
- Built on top of the oplog. Change streams create a cursor that tails the oplog and filters for relevant changes.
- Applications receive change events (insert, update, replace, delete, drop, rename, invalidate) as they happen.
- Each event includes a **resume token**. If the application disconnects, it can resume from the exact position using the token, as long as the oplog still contains that position.
- Use cases: event-driven architectures, syncing data to search indexes, triggering side effects, CDC (change data capture) without external tools.
- Limitations: depends on oplog availability. If the oplog rolls over past your resume token, you must reprocess from scratch. This is why oplog sizing matters.
- Comparison to Debezium: Debezium also uses the oplog for MongoDB CDC but runs as a Kafka Connect connector, pushing changes into Kafka topics. Change streams are MongoDB-native and do not require Kafka.

### DQ8. When should you embed documents vs reference them? What are the real-world implications?
- **Embed** (denormalize): store related data inside the same document.
  - Good when: data is always accessed together, the embedded data does not grow unboundedly, and the total document stays under 16MB.
  - Example: user profile with addresses. You almost always need addresses when you fetch the user.
  - Advantage: single read, no joins, atomicity (single document writes are atomic without transactions).
- **Reference** (normalize): store a reference (ObjectId or other key) and look up the related document separately.
  - Good when: related data is accessed independently, the related collection grows independently, or the relationship is many-to-many.
  - Example: orders referencing products. Products exist independently and are shared across many orders.
  - Disadvantage: requires multiple queries or `$lookup` (which is essentially a left outer join and can be slow on large datasets).
- **Hybrid**: embed frequently accessed fields and reference the rest.
  - Example: an order embeds the product name and price at the time of purchase (snapshot) but references the product for full details.
- Anti-pattern: embedding data that grows without bound (e.g., embedding all comments in a blog post document). This leads to documents approaching the 16MB limit and causes performance degradation.

### DQ9. What is MongoDB's aggregation pipeline and how does it compare to SQL?
- The aggregation pipeline is MongoDB's way of doing complex data transformations, grouping, filtering, and joining.
- It consists of **stages** that process documents sequentially: `$match` → `$group` → `$sort` → `$project` → `$lookup` → etc.
- SQL equivalents:
  - `$match` ≈ `WHERE`
  - `$group` ≈ `GROUP BY`
  - `$sort` ≈ `ORDER BY`
  - `$project` ≈ `SELECT (columns)`
  - `$lookup` ≈ `LEFT JOIN`
  - `$unwind` ≈ flattening array joins
  - `$limit` / `$skip` ≈ `LIMIT` / `OFFSET`
- Performance tips:
  - Put `$match` early in the pipeline to reduce the number of documents processed by later stages.
  - Use indexes: `$match` and `$sort` at the beginning of the pipeline can use indexes.
  - `$lookup` (joins) can be slow on large collections. Embedding data may be better if joins are frequent.
- Senior insight: the aggregation pipeline is powerful but can become complex and hard to maintain for very sophisticated queries. For heavy analytical workloads, consider using a dedicated analytics database or data warehouse.