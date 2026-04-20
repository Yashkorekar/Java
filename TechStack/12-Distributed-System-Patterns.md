# Distributed System Patterns Interview Prep and Deep Guide

## 1. What this guide covers
- This file covers the critical distributed system patterns that appear in almost every system design interview.
- These patterns are technology-agnostic. They apply whether you are using Redis, Kafka, PostgreSQL, DynamoDB, or any other tool.
- Topics: consistent hashing, consistency models, circuit breaker, retry patterns, idempotency, bulkhead, timeouts, backpressure, distributed locking, and leader election.
- Each topic starts from basics so a 0-experience developer can understand, then goes to the depth a 10-year senior would be expected to know.

## 2. Consistent Hashing — the non-negotiable topic

### The problem it solves
- You have N servers (cache nodes, database shards, or service instances). You want to distribute data or requests evenly.
- Naive approach: `server = hash(key) % N`. This works until you add or remove a server. When N changes, almost all keys remap to different servers. For a cache cluster, this means almost all cached data becomes invalid simultaneously — a catastrophic cache miss storm.

### How consistent hashing works
- Imagine a ring (circle) with positions from 0 to 2^32 - 1 (or any large number).
- Each server is placed on the ring at a position determined by hashing its name or ID: `position = hash(server_name)`.
- Each key is also hashed to a position on the ring: `position = hash(key)`.
- To find which server owns a key, start at the key's position on the ring and walk clockwise until you hit a server. That server owns the key.

### What happens when a server is added
- The new server is placed on the ring at its hash position.
- Only the keys between the new server and the previous server (counterclockwise) are remapped to the new server.
- All other keys stay on their current servers.
- Instead of remapping ~100% of keys (naive modulo), you remap only ~1/N of keys.

### What happens when a server is removed
- The removed server's keys are inherited by the next server clockwise on the ring.
- Only those keys move. Everything else stays put.

### The hotspot problem and virtual nodes
- If you place only one point per server on the ring, the distribution can be very uneven. One server might own a huge arc of the ring while another owns a tiny one.
- **Virtual nodes (vnodes)**: instead of placing each server at one position, place it at many positions (e.g., 100-200 virtual nodes per physical server). Each virtual node is a separate point on the ring, all mapping to the same physical server.
- This spreads the ring positions more evenly, resulting in much better load distribution.
- When a server is removed, its load is spread across many other servers (because its virtual nodes are scattered around the ring), preventing any single server from being overwhelmed.

### Where consistent hashing is used
- **Cassandra**: uses consistent hashing to distribute data across nodes. Each node owns a range of the token ring.
- **DynamoDB**: uses consistent hashing internally for partition distribution.
- **Memcached / Redis clusters**: client-side consistent hashing for distributing keys across cache nodes.
- **CDN**: mapping content to edge servers.
- **Load balancers**: when you need the same client/session to hit the same backend (e.g., consistent hashing on client IP or session ID).
- **Kafka**: partition assignment is similar in concept (though implemented differently).

### Interview walkthrough example
- "Design a distributed cache for 10 million keys across 5 cache nodes."
  1. Hash the 5 nodes onto the ring, each with 200 virtual nodes = 1000 points on the ring.
  2. Each key is hashed to a ring position and assigned to the next clockwise node.
  3. When node 6 is added: ~1/6 of keys migrate to it. The other 5/6 stay put.
  4. When node 3 fails: its keys (spread across its virtual nodes) redistribute to the remaining nodes roughly equally.

### Deep follow-up questions interviewers ask

#### Why not just use hash(key) % N?
- Because when N changes, almost every key remaps. For a cache, this causes a massive miss storm. For a database, it means massive data migration. Consistent hashing limits the remapping to ~1/N.

#### What is the bounded-load variant of consistent hashing?
- Standard consistent hashing can still create imbalance if certain keys are "hot" (accessed much more than others).
- **Consistent hashing with bounded loads** (Google, 2017): each server has a capacity limit (e.g., 1.25x the average load). If a key maps to a server that is already at its limit, the key is placed on the next server clockwise that has capacity.
- This prevents any single server from being overwhelmed by hot keys while maintaining the benefits of consistent hashing.

#### What is the jump hash algorithm?
- Jump hash (Google) is a simpler algorithm that maps keys to N buckets with perfect uniformity. It uses a fast deterministic pseudo-random sequence.
- Pros: very fast, near-perfect distribution, zero memory overhead.
- Cons: only works when servers are numbered 0 to N-1. Adding/removing servers at arbitrary positions is not supported. Best for systems where you scale by adding nodes at the end (e.g., shards numbered sequentially).

