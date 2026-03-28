# URL Shortener Case Study: Kafka, RabbitMQ, and Redis in One Real App

## 1. Why this example matters
- A URL shortener is a good beginner-friendly system design example because the core idea is easy to understand.
- A user submits a long URL.
- The system creates a short code like `abc123`.
- Later, users open the short link and get redirected to the original long URL.
- As the product grows, the system may also need analytics, spam checks, expiry handling, notifications, and abuse protection.

## 2. Basic URL shortener flow
1. User sends a long URL to the backend.
2. Backend generates a short code.
3. Backend stores `shortCode -> longUrl` in the database.
4. Backend returns the short URL such as `https://sho.rt/abc123`.
5. When someone opens that short URL, the backend finds the original long URL and redirects the user.

## 3. Beginner-friendly first version
- In version 1, you do not need Kafka, RabbitMQ, and Redis all at once.
- A very small system can start with only:
  - one API service
  - one database
  - optionally Redis for fast redirects
- This is important for interviews too: good design means using only what the system actually needs.

## 4. Where Redis fits in a URL shortener

### Main reason to use Redis
- Redirect requests are read-heavy and need to be very fast.
- Redis is often the first extra technology added to a URL shortener.

### Simple flow with Redis
1. User opens `https://sho.rt/abc123`.
2. Backend checks Redis for key `short:abc123`.
3. If found, Redis returns the long URL immediately.
4. If not found, backend reads from the database.
5. Backend stores the result in Redis with TTL.
6. Backend redirects the user.

### What Redis can store
- `shortCode -> longUrl` cache.
- Expiration time for temporary links.
- Rate limiting data per IP or user.
- Session or admin login state.
- Counters for hot links.
- Negative cache for invalid short codes to reduce repeated database misses.

### Why Redis is useful here
- Redirect latency becomes very low.
- Database load drops a lot for popular links.
- Rate limiting becomes easy.

### Caution
- Redis should usually not be the only source of truth for important links.
- The database should remain the durable source of record.

## 5. Where RabbitMQ fits in a URL shortener

### Main reason to use RabbitMQ
- RabbitMQ is useful when the app needs background jobs that should be handled reliably.

### Example background jobs
- Scan the submitted URL for malware or phishing.
- Generate a QR code image for the short URL.
- Send email notification to the user after link creation.
- Process link deletion or expiry cleanup jobs.
- Trigger webhook notifications for enterprise customers.

### Simple flow with RabbitMQ
1. User creates a short URL.
2. Backend stores the main link data in the database.
3. Backend publishes jobs such as `scan-url`, `generate-qr`, and `send-confirmation-email`.
4. RabbitMQ routes each job to the correct queue.
5. Workers process those jobs asynchronously.
6. Failed jobs can be retried or moved to a dead-letter queue.

### Why RabbitMQ is useful here
- The user does not need to wait for every background task before getting the short URL.
- Work is separated cleanly by queue.
- Retry and DLQ behavior is straightforward.

### Caution
- RabbitMQ is not usually the main tool for storing the URL mapping itself.
- It is better for job processing than for long-term analytics history.

## 6. Where Kafka fits in a URL shortener

### Main reason to use Kafka
- Kafka becomes useful when the product wants to capture and process a large stream of events over time.

### Example events
- `LinkCreated`
- `LinkVisited`
- `LinkExpired`
- `LinkDeleted`
- `AbuseDetected`

### Simple flow with Kafka
1. A user opens a short URL.
2. The backend redirects the user quickly.
3. In the background, the backend publishes a `LinkVisited` event to Kafka.
4. Analytics service reads that event and updates dashboards.
5. Fraud detection service reads the same event and looks for abuse.
6. Billing service reads the same event for enterprise usage reporting.
7. Data warehouse pipeline reads the same event for long-term reporting.

### Why Kafka is useful here
- One event can feed many consumer groups.
- Events can be retained and replayed later.
- This is useful for analytics, auditing, experimentation, and machine learning pipelines.

### Caution
- A small URL shortener usually does not need Kafka on day one.
- Kafka becomes useful when event volume, downstream consumers, or analytics needs grow.

## 7. Putting all three together

### Simple mental picture
- Redis helps the redirect path stay fast.
- RabbitMQ helps background jobs run reliably.
- Kafka helps business and analytics events flow to many downstream systems.

### One realistic end-to-end flow
1. User creates a short URL.
2. API stores link in database.
3. API stores hot lookup in Redis.
4. API sends background jobs to RabbitMQ for QR generation and safety checks.
5. API publishes `LinkCreated` event to Kafka.
6. User later opens the short link.
7. Redirect service reads mapping from Redis or database.
8. Redirect service sends user to long URL.
9. Redirect service publishes `LinkVisited` event to Kafka.
10. Analytics, abuse detection, and billing services consume those events independently.

## 8. Which tool is most likely to be used first?
- Redis is the most likely first addition because fast redirects are central to the product.
- RabbitMQ is usually added next when background jobs become important.
- Kafka is usually added later when analytics and event-driven integrations become serious.

## 9. When you might use only one of them
- Only Redis: small or medium system focused mainly on fast redirects and caching.
- Redis + RabbitMQ: product also has background jobs such as QR code generation or phishing scans.
- Redis + Kafka: product needs serious analytics and event pipelines.
- All three: larger product with fast redirects, job processing, and multi-team event consumption.

## 10. Beginner interview answer
- In a URL shortener, Redis is used for fast `shortCode -> longUrl` lookups.
- RabbitMQ is used for asynchronous background work like QR code generation, email notifications, or safety scans.
- Kafka is used for high-volume events like link visits so multiple systems can do analytics, fraud detection, billing, and reporting independently.
- You do not need all three on day one; add them when the system requirements justify them.

## 11. Common mistakes in this design
- Storing only in Redis and losing durable truth.
- Using Kafka for simple background jobs that RabbitMQ could handle more directly.
- Using RabbitMQ when the real need is retained analytics replay.
- Adding all three technologies before the system actually needs them.
- Forgetting rate limiting and abuse protection for public links.

## 12. Quick revision checklist
- Can you explain why Redis is the best fit for fast redirects?
- Can you explain why RabbitMQ is a good fit for background jobs?
- Can you explain why Kafka is useful for visit analytics and replayable events?
- Can you explain why the database should remain the durable source of truth?
- Can you explain why a small product may start without Kafka or RabbitMQ?

## 13. One-line memory anchors
- Redis speeds up reads.
- RabbitMQ handles async jobs.
- Kafka handles analytics events at scale.
- The database keeps durable truth.