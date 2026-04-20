# Tech Stack Interview Prep (Index)

This folder covers high-value system design and interview knowledge for common building blocks and infrastructure tools:
- Kafka for distributed event streaming and durable logs
- RabbitMQ for message broking and flexible routing
- Redis for caching, fast state, and lightweight messaging
- Load balancers and API gateways for traffic entry, routing, and control
- CDN and edge caching for global delivery and origin protection
- MongoDB for document-oriented storage
- Elasticsearch for full-text search and analytics
- Service discovery for dynamic microservice communication
- Databases in depth for indexing, replication, sharding, transactions, MVCC, and more

## Suggested order
1. `01-Kafka.md`
2. `02-RabbitMQ.md`
3. `03-Redis.md`
4. `04-URL-Shortener-Case-Study.md`
5. `05-System-Design-Study-Roadmap.md`
6. `06-Load-Balancer-And-API-Gateway.md`
7. `07-CDN-And-Edge-Caching.md`
8. `08-MongoDB.md`
9. `09-Elasticsearch.md`
10. `10-Service-Discovery.md`
11. `11-Databases-In-Depth.md`

## How to use these notes
- Start with the problem each tool solves before memorizing commands or APIs.
- For each tool, learn the data model, delivery guarantees, scaling model, and failure behavior.
- Practice the comparison sections; interviewers often ask why one tool is better than another for a given use case.
- Revise the common mistakes and tricky questions at the end of each file.
- Each file now has a **Senior-Level Deep Follow-up Questions** section at the bottom. These are the questions interviewers ask AFTER you answer the basics correctly — the ones that test real understanding vs memorization.

## Quick chooser
| Need | Best fit | Why |
| --- | --- | --- |
| Durable event log, replay, analytics, CDC, high-throughput streams | Kafka | Partitioned append-only log with replay, retention, and strong streaming ecosystem |
| Job queue, work distribution, rich routing, delayed retries, request/reply | RabbitMQ | Exchange-to-queue routing, acknowledgements, dead-lettering, and queue-oriented semantics |
| Sub-millisecond cache, session store, counters, rate limiting, leaderboards | Redis | In-memory data structures, TTL, atomic operations, and very low latency |
| Traffic distribution, TLS termination, routing, edge entry point | Load Balancer and API Gateway | Controls how requests enter and move through the system |
| Faster global content delivery and less origin load | CDN and Edge Caching | Serves cacheable content near users and shields the backend |
| Flexible JSON-like data with evolving schema | MongoDB | Document database with indexing, replication, and sharding |
| Full-text search, autocomplete, filters, relevance ranking | Elasticsearch | Search engine built on inverted indexes and distributed querying |
| Dynamic service-to-service communication in microservices | Service Discovery | Registry-based or DNS-based dynamic endpoint resolution |
| ACID, CAP, indexing, sharding, replication, MVCC, query optimization | Databases In-Depth | Cross-cutting database concepts that apply to any database choice |

## Common trap
- Kafka, RabbitMQ, and Redis are not interchangeable even though all three can move data between services.
- The correct choice depends on durability, replay, routing, ordering, throughput, latency, and operational trade-offs.

## High-value comparison questions
- Why is Kafka usually stronger for event replay but weaker for fine-grained routing?
- Why is RabbitMQ usually stronger for worker queues but weaker for long-retention analytics?
- Why is Redis excellent for cache and ephemeral state but risky as a primary durable event backbone?

## Practical interview framing
- If the interviewer says event streaming, replay, CDC, analytics, or immutable log, think Kafka first.
- If the interviewer says work queue, retries, routing keys, dead-lettering, or task consumers, think RabbitMQ first.
- If the interviewer says cache, sessions, counters, ranking, rate limiting, or very fast state lookup, think Redis first.
- If the interviewer says entry point, traffic routing, SSL termination, rate limiting, or public API front door, think load balancer and API gateway.
- If the interviewer says static assets, global latency, edge delivery, cache hit ratio, or origin protection, think CDN.
- If the interviewer says flexible schema, content catalog, profile documents, nested objects, or easy JSON-style persistence, think MongoDB.
- If the interviewer says search box, autocomplete, typo tolerance, relevance, or faceted search, think Elasticsearch.
- If the interviewer says microservices finding each other, dynamic scaling, Eureka, Consul, or registry, think service discovery.
- If the interviewer says ACID, CAP, indexing, B-tree, sharding, replication, MVCC, or transactions, think databases in-depth concepts.

## Real-life system case study
- `04-URL-Shortener-Case-Study.md` explains how Kafka, RabbitMQ, and Redis can fit into one beginner-friendly product design.
- It also explains when you should start with only one of them instead of using all three from day one.

## What to study next for system design
- `05-System-Design-Study-Roadmap.md` gives a practical order for preparing for interviews.
- The new notes add four very common system-design topics that interviewers expect you to recognize quickly.