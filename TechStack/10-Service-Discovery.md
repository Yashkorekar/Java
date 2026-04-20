# Service Discovery Interview Prep and Deep Guide

## 1. What service discovery is
- Service discovery is the mechanism by which services in a distributed system find each other's network locations (IP addresses and ports) dynamically, without hardcoding.
- In a microservices architecture, services are deployed across many instances that can start, stop, move, and scale at any time. Service discovery tracks where everything is.

## 2. 30-second answer
- In microservices, services need to call other services. But instances are dynamic — they come and go due to scaling, deployments, and failures.
- Service discovery maintains a registry of all running service instances and their locations.
- When service A needs to call service B, it asks the discovery system "where is service B?" and gets a current, healthy endpoint.

## 3. What problems service discovery solves
- Eliminates hardcoded IP addresses and ports in configuration.
- Handles dynamic scaling (new instances register, terminated instances deregister).
- Handles rolling deployments where old instances are replaced by new ones.
- Enables load distribution across multiple instances.
- Integrates with health checking to route only to healthy instances.
- Supports multi-environment setups (dev, staging, prod) without config changes.

### Beginner-friendly example: ecommerce microservices
- You have an Order Service, Payment Service, Inventory Service, and Notification Service.
- The Order Service needs to call the Payment Service. But Payment Service has 5 instances running, and their IPs change every time they restart or scale.
- Without service discovery: you hardcode IPs in the Order Service config. Every time Payment Service scales or redeploys, you manually update configs everywhere. This is fragile and unmanageable.
- With service discovery: Payment Service instances register themselves on startup. Order Service asks the registry "give me a healthy Payment Service instance" and gets a current address. When instances come and go, the registry is automatically updated.

### Beginner memory trick
- Service discovery is like a company phone directory that updates itself automatically.
- When a new employee (service instance) joins, they are added. When they leave, they are removed. When you need to reach someone, you look them up — you never memorize phone numbers.

## 4. What service discovery is not
- It is not a load balancer (though it often works alongside one).
- It is not a configuration management system (though some tools combine both).
- It is not a DNS server (though DNS-based discovery is one approach).
- It is not a message broker. It helps services find each other, not communicate asynchronously.

## 5. Mental model
- There are three actors: the **service provider** (registers itself), the **service registry** (stores locations), and the **service consumer** (looks up locations).
- The flow: register → discover → connect.
- Health checks ensure the registry only contains healthy instances.

## 6. Two fundamental patterns

### Client-side discovery
- The client (caller) is responsible for querying the service registry and choosing an instance.
- Flow:
  1. Service instances register with the registry on startup.
  2. When the client needs to call a service, it queries the registry directly.
  3. The client receives a list of healthy instances.
  4. The client uses a load-balancing strategy (round-robin, random, weighted) to pick one.
  5. The client makes the request directly to the chosen instance.
- Examples: Netflix Eureka + Ribbon, Spring Cloud LoadBalancer.
- Pros: no extra network hop through a centralized load balancer. The client has full control over load-balancing logic.
- Cons: every client must implement discovery and load-balancing logic. Couples clients to the registry.

### Server-side discovery
- The client sends a request to a load balancer or router, which queries the registry and forwards the request to a healthy instance.
- Flow:
  1. Service instances register with the registry.
  2. The client sends a request to a well-known endpoint (the load balancer/router).
  3. The load balancer queries the registry, picks an instance, and forwards the request.
  4. The client does not know or care about individual instance addresses.
- Examples: AWS ALB with ECS service discovery, Kubernetes Services, Consul + Nginx/Envoy.
- Pros: client is simpler — it just calls one address. Discovery logic is centralized.
- Cons: extra network hop. The load balancer/router becomes an infrastructure component to manage.

## 7. Service registry

### What it stores
- Service name (logical name like `payment-service`).
- Instance ID (unique per instance).
- Network location (IP address, port).
- Health status (healthy, unhealthy, draining).
- Metadata (version, datacenter, tags, weight).

### Registry availability
- The registry is a critical component. If it goes down, services cannot discover each other.
- Production registries must be highly available (clustered, replicated).
- Some systems cache the last known state client-side so a brief registry outage does not immediately break everything.

## 8. Health checks and heartbeats
- **Self-registration with heartbeats**: the service sends periodic heartbeats to the registry. If heartbeats stop, the registry marks the instance as unhealthy and eventually deregisters it.
  - Used by: Eureka, Consul (TTL checks).
