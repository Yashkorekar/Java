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

## 39. Senior-Level Deep Follow-up Questions

These are the questions interviewers ask after you answer the basics correctly. They test whether you truly understand Kafka internals or just memorized the glossary.

### DQ1. How does Kafka achieve zero-copy optimization and why does it matter?
- Traditional data transfer: data goes from disk → OS page cache → kernel buffer → user-space application buffer → kernel socket buffer → NIC (network card). This involves multiple copies and context switches.
- Kafka's approach: when a consumer fetches data, Kafka uses the `sendfile()` system call (or equivalent). This transfers data directly from the OS page cache to the network socket buffer without ever copying it into the JVM's user-space memory.
- This is called "zero-copy" because the application (Kafka broker) never touches the bytes. Data goes from disk/page cache → kernel socket buffer → NIC.
- Why it matters: this eliminates CPU cycles for copying, reduces memory bus contention, and dramatically improves throughput. This is one of the key reasons Kafka can sustain gigabytes per second of throughput.
- Combined with sequential I/O (append-only logs that benefit from OS read-ahead and page cache) and batching, zero-copy makes Kafka unusually efficient for a JVM-based system.

### DQ2. What happens internally during ISR shrink and expand? How does it affect writes?
- ISR (In-Sync Replicas) is the set of replicas that are sufficiently caught up to the leader.
- **ISR shrink**: when a follower falls behind (e.g., slow disk, network issue, GC pause), the leader removes it from the ISR after `replica.lag.time.max.ms`. The ISR shrinks.
  - If `acks=all`, writes only wait for acknowledgement from the remaining ISR members. Fewer ISR members means faster acks but lower fault tolerance.
  - If ISR drops below `min.insync.replicas`, the leader rejects writes with `NotEnoughReplicasException`. This is a safety mechanism — it prevents writes that cannot meet the durability guarantee.
- **ISR expand**: when the lagging follower catches up, the leader adds it back to the ISR.
- Monitoring ISR shrink/expand events is critical in production. Frequent ISR fluctuations indicate infrastructure problems (slow disks, network saturation, or broker overload).
- Key insight: ISR is not a binary alive/dead check. It is a measure of how caught up a follower is. A follower can be alive but not in the ISR because it is lagging.

### DQ3. What is the controller broker and what happens if it fails?
- In a Kafka cluster, one broker is elected as the controller. The controller is responsible for:
  - Managing partition leader elections when brokers go down.
  - Assigning replicas to brokers.
  - Monitoring broker liveness.
  - Propagating metadata changes (new topics, partition reassignments, etc.) to other brokers.
- If the controller fails:
  - In ZooKeeper mode: a new controller is elected via ZooKeeper. During the brief election period, no leader elections or metadata changes can occur. Existing reads and writes to partitions with healthy leaders continue normally. The impact is on partition management, not on ongoing data flow.
  - In KRaft mode: the metadata quorum (Raft group) elects a new leader controller. This is faster and simpler because there is no external ZooKeeper dependency.
- If controller election takes too long or fails repeatedly, the cluster cannot respond to broker failures or create new topics, which eventually degrades availability.

