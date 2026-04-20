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

## 35. Senior-Level Deep Follow-up Questions

These are the questions interviewers ask after you answer the basics correctly. They test whether you truly understand internals or just memorized surface-level facts.

### DQ1. If Redis is single-threaded, how does it handle millions of concurrent requests?
- Redis uses a single main thread for command execution, but that does not mean the entire system is single-threaded.
- The key insight is that Redis uses an event loop based on I/O multiplexing (epoll on Linux, kqueue on macOS). One thread monitors thousands of socket connections simultaneously without blocking on any single one.
- When data arrives on any socket, the event loop picks it up, executes the command (which is almost always sub-microsecond because it is purely in-memory), writes the response, and moves to the next ready socket.
- Because each operation is in-memory and there is no disk I/O, no lock contention, and no context switching between threads, a single thread can process 100,000+ operations per second easily.
- Starting from Redis 6, I/O threading was introduced. The main thread still executes commands, but reading from sockets and writing responses can be offloaded to multiple I/O threads. This helps when the bottleneck is network I/O parsing, not command execution.
- So the correct senior answer is: Redis is single-threaded for command execution (which avoids locking), but uses I/O multiplexing to handle many connections, and optionally I/O threads for network read/write. The single-threaded model is actually a strength, not a weakness, because it eliminates lock contention and makes every operation atomic without any locking overhead.

### DQ2. What exactly happens during a fork() for RDB snapshots and what are the memory implications?
- When Redis creates an RDB snapshot or rewrites the AOF, it calls `fork()` to create a child process.
- After `fork()`, the child process has a copy of the parent's memory space. On modern Linux, this uses copy-on-write (COW). Initially, both parent and child share the same physical memory pages.
- As the parent Redis process continues serving writes, any page that gets modified is copied before the write. This means the child keeps seeing the old data (point-in-time snapshot) while the parent works on new data.
- The memory overhead depends on how much data changes during the snapshot. If 10% of keys are updated during the save, roughly 10% extra memory is needed for COW pages.
- In the worst case (heavy write workload during snapshot), memory usage can nearly double temporarily.
- This is why Redis documentation recommends leaving enough free memory headroom, and why huge Redis instances on machines with tight memory can be dangerous during background saves.
- On Linux, `Transparent Huge Pages` (THP) can make COW overhead much worse because a single byte change causes a 2MB huge page copy instead of a 4KB regular page. This is why Redis warns you to disable THP.

### DQ3. How does Redis Cluster handle slot migration and what happens to requests during resharding?
- Redis Cluster divides the key space into 16384 hash slots. Each master node owns a subset of these slots.
- When you add a node or rebalance, slots are migrated from one node to another.
- During migration of a slot, the source node marks the slot as MIGRATING and the target marks it as IMPORTING.
- If a client sends a request for a key in a migrating slot:
  - If the key still exists on the source, the source handles it normally.
  - If the key has already been moved to the target, the source responds with an ASK redirect, telling the client to try the target node for this specific request.
  - MOVED redirect means the slot has permanently moved. ASK redirect means it is in the process of moving.
- Keys are migrated one by one (or in batches). During migration, there is a brief period where some keys are on the old node and some are on the new node.
- The client libraries handle ASK and MOVED redirects transparently, so the application usually does not notice, but there can be a small latency increase during resharding.
- Large keys (big sets, large hashes) can cause migration pauses because migrating one big key blocks the source node's main thread. This is another reason big keys are dangerous.

### DQ4. Explain the Redlock algorithm and why Martin Kleppmann criticized it.
- Redlock was proposed by Salvatore Sanfilippo (Redis creator) as a distributed lock algorithm using multiple independent Redis masters.
- The idea: acquire locks on N independent Redis nodes (typically 5). If you acquire the lock on a majority (N/2 + 1) within a short time, the lock is considered acquired. The lock has a TTL to prevent deadlocks.
- Martin Kleppmann's criticism (in his "How to do distributed locking" blog post):
  - He argued that Redlock depends on timing assumptions. If a process pauses (e.g., GC pause, page fault) after acquiring the lock but before doing work, the lock's TTL may expire. Another client then acquires the same lock, and now two clients think they hold it.
  - He proposed that for correctness-critical locks, you need fencing tokens (a monotonically increasing number that the storage system validates). Redis does not natively support fencing tokens.
  - His core argument: if you need the lock only for efficiency (avoiding duplicate work), a single Redis instance lock is simpler and good enough. If you need the lock for correctness (preventing data corruption), Redlock is not safe enough and you need something like ZooKeeper with fencing.
- Salvatore responded defending Redlock, arguing the timing issues are manageable in practice.
- The senior takeaway: use Redis locks for efficiency and best-effort mutual exclusion. For strict safety-critical distributed locking, understand the limitations and consider alternatives like ZooKeeper, etcd, or database-based locking with fencing tokens.

