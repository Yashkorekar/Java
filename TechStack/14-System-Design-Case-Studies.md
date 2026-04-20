# System Design Case Studies and Interview Framework

## 1. The Framework: How to approach any system design interview

### Phase 1: Requirements clarification (3-5 minutes)
- **Functional requirements**: what should the system do? List 3-5 core features. Do NOT try to design everything.
- **Non-functional requirements**: what are the scale, latency, availability, consistency, and durability requirements?
- **Ask**: how many users? How many DAU? Read-heavy or write-heavy? What is acceptable latency? Is strong consistency needed?
- **Scope**: explicitly state what you WILL and will NOT cover. "I'll focus on the core feed generation and ignore the ads system."

### Phase 2: Back-of-envelope estimation (2-3 minutes)
- Estimate QPS (read and write separately), storage, bandwidth, and cache size.
- Use these numbers to justify design decisions later. "At 50K read QPS, a single DB won't cut it — we need caching and read replicas."

### Phase 3: High-level design (5-10 minutes)
- Draw the main components: clients, load balancer, application servers, cache, database, message queue, CDN (if applicable).
- Show the main data flows: how does a write flow? How does a read flow?
- Keep it simple. 5-8 boxes connected by arrows.

### Phase 4: Deep dive (10-15 minutes)
- Pick 2-3 components and go deep. The interviewer will often guide this.
- Topics to deep dive: database schema and choice, caching strategy, message queue usage, data partitioning/sharding, replication, consistency trade-offs.
- This is where you show senior-level thinking: trade-offs, failure scenarios, what happens at 10x scale.

### Phase 5: Trade-offs and wrap-up (2-3 minutes)
- Discuss trade-offs you made and alternatives you considered.
- Mention bottlenecks and how you would address them.
- Discuss monitoring, alerting, and operational concerns.

### The golden rules
- Always start with requirements and estimation. Never jump straight to drawing boxes.
- Trade-offs matter more than solutions. "I chose X because Y and Z, but the downside is W."
- No perfect design exists. The interviewer wants to see how you think, not a textbook answer.
- Drive the conversation. Don't wait for the interviewer to ask "what about caching?"

---

## 2. Case Study: Twitter / News Feed System

### Requirements

#### Functional
- Users can post tweets (text, up to 280 chars, optionally with images/videos).
- Users can follow other users.
- Users see a home timeline (feed of tweets from people they follow).
- Users can like, retweet, and reply.

#### Non-functional
- 300M MAU, 150M DAU.
- Timeline should load in <500ms.
- High availability (users expect Twitter to always be up).
- Eventual consistency is acceptable for the feed (a few seconds delay is fine).
- Posts should be durable (never lose a tweet).

### Estimation
- 500M tweets/day → ~6,000 write QPS. Peak: ~18,000.
- Each user reads timeline ~10 times/day → 1.5B reads/day → ~17,000 read QPS. Peak: ~50,000.
- Tweet size: ~1 KB (text + metadata). Storage: 500 GB/day, ~180 TB/year.
- Timeline: ~100 tweets × 1 KB = 100 KB per request. Bandwidth: 50,000 × 100 KB = 5 GB/s peak (needs CDN for media).

### High-level design

```
Client → CDN (static/media) → Load Balancer → API Servers
    ↓
Tweet Service → Tweet DB (write)
    ↓
Fan-out Service → Message Queue → Timeline Cache (Redis)
    ↓
Timeline Service → Timeline Cache (read) → Client
```

### Core design decisions

#### The big question: Fan-out on write vs fan-out on read

