# Load Balancer and API Gateway Interview Prep

## 1. What these are
- A load balancer distributes incoming traffic across multiple backend servers.
- An API gateway is a managed entry point for APIs that can route requests, authenticate users, apply rate limits, and enforce policies.
- They are related, but not the same thing.
- In many real systems, both exist together.

## 2. 30-second answer
- Use a load balancer when you need to spread traffic, improve availability, and hide multiple backend instances behind one endpoint.
- Use an API gateway when you need API-specific control such as authentication, rate limiting, routing by path, versioning, request transformation, or centralized policy enforcement.

## 3. Beginner-friendly example: ecommerce site
- A user opens an ecommerce app and hits `api.shop.com`.
- The request first goes through an API gateway.
- The gateway checks authentication and maybe rate limits the caller.
- Then traffic goes to a load balancer.
- The load balancer sends the request to one healthy application server out of many.
- The important beginner idea is this: the gateway controls the request, and the load balancer spreads the request.

### Beginner memory trick
- API gateway is the security desk and traffic manager at the building entrance.
- Load balancer is the receptionist who sends visitors to one available team member.

## 4. Why systems need them
- One server is not enough for reliability at scale.
- You need a way to avoid sending traffic to unhealthy servers.
- You need a central place to manage auth, routing, throttling, and policies.
- You want backend services to scale without clients knowing instance details.

## 5. Core concepts
| Concept | Meaning |
| --- | --- |
| Load balancer | Distributes traffic across backend instances |
| Reverse proxy | Server that accepts client requests and forwards them to internal services |
| API gateway | API-focused reverse proxy with policy and routing features |
| Health check | Probe used to decide whether an instance should receive traffic |
| Sticky session | Sends the same client repeatedly to the same backend |
| TLS termination | HTTPS is decrypted at the edge or load balancer |
| Rate limiting | Controls how many requests a client can make |
| Circuit breaker | Stops sending requests to a failing dependency |

## 6. Types of load balancers
- Layer 4 load balancer routes using transport-level data such as IP and port.
- Layer 7 load balancer understands HTTP concepts such as host, path, method, and headers.
- Layer 7 is common in web systems because it supports smarter routing and policies.

## 7. Common load-balancing algorithms
- Round robin: send requests one by one to each server.
- Weighted round robin: stronger servers get more traffic.
- Least connections: send traffic to the server with fewer active connections.
- Least response time: prefer the fastest backend.
- Consistent hashing: useful when session affinity or cache locality matters.

## 8. What a load balancer usually does
- Distributes traffic across instances.
- Runs health checks.
- Stops sending traffic to failed nodes.
- Can terminate TLS.
- Can expose one public endpoint for many servers.
- Can help with blue-green or canary deployments.

## 9. What an API gateway usually does
- Authentication and authorization.
- Rate limiting and quota checks.
- Routing by API path or version.
- Request and response transformation.
- Aggregating multiple backend calls for one client request in some setups.
- Logging, observability, and API policy enforcement.

## 10. Load balancer vs API gateway
| Question | Load balancer | API gateway |
| --- | --- | --- |
| Main job | Distribute traffic | Control and manage API traffic |
| Routing | Instance/server selection | Path, version, tenant, policy-based routing |
| Auth | Usually limited | Commonly supported |
| Rate limiting | Sometimes basic | Common and important |
| Best fit | Service availability and scale | Public API front door |

## 11. Sticky sessions
- Sticky sessions keep a user's requests on the same server.
- They can help with legacy stateful apps.
- They reduce balancing quality and make scaling harder.
- Modern systems usually prefer storing session state in Redis or a database instead of relying on stickiness.

## 12. Health checks and failover
- Health checks detect broken instances.
- A good load balancer removes unhealthy nodes quickly.
- Readiness and liveness checks are both important.
- Bad health checks can create false failures or keep bad instances in rotation.

