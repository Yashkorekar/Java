# Kafka Interview Prep and Deep Guide

## 1. What Kafka is
- Apache Kafka is a distributed event streaming platform built around durable, partitioned, append-only logs.
- Producers write records to topics, Kafka stores them durably, and consumers read them using offsets.
- Kafka is not just a queue. Its real strength is high-throughput event streaming, replay, and multiple independent consumers over the same retained data.

## 2. 30-second answer
- Kafka is used when many systems need to publish and consume events at scale.
- A topic is split into partitions, each partition is an ordered append-only log, producers write records, consumers read by offset, and data stays for retention instead of being deleted immediately after consumption.
- This makes Kafka strong for event-driven architecture, analytics pipelines, CDC, audit trails, and stream processing.

## 3. What problems Kafka solves
- Decouples producers from consumers.
- Handles very high write and read throughput.
- Lets multiple consumer groups read the same data independently.
- Supports replay and reprocessing because old events remain for retention.
- Preserves order within a partition.
- Enables event sourcing, auditability, and streaming pipelines.

### Beginner-friendly example: ecommerce order events
- A customer places an order on an ecommerce site.
- The order service publishes one `OrderPlaced` event to a Kafka topic like `orders`.
- The inventory service reads that event and reduces stock.
- The email service reads the same event and sends order confirmation.
- The analytics service reads the same event and updates sales dashboards.
- The recommendation service reads the same event and learns what the user bought.
- The important beginner idea is this: one event is written once, but many different systems can read it independently.
- If the email service is temporarily down, the event is still in Kafka and that service can catch up later.

### Beginner memory trick
- Kafka is like a durable company activity log.
- Every team can read the same log at its own pace without deleting it for others.

## 4. What Kafka is not ideal for
- Per-message business workflows that need rich routing rules like direct, topic, and fanout exchanges.
- Very low-volume systems where operational complexity is not justified.
- Workloads where each message must be individually delayed, expired, or retried with simple broker-native controls.
- Global total ordering across all messages in a topic.
- Synchronous request/reply as the core pattern.

## 5. Mental model
- Think of a topic as a named log.
- Think of a partition as one ordered append-only file.
- Producers append new records.
- Consumers remember where they are using offsets.
- Reading does not remove data.
- Kafka removes data later based on retention or compaction policy.

## 6. Core concepts
| Concept | Meaning |
| --- | --- |
| Broker | A Kafka server that stores partitions and serves reads and writes |
| Topic | Logical stream name like `orders` or `payments` |
| Partition | Ordered shard of a topic |
| Offset | Position of a record inside a partition |
| Producer | Client that writes records |
| Consumer | Client that reads records |
| Consumer group | A set of consumers sharing work for a topic |
| Leader | Replica that handles reads and writes for a partition |
| Follower | Replica that copies data from leader |
| ISR | In-sync replicas, the replicas sufficiently caught up |
| Retention | How long or how much data Kafka keeps |
| Compaction | Keeping the latest value per key instead of deleting immediately |

## 7. Message model
- A Kafka record usually has key, value, timestamp, headers, topic, partition, and offset.
- The key is important because it often controls partitioning.
- Records with the same key usually go to the same partition, which preserves order for that key.

## 8. How data flows
1. Producer sends a record to a topic.
2. Kafka chooses a partition based on key or partitioner logic.
3. The partition leader appends the record to its log.
4. Followers replicate the record from the leader.
5. Producer receives an acknowledgement depending on `acks` configuration.
6. Consumers poll the partition data.
7. Consumers commit offsets to remember progress.
8. Data remains until retention or compaction removes it.

## 9. Topics, partitions, and ordering
- Ordering is guaranteed only within a single partition.
- There is no topic-wide total order once a topic has more than one partition.
- More partitions mean more parallelism but also more operational overhead.
- Partition count effectively sets the upper bound for active consumers in one consumer group.

## 10. Offsets and consumer groups
- Offsets are not message IDs; they are positions within a partition.
- In practice, the consumer group owns progress, not the broker in a queue-like delete-on-consume way.
- If two different consumer groups read the same topic, each group tracks its own offsets independently.
- This is why Kafka is good for fan-out scenarios where multiple downstream systems need the same events.