**Fan-out on write (push model)**
- When a user tweets, immediately push that tweet into the timeline cache of every follower.
- User A has 1000 followers → write 1000 entries into Redis (one per follower's timeline).
- Read is instant: just fetch the pre-built timeline from cache.
- Pros: very fast reads (O(1) cache lookup).
- Cons: write amplification for users with many followers. A celebrity with 50M followers → 50M writes per tweet. Slow for high-follower users.

**Fan-out on read (pull model)**
- When a user opens their timeline, fetch tweets from all users they follow AND merge/sort in real-time.
- No pre-computation on write. Write is just one DB insert.
- Pros: writes are simple and fast. No amplification.
- Cons: reads are slow. If you follow 500 users, you need to fetch from 500 sources and merge. High latency.

**Hybrid approach (what Twitter actually does)**
- For regular users (< 10K followers): fan-out on write. Push tweet to follower timelines.
- For celebrities (> 10K followers): fan-out on read. When a user opens their timeline, merge their pre-built timeline (from fan-out on write of regular users) with tweets from celebrities they follow (fetched on read).
- This avoids the 50M-write problem for celebrities while keeping reads fast for most content.

#### Database choice
- **Tweet storage**: partitioned by tweet_id (or user_id). High write throughput needed. Options:
  - MySQL/PostgreSQL with sharding (Twitter originally used MySQL).
  - Cassandra or DynamoDB for write-heavy workload.
- **User/follower graph**: user_id → list of follower_ids. Could be in graph DB or simple relational table with indexing.
- **Timeline cache**: Redis sorted sets. Key = user_id, value = list of tweet_ids sorted by timestamp. Keep only the last 800-1000 tweets per user.

#### Data partitioning
- Tweets: partition by tweet_id (using a snowflake-like ID generator for global uniqueness + time ordering).
- User data: partition by user_id.
- Timeline cache: partition by user_id.

### Deep dive: Timeline generation

#### Write path
1. User submits tweet → API server → Tweet Service.
2. Tweet Service writes to Tweet DB (partitioned).
3. Tweet Service publishes event to Fan-out Service (via Kafka).
4. Fan-out Service looks up followers of the tweeting user.
5. For each follower (if < 10K followers), insert tweet_id into follower's Redis timeline.
6. If celebrity, skip fan-out. Celebrity tweets are pulled at read time.

#### Read path
1. User opens home timeline → Timeline Service.
2. Fetch pre-built timeline from Redis (tweet_ids).
3. Fetch actual tweet content for those IDs (from Tweet Cache or Tweet DB).
4. If user follows celebrities, fetch celebrity tweets from Celebrity Tweet Cache, merge with pre-built timeline, sort by time.
5. Return merged timeline.

### Failure scenarios and trade-offs
- **Redis failure**: timeline becomes unavailable. Fallback: rebuild from DB (slow). Need Redis replication + persistence.
- **Fan-out lag**: after posting, a follower may not see the tweet for a few seconds. Acceptable for eventual consistency.
- **Hot partition**: a viral tweet causes massive reads on one tweet record. Cache it aggressively or use CDN for tweet content.
- **Stale timeline**: if fan-out fails for some followers, their timeline is incomplete. Need reconciliation mechanism (periodic rebuild).

### Senior follow-up questions
- How do you handle a user with 100M followers posting a tweet? (Celebrity bypass, fan-out on read)
- How do you rank tweets instead of showing chronological order? (ML ranking service, fetch candidate tweets, score, reorder)
- How do you handle real-time updates (new tweets appearing without refresh)? (WebSocket or long polling for push notifications, or periodic polling)
- How do you delete a tweet? (Remove from Tweet DB + fan-out delete to all follower timelines — hard to guarantee completion. Mark as deleted, filter on read.)
- What happens if a user unfollows someone? (Remove that user's tweets from the follower's timeline cache — expensive. Or just filter on read.)

---

## 3. Case Study: WhatsApp / Chat System

### Requirements

#### Functional
- One-on-one messaging.
- Group messaging (up to 256 members).
- Message delivery status (sent, delivered, read).
- Online/offline presence indicator.
- Media sharing (images, videos, documents).
- Message history/sync across devices.

#### Non-functional
- 500M DAU, each sends 40 messages/day.
- Message delivery latency < 200ms (real-time feel).
- Messages must never be lost (durability).
- End-to-end encryption.
- Support offline message delivery (store and forward).

### Estimation
- 20B messages/day → 230,000 write QPS. Peak: ~700,000.
- Message size: ~200 bytes text + metadata. Media: separate storage.
- Storage: 20B × 200 bytes = 4 TB/day. Per year: ~1.5 PB.
- Bandwidth: 700K QPS × 200 bytes = 140 MB/s text. Media adds 10-100x.

### High-level design

```
Client ↔ WebSocket/Long-poll → Load Balancer → Chat Servers (stateful)
    ↓
Message Service → Message Queue (Kafka) → Message DB (Cassandra)
    ↓
Notification Service → Push Notification (APNs, FCM)
    ↓
Presence Service (Redis) → heartbeat tracking
    ↓
Media Service → Object Storage (S3) → CDN
```

### Core design decisions

#### Connection management
- **WebSocket**: persistent bidirectional connection between client and server. Best for real-time chat.
- Each chat server maintains many WebSocket connections (100K-1M per server).
- Connection is stateful: a specific server knows which users are connected to it.
- Need a **connection registry** (Redis or ZooKeeper): maps user_id → chat_server_id. When sending a message to user B, the system looks up which chat server B is connected to.

#### Message flow (1:1 chat)
1. User A sends message → A's chat server.
2. Chat server stores message in Message DB (Cassandra, partitioned by conversation_id).
3. Chat server looks up which server user B is connected to (from connection registry).
4. If B is online: forward message to B's chat server → B's chat server pushes via WebSocket → B receives.
5. If B is offline: message is stored (already in DB). When B comes online, B's chat server fetches undelivered messages from DB.
6. Push notification sent to B's device via APNs/FCM.

#### Message delivery status
- **Sent**: server acknowledged receipt from sender. Server sends ACK back to sender.
- **Delivered**: message reached recipient's device. Recipient's client sends delivery ACK to server. Server relays delivery status to sender.
- **Read**: recipient opened the message. Recipient's client sends read receipt to server. Server relays to sender.
- All statuses are stored in the Message DB and synced.

#### Group messaging
- Group has member list (stored in a Group Service/DB).
- When a message is sent to a group, the server looks up all group members and sends to each.
- Fan-out is limited (max 256 members in WhatsApp). Unlike Twitter, this fan-out is small and manageable.
- Each member receives the message independently (same as 1:1 but repeated for each member).

#### Presence (online/offline)
- Client sends heartbeat every N seconds (e.g., 30 seconds) to a Presence Service.
- Presence data stored in Redis (key = user_id, value = last_heartbeat_timestamp, with TTL).
- If heartbeat is not received within timeout, user is marked offline.
- Publishing presence to all contacts is expensive (500 friends × heartbeat every 30s). Optimize: only check presence when a chat window is open (lazy presence).

### Database choice
- **Message storage**: Cassandra. Partition key = conversation_id (or user_id + conversation_id composite). Clustering key = message_timestamp. Write-optimized, horizontally scalable, handles TB/day easily.
- **User/group metadata**: MySQL or PostgreSQL (relational, ACID for group membership changes).
- **Connection registry**: Redis (fast lookup, user_id → server mapping, TTL for auto-cleanup).
- **Media**: S3 or equivalent object storage. Store the URL in the message record.

### Failure scenarios and trade-offs
- **Chat server crash**: all WebSocket connections on that server drop. Clients reconnect to another server (load balancer routes to available server). Connection registry is updated. Undelivered messages are fetched from DB.
- **Message ordering**: within a conversation, messages must be ordered. Use server-assigned sequence numbers per conversation (not wall-clock time, because clocks can be skewed).
- **Duplicate messages**: if the sender doesn't receive ACK (network issue), it retries. Server must deduplicate using message_id (idempotency key).
- **Database lag**: if Cassandra replica is slow, recently stored messages might not be immediately readable. Read from primary/quorum for recent messages.

### Senior follow-up questions
- How do you handle end-to-end encryption? (Signal protocol: each pair has a shared key derived via Diffie-Hellman. Server cannot read messages. Keys are exchanged during initial setup.)
- How do you sync messages across multiple devices? (Each device maintains a cursor of last synced message. On connect, fetch messages after cursor.)
- How do you handle message search? (Cannot search encrypted content server-side. Search happens on-device only.)
- What happens when a user is in a group of 256 and sends a message? (Server fans out to 255 members. Each member is looked up in connection registry. For offline members, push notification + store in DB.)
- How do you scale WebSocket servers? (Horizontal scaling with connection registry. Sticky sessions via load balancer for reconnections. Each server handles 100K-1M connections.)

---

## 4. Case Study: YouTube / Video Streaming

### Requirements

#### Functional
- Users upload videos.
- Users watch videos (streaming).
- Search for videos.
- Like, comment, subscribe.
- Video recommendations.

#### Non-functional
- 1B DAU, 5B video views/day.
- 500K new video uploads/day.
- Videos available in multiple resolutions (144p to 4K).
- Global availability (low latency worldwide).
- High availability for video playback (users expect it to always work).

### Estimation
- Video views: 5B/day → ~58,000 QPS. Peak: ~175,000.
- Uploads: 500K/day → ~6 QPS. But each upload is heavy (processing, storage).
- Storage: average original video 500 MB. 500K × 500 MB = 250 TB/day raw. After transcoding to multiple resolutions (~5x): 1.25 PB/day. Per year: ~450 PB.
- Bandwidth: average stream at 5 Mbps. 58,000 concurrent streams (average) → 290 Gbps. Peak: ~1 Tbps. CDN is absolutely essential.

### High-level design

```
Upload Flow:
Client → Load Balancer → Upload Service → Object Storage (raw)
    ↓
Transcoding Queue (Kafka) → Transcoding Workers → Object Storage (multi-resolution)
    ↓
Metadata Service → Metadata DB → Search Index (Elasticsearch)

Streaming Flow:
Client → CDN (edge) → Origin Storage (on miss)
    ↓
Metadata Service → Metadata DB/Cache → Video metadata (title, URL, etc.)
```

### Core design decisions

#### Video upload and processing pipeline
1. Client uploads video to Upload Service.
2. Upload Service stores raw video in Object Storage (S3-like).
3. Upload Service publishes a message to Transcoding Queue (Kafka).
4. Transcoding Workers pick up the message and transcode the video into multiple resolutions and formats:
   - 144p, 240p, 360p, 480p, 720p, 1080p, 4K.
   - Formats: H.264, VP9, AV1.
   - Each resolution/format combination is a separate file.
5. Transcoded files are stored in Object Storage.
6. Metadata Service is updated: video is now "available" with URLs for each resolution.
7. Thumbnails are generated (by thumbnail service or as part of transcoding).
8. Video metadata is indexed in Elasticsearch for search.

#### Why async transcoding
- Transcoding is CPU-intensive and slow (minutes to hours for long videos).
- Users don't expect instant availability. YouTube shows "processing" for a while.
- Using a message queue (Kafka) decouples upload from processing. Workers can scale independently.
- If a worker crashes, the message is re-delivered to another worker (at-least-once).

#### Video storage
- Raw video + all transcoded versions stored in distributed object storage (S3, Google Cloud Storage).
- Petabyte-scale storage is needed. Object storage is the only practical option.
- Videos are immutable (write once, never update). This simplifies storage design.
- Cold storage for old/unpopular videos. Move to cheaper storage tiers (S3 Glacier).

#### Video streaming and CDN
- Videos are served via CDN (Akamai, CloudFront, Google's global edge network).
- CDN caches popular videos at edge locations close to users.
- **Adaptive bitrate streaming (ABR)**: video is split into small chunks (2-10 seconds each). Client requests chunks one at a time. Based on network conditions, the client can switch to a higher or lower resolution mid-stream.
- Protocols: HLS (Apple), DASH (standard). Both work by providing a manifest file that lists available chunks at each resolution.
- CDN handles the massive bandwidth. Origin servers are only hit for cache misses.

#### Metadata storage
- Video metadata (title, description, uploader, upload date, view count, URLs): stored in a relational DB (MySQL/PostgreSQL) or document DB (MongoDB).
- View count: high-write counter. Don't update DB on every view. Use Redis counter and periodically flush to DB. Or use Kafka to aggregate view events.
- Comments: separate table/collection. Can be eventually consistent (a comment appearing 1 second late is fine).

### Database and caching
- **Video metadata**: MySQL/PostgreSQL with read replicas. Cache hot metadata in Redis/Memcached.
- **User data**: MySQL/PostgreSQL.
- **Video content**: Object Storage (S3-like).
- **Search**: Elasticsearch for video search.
- **Recommendations**: separate ML service with its own data store.
- **View counts**: Redis for real-time counter, periodic flush to DB.

### Failure scenarios and trade-offs
- **Transcoding failure**: message stays in Kafka (not ACKed). Retried by another worker. Idempotent transcoding (same input always produces same output).
- **CDN miss**: request goes to origin. If origin is slow, CDN can serve stale content. For videos, content is immutable so "stale" is not an issue — either the CDN has it or it fetches from origin.
- **Upload failure**: client can resume upload (chunked upload protocol). Upload service tracks which chunks were received.
- **View count accuracy**: using Redis counters means the count in the DB lags. For most users, approximate counts are fine. "1.2M views" doesn't need to be exact.

### Senior follow-up questions
- How does YouTube handle copyright detection? (Content ID: uploaded video's audio/video fingerprint is compared against a database of copyrighted content. Done during/after transcoding.)
- How do you handle live streaming? (Different architecture: ingest server receives live stream, transcodes in real-time, distributes chunks to CDN with near-zero delay. Uses RTMP for ingestion, HLS/DASH for delivery.)
- How would you design the recommendation system? (Collaborative filtering + content-based. User watch history → feature vectors → ML model predicts engagement. Separate from core video serving.)
- How do you handle video deletion? (Mark as deleted in metadata. Remove from search index. Async process to delete from object storage and CDN. CDN cache TTL handles edge purging.)
- How do you prevent abuse (spam uploads, inappropriate content)? (Upload rate limiting, ML-based content moderation on upload, user reporting system, manual review queue.)

---

## 5. Case Study: Uber / Ride Matching

### Requirements

#### Functional
- Rider requests a ride (with pickup and destination).
- System matches rider with nearest available driver.
- Real-time location tracking of driver.
- ETA estimation.
- Payment processing after ride completion.
- Trip history.

#### Non-functional
- 20M rides/day, 5M concurrent users during peak.
- Match rider to driver within 10 seconds.
- Location updates every 3-5 seconds from active drivers.
- High availability (riders must always be able to request rides).
- Geographically distributed.

### Estimation
- Active drivers sending location: 2M active drivers × 1 update per 4 seconds = 500K location updates/second. Peak: ~1M/s.
- Ride requests: 20M/day → ~230 QPS. Peak: ~700.
- Location data: each update ~100 bytes (driver_id, lat, lng, timestamp). 500K × 100 bytes = 50 MB/s.
- Storage: ride records are small (~1 KB each). 20M/day × 1 KB = 20 GB/day. Location history is much larger: 500K × 100 bytes × 86400 seconds / 4 ≈ 1 TB/day.

### High-level design

```
Driver App → Location Service → Location Store (Redis/in-memory grid)
    ↓
Rider App → Ride Request Service → Matching Service (queries Location Store)
    ↓
Matching Service → Driver notification via Push/WebSocket
    ↓
Trip Service → Trip DB
    ↓
Payment Service → Payment Gateway
    ↓
ETA Service (maps/routing) → precomputed road graph
```

### Core design decisions

#### Real-time location tracking
- Drivers send GPS location every 3-5 seconds.
- Location data is stored in an in-memory spatial index for fast nearest-neighbor queries.
- **Geohashing**: convert (latitude, longitude) into a string that represents a geographic grid cell. Nearby locations share a common prefix.
  - Example: geohash "9q8y" covers a region in San Francisco. All drivers in that cell have geohashes starting with "9q8y".
  - To find nearby drivers: query the driver's geohash cell AND adjacent cells.
- **Storage**: Redis with geospatial commands (GEOADD, GEORADIUS) or a dedicated spatial index (e.g., H3 hexagonal grid, S2 cells).
- Old locations are not queried in real-time. Historical location data can be archived to a time-series DB or object storage.

#### Ride matching algorithm
1. Rider requests a ride with pickup location.
2. Matching Service queries Location Store for drivers within a radius (e.g., 3 km) of the pickup.
3. Filter: only available drivers (not already on a trip).
4. Rank by: distance to pickup, ETA (accounting for road network, not just straight line), driver rating, driver acceptance rate.
5. Send ride request to the top-ranked driver.
6. Driver has ~15 seconds to accept. If they decline or timeout, send to next driver.
7. Once accepted, match is confirmed. Rider is notified.

#### Why geohashing/spatial indexing
- You cannot scan all 2M drivers for every ride request. That is O(N).
- Geohashing partitions the world into grid cells. For a ride request, you only check drivers in the relevant cell(s). That is O(drivers in cell) which is typically 10-100, not millions.
- **H3 (Uber's actual system)**: hexagonal grid cells. Better than square geohashes because hexagons have uniform adjacency (6 neighbors, all equidistant). Uber open-sourced H3.

#### Supply-demand and surge pricing
- When demand exceeds supply in an area, prices increase (surge pricing).
- Implementation: divide the city into zones (using geohash cells or H3 hexagons). Track supply (available drivers) and demand (ride requests) per zone. If demand/supply ratio exceeds a threshold, apply a multiplier.
- Surge pricing serves two purposes: increases supply (more drivers go to high-demand areas for more money) and decreases demand (some riders choose to wait).

### Database choice
- **Location data (real-time)**: Redis with geospatial features or custom in-memory spatial index. Very fast reads and writes.
- **Trip data**: PostgreSQL or MySQL with sharding (partition by city or time range). ACID for payment-related data.
- **User profiles/driver info**: PostgreSQL or MySQL.
- **Location history**: time-series DB (InfluxDB, TimescaleDB) or object storage for analytics.
- **ETA/routing**: precomputed road graph (from OpenStreetMap or proprietary data). Shortest path algorithms (Dijkstra, A*) with real-time traffic data.

### Failure scenarios and trade-offs
- **Location Service overload**: 1M updates/sec is a lot. Horizontal sharding by geographic region. Each server handles drivers in its region.
- **Matching failure**: if no driver accepts, expand search radius. If still no match, notify rider "no drivers available."
- **Driver goes offline during ride**: rider is notified. Ride is marked as interrupted. System can try to rematch.
- **Payment failure**: ride is completed but payment fails. Queue the payment for retry. Don't block the rider (they've already exited the car).
- **GPS inaccuracy**: GPS can be off by 5-15 meters (or more in urban canyons). Map-match GPS coordinates to the road network to get accurate positions.

### Senior follow-up questions
- How do you handle drivers crossing city/region boundaries? (Handoff between regional servers. The connection registry or location store is updated with the new region.)
- How is ETA calculated in real-time? (Precomputed road graph + real-time traffic data. Dijkstra/A* for routing. Historical data for time-of-day adjustments. ML models for ETA prediction based on current conditions.)
- How do you handle peak traffic (New Year's Eve, concerts)? (Pre-scale infrastructure based on known events. Surge pricing to balance supply/demand. Queue ride requests if capacity is exceeded.)
- How do you ensure payment security? (PCI compliance. Payment tokens instead of raw card numbers. Payment processing via third-party gateway (Stripe, Braintree). Server never stores full card details.)
- How does ride sharing (UberPool) work? (Additional matching logic: find riders with overlapping routes. Route optimization to minimize detour. Dynamic pricing split among riders.)

---

## 6. Case Study: Notification System

### Requirements

#### Functional
- Send push notifications (iOS APNs, Android FCM).
- Send SMS notifications.
- Send email notifications.
- Support for scheduled notifications (send at a future time).
- Notification preferences (users opt in/out of specific types).
- Notification history/log.

#### Non-functional
- 1B notifications/day.
- Delivery within seconds for real-time notifications.
- At-least-once delivery (notifications should not be silently lost).
- Handle spike traffic (flash sales, breaking news).
- Multi-tenant (serves multiple internal services/teams).

### Estimation
- 1B notifications/day → ~12,000 QPS. Peak: ~50,000.
- Each notification: ~500 bytes (type, payload, recipient, metadata).
- Storage: 1B × 500 bytes = 500 GB/day for logs. Per year: ~180 TB.
- External API calls: 1B calls/day to APNs/FCM/email/SMS gateways.

### High-level design

```
Internal Service → Notification API → Validation + Rate Limiter
    ↓
Preference Service → filter by user preferences
    ↓
Priority Queue (Kafka, partitioned by channel)
    ↓
Workers (per channel):
    → Push Worker → APNs / FCM
    → SMS Worker → Twilio / AWS SNS
    → Email Worker → SendGrid / SES
    ↓
Notification Log → DB (for history/analytics)
```

### Core design decisions

#### Multi-channel architecture
- Different channels have different delivery mechanisms, rate limits, costs, and latency:
  - **Push**: APNs (Apple), FCM (Google). Free. Fast (~seconds). Requires device token.
  - **SMS**: via Twilio, AWS SNS, etc. Costs money. Fast. Requires phone number. Use sparingly.
  - **Email**: via SendGrid, SES, etc. Cheap. Slower (seconds to minutes). Requires email address.
- Each channel has its own worker pool. This is a bulkhead pattern — SMS gateway being slow does not affect push delivery.

#### Message queue for decoupling
- Producing services (order service, marketing, alerts) should not wait for notification delivery.
- Kafka or similar queue decouples production from delivery.
- Separate topics/partitions per channel and priority level.
- **Priority**: critical notifications (security alerts, OTPs) go to high-priority queue. Marketing emails go to low-priority queue. Workers process high-priority first.

#### User preference management
- Users can opt out of specific notification types (e.g., marketing emails but keep security alerts).
- Preference Service checks preferences before queuing notification.
- Some notifications are mandatory (password reset, security alerts) and cannot be opted out of.
- Preferences stored in a simple DB or Redis for fast lookups.

#### Device token management
- For push notifications, each device has a token (APNs token for iOS, FCM token for Android).
- Tokens can change (app reinstall, OS update). Clients must re-register tokens.
- When APNs/FCM returns "invalid token," remove it from the database.
- A user can have multiple devices. Store: user_id → [list of device tokens].

#### Rate limiting and throttling
- APNs and FCM have rate limits. Don't blast 1M notifications in 1 second.
- SMS providers charge per message and have throughput limits.
- Internal rate limiting: prevent one internal service from flooding the notification system.
- Per-user rate limiting: don't send more than N notifications per hour to the same user (prevents spam).

### Scheduled notifications
- User or service specifies "send at 2024-01-15 09:00 UTC."
- Store in a scheduled notifications table (sorted by send_time).
- A scheduler service scans for notifications whose send_time has passed and moves them to the delivery queue.
- Implementation: database polling (simple, works at moderate scale), or distributed scheduler (Quartz, custom solution with Redis sorted sets, or Kafka delayed messages).

### Delivery guarantees and reliability
- **At-least-once delivery**: the message queue provides this. If a worker crashes before acknowledging, the message is redelivered.
- **Deduplication**: workers should be idempotent. If the same notification is delivered twice, the user sees it once (or it's a minor annoyance, not a disaster).
- **Dead letter queue (DLQ)**: if a notification fails after N retries (bad token, invalid email, provider error), move it to a DLQ for investigation.
- **Retry strategy**: exponential backoff for transient failures. Do NOT retry for permanent failures (invalid token, unsubscribed email).

### Notification log and analytics
- Log every notification: who sent it, to whom, which channel, when, delivery status (sent, delivered, opened, failed).
- Storage: high-volume write. Use Cassandra, ClickHouse, or BigQuery for analytics.
- Useful for: delivery rate monitoring, A/B testing notification content, debugging delivery failures, compliance audits.

### Failure scenarios and trade-offs
- **APNs/FCM outage**: messages queue up in Kafka. When the provider recovers, workers drain the queue. Notifications are delayed but not lost.
- **SMS provider failure**: failover to a secondary provider (e.g., Twilio primary, AWS SNS secondary).
- **Duplicate notifications**: at-least-once means duplicates are possible. Annoying but not catastrophic. More important than missing a notification.
- **Notification storm**: a marketing campaign sends 100M emails at once. Without rate limiting, this overwhelms the email provider and may get your domain blacklisted. Solution: spread sends over a time window, use rate limiters.

### Senior follow-up questions
- How do you handle millions of notifications for a flash sale? (Pre-schedule, rate-limit delivery over a window (e.g., 30 minutes), scale workers horizontally, use multiple provider accounts if needed.)
- How do you track push notification delivery and open rates? (APNs/FCM provide delivery receipts. For "opened," the app sends an event when the user taps the notification. For email, use tracking pixels.)
- How do you handle timezone-aware notifications? (Store user timezone. Scheduler converts "send at 9 AM local time" to UTC. Group users by timezone and schedule accordingly.)
- How do you prevent a rogue internal service from spamming users? (Per-service rate limits, per-user rate limits, mandatory notification type categorization, and approval workflow for marketing notifications.)
- How would you add a new channel (e.g., WhatsApp)? (Add a new worker pool for the WhatsApp channel. Register it in the channel router. Implement the provider API integration. User preference service adds WhatsApp opt-in/out. The core queue and notification API remain unchanged — just a new consumer.)

---

## 7. Design Patterns That Apply Across All Case Studies

### Pattern: Separate read and write paths (CQRS-like)
- Twitter: write goes to DB + fan-out, read comes from cache.
- YouTube: write goes to object storage + transcoding pipeline, read comes from CDN.
- Uber: write is location update to in-memory store, read is spatial query.
- Almost every large-scale system separates read and write because they have very different requirements.

### Pattern: Async processing for heavy work
- YouTube: transcoding is async via Kafka.
- Notification: delivery is async via message queue.
- Twitter: fan-out is async via message queue.
- Rule: if it takes more than 100ms or can fail independently, make it async.

### Pattern: Cache the read path
- Twitter: timeline cached in Redis.
- YouTube: video metadata cached, video content served from CDN (which is a cache).
- Uber: driver locations in Redis for fast spatial queries.
- Rule: read-heavy systems NEED caching. Calculate the QPS and prove that the DB alone cannot handle it.

### Pattern: Partition/shard by the access pattern
- Twitter: tweets by tweet_id, timeline cache by user_id.
- WhatsApp: messages by conversation_id.
- Uber: locations by geographic region.
- Notification: queue by channel and priority.
- Rule: partition by the key you query most. If you always query by user, partition by user.

### Pattern: Use the right storage for each data type
- Relational DB: structured data with ACID needs (users, payments, orders).
- Document DB: flexible schema (product catalogs, CMS).
- Key-value store/cache: fast lookups, sessions, counters.
- Object storage: large blobs (videos, images, backups).
- Time-series DB: metrics, location history, IoT data.
- Search engine: full-text search (Elasticsearch).
- Message queue: async communication, event streaming (Kafka).

### Pattern: Graceful degradation
- If recommendations fail, show popular/trending (YouTube).
- If the matching algorithm is slow, expand radius and use simpler matching (Uber).
- If push notification fails, fall back to SMS or email (Notification).
- Rule: every feature should have a degraded mode. Nothing should cause a complete outage.

## 8. Common mistakes in system design interviews
- **Jumping to the solution**: starting with "I'll use Kafka and Redis" before stating requirements.
- **Not estimating**: designing a complex system for 1000 users that could be served by a single server.
- **Over-designing**: adding Kafka, Redis, Elasticsearch, and microservices for a system that needs a single PostgreSQL instance.
- **Ignoring failure cases**: only showing the happy path. Interviewers want to hear: "what if this component fails?"
- **Not discussing trade-offs**: presenting one design as if it has no downsides. Every choice has a cost.
- **Trying to design everything**: covering 10 features shallowly instead of 3 features deeply.
- **Not driving the conversation**: waiting for the interviewer to ask questions instead of proactively discussing interesting design decisions.

## 9. Quick revision checklist
- Can you draw the high-level design for each case study in 2 minutes?
- Can you explain fan-out on write vs fan-out on read?
- Can you explain how Uber uses geohashing for driver matching?
- Can you estimate QPS, storage, and bandwidth for a given system?
- Can you explain the video upload + transcoding pipeline?
- Can you explain WebSocket-based chat message delivery?
- Can you explain notification system's multi-channel architecture?
- For each system: what is the hardest scaling challenge and how would you solve it?

## 10. One-line memory anchors
- Framework: Requirements → Estimate → High-level → Deep dive → Trade-offs.
- Twitter: fan-out on write for normal users, fan-out on read for celebrities.
- WhatsApp: WebSocket for real-time, connection registry for routing, Cassandra for storage.
- YouTube: CDN for delivery, async transcoding pipeline, adaptive bitrate streaming.
- Uber: geohash for spatial indexing, 500K location updates/second, surge pricing = supply-demand balancing.
- Notification: multi-channel workers, priority queues, at-least-once with DLQ.