## 13. TLS termination
- TLS termination at the load balancer reduces crypto work on application servers.
- It simplifies certificate management.
- Sometimes TLS is re-encrypted again between the balancer and internal service if stronger security is required.

## 14. API gateway patterns
- Public API gateway for mobile and web clients.
- Backend-for-frontend gateway for different client types.
- Internal gateway for service-to-service policy enforcement in some organizations.

## 15. Best use cases
- Any service exposed to public internet traffic.
- Multi-instance backend applications.
- Microservices with many client-facing APIs.
- Systems needing central auth, quota, and routing policies.

## 16. When not to overdo it
- A tiny internal tool may not need a full API gateway.
- Too many layers at the edge can make debugging and latency worse.
- Do not add a gateway only because it sounds modern.

## 17. Pros
- Better availability.
- Easier horizontal scaling.
- Centralized traffic management.
- Cleaner security and policy enforcement.
- Simpler client interaction with many backend services.

## 18. Cons
- Extra hop adds some latency.
- Misconfiguration can break the whole platform.
- Sticky sessions can create imbalance.
- A large gateway layer can become operationally complex.

## 19. Common tools and products
- NGINX
- HAProxy
- Envoy
- AWS ALB and NLB
- Kong
- Apigee
- AWS API Gateway

## 20. Common mistakes
- Confusing API gateway with load balancer as if they are identical.
- Keeping session state only in app memory and then struggling to scale horizontally.
- Weak health checks that report healthy even when the app is broken.
- Forgetting rate limiting on public APIs.
- Routing all traffic through one bottleneck without redundancy.

## 21. Tricky interview questions and answers

### Q1. Do I always need both a load balancer and an API gateway?
- No. Some systems only need a load balancer. Public API-heavy systems often benefit from both.

### Q2. Why can sticky sessions be a problem?
- They create uneven traffic distribution and make failover and scaling harder.

### Q3. What is the difference between Layer 4 and Layer 7 load balancing?
- Layer 4 routes using network information like IP and port; Layer 7 understands HTTP details like path and headers.

### Q4. Where should authentication happen?
- Often at the API gateway for public APIs, though backend services may still validate tokens or enforce deeper authorization.

### Q5. Why terminate TLS at the load balancer?
- It centralizes certificate handling and reduces app-server complexity.

### Q6. What happens if the load balancer fails?
- It becomes a single point of failure unless you deploy redundant load balancers or use a managed highly available service.

### Q7. Is API gateway only for microservices?
- No. It is useful anywhere a centralized API entry point adds value.

### Q8. Why is rate limiting important?
- It protects the system from abuse, accidental bursts, and noisy clients.

## 22. Quick revision checklist
- Can you explain load balancer vs API gateway clearly?
- Can you explain Layer 4 vs Layer 7?
- Can you explain sticky sessions and why Redis-backed sessions are often better?
- Can you explain health checks, failover, and TLS termination?
- Can you explain where rate limiting fits?

## 23. One-line memory anchors
- Load balancer spreads traffic.
- API gateway controls API traffic.
- Sticky sessions are usually a workaround, not the ideal design.
- Health checks are as important as routing logic.

## 24. Senior-Level Deep Follow-up Questions

### DQ1. How does consistent hashing work in load balancers and why does it matter?
- Traditional hashing (e.g., `hash(request) % N` where N is the number of servers): when you add or remove a server, almost all requests remap to different servers. This destroys cache locality and causes a thundering herd to backends.
- Consistent hashing: servers are placed on a virtual ring. A request is hashed and routed to the next server clockwise on the ring. When a server is added or removed, only the requests that mapped to the affected segment of the ring are redistributed.
- This means adding a server only moves ~1/N of requests, not all of them.
- Virtual nodes: each physical server is placed at multiple points on the ring. This evens out the distribution and prevents one server from getting a disproportionate share.
- Use cases: load balancing to caches (so the same user/key hits the same cache server), distributed hash tables, CDN origin selection.
- Senior insight: consistent hashing is critical when your backend servers are stateful or have local caches. For stateless backends, round-robin or least-connections may be simpler and sufficient.