#### How does Cassandra's token ring work?
- Each Cassandra node is assigned token ranges on a ring (0 to 2^63 - 1 by default with Murmur3 partitioner).
- When data is written, the partition key is hashed (Murmur3), and the hash determines which range (and therefore which node) owns the data.
- With virtual nodes (vnodes, default 256 per node), each physical node owns many small ranges instead of one large range, improving distribution and rebalancing speed.
- When a node joins, it takes over some ranges from existing nodes. When a node leaves, its ranges are distributed to remaining nodes.

## 3. Consistency Models — much deeper than just "strong vs eventual"

### Why consistency matters
- In a distributed system, data exists on multiple nodes. When one node is updated, others may not reflect the change immediately.
- The consistency model defines what guarantees the system provides about the order and visibility of operations across nodes.

### Spectrum of consistency (from strongest to weakest)

#### Linearizability (strongest)
- Every operation appears to take effect at a single, instantaneous point in time between its start and end.
- Once a write completes, all subsequent reads (from any client, on any node) will see that write.
- It is as if there is a single copy of the data and all operations are atomic.
- This is the gold standard but the most expensive. It requires coordination (consensus) which adds latency.
- Examples: ZooKeeper reads in sync mode, etcd, CockroachDB (default), Spanner.

#### Sequential consistency
- All operations appear in some total order that is consistent with the order of operations on each individual client.
- But the total order may not match real-time ordering. If client A writes before client B (in wall-clock time), the system may order client B's write first — as long as each client's operations maintain their internal order.
- Weaker than linearizability because it does not respect real-time ordering.

#### Causal consistency
- Operations that are causally related are seen in the same order by all nodes. Operations that are NOT causally related (concurrent) may be seen in different orders by different nodes.
- Example: if Alice posts a message and then Bob replies to it, everyone will see Alice's post before Bob's reply. But if Alice and Charlie post unrelated messages simultaneously, different users might see them in different orders.
- Stronger than eventual consistency but weaker than linearizability. Does not require global coordination, so it is faster.
- Used by: MongoDB (causal consistency sessions), some CRDT-based systems.

#### Read-your-writes consistency
- A client will always see its own writes. After writing, the same client's subsequent reads will reflect that write.
- Other clients may not see the write immediately.
- This is the minimum consistency most users expect. "I updated my profile and I can see the change."
- Implementation: route reads to the same node that received the write (sticky sessions), or read from primary, or use version tokens.

#### Monotonic reads
- If a client reads a value at time T, it will never see an older value in subsequent reads.
- Prevents the "time travel" problem where a client reads new data, then old data, then new data again (because reads hit different replicas with different lag).
- Implementation: stick reads to one replica, or track read positions.

#### Eventual consistency (weakest practical model)
- If no new writes occur, all replicas will eventually converge to the same value.
- No guarantee about when. Could be milliseconds, could be seconds.
- Does not guarantee order or recency.
- Examples: DNS propagation, Cassandra with consistency level ONE, DynamoDB (eventually consistent reads), S3 (historically, now strong after 2020).

### Strong consistency vs eventual consistency — the real trade-off
- **Strong consistency** (linearizability):
  - Every read returns the most recent write.
  - Requires coordination between nodes (consensus protocol: Raft, Paxos).
  - Higher latency (must wait for majority/all nodes to agree).
  - Lower availability during network partitions (CP in CAP).
  - Use when: financial transactions, inventory counts, leader election, configuration management.
- **Eventual consistency**:
  - Reads may return stale data.
  - No coordination needed. Any node can respond immediately.
  - Lower latency, higher availability (AP in CAP).
  - Use when: social media feeds, analytics counters, recommendation scores, DNS, CDN cached content.

### Quorum consistency
- Used by Cassandra, DynamoDB, Riak, and similar leaderless systems.
- **W** = number of nodes that must acknowledge a write.
- **R** = number of nodes that must respond to a read.
- **N** = total replicas.
- If **R + W > N**, reads and writes overlap — at least one node in the read quorum has the latest write. This gives strong-ish consistency.
- Common configuration: N=3, W=2, R=2 → R+W=4 > 3 ✓.
- Trade-offs:
  - W=1, R=1: fastest, but no overlap. Eventual consistency only.
  - W=N, R=1: writes are slow (must reach all nodes) but reads are fast. Write availability is low.
  - W=1, R=N: writes are fast but reads are slow and read availability is low.
- "Sloppy quorum": during partitions, writes go to ANY available nodes (not just the designated replicas). Hinted handoff later moves data to the correct replicas. This improves availability but weakens the quorum guarantee.

