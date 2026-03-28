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