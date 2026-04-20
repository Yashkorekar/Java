# Back-of-Envelope Estimation Guide for System Design Interviews

## 1. Why estimation matters
- In system design interviews, you are expected to estimate scale BEFORE designing.
- This is not about getting exact numbers. It is about showing that you can reason about scale and make design decisions based on data, not guesses.
- A candidate who says "we need 50,000 QPS for reads so we need caching and read replicas" is far stronger than one who says "let's add Redis because it's good."

## 2. Numbers every engineer should know

### Latency numbers (approximate, for mental math)

| Operation | Time |
| --- | --- |
| L1 cache reference | 1 ns |
| L2 cache reference | 4 ns |
| RAM reference | 100 ns |
| SSD random read | 100 μs (0.1 ms) |
| HDD random read | 10 ms |
| Network round-trip (same datacenter) | 0.5 ms |
| Network round-trip (cross-continent) | 100-150 ms |
| Redis GET (network + in-memory) | 0.1-0.5 ms |
| Simple database query (indexed, in cache) | 1-5 ms |
| Complex database query (joins, no cache) | 10-100 ms |
| HTTP request to external API | 50-500 ms |

### Key insight from these numbers
- Memory is ~100,000x faster than disk.
- SSD is ~100x faster than HDD.
- Network within a DC is ~500x slower than RAM.
- This is why caching (Redis/Memcached) is so effective — it replaces disk-speed DB lookups with memory-speed lookups, even with network overhead.

### Storage and throughput numbers

| Unit | Value |
| --- | --- |
| 1 KB | 1,000 bytes (a short text, a JSON record) |
| 1 MB | 1,000 KB (a high-quality photo, a small document) |
| 1 GB | 1,000 MB (a short movie, 1 billion bytes) |
| 1 TB | 1,000 GB (a decent-sized database) |
| 1 PB | 1,000 TB (large-scale data warehouse) |

### Useful constants for estimation

| Fact | Value |
| --- | --- |
| Seconds in a day | ~86,400 ≈ ~100,000 (use 10^5 for easy math) |
| Seconds in a month | ~2.6 million ≈ ~2.5 × 10^6 |
| Seconds in a year | ~31.5 million ≈ ~3 × 10^7 |
| Days in a year | 365 |
| 1 million requests/day | ~12 QPS (1M / 86400) |
| 100 million requests/day | ~1,200 QPS |
| 1 billion requests/day | ~12,000 QPS |

### Quick QPS conversion table

| Requests per day | Average QPS | Peak QPS (2-5x) |
| --- | --- | --- |
| 1 million | ~12 | 24-60 |
| 10 million | ~120 | 240-600 |
| 100 million | ~1,200 | 2,400-6,000 |
| 1 billion | ~12,000 | 24,000-60,000 |
| 10 billion | ~120,000 | 240,000-600,000 |

### Peak multiplier
- Average QPS is meaningless alone. Traffic has peaks.
- Rule of thumb: peak QPS = 2x to 5x average QPS.
- For social media: 3x-5x (spikes during events, morning/evening).
- For ecommerce: 5x-10x (flash sales, Black Friday).
- Always design for peak, not average.

## 3. Step-by-step estimation framework

### Step 1: Clarify the numbers
- How many users? (total registered, monthly active, daily active)
- What is the read:write ratio? (Twitter is ~100:1 read-heavy. Chat is ~1:1.)
- What is the average request size? (a tweet: ~300 bytes. An image: ~200 KB. A video: ~50 MB.)
- How long is data retained? (7 days? Forever? Archival after 1 year?)

### Step 2: Estimate QPS
- Formula: `QPS = DAU × requests_per_user_per_day / 86400`
- Example: 10M DAU, each user makes 20 requests/day → 200M requests/day → ~2,300 QPS average → ~7,000-12,000 QPS peak.

### Step 3: Estimate storage
- Formula: `Storage per day = write_QPS × avg_object_size × 86400`
- Example: 100 write QPS × 1 KB per record × 86400 = 8.64 GB per day.
- Per year: 8.64 GB × 365 ≈ 3.15 TB per year.
- For 5 years: ~15.8 TB.
- Add replication factor (typically 3x) → ~47 TB total storage.