### CRDT (Conflict-free Replicated Data Types)
- Data structures that can be updated independently on different replicas and merged automatically without conflicts.
- Types: counters (G-Counter, PN-Counter), sets (G-Set, OR-Set), registers (LWW-Register), maps.
- Used by: Redis (CRDT-based active-active replication in Redis Enterprise), Riak, collaborative editing systems.
- Why it matters: CRDTs allow multi-leader/leaderless replication without conflict resolution logic. The data types are mathematically designed to converge.
- Limitation: not all data structures can be made into CRDTs. Complex business logic may not fit.

## 4. Circuit Breaker Pattern

### The problem
- Service A calls Service B. Service B is slow or down. Without protection, Service A keeps sending requests, waits for timeouts, exhausts its thread pool, and becomes slow or unresponsive too. This cascading failure can take down the entire system.

### How circuit breaker works
- Inspired by electrical circuit breakers. Three states:
  1. **Closed** (normal): requests flow through. Failures are counted.
  2. **Open** (tripped): when failure count exceeds a threshold within a time window, the circuit "opens." All requests fail immediately (fast fail) without actually calling Service B. This protects both Service A (from waiting) and Service B (from being overwhelmed).
  3. **Half-Open** (testing recovery): after a configured wait period, the circuit allows a small number of test requests through. If they succeed, the circuit closes (back to normal). If they fail, the circuit opens again.

### Configuration parameters
- **Failure threshold**: number or percentage of failures to trigger opening. Example: 50% failure rate in the last 10 requests.
- **Timeout duration**: how long the circuit stays open before trying half-open. Example: 30 seconds.
- **Success threshold in half-open**: number of successful test requests needed to close the circuit. Example: 3 consecutive successes.
- **Sliding window**: count-based (last N requests) or time-based (last N seconds).

### What counts as a failure
- Timeouts, HTTP 5xx errors, connection refused, socket exceptions.
- HTTP 4xx (client errors) should usually NOT trip the circuit — those are the caller's fault, not the service's.
- Slow responses (above a latency threshold) can optionally be counted as failures.

### Fallback strategies
- When the circuit is open, what does the caller return?
  - **Cached response**: return the last known good response.
  - **Default value**: return a safe default (e.g., empty search results, generic recommendations).
  - **Degraded experience**: show a simplified version of the page.
  - **Error message**: tell the user the feature is temporarily unavailable.
  - **Queue for retry**: accept the request and process it later.

### Java ecosystem
- **Resilience4j**: the modern standard for Java/Spring. Provides circuit breaker, retry, rate limiter, bulkhead, and time limiter as composable decorators.
  - `@CircuitBreaker(name = "paymentService", fallbackMethod = "fallback")`
  - Configuration via `application.yml` or code.
- **Hystrix** (Netflix): deprecated but still in some legacy codebases. Resilience4j is the replacement.
- **Spring Cloud Circuit Breaker**: abstraction layer that can use Resilience4j or other implementations.

### Interview deep follow-ups

#### What is the difference between circuit breaker and retry?
- Retry tries again immediately or after a short delay. It helps with transient failures (network blip, temporary overload).
- Circuit breaker stops trying when failures are persistent. It prevents wasting resources on a service that is clearly down.
- Best practice: use BOTH. Retry first (for transient issues), then circuit breaker to stop retrying when the service is consistently failing.
- The retry should be INSIDE the circuit breaker. If the circuit is open, no retry is attempted.

#### What is the difference between circuit breaker and timeout?
- Timeout limits how long you wait for a single request.
- Circuit breaker tracks failure patterns across multiple requests and stops sending requests when a pattern of failure is detected.
- Both work together: timeout prevents individual requests from blocking forever; circuit breaker prevents the system from repeatedly calling a broken service.

#### Can you have a circuit breaker per endpoint?
- Yes, and you should. If Service B has `/payments` and `/refunds`, a failure in `/payments` should not trip the circuit for `/refunds`.
- Resilience4j supports this via named circuit breaker instances.

## 5. Retry Patterns

### Why retry
- Many failures in distributed systems are transient: brief network issues, a server restarting, a momentary overload.
- Retrying after a short delay often succeeds.

### Retry strategies
- **Immediate retry**: try again right away. Only useful for very brief glitches.
- **Fixed delay**: wait a constant time (e.g., 1 second) between retries.
- **Exponential backoff**: double the wait each time: 1s, 2s, 4s, 8s, 16s... Prevents overwhelming the recovering service.
- **Exponential backoff with jitter**: add randomness to the delay. Without jitter, all clients retry at the same times (synchronized thundering herd). Jitter spreads retries out.
  - Full jitter: `delay = random(0, base * 2^attempt)`
  - Equal jitter: `delay = base * 2^attempt / 2 + random(0, base * 2^attempt / 2)`
  - Decorrelated jitter: `delay = random(base, previous_delay * 3)`