- **Active health checking**: the registry (or a sidecar/agent) actively probes the service (HTTP GET /health, TCP connect, gRPC health check) at regular intervals.
  - Used by: Consul (HTTP/TCP/script checks), Kubernetes (liveness/readiness probes), Envoy.
- **Passive health checking**: the load balancer or proxy observes real traffic. If a backend returns errors or times out, it is marked unhealthy.
  - Used by: Envoy, Nginx (passive upstream checks).
- Best practice: combine active health checks (for proactive detection) with passive checks (for catching issues that health endpoints miss).

## 9. Common tools and platforms

### Eureka (Netflix)
- Designed by Netflix for AWS microservices. Very popular in the Spring Cloud ecosystem.
- Client-side discovery pattern. Eureka server is the registry; Eureka client is embedded in each service.
- Services register on startup and send heartbeats every 30 seconds (default).
- If heartbeats stop for 90 seconds, the instance is evicted.
- **Self-preservation mode**: if Eureka detects that too many instances suddenly stop heartbeating (possibly due to a network issue, not actual failures), it stops evicting instances to prevent a cascade of deregistrations. This prioritizes availability over accuracy.
- Eureka servers replicate state to each other (AP system in CAP terms — eventual consistency).
- Pros: well-integrated with Spring Boot, simple model.
- Cons: no built-in health checking beyond heartbeats, no key-value store, primarily Java ecosystem.

### Consul (HashiCorp)
- A full-featured service discovery and configuration tool.
- Supports both client-side and server-side discovery.
- Features: service registry, health checking (HTTP, TCP, gRPC, script), key-value store, multi-datacenter support, DNS interface, service mesh (Consul Connect with sidecar proxies for mTLS).
- Uses Raft consensus for strong consistency (CP in CAP terms for the server cluster).
- Agents run on every node. The agent handles registration and health checking locally, then syncs with Consul servers.
- DNS-based discovery: services can be discovered via DNS queries like `payment-service.service.consul`.
- Pros: language-agnostic, multi-datacenter, rich health checks, built-in KV store.
- Cons: more operational overhead than Eureka. Requires running agents on every node.

### etcd
- A distributed key-value store using Raft consensus. Not a purpose-built service registry, but widely used as the backing store for service discovery.
- Kubernetes uses etcd as its core data store (stores all cluster state including service endpoints).
- Strong consistency (CP). Reads and writes go through the Raft leader.
- Applications can watch keys for changes and get notified when service instances register or deregister.
- Pros: very reliable, strongly consistent, proven at scale (Kubernetes depends on it).
- Cons: lower-level than Consul — no built-in health checking or DNS interface. You build discovery logic on top of it.

### ZooKeeper (Apache)
- One of the oldest coordination services. Originally built for Hadoop, used by Kafka (old versions), HBase, and many others.
- Provides: distributed configuration, leader election, naming, group membership, barriers, locks.
- Can be used for service discovery by having services create ephemeral nodes. When a service dies, its ephemeral node disappears, and watchers are notified.
- Uses ZAB (ZooKeeper Atomic Broadcast) consensus protocol.
- Pros: battle-tested, strong consistency.
- Cons: complex to operate, Java-heavy, not designed specifically for service discovery (general-purpose coordination). Being replaced by purpose-built tools in many architectures.

### Kubernetes service discovery
- Kubernetes has built-in service discovery via **Services** and **DNS**.
- A Kubernetes Service is an abstraction that defines a logical set of Pods and a policy to access them.
- **ClusterIP** (default): creates a virtual IP inside the cluster. DNS resolves `payment-service.namespace.svc.cluster.local` to this virtual IP. kube-proxy routes traffic to healthy pods.
- **Headless Service** (`clusterIP: None`): DNS returns the individual pod IPs directly, allowing clients to do their own load balancing.
- **Endpoints/EndpointSlices**: Kubernetes watches Pods and updates the endpoint list as pods start, stop, and pass/fail readiness probes.
- **CoreDNS**: the DNS server inside Kubernetes. Resolves service names to IPs.
- This is server-side discovery baked into the platform. Services just call other services by name.
- Pros: no external registry needed, tightly integrated with container orchestration, works with any language.
- Cons: only works within Kubernetes, limited cross-cluster discovery without additional tools.

