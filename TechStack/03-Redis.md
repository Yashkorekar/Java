# Redis Interview Prep and Deep Guide

## 1. What Redis is
- Redis is an in-memory data structure store used for caching, fast state management, lightweight messaging, and sometimes as a primary data store for specific workloads.
- It is known for very low latency and simple but powerful data structures.
- Redis is not only a cache; it can also support sessions, rate limiting, counters, leaderboards, streams, locks, and ephemeral coordination patterns.

## 2. 30-second answer
- Redis keeps data primarily in memory, which makes it extremely fast.
- It supports data structures like strings, hashes, lists, sets, sorted sets, bitmaps, hyperloglogs, geospatial indexes, and streams.
- It is commonly used for cache, session storage, counters, distributed locks, rate limiting, leaderboards, and short-lifecycle state.

## 3. What problems Redis solves
- Reduces database load through caching.
- Stores hot data with very low read latency.
- Supports atomic increments, expirations, and simple coordination.
- Handles session storage and token state efficiently.
- Supports rate limiting, counters, and ranking.
- Supports lightweight pub/sub and stream-based event consumption.

### Beginner-friendly example: ecommerce product page and cart
- A user opens a product page on an ecommerce site.
- The application first checks Redis for a key like `product:123`.
- If the product data is already in Redis, the page loads very fast.
- If it is not in Redis, the application reads from the database, returns the response, and stores the result in Redis with a TTL.
- The same site can also store cart data, user sessions, coupon usage counters, and rate-limit values in Redis.
- The important beginner idea is this: Redis keeps frequently used data very close to the application so the database does less work.

### Beginner memory trick
- Redis is like a super-fast temporary shelf near the application.
- Frequently used items are kept on that shelf so you do not walk to the warehouse every time.

## 4. What Redis is not ideal for
- Large durable historical event logs like Kafka.
- Rich routing and queue workflow features like RabbitMQ.
- Complex relational querying and strong transactional guarantees like an RDBMS.
- Massive persistent storage without memory planning.

## 5. Mental model
- Redis is memory-first.
- Data access is very fast because the primary working set is in RAM.
- Keys can have TTL.
- Operations on a single key are usually atomic.
- It is excellent for hot, short-lived, or frequently accessed state.

## 6. Core data types
| Type | Typical use |
| --- | --- |
| String | Cache values, counters, flags, tokens |
| Hash | User session or object fields |
| List | Simple queues or recent items |
| Set | Unique membership, tags, followers |
| Sorted set | Leaderboards, ranked feeds, scheduling |
| Bitmap | Compact boolean tracking |
| HyperLogLog | Approximate cardinality |
| Geospatial | Location-based queries |
| Stream | Event stream and consumer-group style processing |

## 7. Common usage patterns
- Cache-aside: app reads DB on miss, then stores result in Redis.
- Read-through: cache layer handles loading on miss.
- Write-through: writes go to cache and backing store together.
- Write-behind: cache accepts writes and flushes later.
- Session store: store logged-in user session or token metadata.
- Counter store: page views, likes, stock counts, quotas.
- Rate limiter: request allowance tracked per user or IP.
- Leaderboard: sorted sets for rank and score.
- Locking and coordination: short-lived distributed locks.

## 8. Pub/Sub vs Streams
- Pub/Sub is fire-and-forget and does not retain messages for replay.
- If subscribers are offline, they miss Pub/Sub messages.
- Redis Streams store entries and support consumer groups.
- Streams are better when you need durable-ish consumption and replay within Redis use cases.
- Even then, Redis Streams are not a drop-in replacement for Kafka in large-scale event backbones.

## 9. Persistence options
- RDB snapshots persist point-in-time snapshots.
- AOF append-only file logs write operations for better durability.
- AOF can be configured with different fsync policies for different safety and performance trade-offs.
- Some setups use both RDB and AOF.
- If persistence is disabled or weakly configured, a restart can lose recent or all in-memory data.

## 10. Replication, Sentinel, and Cluster
- Replication creates replicas from a primary.
- Sentinel monitors nodes and helps with failover in non-cluster mode.
- Redis Cluster shards data across nodes for scale and high availability.
- Sentinel is about high availability for primary-replica deployments.
- Cluster is about sharding plus high availability.

## 11. TTL and expiration
- TTL is one of Redis's most useful features.
- Use TTL for sessions, OTPs, cache entries, distributed locks, and temporary state.
- Forgetting TTL can cause stale data and memory growth.
- Overusing the same TTL for everything can cause stampedes when many keys expire together.