- **Best practice**: exponential backoff with jitter is the industry standard. AWS SDKs use it by default.

### What to retry and what NOT to retry
- **Retry**: network timeouts, HTTP 503, HTTP 429 (with Retry-After header), connection reset, temporary DNS failures.
- **Do NOT retry**: HTTP 400 (bad request — your input is wrong, retrying won't help), HTTP 401/403 (auth failure), HTTP 404 (resource doesn't exist), business logic errors, non-idempotent operations without idempotency keys.

### Max retries and retry budget
- Always set a maximum retry count (e.g., 3-5). Infinite retries can amplify failures.
- **Retry budget**: limit the percentage of requests that can be retries (e.g., 10% of total traffic). If more than 10% of requests are retries, stop retrying — the system is overloaded and retries are making it worse.
- This is used by gRPC and service meshes (Envoy).

### Retry amplification
- The most dangerous retry anti-pattern. In a microservice chain A → B → C:
  - A retries B 3 times. B retries C 3 times.
  - If C is slow, B sends 3 requests to C per A's request. A sends 3 requests to B.
  - Total requests to C: 3 × 3 = 9 for one original request.
  - With more hops: exponential amplification.
- Solution: retry only at the outermost layer, or use retry budgets, or use deadlines that propagate through the chain.

## 6. Idempotency — the must-know pattern

### What it means
- An operation is idempotent if performing it multiple times has the same effect as performing it once.
- `GET /user/123` is naturally idempotent — reading the same resource multiple times does nothing.
- `DELETE /user/123` is idempotent — deleting the same resource twice results in the same state (user is gone).
- `POST /payment` is NOT naturally idempotent — charging a customer twice is very different from charging once.

### Why it matters in distributed systems
- Networks are unreliable. A client sends a payment request, the server processes it, but the response is lost. The client does not know if the payment succeeded. It retries. Without idempotency, the customer is charged twice.
- At-least-once delivery (Kafka, RabbitMQ): messages may be delivered more than once. Consumers must handle duplicates.
- Retry patterns: retries can cause duplicate processing.

### How to implement idempotency

#### Idempotency key
- The client generates a unique key (UUID) for each operation and sends it with the request (usually in a header like `Idempotency-Key`).
- The server checks: "have I seen this key before?"
  - If yes: return the stored response from the first processing. Do NOT process again.
  - If no: process the request, store the result associated with the key, return the response.
- Storage: store idempotency keys in Redis (fast, with TTL for auto-cleanup) or a database table.
- TTL: keys should expire after a reasonable window (e.g., 24-48 hours). If the same key arrives after expiry, it is treated as a new request.

#### Database-level idempotency
- Use unique constraints. `INSERT INTO payments (idempotency_key, amount, ...) ON CONFLICT (idempotency_key) DO NOTHING`.
- Use conditional updates: `UPDATE inventory SET stock = stock - 1 WHERE product_id = ? AND stock > 0`.
- Use versioned updates (optimistic locking): `UPDATE ... WHERE version = ?`.

#### Natural idempotency
- Some operations are naturally idempotent: setting a value (PUT), deleting a resource, adding to a set (not a list).
- Design APIs to use idempotent operations where possible. Prefer `SET balance = 500` over `ADD balance 100`.

### Stripe's idempotency model (the gold standard)
- Stripe requires `Idempotency-Key` header for POST requests.
- If the same key is sent again within 24 hours, Stripe returns the exact same response without re-processing.
- If the original request is still processing, the retry gets a 409 Conflict.
- Keys expire after 24 hours.
- This is the model most payment and fintech companies follow.

### Interview follow-up: What is the difference between idempotency and exactly-once?
- Idempotency is about making duplicate processing safe (same result).
- Exactly-once is about preventing duplicates from happening.
- In practice, most distributed systems achieve "effectively exactly-once" by combining at-least-once delivery with idempotent processing.

## 7. Bulkhead Pattern

### The problem
- If all requests to all downstream services share the same thread pool, one slow or failing service can consume all threads. Other services (which are perfectly healthy) also fail because no threads are available to call them.

### How bulkhead works
- Inspired by ship bulkheads (compartments that prevent one leak from sinking the whole ship).
- Isolate resources for different services or call types:
  - **Thread pool isolation**: each downstream service gets its own thread pool with a fixed size. If the payment service is slow and exhausts its 10 threads, the inventory service's 10 threads are unaffected.
  - **Semaphore isolation**: limit the number of concurrent calls to a service using a semaphore (counter). Lighter weight than thread pools.
  - **Connection pool isolation**: separate database connection pools for different workloads (e.g., OLTP vs reporting queries).