### Step 4: Estimate bandwidth
- Formula: `Bandwidth = QPS × avg_response_size`
- Example: 5,000 QPS × 10 KB per response = 50 MB/s = 400 Mbps.
- For serving images: 5,000 QPS × 200 KB = 1 GB/s = 8 Gbps. → CDN is essential.

### Step 5: Estimate cache size
- Pareto principle: 20% of data serves 80% of requests.
- Formula: `Cache size = daily_read_requests × avg_object_size × cache_fraction`
- Example: 10M read requests/day × 1 KB × 20% = 2 GB of cache.
- Redis instance with 8-16 GB memory handles this easily.
- Another approach: cache the top N hot items. If you have 10M items and the top 100K are hot, cache those: 100K × 1 KB = 100 MB.

## 4. Estimation examples

### Example 1: Twitter-like system
- **Users**: 300M MAU, 150M DAU.
- **Write**: 500M tweets/day (some users tweet multiple times, retweets, etc.).
- **Read**: each user reads ~100 tweets/day → 15B reads/day.
- **QPS**:
  - Write: 500M / 86400 ≈ 6,000 write QPS. Peak: ~18,000.
  - Read: 15B / 86400 ≈ 175,000 read QPS. Peak: ~500,000.
- **Storage**:
  - Tweet: ~300 bytes text + metadata ≈ 1 KB.
  - 500M × 1 KB = 500 GB/day. Per year: ~180 TB. With 3x replication: ~540 TB.
  - Media (images/videos) would be stored separately on object storage (S3).
- **Bandwidth**:
  - Read: 175,000 QPS × 1 KB = 175 MB/s ≈ 1.4 Gbps (text only, images much more).
- **Cache**:
  - Cache hot tweets for the last 24 hours. 500M tweets × 20% hot × 1 KB = 100 GB cache.
  - Multiple Redis instances or Redis Cluster.
- **Design implications**: massively read-heavy → cache is critical. Fan-out on read vs fan-out on write for timeline generation.

### Example 2: URL shortener
- **Users**: 100M URLs created/month, 10B redirects/month.
- **QPS**:
  - Write: 100M / (30 × 86400) ≈ 40 QPS. Peak: ~120.
  - Read: 10B / (30 × 86400) ≈ 3,850 QPS. Peak: ~12,000.
- **Storage**:
  - URL mapping: ~500 bytes (short code + long URL + metadata).
  - 100M/month × 500 bytes = 50 GB/month. Per year: 600 GB. 5 years: 3 TB.
- **Cache**:
  - 20% of URLs get 80% of traffic. Cache 20M URLs × 500 bytes = 10 GB. Easily fits one Redis instance.
- **Design implications**: read-heavy, cache-friendly, simple data model. A single database with read replicas + Redis cache handles this scale.

### Example 3: Chat system (WhatsApp-like)
- **Users**: 500M DAU, each sends 40 messages/day.
- **Messages**: 500M × 40 = 20B messages/day.
- **QPS**:
  - Write: 20B / 86400 ≈ 230,000 QPS. Peak: ~700,000.
  - Read: similar or higher (group messages are read by multiple users).
- **Storage**:
  - Message: ~100 bytes text + metadata ≈ 200 bytes.
  - 20B × 200 bytes = 4 TB/day. Per year: ~1.5 PB.
- **Bandwidth**:
  - 230,000 QPS × 200 bytes = 46 MB/s write. Much more for reads (especially media).
- **Design implications**: extreme write throughput → needs partitioned/sharded storage (Cassandra-like). Real-time delivery → WebSocket + message broker. Media → object storage + CDN.

### Example 4: Video streaming (YouTube-like)
- **Users**: 1B DAU, each watches 5 videos/day.
- **Reads**: 5B video views/day.
- **Uploads**: 500K new videos/day.
- **QPS**:
  - Video metadata reads: 5B / 86400 ≈ 58,000 QPS.
  - Video uploads: 500K / 86400 ≈ 6 QPS (but each upload triggers heavy processing).