## 12. Eviction policies
- `noeviction`: writes fail when memory is full.
- `allkeys-lru`: evict least recently used keys across all keys.
- `volatile-lru`: evict least recently used keys only among keys with TTL.
- `allkeys-lfu`: evict least frequently used keys across all keys.
- `volatile-ttl`: evict keys with TTL based on expiration proximity.
- Eviction policy matters a lot for cache behavior under pressure.

## 13. Transactions and atomicity
- `MULTI` and `EXEC` group commands.
- `WATCH` provides optimistic locking.
- Redis transactions do not behave like full ACID SQL transactions.
- There is no rollback model like a relational database.
- Single-threaded command execution and Lua scripts are often used for atomic multi-step logic.

## 14. Lua scripts and functions
- Lua scripts allow you to execute multiple operations atomically.
- They are useful for rate limiting, conditional updates, counters with TTL, or lock release logic.
- Keep scripts efficient; long-running scripts block Redis event processing.

## 15. Pipelining
- Pipelining batches multiple commands in one network round trip.
- It improves throughput dramatically for chatty workloads.
- It does not make commands one atomic transaction.

## 16. Memory realities
- Redis performance depends on memory sizing and key design.
- Big keys are dangerous because one huge value or structure can block or slow operations.
- Hot keys are dangerous because one key may receive disproportionate traffic and overload a single shard or CPU path.
- Serialization format matters; overly large objects waste memory and bandwidth.

## 17. Cache failure patterns
- Cache penetration: repeated misses for nonexistent data. Mitigate with null caching or Bloom-filter style techniques.
- Cache breakdown: one hot key expires and causes a surge to the database. Mitigate with mutex, request coalescing, or proactive refresh.
- Cache avalanche: many keys expire together. Mitigate with TTL jitter and staggered refresh.

## 18. Distributed locks
- Redis is often used for distributed locks with `SET key value NX PX ttl`.
- Lock release should verify ownership token before deleting.
- Lock TTL is essential to avoid deadlock on client crash.
- Be careful claiming Redis locks are perfect global correctness primitives; edge cases and network partitions matter.
- For strict correctness across distributed systems, evaluate whether Redis locking is sufficient before relying on it for critical invariants.

## 19. Java ecosystem
- Spring Data Redis is common in Spring Boot.
- Lettuce is a common Redis driver and supports reactive use cases.
- Redisson offers higher-level constructs like locks, maps, and distributed objects.
- In Java interviews, be ready to explain cache-aside, TTL, eviction, sessions, and rate limiting with Redis.

## 20. Security
- Enable authentication and ACLs.
- Use TLS where needed.
- Do not expose Redis directly to the public internet.
- Restrict dangerous commands when appropriate.
- Treat Redis dumps and AOF files as sensitive data.

## 21. Monitoring and operations
- Memory usage and fragmentation.
- Hit rate and miss rate.
- Evictions.
- Expired keys.
- Replication lag.
- Slow log entries.
- Connected clients and blocked clients.
- Hot key and big key patterns.
- Persistence health and rewrite activity.

## 22. Performance tuning levers
| Lever | Effect | Trade-off |
| --- | --- | --- |
| TTL strategy | Prevents stale data and controls memory | Bad TTL design can cause stampedes |
| Pipelining | Better throughput | Not atomic |
| Eviction policy | Better behavior under memory pressure | Wrong policy hurts hit rate |
| Data type choice | Better memory and query efficiency | Wrong type creates waste or complexity |
| Sharding/Cluster | More scale | More operational complexity |

## 23. Best use cases
- Application cache.
- Session storage.
- API rate limiting.
- Token blacklists or OTP storage.
- Leaderboards and ranking.
- Real-time counters.
- Lightweight queues or streams for moderate workloads.
- Feature flags and ephemeral config.

## 24. When not to use Redis
- You need durable replayable event history like Kafka.
- You need rich broker routing and queue semantics like RabbitMQ.
- You need relational joins and strong SQL transactions.
- Your dataset does not fit memory economically.
- You are treating cache as the only copy of business-critical truth without careful persistence design.

## 25. Pros
- Extremely low latency.
- Simple and powerful data structures.
- Great TTL and atomic operation support.
- Excellent for hot data and ephemeral state.
- Useful for many infra patterns beyond cache.

## 26. Cons
- Memory is expensive compared with disk.
- Weak fit for rich relational queries.
- Durability depends on persistence configuration.
- Big keys and hot keys can become serious problems.
- Pub/Sub is not durable.
- Can be overused as a hammer for unrelated problems.

## 27. Redis vs Memcached
- Redis offers richer data structures, persistence options, replication, Lua scripting, and more functionality.
- Memcached is simpler and focused mainly on basic caching.
- Redis is usually chosen when you need more than just plain key-value cache.