### Java implementation
- Resilience4j Bulkhead:
  - `BulkheadConfig.custom().maxConcurrentCalls(10).maxWaitDuration(Duration.ofMillis(500)).build()`
  - When max concurrent calls are reached, additional calls wait (up to maxWaitDuration) or are rejected.
- Thread pool bulkhead: runs calls in a separate thread pool. More isolation but more overhead.
- Semaphore bulkhead: limits concurrency on the calling thread. Less overhead but no thread isolation.

### When to use
- When a service calls multiple downstream services and a failure in one should not affect the others.
- When different workloads share the same resource (e.g., critical API calls vs background sync using the same DB connection pool).

## 8. Timeout Patterns

### Why timeouts matter
- Without timeouts, a request to a slow/dead service blocks the calling thread forever. The thread pool fills up, the caller becomes unresponsive, and the failure cascades.
- Timeouts are the first line of defense in distributed systems.

### Types of timeouts
- **Connection timeout**: how long to wait to establish a TCP connection. Usually short (1-5 seconds). If the service is reachable, connection is fast.
- **Read/socket timeout**: how long to wait for a response after the connection is established. Depends on the expected operation time.
- **Request timeout**: total time allowed for the entire operation (connection + waiting + reading).

### Deadline propagation
- In a chain A → B → C, if A has a 5-second timeout:
  - A calls B with a deadline of "now + 5 seconds."
  - B should pass the remaining time to C. If 2 seconds were used for A→B, B calls C with "now + 3 seconds."
  - If the overall deadline has passed, no point in calling C at all.
- Without deadline propagation, each service uses its own timeout, and the total can exceed the user's patience.
- gRPC has built-in deadline propagation. HTTP-based systems must implement it via headers (e.g., `X-Deadline`).

### Common mistake: too-generous timeouts
- Setting a 30-second timeout "to be safe" means that when the service is down, you waste 30 seconds per request before failing. 100 concurrent requests × 30 seconds = all threads occupied for 30 seconds = cascading failure.
- Set timeouts based on P99 latency of the downstream service. If the service normally responds in 200ms at P99, a 1-2 second timeout is reasonable.

## 9. Backpressure

### What it is
- When a producer generates work faster than the consumer can process, the consumer needs a way to signal "slow down" to the producer.
- Without backpressure, the consumer's queue grows without bound, memory fills up, and the system crashes.

### Backpressure mechanisms
- **Drop requests**: return HTTP 503 or drop messages when the queue is full. Simple but lossy.
- **Block the producer**: if the buffer is full, the producer blocks until space is available. Used by Kafka producer (`max.block.ms`), reactive streams (`Subscription.request(n)`).
- **Rate limit the producer**: explicitly limit the rate of incoming requests (token bucket, etc.).
- **Load shedding**: intelligently drop lower-priority work when overloaded. Serve critical requests, reject non-critical ones.
- **Reactive streams (Java)**: `Publisher.subscribe(Subscriber)` with `Subscription.request(n)`. The subscriber tells the publisher how many items it can handle. This is pull-based backpressure. Used by Project Reactor (Spring WebFlux) and RxJava.

### Where you see it
- Kafka producer buffer full → blocks or throws.
- RabbitMQ flow control → blocks publishers.
- HTTP 429 Too Many Requests → client must slow down.
- Reactive streams → subscriber controls the pace.
- TCP flow control → receiver window tells sender to slow down.

## 10. Distributed Locking — beyond Redis basics

### When you need it
- Multiple processes/services need mutually exclusive access to a shared resource.
- Examples: preventing double-payment, ensuring only one instance processes a scheduled job, coordinating access to a shared file.

### Implementation approaches

#### Redis-based (simple, best-effort)
- `SET lock:payment:123 <owner-token> NX PX 30000` (set if not exists, with 30s TTL).
- To release: check owner-token, then delete (using Lua script for atomicity).
- Pros: fast, simple.
- Cons: not safe during Redis failover. If the primary crashes after the lock is set but before replication, the new primary does not have the lock. Two clients may hold the same lock.
- **Good for**: efficiency locks (preventing duplicate work). Not for correctness-critical locks.

#### Redlock (stronger Redis-based)
- Acquire locks on N/2 + 1 out of N independent Redis instances.
- Safer than single-instance locking, but criticized by Martin Kleppmann (see Redis DQ4 in 03-Redis.md).
- Still timing-dependent. Not suitable for strict correctness.

#### ZooKeeper-based
- Create an ephemeral sequential node under a lock path.
- The client with the lowest sequence number holds the lock.
- When the lock holder's session expires or the client disconnects, the ephemeral node is deleted and the next client gets the lock.
- Pros: strong consistency (ZAB consensus), no timing issues, handles client crashes cleanly via ephemeral nodes.
- Cons: higher latency than Redis, operational complexity of running ZooKeeper.