## 11. Producer deep dive
- `acks=0`: fastest, least durable. Producer does not wait for broker acknowledgement.
- `acks=1`: leader acknowledges after write to leader only. Better than `0`, but leader loss can still lose data.
- `acks=all` or `-1`: leader waits for all required in-sync replicas. Best durability when paired with `min.insync.replicas`.
- `retries`: allows resend on transient failure.
- `enable.idempotence=true`: prevents duplicates from producer retries in common cases.
- `linger.ms`: waits a little to batch more records for throughput.
- `batch.size`: controls batching size.
- `compression.type`: `snappy`, `lz4`, `zstd`, or `gzip` reduce network and storage costs.

## 12. Consumer deep dive
- Consumers usually use pull, not push.
- A consumer group splits partitions across consumers.
- Within one group, one partition is actively consumed by one consumer at a time.
- Offset commit can be automatic or manual.
- Manual commit gives more control for at-least-once processing.
- Consumer lag is the difference between latest offset and committed/processed offset.

## 13. Delivery semantics
- At-most-once: commit offset before processing or accept possible loss.
- At-least-once: process and then commit offset; duplicates are possible.
- Exactly-once: possible for Kafka-to-Kafka pipelines with idempotent producers and transactions, but it is not magic end-to-end exactly-once for arbitrary external side effects.

## 14. Replication and durability
- Replication factor controls how many copies of a partition exist.
- One replica is leader; others are followers.
- ISR is the set of followers sufficiently caught up.
- `min.insync.replicas` defines the minimum replicas required to acknowledge writes when `acks=all` is used.
- If you use `acks=all` with replication factor `3` and `min.insync.replicas=2`, Kafka can tolerate one replica loss without losing write safety.
- Unclean leader election can reduce downtime but may lose data; in critical systems it is typically avoided.

## 15. Storage model
- Kafka writes to disk sequentially, which is efficient.
- It relies heavily on the OS page cache.
- Data is stored in segment files, not one endless file.
- Kafka is fast because it uses append-only writes, batching, compression, and sequential I/O patterns.

## 16. Retention and compaction
- `cleanup.policy=delete` means Kafka removes old data by time or size retention.
- `cleanup.policy=compact` means Kafka keeps the latest value for each key.
- Compaction is useful for changelog topics, user profile latest state, or materialized view rebuilding.
- Delete retention and compaction can be combined depending on use case.
- Tombstones are special records with null values used to delete keys in compacted topics.

## 17. Rebalancing
- When consumers join, leave, or fail, Kafka reassigns partitions.
- Rebalances can pause processing and hurt latency.
- Too many rebalances usually signal unstable consumers, long processing, or bad timeout configuration.
- Cooperative rebalancing can reduce disruption compared with older eager rebalancing behavior.

## 18. Scaling model
- Add partitions to scale throughput and parallelism.
- Add brokers to scale storage and network capacity.
- Add consumers, but only up to the partition count for parallel processing inside one group.
- Be careful: changing partition count changes key-to-partition mapping and can affect ordering expectations.

## 19. Failure behavior
- If a consumer dies, another consumer in the group can take over after rebalance.
- If a broker dies, partition leaders move to surviving replicas if replicas are available.
- If ISR shrinks too much, writes with strict durability settings may fail rather than risk silent data loss.
- If consumers are slower than producers, lag builds up and retention may eventually remove old data before slow consumers catch up.

## 20. ZooKeeper vs KRaft
- Older Kafka clusters used ZooKeeper for metadata coordination.
- Modern Kafka uses KRaft, where Kafka manages metadata through an internal Raft-based quorum.
- KRaft simplifies architecture by removing the extra ZooKeeper dependency.
- In 2026, KRaft is the modern direction and what interviewers usually expect you to know.

## 21. Exactly-once semantics in plain English
- Idempotent producer prevents duplicates caused by retrying the same produce request.
- Transactions let a producer write atomically to multiple partitions and topics and coordinate consumed offsets for Kafka-to-Kafka workflows.
- Exactly-once does not mean your external database, email API, and third-party payment gateway suddenly become exactly-once.
- For side effects outside Kafka, you still need patterns like idempotent consumers, deduplication keys, transactional outbox, or compensating logic.