### DQ5. How does Redis handle memory fragmentation and what can you do about it?
- Redis allocates and frees memory as keys are created, updated, and deleted. Over time, the allocator (jemalloc by default) may have free memory scattered across many small gaps that cannot be reused for larger allocations.
- `INFO memory` shows `mem_fragmentation_ratio`. If this is significantly above 1.0 (e.g., 1.5 means 50% overhead), fragmentation is a problem.
- Causes: frequent creation and deletion of keys with varying sizes, especially many small keys mixed with large ones.
- Solutions:
  - Redis 4.0+ introduced `activedefrag yes` which runs a background defragmentation process. It moves memory allocations to consolidate free space, similar to a garbage compactor.
  - Restart Redis (a drastic option that rebuilds memory layout from scratch via RDB/AOF reload).
  - Use more consistent key sizes where possible.
  - jemalloc itself is designed to minimize fragmentation, but no allocator is perfect under adversarial workloads.
- The fragmentation ratio below 1.0 can also be concerning; it may indicate Redis is using swap, which destroys performance.

### DQ6. How does Redis achieve sub-millisecond latency? Walk through a request lifecycle.
- Client sends a command over a TCP connection (or Unix socket).
- The Redis event loop (using epoll/kqueue) detects the socket is readable.
- The I/O layer reads the RESP protocol bytes from the socket buffer.
- The command is parsed and looked up in the command table.
- The command executes against in-memory data structures. For example, a GET on a string key is a hash table lookup, which is O(1). A ZADD on a sorted set is a skip list insertion, which is O(log N).
- The result is serialized into RESP format and written to the socket output buffer.
- The event loop moves to the next ready connection.
- Total time for most commands: single-digit microseconds. Network round-trip (even on localhost) typically adds 50-100 microseconds. Over a network, 0.1-0.5ms is common.
- There is no disk I/O in the read path (memory only), no thread context switching, no lock acquisition, and no query parsing or optimization like SQL databases. This is why Redis is so fast.

### DQ7. What is the thundering herd problem in Redis caching and how do you solve it?
- When a very popular cache key expires, hundreds or thousands of requests simultaneously discover the cache miss and all hit the database at once.
- This can overwhelm the database and cause cascading failures.
- Solutions:
  - **Mutex/lock pattern**: the first request that discovers the miss acquires a short-lived Redis lock, fetches from DB, and populates cache. Other requests either wait briefly or get a stale value.
  - **Request coalescing (singleflight)**: at the application level, only one goroutine/thread fetches; others wait for the same result. Go's `singleflight` package is a famous example.
  - **Proactive refresh**: refresh the cache before it expires. A background job or a near-expiry trigger reloads the value while the old value is still being served.
  - **TTL jitter**: add a random offset to TTL values so keys do not all expire at the same time.
  - **Stale-while-revalidate**: serve the stale cached value while asynchronously refreshing it. This keeps latency low and prevents stampedes.

### DQ8. What are the internals of Redis sorted sets? Why skip lists instead of balanced trees?
- Redis sorted sets use two data structures internally:
  - A **hash table** that maps member to score (for O(1) score lookups and membership checks).
  - A **skip list** that maintains elements sorted by score (for O(log N) range queries and insertions).
- Why skip lists instead of AVL/red-black trees?
  - Skip lists are simpler to implement and reason about.
  - They have similar O(log N) performance characteristics for insertion, deletion, and search.
  - They are easier to implement range operations efficiently. Getting elements between score A and score B is very natural in a skip list — you find the starting point and walk forward.
  - They are easier to implement concurrently (though Redis is single-threaded, simplicity still matters for maintainability).
  - Salvatore Sanfilippo specifically chose skip lists because they are simpler, debuggable, and equally efficient for Redis's use cases.
- For small sorted sets (few elements), Redis uses a compact `ziplist` (or `listpack` in newer versions) encoding instead, saving memory.

### DQ9. How does Redis handle persistence without blocking the main thread?
- **RDB**: uses `fork()` + child process. The child writes the snapshot to disk. The parent continues serving requests. The only blocking part is the `fork()` system call itself, which is usually fast but can take milliseconds on very large datasets.
- **AOF**: write operations are appended to the AOF buffer. The actual `fsync` to disk can happen in three modes:
  - `always`: fsync after every write (safest, slowest — can add latency).
  - `everysec`: fsync once per second in a background thread (good balance — at most 1 second of data loss).
  - `no`: let the OS decide when to flush (fastest, least safe).
- **AOF rewrite**: similar to RDB, uses `fork()` to create a child that rewrites the AOF file. New writes during rewrite go to both the old AOF and a rewrite buffer. When the child finishes, the rewrite buffer is merged.
- Key insight: the persistence mechanisms are designed to minimize impact on the main thread. The `fork()` + COW approach is the critical technique that makes this possible.