#### etcd-based
- Similar to ZooKeeper but using Raft consensus and a simpler API.
- Lease-based: create a key with a lease (TTL). The client that creates it holds the lock. Lease must be periodically renewed.
- If the client dies, the lease expires and the lock is released.

#### Database-based
- `SELECT ... FOR UPDATE` on a lock row. Or an advisory lock (`pg_advisory_lock` in PostgreSQL).
- Pros: no extra infrastructure. Uses the database you already have.
- Cons: database connections are expensive for long-held locks. Can cause contention.

### Fencing tokens
- The critical concept for correctness-safe distributed locking.
- Each lock acquisition gets a monotonically increasing fencing token (sequence number).
- The resource being protected (e.g., storage service) checks the fencing token. If a request arrives with a lower token than the last one seen, it is rejected.
- This prevents the scenario where: client A acquires lock, pauses (GC, network), lock expires, client B acquires lock, client A resumes and makes changes — now both clients think they have the lock.
- With fencing tokens, client A's write is rejected because its token is lower than client B's.
- ZooKeeper's sequential node number naturally serves as a fencing token. Redis does not natively support this.

## 11. Leader Election

### When you need it
- Some tasks must be done by exactly one process: cron jobs, database migrations, partition assignments, cache warming.
- Multiple instances run for redundancy, but only one should be the "leader" doing the work.

### Approaches
- **ZooKeeper**: create ephemeral nodes. The client with the lowest sequence number is leader. If it dies, the next one takes over.
- **etcd**: use lease-based key creation. First to create the key is leader. Lease renewal keeps leadership. Expiry triggers re-election.
- **Database**: use a `SELECT ... FOR UPDATE` or a row with a leader column and a heartbeat timestamp. If the heartbeat is stale, another instance can claim leadership.
- **Kubernetes**: use Kubernetes lease objects. Multiple pods compete for a Lease resource. The one that holds it is leader. Spring Cloud Kubernetes and client-go support this.
- **Raft/Paxos**: consensus protocols that include leader election as a core feature. Used internally by etcd, Consul, Kafka (KRaft).

### Split-brain in leader election
- If a network partition occurs, both sides may elect their own leader.
- Prevention: require a majority (quorum) to elect a leader. The minority side cannot elect because it does not have enough votes.
- This is why leader election systems use an ODD number of nodes (3, 5, 7).

## 12. Cache Invalidation — the hardest problem

### Why it is hard
- Phil Karlton's famous quote: "There are only two hard things in Computer Science: cache invalidation and naming things."
- Stale cache serves wrong data. Aggressive invalidation reduces cache benefits. The balance is system-specific.

### Caching strategies (recap + depth)

#### Cache-aside (lazy loading)
- Application checks cache first. On miss, loads from DB, writes to cache, returns.
- Pros: only caches data that is actually requested. Simple.
- Cons: first request is always a miss (cold cache). Data can become stale if the DB is updated without invalidating cache.
- **The most common strategy**. Used in most web applications.

#### Read-through
- The cache itself loads data from the DB on miss. Application only talks to the cache.
- Pros: application code is simpler. Cache handles loading logic.
- Cons: cache library/layer must support this. Same staleness issue.

#### Write-through
- Every write goes to cache AND DB synchronously.
- Pros: cache is always up-to-date. Reads are fast and consistent.
- Cons: writes are slower (two writes per operation). Cache may store data that is never read (wasted memory).

#### Write-behind (write-back)
- Write to cache immediately, return to client, then asynchronously flush to DB.
- Pros: very fast writes. Great for write-heavy workloads.
- Cons: data loss risk if cache crashes before flushing. Complexity in ensuring eventual DB write.

#### Write-around
- Write directly to DB, bypassing the cache. The cache is only populated on reads (cache-aside).
- Pros: avoids caching data that may not be read soon.
- Cons: first read after write is a miss.

### Invalidation strategies

#### TTL-based
- Cache entry expires after a fixed time. Simple but may serve stale data until TTL expires or waste cache on unnecessarily short TTLs.
- **TTL jitter**: add random offset to prevent thundering herd when many keys expire simultaneously.

#### Event-driven invalidation
- When data changes in the DB, publish an event (via Kafka, RabbitMQ, or DB trigger). Cache subscriber receives the event and invalidates or updates the cache entry.
- Pros: near-real-time freshness.
- Cons: infrastructure complexity. Event delivery may be delayed or lost (need to handle this).

#### Change Data Capture (CDC) for invalidation
- Use Debezium or similar CDC tool to watch database changes and push invalidation events.
- Most reliable event-driven approach because it reads the DB's own WAL/binlog.