### DQ4. How does Kafka handle backpressure? What happens when consumers are slower than producers?
- Kafka does not have explicit backpressure signaling like reactive streams. Instead, it handles the producer-consumer speed mismatch through retention.
- When consumers are slower than producers:
  - Consumer lag increases. Lag is the difference between the log-end offset and the consumer's committed offset.
  - Kafka keeps the data for the configured retention period or size. As long as consumers catch up before retention deletes their data, nothing is lost.
  - If consumers are so slow that retention expires before they consume, those messages are permanently lost (from the consumer's perspective).
- Producer-side: if brokers are slow or overwhelmed, the producer's internal buffer fills up. When `buffer.memory` is full, `send()` blocks (up to `max.block.ms`) and eventually throws an exception. This is the producer-side backpressure mechanism.
- Broker-side: brokers can become overwhelmed if replication falls behind, disk I/O saturates, or request queues fill up. Quotas can be configured to throttle clients.
- Practical senior approach: monitor consumer lag, set alerts, auto-scale consumers, and ensure retention is long enough to survive consumer outages.

### DQ5. How does Kafka's log compaction actually work internally?
- Log compaction applies per partition for topics with `cleanup.policy=compact`.
- Kafka maintains a "cleaner" background thread pool. The cleaner:
  1. Scans the partition's log segments from oldest to newest.
  2. Builds an in-memory offset map: for each key, it records the latest offset where that key appears.
  3. Rewrites old segments, keeping only the record with the latest offset for each key and discarding older duplicates.
  4. The most recent segment (the "active" segment) is never compacted — it is still being written to.
- Tombstones (records with null value) are kept for a configurable `delete.retention.ms` period so that downstream consumers can learn about deletions, then they are removed.
- Compaction does not change offsets. Gaps in offset sequence are normal in compacted topics.
- The "dirty ratio" (`min.cleanable.dirty.ratio`) controls when compaction triggers. A value of 0.5 means compaction starts when 50% of the log is "dirty" (has superseded records).
- Compaction is useful for: changelog topics, latest-state-per-entity patterns, rebuilding materialized views, and Kafka Streams state stores.

### DQ6. How do Kafka transactions work internally? Walk through a consume-transform-produce flow.
- Kafka transactions allow atomic writes across multiple partitions and topics, and coordination with consumer offset commits.
- How it works:
  1. Producer initializes with a `transactional.id`. The broker assigns a producer epoch.
  2. Producer calls `beginTransaction()`.
  3. Producer sends records to multiple topics/partitions as part of the transaction.
  4. Producer sends consumer offsets (the offsets of input records it consumed) as part of the transaction.
  5. Producer calls `commitTransaction()`.
  6. The transaction coordinator (a special broker) writes a COMMIT marker to an internal `__transaction_state` topic.
  7. Commit markers are written to all partitions involved in the transaction.
- Consumer-side: consumers with `isolation.level=read_committed` only see records from committed transactions. They buffer uncommitted records and release them only after seeing the commit marker.
- If the producer crashes before committing, the transaction coordinator aborts the transaction after a timeout, and ABORT markers are written. `read_committed` consumers skip aborted records.
- Performance impact: transactions add a small overhead for coordination and commit markers. For most use cases, this is acceptable.
- Key limitation: transactions are for Kafka-to-Kafka flows. They do not magically make your database write + Kafka publish atomic. For that, you need the transactional outbox pattern.

### DQ7. What is the relationship between partition count and end-to-end latency?
- More partitions can increase end-to-end latency for several reasons:
  - Each partition has its own leader. More partitions mean more leaders to manage, more ISR tracking, and more replication traffic.
  - Consumer rebalances take longer with more partitions because the coordinator must reassign more partition assignments.
  - Producer batching with `linger.ms` interacts with partition count. If the producer sends to many partitions, each partition's batch fills more slowly, either increasing latency or reducing batch efficiency.
  - If a broker fails, more partitions mean more leader elections, which increases recovery time.
- However, more partitions increase throughput because more consumers can work in parallel.
- The sweet spot depends on the workload. For low-latency requirements, keep partition counts moderate. For high-throughput requirements, increase partitions but accept the trade-offs.
- Practical guideline: start with enough partitions for your throughput needs, not more. You can add partitions later (with the key-mapping caveat), but you cannot reduce them.

### DQ8. How does Kafka handle cross-datacenter replication?
- Kafka itself is designed for a single cluster in one datacenter/region. Cross-DC replication requires additional tooling.
- **MirrorMaker 2 (MM2)**: built on Kafka Connect, replicates topics between clusters. It handles topic renaming (prefixing), offset translation, and consumer group checkpoint sync.
  - Active-passive: one primary cluster, one DR cluster. Consumers failover to DR cluster if primary is down.
  - Active-active: both clusters accept writes and replicate to each other. This introduces complexity around conflict resolution and topic naming.
- **Cluster Linking (Confluent)**: a commercial feature that creates mirror topics in a destination cluster with low overhead. Simpler than MM2 for supported platforms.
- Challenges:
  - Cross-DC latency affects replication lag. If DC1 → DC2 is 50ms, replication will always lag at least that much.
  - Offset mismatch: offsets in the source and destination clusters are not the same. MM2 handles offset translation, but consumer failover still requires care.
  - Active-active conflict resolution is application-dependent. Kafka has no built-in CRDT or conflict resolution.
- Senior insight: cross-DC Kafka is operationally complex. Many teams use active-passive with automated failover for simplicity, accepting brief unavailability during switchover.

### DQ9. What is the fetch protocol and how do consumer internals work?
- Consumers use a pull-based model. They send FetchRequests to brokers.
- A FetchRequest specifies: topic, partition, start offset, and maximum bytes.
- The broker reads from the partition log (often directly from OS page cache) and returns a FetchResponse with the records.
- Consumer configuration affects behavior:
  - `fetch.min.bytes`: broker waits until at least this many bytes are available before responding. Higher values improve batching but increase latency.
  - `fetch.max.wait.ms`: maximum time the broker waits to accumulate `fetch.min.bytes`.
  - `max.poll.records`: maximum records returned per `poll()` call to the application.
  - `max.partition.fetch.bytes`: maximum data per partition per fetch.
- The consumer's internal flow: `poll()` → send FetchRequests to assigned partition leaders → receive responses → deserialize records → return to application → application processes → commit offsets.
- If the application takes too long between `poll()` calls (longer than `max.poll.interval.ms`), the consumer is considered dead and a rebalance is triggered. This is a common source of unexpected rebalances.

### DQ10. What exactly happens when you increase partition count on a live topic?
- You can increase partition count, but you cannot decrease it.
- When you add partitions:
  - New partitions start empty. Existing data stays in old partitions.
  - Key-to-partition mapping changes because the hash function distributes keys across more partitions. A key that used to go to partition 3 might now go to partition 5.
  - This breaks per-key ordering guarantees for new records because old records for a key are in the old partition and new records go to the new partition.
  - Consumers in a consumer group will rebalance to pick up the new partitions.
  - Compacted topics are especially affected because the key-to-partition mapping is fundamental to compaction semantics.
- When NOT to increase partitions: when key-based ordering is critical and your consumers rely on all records for a key being in the same partition.
- Alternative: if you need more throughput but cannot break key ordering, add more brokers (to distribute existing partitions across more hardware) or optimize consumer processing speed.

### DQ11. How does consumer group coordination and rebalancing work internally?
- Every consumer group has a coordinator broker (determined by hashing the group ID).
- When a consumer joins:
  1. It sends a JoinGroup request to the coordinator.
  2. The coordinator waits for all group members to join (or for `session.timeout.ms` to elapse).
  3. One consumer is designated as the group leader.
  4. The group leader receives the list of all members and the topics they subscribe to.
  5. The group leader runs the partition assignment strategy (Range, RoundRobin, Sticky, or Cooperative Sticky) and sends the assignment back to the coordinator.
  6. The coordinator distributes the assignments to all members via SyncGroup responses.
- Rebalance triggers: consumer join, consumer leave, consumer heartbeat timeout, topic metadata change (new partitions), or subscription change.
- **Eager rebalancing** (old behavior): all consumers revoke all partitions, then reassignment happens. This causes a stop-the-world pause.
- **Cooperative rebalancing** (modern, preferred): only the partitions that need to move are revoked. Other partitions continue being processed. This dramatically reduces rebalance impact.
- Senior tip: always use cooperative sticky assignor in modern Kafka to minimize rebalance disruption.

### DQ12. How does Kafka handle message ordering across producer retries?
- Without idempotence: if a producer sends batch A then batch B to the same partition, and batch A fails but batch B succeeds, a retry of batch A would place it after B, breaking order.
- `max.in.flight.requests.per.connection`: limits how many unacknowledged requests the producer sends per connection. Setting this to 1 guarantees order but reduces throughput.
- With `enable.idempotence=true` (default in modern Kafka): the producer assigns a sequence number to each batch per partition. The broker rejects out-of-order or duplicate batches. This allows `max.in.flight.requests.per.connection` up to 5 while still maintaining order and preventing duplicates.
- The idempotent producer assigns a Producer ID (PID) and a monotonically increasing sequence number. The broker tracks the expected next sequence per PID per partition. If a retry arrives, the broker checks the sequence number — if it is a duplicate, it is acknowledged but not re-appended; if it is out of order, it is rejected.
- Senior takeaway: always enable idempotence (it is the default now). It gives you ordering + deduplication for free in most scenarios.