## 10. DNS-based service discovery
- Instead of a specialized registry, use DNS to resolve service names to instance IPs.
- Simple approach: a DNS name like `payment-service.internal` resolves to the IPs of all healthy instances. The client picks one (round-robin DNS or application-level choice).
- Challenges:
  - DNS caching: clients and resolvers cache DNS responses. If an instance goes down, stale DNS cache can route to dead instances for the TTL duration.
  - TTL trade-off: short TTLs mean more DNS queries but fresher data. Long TTLs mean fewer queries but stale data.
  - Limited health checking: basic DNS does not do health checks. You need an external system (like Route 53 health checks or Consul DNS) to update records based on health.
- **AWS Cloud Map**: a managed service that combines DNS-based discovery with health checking and API-based discovery. Services register instances, and consumers discover them via DNS or API.
- Senior insight: DNS-based discovery is the simplest approach and works for many systems. But for dynamic environments with frequent scaling, a purpose-built registry is usually better.

## 11. Service mesh and discovery
- In a service mesh (Istio, Linkerd, Consul Connect), service discovery is handled by the mesh infrastructure.
- Each service has a sidecar proxy (usually Envoy). The sidecar knows all service endpoints (from the mesh control plane) and handles routing, load balancing, retries, and mTLS.
- The application calls `http://payment-service:8080` and the sidecar intercepts, resolves the actual endpoints, applies routing rules, and forwards the request.
- Discovery is completely transparent to the application code.
- The control plane (e.g., Istio's istiod) watches Kubernetes endpoints and pushes updates to all sidecars.

## 12. Registration patterns

### Self-registration
- The service instance registers itself with the registry on startup and deregisters on shutdown.
- Used by: Eureka, Spring Cloud.
- Pros: simple, no external component needed.
- Cons: couples the service to the registry. Every service must include registration logic.

### Third-party registration
- An external component (registrar) watches for new instances and registers them automatically.
- Used by: Kubernetes (kubelet registers pods), Consul (agent registers services from config), Registrator (watches Docker containers).
- Pros: service code has no registry knowledge. Clean separation of concerns.
- Cons: requires managing the registrar component.

## 13. Failure scenarios and resilience

### Registry goes down
- If the registry is unavailable, new registrations and lookups fail.
- Mitigation: clients cache the last known endpoint list. Existing connections continue working. Registry is deployed as a highly available cluster.

### Service crashes without deregistering
- If a service crashes (kill -9, OOM), it cannot deregister itself.
- Mitigation: heartbeat TTL (registry evicts after missed heartbeats), active health checks (registry probes the service and detects failure).

### Stale entries
- If health checks are slow or misconfigured, the registry may contain unhealthy instances.
- Mitigation: clients implement circuit breakers and retries. Combine with client-side health checking.

### Split-brain in the registry cluster
- If the registry cluster has a network partition, different parts may have different views of registered services.
- AP registries (Eureka): may serve slightly stale data but remain available. Self-preservation mode helps.
- CP registries (Consul, etcd, ZooKeeper): the minority side may become unavailable for writes but data stays consistent.

### Thundering herd on registry
- If all service instances restart simultaneously (e.g., after a deployment), they all hit the registry at once.
- Mitigation: stagger startup, use jitter in registration timing.

## 14. Cross-datacenter and multi-region discovery
- Challenge: services in datacenter A need to discover services in datacenter B.
- Solutions:
  - **Consul multi-datacenter**: Consul supports WAN federation between datacenters. Agents in each DC communicate, and services can be discovered across DCs.
  - **Global load balancer + per-DC discovery**: each DC has its own registry. A global load balancer (Route 53, global LB) routes to the right DC.
  - **Service mesh federation**: mesh control planes in different clusters/DCs are federated to share endpoint information.
- Latency consideration: cross-DC calls are slower. Prefer local discovery when possible and fall back to cross-DC only when the local service is unavailable.

## 15. Java ecosystem specifics
- **Spring Cloud Netflix Eureka**: the most common choice for Spring Boot microservices. Annotations like `@EnableEurekaServer`, `@EnableEurekaClient`, `@LoadBalanced RestTemplate`.
- **Spring Cloud Consul**: alternative to Eureka using Consul as the backend. Provides `@EnableDiscoveryClient` for abstraction.
- **Spring Cloud Kubernetes**: uses Kubernetes native service discovery. No external registry needed. `@EnableDiscoveryClient` with `spring-cloud-starter-kubernetes-client`.
- **Spring Cloud LoadBalancer**: client-side load balancing that integrates with any `DiscoveryClient` (Eureka, Consul, Kubernetes). Replaced Netflix Ribbon.
- **gRPC service discovery**: gRPC name resolvers can integrate with Consul, Eureka, or Kubernetes for service resolution.
- **Feign clients**: declarative REST clients (`@FeignClient(name = "payment-service")`) that use service discovery automatically to resolve the service name to a URL.

## 16. Comparison table

| Feature | Eureka | Consul | etcd | ZooKeeper | Kubernetes |
| --- | --- | --- | --- | --- | --- |
| Primary purpose | Service discovery | Discovery + config + mesh | Key-value store | Coordination | Container orchestration |
| Consistency model | AP (eventual) | CP (Raft) | CP (Raft) | CP (ZAB) | CP (etcd-backed) |
| Health checking | Heartbeat only | HTTP, TCP, gRPC, script | External | External | Liveness, readiness probes |
| DNS interface | No | Yes | No | No | Yes (CoreDNS) |
| Multi-datacenter | Limited | Native | Via separate clusters | Via separate ensembles | Federation / multi-cluster tools |
| KV store | No | Yes | Yes (core feature) | Yes | ConfigMaps/Secrets |
| Language | Java | Go (polyglot clients) | Go | Java | Go |
| Best fit | Spring Boot microservices | Polyglot microservices | Kubernetes backing store | Legacy coordination | Kubernetes-native apps |

## 17. Security
- Encrypt registry communication (TLS/mTLS between services and registry).
- Authenticate services before allowing registration (prevent rogue services from registering).
- Use ACLs to control which services can discover which other services.
- In service mesh, mTLS between sidecars ensures service-to-service communication is encrypted and authenticated.
- Do not expose the service registry to the public internet.

## 18. Monitoring and operations
- Monitor registry health (cluster members, leader election, replication lag).
- Monitor registered service count and instance count.
- Monitor health check pass/fail rates.
- Alert on sudden deregistration spikes (might indicate infrastructure issues, not real failures).
- Monitor discovery latency (how long lookups take).
- Monitor stale entries (instances that are registered but not responding).

## 19. Best use cases
- Microservices architectures where services scale dynamically.
- Container orchestration platforms (Kubernetes, ECS, Nomad).
- Multi-environment setups (dev, staging, prod) with different instance counts.
- Canary deployments where traffic is gradually shifted to new versions.
- Multi-region architectures where services exist across datacenters.

## 20. When you might not need it
- Monolithic applications where all components run in one process.
- Very small systems with 2-3 static services behind a fixed load balancer.
- Serverless architectures where the platform handles routing (Lambda + API Gateway).
- Internal tools with a single deployment target and no scaling needs.

## 21. Common mistakes
- Hardcoding service URLs instead of using discovery.
- Not implementing health checks, so dead instances stay in the registry.
- Not caching discovery results client-side, causing excessive registry load.
- Choosing a CP registry when availability matters more than strict consistency (or vice versa).
- Not planning for registry failures in the architecture.
- Exposing the registry to external networks without authentication.
- Ignoring DNS caching when using DNS-based discovery.

## 22. Practical patterns
- **Sidecar pattern**: a sidecar proxy (Envoy, Consul Connect sidecar) handles discovery and routing. The application is unaware of the infrastructure.
- **Circuit breaker + discovery**: combine discovery with circuit breakers (Resilience4j, Hystrix) so calls to unhealthy instances fail fast and are retried on other instances.
- **Graceful shutdown**: services deregister before stopping, then drain connections. This prevents clients from routing to a shutting-down instance.
- **Canary via discovery**: register canary instances with specific metadata. The client-side load balancer routes a percentage of traffic to canary instances based on metadata.
- **Service versioning**: register services with version metadata. Clients can choose to call a specific version.

## 23. Tricky interview questions and answers

### Q1. What is the difference between service discovery and a load balancer?
- Service discovery tells you WHERE the service instances are. A load balancer distributes traffic ACROSS those instances. They often work together, but they solve different problems.

### Q2. What happens if the service registry goes down?
- Depends on the system. Well-designed clients cache the last known endpoints and continue working. New registrations and deregistrations fail until the registry recovers. This is why the registry must be highly available.

### Q3. Why does Eureka use eventual consistency (AP) instead of strong consistency (CP)?
- Eureka prioritizes availability. In a network partition, Eureka servers can still serve (possibly stale) data rather than becoming unavailable. For service discovery, it is usually better to route to a potentially stale endpoint than to refuse all lookups.

### Q4. Client-side vs server-side discovery — when to use which?
- Client-side: when you want maximum control and minimum latency (no extra hop). Common in Spring Cloud apps.
- Server-side: when you want simplicity for clients and centralized control. Common in Kubernetes and infrastructure-level routing.

### Q5. How does Kubernetes service discovery work without Eureka or Consul?
- Kubernetes has built-in discovery via Services and DNS. Pods are tracked by the Endpoints controller. CoreDNS resolves service names to ClusterIP or Pod IPs. No external registry is needed.

### Q6. What is Eureka's self-preservation mode?
- When the number of heartbeat renewals drops significantly below expected (more than 15% loss), Eureka stops evicting instances. It assumes a network issue rather than mass failures. This prevents cascading deregistration.

### Q7. How does Consul differ from Eureka?
- Consul is language-agnostic with richer health checks, DNS interface, KV store, multi-DC support, and service mesh. Eureka is Java-centric with a simpler model. Consul is CP; Eureka is AP.

### Q8. What is the difference between liveness and readiness probes in Kubernetes?
- Liveness: "is the process alive?" If it fails, Kubernetes restarts the pod.
- Readiness: "is the process ready to serve traffic?" If it fails, the pod is removed from the Service's endpoint list but not restarted.
- A service can be alive but not ready (e.g., still loading data or warming caches).

### Q9. How do you handle service discovery across regions?
- Options: Consul multi-DC federation, global load balancer with per-region registries, service mesh federation, or DNS-based global traffic management (Route 53, Cloudflare).

### Q10. Why is DNS-based discovery not always sufficient?
- DNS caching can route to dead instances. DNS has limited health checking. DNS cannot carry metadata (version, weight, tags). TTL tuning is a trade-off between freshness and DNS load.

### Q11. What happens during a rolling deployment with service discovery?
- New instances start and register. Old instances deregister and drain connections. The registry transitions traffic gradually. Readiness probes ensure new instances only receive traffic when they are ready.

### Q12. Can two services register with the same name?
- Yes. That is the whole point. Multiple instances of the same logical service register under the same name. The discovery system returns all healthy instances, and the load-balancing strategy picks one.

## 24. Senior-Level Deep Follow-up Questions

### DQ1. How does Eureka's peer-to-peer replication work and what are the consistency implications?
- Eureka servers replicate registrations to each other using peer-to-peer replication. When an instance registers with one Eureka server, that server forwards the registration to all known peers.
- There is no leader — every server accepts writes and replicates to others. This is an AP (available, partition-tolerant) design.
- Consistency implications:
  - During a network partition, different Eureka servers may have different views of registered instances.
  - A registration that reaches server A but not server B (due to a partition) means clients connected to B will not see that instance.
  - Self-preservation mode prevents mass evictions during partitions, which can mean stale entries persist.
- In practice, this eventual consistency is acceptable because service discovery is a best-effort lookup. If a client gets a slightly stale endpoint, it fails and retries on another.
- Senior insight: if you need strictly consistent service discovery, Consul or etcd-backed solutions are better. But strict consistency comes with availability trade-offs.

### DQ2. How does Consul's gossip protocol work alongside Raft consensus?
- Consul uses TWO protocols for different purposes:
  - **Serf gossip protocol** (SWIM-based): used for membership detection and failure detection among all agents (clients and servers). Agents gossip to each other over UDP. If an agent stops responding, gossip detects it quickly and propagates the failure information across the cluster. This is lightweight and scalable.
  - **Raft consensus**: used among Consul servers for consistent storage of service catalog, KV data, and health check results. One server is the leader; writes go through the leader and are replicated to followers.
- The combination: gossip handles the "who is alive?" question at the agent level (fast, decentralized). Raft handles the "what is the authoritative state?" question at the server level (consistent, centralized).
- For cross-datacenter: gossip operates within a datacenter via a LAN gossip pool. A separate WAN gossip pool connects servers across datacenters, enabling cross-DC service discovery.

### DQ3. How does Kubernetes keep Service endpoints in sync as pods come and go?
- The **Endpoints controller** (or EndpointSlice controller in modern Kubernetes) watches for changes to Pods and Services.
- When a Pod matches a Service's selector and passes its readiness probe:
  1. The Endpoints controller adds the Pod's IP to the Service's Endpoints (or EndpointSlice) object.
  2. kube-proxy (running on every node) watches Endpoints changes and updates iptables rules (or IPVS rules) to route traffic to the correct Pod IPs.
  3. CoreDNS can also watch EndpointSlices for headless services to return updated Pod IPs.
- When a Pod fails its readiness probe, is deleted, or is terminating:
  1. The Endpoints controller removes the Pod's IP from the Service's Endpoints.
  2. kube-proxy updates routing rules to stop sending traffic to that Pod.
- The speed of this process depends on: watch propagation delay, kube-proxy sync interval, and DNS caching (if using headless services).
- **EndpointSlices** (introduced in K8s 1.17): splits endpoint lists into smaller objects to improve scalability. Large services with thousands of pods caused bottlenecks with the single Endpoints object.
- Senior insight: there is always a small window between a Pod becoming unhealthy and traffic stopping (seconds). This is why graceful shutdown with `preStop` hooks and connection draining matters.

### DQ4. What is the CAP theorem trade-off in service discovery? Which should you choose?
- **AP (Eureka)**: the registry is always available, but may return stale data during network partitions. If an instance deregistered but the data has not propagated, a client might try to connect to a dead instance. The client handles this with retries and timeouts.
- **CP (Consul, etcd, ZooKeeper)**: the registry is strongly consistent, but may become unavailable to the minority partition. If a network partition splits the cluster, the minority side cannot serve reads/writes. Clients connected to the minority side cannot discover services.
- For service discovery, **AP is usually preferred** because:
  - It is better to get a slightly stale endpoint (and fail fast + retry) than to get NO endpoint at all.
  - Clients already need to handle transient failures. A stale entry is just another transient failure.
  - Registry downtime (CP during partition) can cascade into complete service-to-service communication failure.
- Counter-argument for CP: if your system uses discovery for critical decisions (e.g., leader election, shard assignment), consistency may be more important than availability.

### DQ5. How do you implement graceful shutdown with service discovery to achieve zero dropped requests?
- The challenge: when a service instance shuts down, in-flight requests must complete, and new requests must not be routed to it.
- Steps for graceful shutdown:
  1. **Deregister from the registry**: the instance tells the registry it is shutting down (or the readiness probe starts failing in Kubernetes).
  2. **Wait for propagation**: there is a delay between deregistration and all clients learning about it (due to caching, polling intervals, DNS TTLs). Wait for this propagation period.
  3. **Drain existing connections**: stop accepting new connections but allow in-flight requests to complete (up to a timeout).
  4. **Shutdown**: after draining (or timeout), the process exits.
- In Kubernetes: configure a `preStop` hook that pauses (e.g., `sleep 5`) to allow the Endpoints controller and kube-proxy to propagate the removal. Meanwhile, the application's shutdown hook drains requests.
- In Spring Boot: `server.shutdown=graceful` with `spring.lifecycle.timeout-per-shutdown-phase` handles draining.
- Senior insight: the propagation delay is the hardest part. Even with immediate deregistration, cached endpoints in clients and load balancers may still route to the old instance for a few seconds.

## 25. Quick revision checklist
- Can you explain client-side vs server-side discovery?
- Can you explain how a service registry works?
- Can you explain Eureka, Consul, and Kubernetes service discovery?
- Can you explain health checks and heartbeats?
- Can you explain self-registration vs third-party registration?
- Can you explain DNS-based discovery and its limitations?
- Can you explain the CAP trade-off for registries?
- Can you explain graceful shutdown with service discovery?

## 26. One-line memory anchors
- Service discovery is a self-updating phone directory for microservices.
- Client-side discovery: the caller picks the instance. Server-side: a router picks it.
- Eureka is AP and simple. Consul is CP and feature-rich. Kubernetes has it built in.
- Health checks keep the registry honest.
- Graceful shutdown and propagation delay are the hardest operational problems.