#### Manual invalidation
- Application explicitly deletes or updates the cache entry after every write.
- Pros: precise control.
- Cons: easy to miss an invalidation path (especially with complex code). Race conditions (write to DB, cache invalidation fails, stale data persists).

#### Version-based invalidation
- Include a version number in the cache key (e.g., `product:123:v5`). When data changes, increment the version. Old cache entries are never read again (they are eventually evicted by LRU/TTL).
- Pros: avoids explicit invalidation entirely.
- Cons: old versions waste cache space until evicted.

### The cache stampede / thundering herd problem (from Redis DQ7)
- When a popular cache key expires, many requests simultaneously hit the DB.
- Solutions: mutex/lock, request coalescing (singleflight), proactive refresh, stale-while-revalidate, TTL jitter.
- This is one of the most asked interview questions about caching.

### The cache consistency problem
- In a system with DB + cache, there is always a window where they are inconsistent.
- Common approach: **invalidate (delete) the cache entry after updating the DB**, not before. If you delete first, another request may repopulate the cache with old data before your DB write completes.
- Even better: **DB write first, then delete cache**. If the cache delete fails, the entry will be stale until its TTL expires (which is the safer failure mode).
- Double-delete: delete cache, write DB, then delete cache again after a short delay. Handles race conditions better.
- The nuclear option: cache-aside with short TTLs and accept brief staleness.

## 13. Rate Limiting — complete picture

### Why rate limiting
- Protect services from abuse, DoS attacks, noisy clients, and accidental traffic spikes.
- Ensure fair usage across clients.
- Prevent cascading failures from overwhelming downstream services.

### Algorithms (recap + depth)

#### Fixed window counter
- Count requests in fixed time windows (e.g., per minute).
- Simple. But has the **boundary burst problem**: a client sends 100 requests in the last second of minute 1 and 100 requests in the first second of minute 2 = 200 requests in 2 seconds, despite a 100/min limit.

#### Sliding window log
- Store the timestamp of every request. Count requests within the last window.
- Accurate but memory-intensive (stores every timestamp).

#### Sliding window counter
- Combine the previous window's count with the current window's count, weighted by time elapsed.
- Formula: `count = previous_window_count × (1 - elapsed / window_size) + current_window_count`
- Good balance of accuracy and memory efficiency.

#### Token bucket
- A bucket holds tokens. Tokens are added at a fixed rate (e.g., 10/second). Each request consumes one token. If the bucket is empty, the request is rejected.
- Allows bursts up to the bucket size.
- **Most widely used** algorithm. Used by AWS API Gateway, Stripe, most cloud providers.

#### Leaky bucket
- Requests enter a queue (bucket). The queue drains at a fixed rate. If the queue is full, new requests are dropped.
- Smooths out bursts. Output rate is constant.
- Different from token bucket: token bucket allows bursts; leaky bucket does not.

### Distributed rate limiting
- When you have multiple API gateway instances, each must share rate limit state.
- **Centralized counter** (Redis): all instances check/increment a counter in Redis. Atomic operations (`INCR`, Lua scripts) ensure correctness.
- **Local + sync**: each instance tracks locally and periodically syncs with a central store. Less accurate but lower latency.
- **Approximate**: each instance gets a fraction of the total limit (e.g., 5 instances, 100/min total → 20/min per instance). Simple but not adaptive to uneven traffic distribution.

### What to return when rate limited
- HTTP 429 Too Many Requests.
- Headers: `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset`, `Retry-After`.
- These headers help well-behaved clients adjust their request rate.

### Multi-level rate limiting
- Per IP: prevent abuse from a single source.
- Per user/API key: enforce per-customer quotas.
- Per endpoint: different limits for different APIs (e.g., 1000/min for reads, 100/min for writes).
- Global: total system capacity limit.

## 14. Consistency Patterns in Practice

### Read-after-write consistency for users
- Problem: user updates profile on server A (write goes to primary). User's next request hits server B (which reads from a replica that hasn't replicated yet). User doesn't see their own update.
- Solutions:
  - Read from primary for own data (within a time window after write).
  - Include a write timestamp in the session; read from replica only if replica is past that timestamp.
  - Sticky sessions to the same replica.

### Monotonic reads
- Problem: user sees new data, refreshes, sees old data (because different requests hit different replicas).
- Solution: route each user's reads to the same replica (hash user ID to replica).

### Write-follows-reads (session causality)
- Problem: user reads data, makes a decision based on it, writes. But the write goes to a different node that hasn't seen the data the user read.
- Solution: propagate causal metadata (vector clocks, lamport timestamps) with requests.

