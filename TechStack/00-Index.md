# Tech Stack Interview Prep (Index)

This folder covers high-value system design and interview knowledge for three common infrastructure tools:
- Kafka for distributed event streaming and durable logs
- RabbitMQ for message broking and flexible routing
- Redis for caching, fast state, and lightweight messaging

## Suggested order
1. `01-Kafka.md`
2. `02-RabbitMQ.md`
3. `03-Redis.md`
4. `04-URL-Shortener-Case-Study.md`

## How to use these notes
- Start with the problem each tool solves before memorizing commands or APIs.
- For each tool, learn the data model, delivery guarantees, scaling model, and failure behavior.
- Practice the comparison sections; interviewers often ask why one tool is better than another for a given use case.
- Revise the common mistakes and tricky questions at the end of each file.

## Quick chooser
| Need | Best fit | Why |
| --- | --- | --- |
| Durable event log, replay, analytics, CDC, high-throughput streams | Kafka | Partitioned append-only log with replay, retention, and strong streaming ecosystem |
| Job queue, work distribution, rich routing, delayed retries, request/reply | RabbitMQ | Exchange-to-queue routing, acknowledgements, dead-lettering, and queue-oriented semantics |
| Sub-millisecond cache, session store, counters, rate limiting, leaderboards | Redis | In-memory data structures, TTL, atomic operations, and very low latency |

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

## Real-life system case study
- `04-URL-Shortener-Case-Study.md` explains how Kafka, RabbitMQ, and Redis can fit into one beginner-friendly product design.
- It also explains when you should start with only one of them instead of using all three from day one.