### DQ2. Explain L4 vs L7 load balancing in depth. What are the real trade-offs?
- **Layer 4 (Transport)**: the load balancer sees TCP/UDP packets. It routes based on source IP, destination IP, and port numbers. It does not inspect the HTTP payload.
  - Pros: very fast (hardware or kernel-level processing), lower CPU usage, protocol-agnostic.
  - Cons: cannot route based on URL path, headers, cookies, or content. Cannot do SSL termination (or does it differently). Cannot inject headers like X-Forwarded-For at the HTTP level.
  - Examples: AWS NLB, HAProxy in TCP mode, Linux IPVS.
- **Layer 7 (Application)**: the load balancer fully parses HTTP (or other application protocols). It can route based on URL path, headers, cookies, query parameters, and even request body.
  - Pros: content-based routing (e.g., `/api/*` to backend A, `/static/*` to backend B), SSL termination, header manipulation, request/response transformation, WAF integration.
  - Cons: higher CPU usage per request, more complex configuration, higher latency per hop.
  - Examples: AWS ALB, Nginx, Envoy, HAProxy in HTTP mode.
- When to use L4: high-throughput TCP workloads (databases, gRPC, gaming), or when you just need simple connection distribution.
- When to use L7: HTTP services that need path-based routing, A/B testing, canary deployments, header-based auth injection, or WebSocket upgrade handling.

### DQ3. How does connection draining (graceful shutdown) work?
- When a backend server needs to be removed (for deployment, scaling down, or maintenance), immediately dropping all connections causes failed requests.
- Connection draining: the load balancer stops sending NEW requests to the server but allows EXISTING in-flight requests to complete (up to a configurable timeout).
- Flow:
  1. Mark the server as "draining" in the load balancer.
  2. New requests are routed to other healthy servers.
  3. Existing connections continue until they finish or the drain timeout expires.
  4. After all connections finish (or timeout), the server is removed.
- This is critical for zero-downtime deployments. Without it, users see errors during deploys.
- Kubernetes does this via `preStop` hooks and readiness probe changes. The pod is removed from the Service endpoints, then given a `terminationGracePeriodSeconds` window to finish existing requests.

### DQ4. How does a load balancer handle WebSocket connections?
- WebSocket starts as an HTTP request with an `Upgrade: websocket` header, then transitions to a persistent bidirectional TCP connection.
- L7 load balancers must support the HTTP upgrade mechanism. Most modern ones (Nginx, Envoy, ALB) do.
- After the upgrade, the load balancer maintains the persistent connection between client and backend. It becomes essentially a L4 proxy for that connection.
- Challenges:
  - Long-lived connections mean the load balancer holds state per connection. Thousands of WebSocket clients can exhaust connection limits.
  - Backend server replacement requires careful draining because WebSocket connections are long-lived.
  - Sticky sessions become implicit — a WebSocket connection is inherently bound to one backend for its lifetime.
  - Heartbeats and idle timeouts: the LB may close idle connections. Configure idle timeout higher for WebSocket workloads.
- Senior tip: for large-scale WebSocket systems, consider having a dedicated WebSocket tier behind a separate load balancer with appropriate timeout and connection limit configurations.

### DQ5. What is the split-brain problem in active-passive load balancer setups?
- In active-passive HA, one load balancer handles traffic and the passive one waits. They use heartbeats (often via VRRP or keepalived) to detect failures.
- Split-brain: if the heartbeat network fails but both LBs are actually alive, each one thinks the other is dead. Both become active, both claim the same virtual IP, and clients may reach either one.
- Consequences: inconsistent routing, duplicate request handling, and potential data corruption in stateful systems.
- Mitigations:
  - Use a separate dedicated heartbeat network.
  - Use fencing (STONITH — Shoot The Other Node In The Head): the new active node forces the old one to shut down before taking over.
  - Use quorum-based decisions with 3 or more nodes.
  - Use managed/cloud load balancers (e.g., AWS ALB/NLB) that handle HA internally and eliminate this problem.