### DQ10. How does Redis handle partial failures in a cluster? What about split-brain?
- In Redis Cluster, if a master becomes unreachable by the majority of masters, its replica is promoted after a configurable timeout.
- If a network partition isolates a master with some clients but separates it from the majority of the cluster:
  - The isolated master will stop accepting writes after `cluster-node-timeout` because it cannot communicate with enough other masters.
  - The other side of the partition will elect a new master from the replicas.
  - When the partition heals, the old master (now a stale master) will discover it has been replaced, demote itself to replica, and sync from the new master. Any writes accepted by the old master during the brief window before it stopped accepting writes are lost.
- This means Redis Cluster can lose acknowledged writes during network partitions. The window is bounded by `cluster-node-timeout`.
- `min-replicas-to-write` and `min-replicas-max-lag` can be configured to make a master refuse writes if it does not have enough reachable replicas, but this trades availability for safety.
- Senior takeaway: Redis Cluster prioritizes availability with eventual consistency. It is not CP in CAP terms. If you need absolute write safety during partitions, Redis Cluster alone is not sufficient.

### DQ11. How does Redis handle client-side caching (tracking)?
- Redis 6 introduced server-assisted client-side caching via the `CLIENT TRACKING` command.
- The idea: the client caches values locally (in application memory). Redis tracks which keys each client has cached. When a tracked key is modified, Redis sends an invalidation message to the client.
- Two modes:
  - **Default mode**: Redis tracks exact keys per client. When a key changes, it invalidates that specific client.
  - **Broadcasting mode**: clients subscribe to key prefixes. Redis broadcasts invalidations for any key matching the prefix. Simpler but generates more invalidation traffic.
- This significantly reduces network round-trips for read-heavy workloads where the same keys are read repeatedly.
- The client must handle invalidation messages and evict its local cache entry.
- Lettuce (Java Redis driver) supports this feature.

### DQ12. What is the difference between Redis pipelining, transactions, and Lua scripts?
- **Pipelining**: sends multiple commands without waiting for each response. Saves network round-trips. Commands are NOT atomic — other clients' commands can interleave between them.
- **Transactions (MULTI/EXEC)**: groups commands and executes them sequentially without interleaving from other clients. But there is no rollback — if one command fails, the others still execute. WATCH adds optimistic locking.
- **Lua scripts**: execute atomically on the server side. No other command runs between steps of the script. Can contain conditional logic, loops, and complex operations. The closest thing to a stored procedure.
- When to use what:
  - Pipelining: when you need speed and commands are independent.
  - Transactions: when you need non-interleaved execution of a fixed set of commands.
  - Lua: when you need atomic conditional logic (e.g., check-and-set, rate limiting with complex rules, lock release with ownership verification).

### DQ13. Why does Redis recommend disabling Transparent Huge Pages (THP)?
- Linux THP uses 2MB memory pages instead of 4KB pages.
- During `fork()` for background persistence, copy-on-write happens at the page level. With THP, modifying even 1 byte triggers a copy of a 2MB page instead of a 4KB page.
- This can cause massive memory spikes and significant latency spikes during RDB saves or AOF rewrites.
- Redis explicitly checks for THP on startup and warns if it is enabled.
- Always disable THP on Redis servers.

### DQ14. How would you design a rate limiter using Redis? Compare approaches.
- **Fixed window counter**: use `INCR key` and `EXPIRE key window_seconds`. Simple but has the boundary problem — a burst at the end of one window plus the start of the next can allow 2x the limit.
- **Sliding window log**: store each request timestamp in a sorted set. Count entries within the window using `ZRANGEBYSCORE`. Accurate but memory-heavy for high-traffic endpoints.
- **Sliding window counter**: combine fixed window counts with interpolation. Use two consecutive window counters and calculate a weighted count. Good balance of accuracy and efficiency.
- **Token bucket**: use a key with a count and a timestamp. On each request, calculate tokens that should have been added since the last request, add them (up to max), then subtract one. Can be implemented atomically with a Lua script.
- **Leaky bucket**: similar to token bucket but focused on smoothing output rate. Often implemented with a sorted set or list.
- For production systems, the sliding window counter or token bucket (via Lua script) are the most common choices. The Lua script approach ensures atomicity.

### DQ15. What happens when Redis runs out of memory?
- Behavior depends on the configured `maxmemory-policy`:
  - `noeviction`: Redis returns errors for write commands (but reads still work). This is the safest if you cannot afford to lose any key.
  - LRU/LFU/TTL policies: Redis evicts keys according to the policy to make room. Writes succeed as long as eviction frees enough space.
- If `maxmemory` is not set and the machine runs out of physical RAM, the OS may start swapping (which kills Redis performance) or the OOM killer may terminate the Redis process.
- In a cluster, running out of memory on one shard does not affect other shards directly, but it can cause the overwhelmed shard to become unresponsive.
- Best practice: always set `maxmemory`, choose an appropriate eviction policy, and monitor memory usage with alerts well before hitting the limit.