## 22. Kafka ecosystem
- Kafka Connect moves data between Kafka and external systems using source and sink connectors.
- Debezium is commonly used for CDC from databases into Kafka.
- Kafka Streams provides stream processing inside Java applications.
- Schema Registry is commonly used with Avro, Protobuf, or JSON Schema for schema evolution control.
- MirrorMaker or cluster-linking style approaches help with multi-cluster replication and migration.

## 23. Java ecosystem
- Spring for Apache Kafka is common in Spring Boot applications.
- Native Kafka Java clients offer lower-level control.
- Kafka Streams is Java-native and strong for event transformations, joins, windowing, and stateful stream processing.
- In Java interviews, be ready to explain `@KafkaListener`, consumer groups, offset commits, retries, and idempotency.

## 24. Security
- Use TLS for encryption in transit.
- Use SASL mechanisms such as SCRAM, OAuth/OIDC, or Kerberos depending on environment.
- Use ACLs to control topic read and write permissions.
- Encrypt disks or volumes for data at rest when required.
- Avoid exposing brokers directly to public networks.

## 25. Monitoring and operations
- Track consumer lag.
- Track under-replicated partitions.
- Track offline partitions.
- Track broker disk usage and log segment growth.
- Track request latency, produce rate, and fetch rate.
- Track rebalance frequency and duration.
- Track ISR shrink and expansion events.
- Track controller or metadata quorum health.

## 26. Performance tuning levers
| Lever | Effect | Trade-off |
| --- | --- | --- |
| More partitions | More parallelism | More metadata, rebalances, files, and network overhead |
| More batching | Higher throughput | Slightly higher latency |
| Compression | Lower network/storage usage | CPU cost |
| Larger fetches | Better throughput | More memory use and latency per batch |
| Idempotence | Safer retries | Slight overhead, but usually worth it |

## 27. Best use cases
- Order, payment, shipment, and audit event streams.
- CDC pipelines from OLTP databases to analytics or search systems.
- Log aggregation and observability event pipelines.
- Event sourcing architectures.
- Stream processing with joins, windows, and materialized views.
- Large-scale async integration between many services.

## 28. When not to use Kafka
- You need sophisticated broker-native routing by wildcard patterns or multiple exchange types.
- You need simple job queue semantics with per-message ack, retry, and dead-lettering as the first-class model.
- You only have tiny traffic and want the simplest possible setup.
- You need extremely short-lived, ephemeral messaging with no replay and no retention value.

## 29. Pros
- Very high throughput.
- Durable retained event log.
- Strong replay and reprocessing story.
- Strong ecosystem for connectors and stream processing.
- Good horizontal scaling model.
- Multiple independent consumer groups over the same data.

## 30. Cons
- Operationally heavier than a simple broker or cache.
- Ordering is only per partition, not global.
- Rich routing is weaker than RabbitMQ.
- Exactly-once is nuanced and often misunderstood.
- Consumer group rebalances can be disruptive.
- Not the best default for simple request queues.

## 31. Kafka vs RabbitMQ
- Kafka stores an event log and consumers track offsets; RabbitMQ focuses on brokered delivery into queues.
- Kafka is stronger for replay, retention, analytics, and streaming.
- RabbitMQ is stronger for routing patterns, task queues, delayed retries, and classic work distribution.
- Kafka usually wins on throughput at scale; RabbitMQ usually wins on queue-centric workflow ergonomics.

## 32. Kafka vs Redis
- Kafka is durable and optimized for retained event streams.
- Redis is ultra-fast in-memory infrastructure primarily used for cache, state, and lightweight messaging.
- Redis Pub/Sub has no durable replay; Redis Streams add more structure but still do not replace Kafka at large-scale event backbone use cases.
- Kafka is usually the better fit for long-lived, durable, replayable streams.

## 33. Kafka vs database polling
- Kafka-based CDC is usually cleaner and more scalable than repeatedly polling tables.
- Polling increases database load and often creates latency and consistency problems.
- Kafka decouples producers and consumers and offers cleaner fan-out to many downstream systems.

