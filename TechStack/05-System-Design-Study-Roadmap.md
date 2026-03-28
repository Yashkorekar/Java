# System Design Study Roadmap

## 1. Why this roadmap exists
- System design rounds are usually not about memorizing one perfect architecture.
- They are about showing that you can choose the right building blocks, explain trade-offs, and scale a design step by step.
- The topics in this folder are the kinds of components interviewers expect you to know when they ask you to design systems like Uber, YouTube, WhatsApp, Instagram, or a payment platform.

## 2. What interviewers usually want to see
- You clarify functional and non-functional requirements first.
- You estimate scale instead of guessing blindly.
- You choose storage, cache, messaging, and networking components based on actual needs.
- You know the difference between the read path and the write path.
- You can explain bottlenecks, failure points, and trade-offs.
- You do not add complex technologies unless the scale or feature set really needs them.

## 3. Recommended study order
1. Redis: easiest high-impact component for cache, rate limiting, counters, and sessions.
2. Load Balancer and API Gateway: almost every public system starts with traffic entering through these.
3. CDN and Edge Caching: very important for latency, scale, and origin protection.
4. RabbitMQ and Kafka: both matter, but for different messaging problems.
5. MongoDB: useful when document data and flexible schema matter.
6. Elasticsearch: essential when search, autocomplete, and filtering are important.
7. SQL database design: still foundational for transactions and core business data.
8. Sharding, replication, partitioning, consistency, and failover: these are concepts that apply across databases and queues.
9. Object storage, media processing, rate limiting, notifications, and background jobs: common patterns in product design rounds.

## 4. High-yield topics for the next round of study

### A. Front door and traffic handling
- Load balancer
- API gateway
- Reverse proxy
- CDN
- Rate limiting

### B. Data and storage
- SQL database design
- MongoDB
- Elasticsearch
- Object storage
- Sharding and replication

### C. Async and event-driven systems
- RabbitMQ
- Kafka
- Background workers
- Dead-letter queues
- Event sourcing and CDC

### D. Performance and resilience
- Caching patterns
- Circuit breakers
- Retries and backoff
- Idempotency
- Observability and alerting

## 5. Good system-design habits
- Start simple, then scale only where needed.
- Keep the database as the source of truth unless there is a strong reason not to.
- Prefer async processing when the user does not need to wait.
- Use cache for hot reads, not as blind permanent truth.
- Know which parts need strong consistency and which can be eventually consistent.
- Talk about failure handling, not only happy-path architecture.

## 6. A simple way to answer system design questions
1. Clarify the product and traffic assumptions.
2. Define the core API or user flow.
3. Choose the data model and primary storage.
4. Add cache if the system is read-heavy.
5. Add async queues or streams if work should happen in the background.
6. Add CDN, search, or analytics components only when the use case needs them.
7. Discuss scaling, availability, and trade-offs.

## 7. Beginner-friendly mental model
- Redis helps when you need speed.
- RabbitMQ helps when you need background jobs.
- Kafka helps when many systems need the same event stream.
- Load balancer and API gateway help when traffic enters the system.
- CDN helps when users are globally distributed and static or cacheable content is expensive to serve from origin.
- MongoDB helps when your data is document-shaped and changes often.
- Elasticsearch helps when users need powerful search.

## 8. Topics you should add after these files
- Cassandra or DynamoDB for large-scale distributed storage.
- Object storage and blob storage for images, videos, files, and backups.
- WebSocket and real-time messaging for chat and live updates.
- Rate limiter design as a full standalone system-design topic.
- Notification systems for push, email, and SMS.
- Unique ID generation such as Snowflake-style IDs.

## 9. Which topics matter most by product type
- Ecommerce: Redis, CDN, search, SQL, background jobs, recommendation events.
- Social media: caching, feed generation, object storage, search, messaging, ranking.
- Payments: SQL, idempotency, queues, audits, consistency, retries.
- Media streaming: CDN, object storage, queues, transcoding pipelines, analytics.
- Collaboration or chat: WebSocket, Redis, message broker, presence, fan-out.

## 10. Interview traps to avoid
- Choosing technologies because they sound advanced.
- Saying everything should be strongly consistent.
- Using Kafka when a queue is enough.
- Using RabbitMQ when replayable event history is the actual need.
- Using Redis as the only durable data store without careful justification.
- Forgetting rate limiting, abuse control, and observability.

## 11. Quick revision checklist
- Can you explain when to use cache, queue, stream, search engine, CDN, and document store?
- Can you explain the read path and write path separately?
- Can you explain how the design changes at 10x traffic?
- Can you explain one failure mode for each major component?
- Can you explain one simpler alternative before proposing a complex one?

## 12. One-line memory anchors
- System design is mainly about trade-offs, not tool worship.
- Start with requirements, not with technologies.
- Add components because the workload needs them, not because the interviewer mentioned scale.