### DQ6. How does rate limiting work at the API gateway level? What algorithms exist?
- Rate limiting at the gateway protects backends from abuse and ensures fair usage.
- **Fixed window**: count requests per time window (e.g., 100 requests per minute). Simple but has the boundary burst problem.
- **Sliding window log**: track timestamps of each request. Count within the sliding window. Accurate but memory-intensive.
- **Sliding window counter**: combine current and previous window counts with a weighted ratio. Good balance.
- **Token bucket**: tokens are added at a fixed rate (e.g., 10/second). Each request consumes a token. Allows bursts up to the bucket size. Very common in production (used by AWS API Gateway, Stripe, etc.).
- **Leaky bucket**: requests enter a queue that drains at a constant rate. Smooths out bursts but adds latency to queued requests.
- Distributed rate limiting challenge: if you have multiple gateway instances, each one needs to share rate limit state. Common solutions: Redis as a centralized counter store, or use a coordinated approach where each instance gets a fraction of the limit.
- Senior detail: rate limiting must handle edge cases — what about different limits per API key? Per endpoint? Per IP? Response headers (`X-RateLimit-Remaining`, `Retry-After`) for client friendliness.

### DQ7. How does service mesh differ from API gateway? When do you need both?
- **API gateway**: sits at the edge (north-south traffic). Handles external client requests, authentication, rate limiting, request routing, and protocol translation.
- **Service mesh** (e.g., Istio, Linkerd): handles service-to-service traffic (east-west traffic). Runs as a sidecar proxy (usually Envoy) alongside each service instance.
  - Provides: mutual TLS between services, service discovery, load balancing, circuit breaking, retries, observability (distributed tracing, metrics), traffic splitting for canary deploys.
- When you need both:
  - API gateway for external clients entering your system.
  - Service mesh for internal communication between microservices.
- Overlap: some teams use the service mesh's ingress gateway as the API gateway. Others keep them separate for different capabilities.
- Senior insight: a service mesh adds operational complexity (sidecar resource overhead, configuration, debugging). Do not add it until you have enough microservices and enough pain points (security, observability, traffic management) to justify it.

### DQ8. How do you implement zero-downtime deployments using a load balancer?
- **Rolling deployment**: update instances one at a time. The LB drains connections from the instance being updated, removes it, updates it, health-checks it, then adds it back. Other instances serve traffic throughout.
- **Blue-green deployment**: run two identical environments (blue = current, green = new). Deploy to green, test it, then switch the LB to route traffic to green. If something goes wrong, switch back to blue instantly.
- **Canary deployment**: route a small percentage (e.g., 5%) of traffic to the new version via weighted routing in the LB or API gateway. Monitor error rates and latency. Gradually increase traffic to the new version if metrics are healthy.
- The load balancer's role: health checks, weighted routing, connection draining, and the ability to quickly shift traffic between backend groups.
- Kubernetes Ingress controllers, ALB target groups, and Nginx upstream configurations all support these patterns.

### DQ9. What is a reverse proxy vs a load balancer vs an API gateway?
- **Reverse proxy**: sits between clients and servers. Clients talk to the proxy, which forwards to the appropriate backend. Provides: SSL termination, caching, compression, security (hiding backend IPs).
- **Load balancer**: a reverse proxy that specifically focuses on distributing traffic across multiple backend instances using health checks and routing algorithms.
- **API gateway**: a reverse proxy with API-specific intelligence: authentication, rate limiting, request transformation, API versioning, developer portal, analytics.
- In practice: Nginx can act as all three. AWS ALB is a load balancer. Kong/Apigee are API gateways. Envoy is a reverse proxy that can be used as any of the three.
- Senior mental model: reverse proxy ⊂ load balancer ⊂ API gateway. Each adds capabilities on top of the previous.