### Conflict resolution strategies (for multi-leader/leaderless)
- **Last-writer-wins (LWW)**: highest timestamp wins. Simple but can silently drop writes.
- **Merge / custom resolution**: application-specific logic to merge conflicting writes (e.g., union of two sets).
- **CRDTs**: conflict-free by design. Automatic merge.
- **Operational transformation (OT)**: used in collaborative editing (Google Docs). Transforms concurrent operations to preserve intent.

## 15. Common mistakes in distributed system design
- Assuming the network is reliable.
- Not implementing timeouts on every external call.
- Retrying without backoff or jitter.
- Retrying non-idempotent operations.
- Using distributed locks for everything instead of designing for idempotency.
- Choosing strong consistency everywhere (too slow) or eventual consistency everywhere (too risky).
- Not thinking about what happens when a cache is down (will the DB handle the full load?).
- Not monitoring circuit breaker state, retry rates, and rate limiter rejections.

## 16. Tricky interview questions and answers

### Q1. What is the difference between consistency in ACID and consistency in CAP?
- ACID consistency: the database enforces schema rules and constraints. A transaction leaves the DB in a valid state.
- CAP consistency (linearizability): all nodes see the same data at the same time. It is about distributed state agreement, not schema validation.
- They are completely different concepts sharing the same word.

### Q2. Is it possible to have both strong consistency and high availability?
- During normal operation (no partition), yes. Most systems provide both.
- During a network partition, you must choose one (CAP theorem).
- Systems like Spanner achieve strong consistency with high availability by using very reliable networks (Google's private infrastructure) and TrueTime (GPS/atomic clock synchronization), making partitions extremely rare.

### Q3. How would you implement idempotency for a payment API?
- Client sends `Idempotency-Key: <UUID>` header.
- Server: check Redis/DB for this key. If found, return stored response. If not, process payment, store key + response in Redis with 24h TTL, return response.
- For concurrent duplicate requests: use a lock (Redis `SET NX`) on the idempotency key. First request processes; concurrent duplicates wait or get the stored response.

### Q4. What is the difference between circuit breaker and rate limiter?
- Rate limiter controls how many requests are ALLOWED (protecting the system from overload).
- Circuit breaker detects when a downstream service is FAILING and stops calling it (protecting the caller and giving the service time to recover).
- Rate limiter is proactive (applied before the request). Circuit breaker is reactive (applied based on observed failures).

### Q5. Can you have exactly-once processing in a distributed system?
- True exactly-once end-to-end is practically impossible because you cannot control all external side effects.
- What you CAN achieve: at-least-once delivery + idempotent processing = effectively exactly-once from the user's perspective.
- Kafka transactions provide exactly-once for Kafka-to-Kafka pipelines specifically.

### Q6. What happens if your Redis-based distributed lock fails during a Redis failover?
- If the primary Redis fails after granting a lock but before replicating to the replica, the new primary does not have the lock. Another client can acquire it. Two clients hold the same lock.
- Solution: use Redlock (multiple independent instances) for better safety, or use ZooKeeper/etcd for correctness-critical locks, or design for idempotency so double-processing is harmless.

### Q7. When should you NOT use a cache?
- When data changes very frequently (cache is always stale).
- When each request needs unique data (no reuse, so no cache benefit).
- When strong consistency is required and cache invalidation latency is unacceptable.
- When the data set is small enough to be served directly from the DB without performance issues.
- When the cache infrastructure cost exceeds the DB cost savings.

### Q8. What is the thundering herd problem and how do you solve it?
- Many clients simultaneously discover a cache miss (popular key expired or cache restart) and all hit the DB at once.
- Solutions: lock/mutex (one client fetches, others wait), singleflight/request coalescing, stale-while-revalidate, proactive refresh before expiry, TTL jitter.

## 17. Quick revision checklist
- Can you explain consistent hashing with virtual nodes on a whiteboard?
- Can you draw the circuit breaker state machine (closed → open → half-open)?
- Can you explain exponential backoff with jitter?
- Can you explain idempotency key implementation?
- Can you explain the consistency spectrum from linearizability to eventual?
- Can you explain the difference between token bucket and leaky bucket?
- Can you explain fencing tokens for distributed locks?
- Can you explain deadline propagation in a microservice chain?
- Can you explain retry amplification and how to prevent it?

## 18. One-line memory anchors
- Consistent hashing: add/remove servers without remapping the world.
- Circuit breaker: fail fast when downstream is broken.
- Retry with jitter: retry smart, not dumb.
- Idempotency: make duplicates safe, not absent.
- Bulkhead: one sinking compartment does not sink the ship.
- Timeout: never wait forever for anything.
- Cache invalidation: the problem that never has a perfect solution.
- Rate limiting: protect yourself before protecting others.