- **Storage** (the big number):
  - Average video: 200 MB (multiple resolutions stored).
  - 500K × 200 MB = 100 TB/day of new video. Per year: 36.5 PB.
- **Bandwidth**:
  - Video streaming: average 5 Mbps per stream. 5B views/day ÷ 86400 = 58,000 concurrent streams (average). At 5 Mbps: 290 Gbps sustained bandwidth. Peak: ~1 Tbps.
  - CDN is absolutely essential. This cannot be served from origin.
- **Design implications**: CDN for delivery, object storage for video, transcoding pipeline (async with Kafka/queue), metadata in DB + cache, content recommendation is separate system.

## 5. Common capacity planning rules of thumb

| Component | Typical capacity |
| --- | --- |
| Single web server | 1,000-10,000 QPS (depends on processing) |
| Single Redis instance | 100,000+ QPS (simple commands) |
| Single PostgreSQL | 5,000-20,000 QPS (simple queries, indexed) |
| Single MySQL | 5,000-20,000 QPS (similar to PostgreSQL) |
| Single MongoDB | 10,000-50,000 QPS (document reads, indexed) |
| Kafka broker | 100,000-500,000 messages/sec |
| Elasticsearch node | 1,000-10,000 search QPS (depends on query complexity) |

### When you need to scale
- Single DB at 5K QPS → add read replicas for reads, or cache hot data.
- 50K QPS → need caching + read replicas + possibly sharding.
- 500K QPS → definitely need sharding + caching + CDN + multiple application servers.

## 6. The 80/20 rule in system design
- 80% of traffic hits 20% of data → cache the top 20%.
- 80% of writes go to 20% of tables → optimize those tables.
- 80% of latency comes from 20% of code paths → profile and fix those.
- This rule is your best friend for cache sizing and optimization prioritization.

## 7. How to present estimation in an interview

### Do
- Round numbers aggressively. Use powers of 10. Interviewers want to see the thought process, not exact math.
- Show your work: "50M users × 10 requests/day = 500M/day. 500M / 100K seconds ≈ 5,000 QPS."
- State assumptions: "I'll assume a 10:1 read-write ratio."
- Use the numbers to justify design decisions: "At 50K read QPS, we need caching. A single Redis handles 100K QPS, so one instance is enough for now."

### Don't
- Spend more than 3-5 minutes on estimation. It is a means to an end, not the focus of the interview.
- Pretend to be precise. "Exactly 11,574 QPS" looks silly. "About 12,000 QPS" is better.
- Forget peak traffic. Always multiply average by 2-5x.
- Forget replication in storage calculations. Data is usually stored 3x.

## 8. Quick estimation cheat sheet

| I need to estimate... | Formula |
| --- | --- |
| QPS from DAU | DAU × actions_per_user / 86,400 |
| Peak QPS | Average QPS × 3 to 5 |
| Storage per day | Write QPS × object size × 86,400 |
| Storage per year | Storage per day × 365 |
| Bandwidth | QPS × average response size |
| Cache size | Daily reads × object size × 0.2 (Pareto) |
| Number of servers | Peak QPS / QPS per server |
| Number of DB shards | Total storage / storage per shard |

## 9. Estimation traps and edge cases
- **Media changes everything**: text-based systems (chat, tweets) have modest storage. Add images and storage jumps 100x. Add video and it jumps 10,000x.
- **Fan-out multiplier**: a tweet to 1M followers means 1 write generates 1M reads (or 1M writes to fan-out caches).
- **Metadata vs content**: for a video platform, metadata (title, description) is tiny but content (video files) is enormous. Store and serve them differently.
- **Hot vs cold data**: recent data is hot (accessed often). Old data is cold (rarely accessed). Move cold data to cheaper storage (S3, archival tiers).
- **Compression**: text compresses ~10x. Images are already compressed. Factor this into bandwidth and storage.

## 10. One-line memory anchors
- 1 million requests/day ≈ 12 QPS.
- Always design for peak (3-5x average).
- 20% of data serves 80% of requests → cache that 20%.
- Memory is 100,000x faster than disk. This is why caching exists.
- Round aggressively. Show the reasoning, not the precision.
- Media (images/video) changes every number by 100-10,000x.