## 28. Redis vs Kafka
- Redis is primarily an in-memory state and cache system.
- Kafka is primarily a durable event streaming platform.
- Redis Streams can look like messaging, but Kafka is far stronger for large-scale retained event backbones and replay.

## 29. Redis vs RabbitMQ
- Redis can do simple queues and streams, but RabbitMQ is more purpose-built for brokered routing, acknowledgements, DLQ workflows, and delivery control.
- Redis is usually better when the real need is speed and state, not broker complexity.

## 30. Common mistakes
- Thinking Redis is only a cache.
- Using no TTL for cache entries.
- Using the same expiration time for many keys and causing stampedes.
- Ignoring eviction policy and memory limits.
- Storing huge blobs or very large collections under one key.
- Treating Pub/Sub as durable messaging.
- Assuming Redis transactions are full ACID transactions.

## 31. Practical patterns
- Cache-aside for database reads.
- Sliding window rate limiter using sorted sets.
- Fixed window counter using `INCR` plus TTL.
- Session store with TTL refresh.
- Distributed lock with ownership token.
- Leaderboard with sorted sets.
- Stream consumer groups for moderate event workflows.

## 32. Tricky interview questions and answers

### Q1. Is Redis only a cache?
- No. It is also used for sessions, counters, rate limiting, locks, leaderboards, streams, and ephemeral state.

### Q2. Why is Redis fast?
- Because it is primarily memory-based and uses efficient data structures with low-latency access patterns.

### Q3. What is the difference between RDB and AOF?
- RDB stores snapshots; AOF logs write operations. RDB is lighter and faster to restore in some cases, while AOF often offers better durability options.

### Q4. What is the difference between Sentinel and Cluster?
- Sentinel manages failover for primary-replica setups; Cluster shards data across nodes and also provides high availability.

### Q5. What is cache-aside?
- Application reads cache first, loads from database on miss, then writes result back to cache.

### Q6. Why is TTL important?
- It prevents stale data, controls memory, and makes temporary-state patterns practical.

### Q7. What is the difference between Redis Pub/Sub and Streams?
- Pub/Sub is transient and loses messages for offline subscribers; Streams retain entries and support consumer groups.

### Q8. Can Redis replace Kafka?
- Not for most durable event streaming backbones. Redis Streams help for moderate messaging use cases, but Kafka remains stronger for large retained replayable streams.

### Q9. Can Redis replace RabbitMQ?
- For some simple queue-like patterns, partially. For rich routing, acknowledgements, and mature queue workflows, RabbitMQ is usually better.

### Q10. What are hot keys?
- Keys that receive disproportionate traffic and can overload one node or shard.

### Q11. What are big keys?
- Keys whose values or collections are very large and can cause latency, memory, and operational problems.

### Q12. Are Redis transactions ACID like a relational database?
- No. They are useful but not equivalent to full SQL transaction semantics.

### Q13. Why use Lua scripts?
- To make multi-step logic atomic and reduce race conditions.

### Q14. What is an eviction policy?
- The strategy Redis uses to remove keys when memory is full.

### Q15. What is cache avalanche?
- Many keys expiring together, causing a traffic surge to the database.

### Q16. What is cache breakdown?
- A hot key expires and many requests flood the backing store.

### Q17. What is cache penetration?
- Repeated requests for missing data keep bypassing cache and hitting the database.

### Q18. Why can Redis locking be dangerous if oversold?
- Because correctness depends on timing, ownership checks, failover behavior, and partition assumptions. It is useful, but not magic.

### Q19. When should you not store everything in Redis?
- When the dataset is too large or too durable to justify memory cost or when relational queries and strong transactions are required.

### Q20. What is the biggest beginner misunderstanding?
- Treating Redis as either only a cache or as a perfect durable database, instead of understanding its real trade-offs.

## 33. Quick revision checklist
- Can you explain the main Redis data types and their use cases?
- Can you explain cache-aside and TTL?
- Can you explain RDB vs AOF?
- Can you explain Sentinel vs Cluster?
- Can you explain Pub/Sub vs Streams?
- Can you explain eviction and hot-key or big-key problems?
- Can you explain when RabbitMQ or Kafka is a better fit?

## 34. One-line memory anchors
- Redis is memory-first and latency-first.
- TTL is one of its most valuable features.
- Streams are stronger than Pub/Sub for retained consumption, but still not full Kafka replacement.
- Sentinel is for failover; Cluster is for sharding plus HA.
- Big keys, hot keys, and bad TTL strategy cause real production pain.