## 34. Common mistakes
- Saying Kafka deletes a message immediately after a consumer reads it.
- Claiming Kafka guarantees global ordering across a multi-partition topic.
- Assuming exactly-once means all external side effects are automatically safe.
- Ignoring key choice and then being surprised by bad ordering or skew.
- Using `acks=all` but forgetting `min.insync.replicas`.
- Treating retention as backup.
- Creating far too many partitions without understanding the cost.

## 35. Practical patterns
- Transactional outbox: write business change and outbox entry in one DB transaction, then publish reliably to Kafka.
- CDC with Debezium: stream database changes into Kafka without app-level dual writes.
- Log compaction: keep latest state per entity.
- Event sourcing: model facts as immutable events.
- Stream enrichment: join streams with reference data or lookup tables.

## 36. Tricky interview questions and answers

### Q1. Why is Kafka called a distributed log instead of just a queue?
- Because records are appended to partitions and retained for some time, multiple consumer groups can read the same data independently, and consumption does not remove the record immediately.

### Q2. Does consuming a message delete it from Kafka?
- No. Kafka keeps records until retention or compaction removes them.

### Q3. What determines maximum consumer parallelism in one consumer group?
- Partition count. If a topic has 6 partitions, more than 6 active consumers in the same group will leave some consumers idle.

### Q4. Can Kafka guarantee message ordering?
- Yes, but only within a partition.

### Q5. Why use a key in Kafka?
- A key is usually used to keep related records in the same partition, which preserves per-key ordering and can improve locality.

### Q6. Is `acks=all` enough for safe writes?
- Not by itself. Pair it with an appropriate replication factor and `min.insync.replicas`.

### Q7. What happens if a consumer crashes after processing but before committing offset?
- The record may be processed again after restart or reassignment, which is why idempotent consumers matter.

### Q8. What is consumer lag?
- The difference between the end of the log and the consumer's processed or committed position.

### Q9. Why can rebalances be painful?
- Partition ownership changes can pause processing, trigger state movement, and increase latency.

### Q10. What is log compaction used for?
- Keeping the latest value for each key, useful for rebuilding current state rather than replaying every historical change forever.

### Q11. Can Kafka be used as a task queue?
- It can, but it is not always the best fit if you need queue-centric features like per-message TTL, rich routing, and simple retry/DLQ semantics.

### Q12. What is the difference between retention and compaction?
- Retention removes data by age or size; compaction keeps the latest record per key.

### Q13. What happens if one broker fails?
- Leadership can move to an in-sync follower if replicas are healthy.

### Q14. Why is Kafka fast even though it writes to disk?
- Sequential append, batching, page cache, and zero-copy style optimizations make disk-backed throughput strong.

### Q15. What does exactly-once really mean in Kafka?
- It means Kafka can avoid duplicates in supported consume-process-produce pipelines when idempotence and transactions are used correctly. It does not magically guarantee global exactly-once side effects in every external system.

### Q16. What is the role of Schema Registry?
- It manages schemas and compatibility so producers and consumers can evolve safely without breaking each other.

### Q17. Why can adding partitions be dangerous?
- It changes partition mapping for keys, which can break assumptions about ordering or co-location.

### Q18. Why is Kafka good for replay?
- Because messages stay for retention and consumers manage offsets independently.

### Q19. Is Kafka a database?
- Not in the general OLTP sense. It is a distributed log with retention and streaming semantics, not a drop-in replacement for transactional relational storage.

### Q20. What is the single biggest misunderstanding beginners have?
- Treating Kafka like a simple queue instead of understanding that it is a retained, partitioned event log.

## 37. Quick revision checklist
- Can you explain topic, partition, broker, offset, leader, follower, and ISR?
- Can you explain ordering only within a partition?
- Can you explain `acks`, replication factor, and `min.insync.replicas` together?
- Can you explain at-most-once, at-least-once, and exactly-once correctly?
- Can you explain retention vs compaction?
- Can you explain why Kafka is strong for replay and fan-out?
- Can you explain when RabbitMQ is a better fit?
- Can you explain why idempotent consumers still matter?

## 38. One-line memory anchors
- Kafka is a durable distributed log, not just a queue.
- Partition count drives ordering boundaries and consumer parallelism.
- Offsets belong to consumers, not to the message as delete-on-read state.
- `acks=all` plus healthy ISR gives better durability.
- Replay is a core feature, not an accident.