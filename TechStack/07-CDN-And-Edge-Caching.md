# CDN and Edge Caching Interview Prep

## 1. What a CDN is
- A CDN, or Content Delivery Network, is a globally distributed network of edge servers that cache and serve content closer to users.
- Its main goals are lower latency, better throughput, lower origin load, and better availability for cacheable content.

## 2. 30-second answer
- Use a CDN when users are geographically distributed and your application serves static or cacheable content such as images, CSS, JavaScript, videos, downloads, or even some API responses.
- The CDN keeps copies of content at edge locations, so users do not always need to hit the origin server directly.

## 3. Beginner-friendly example: ecommerce product images
- A user in Delhi opens an ecommerce app.
- The app needs product images, CSS, and JavaScript.
- Instead of fetching those files from one server in another region every time, the request goes to the nearest CDN edge location.
- If the file is already cached there, the user gets it quickly.
- The origin server does less work.
- The important beginner idea is this: CDN moves content closer to the user.

### Beginner memory trick
- CDN is like keeping product brochures in many local stores instead of shipping every brochure from one central warehouse.

## 4. Core concepts
| Concept | Meaning |
| --- | --- |
| Origin | Your main backend or storage system where content originally lives |
| Edge location | CDN server close to users |
| Cache hit | Content served directly from edge cache |
| Cache miss | Edge does not have content and must fetch from origin |
| TTL | How long content stays cached |
| Invalidation | Explicitly removing content from cache |
| Cache key | The fields used to decide whether two requests share the same cache entry |
| Origin shield | Extra caching layer that protects the main origin |

## 5. How CDN request flow works
1. User requests a file or cacheable response.
2. DNS or CDN routing sends the request to a nearby edge location.
3. If the content is in cache, the CDN returns it immediately.
4. If not, the CDN fetches it from the origin.
5. The CDN caches it for future requests based on policy.

## 6. What content is good for CDN
- Images
- Videos
- CSS and JavaScript bundles
- Product thumbnails
- Public downloads
- Static landing pages
- Some cacheable API responses such as public metadata or product details

## 7. What content is not automatically good for CDN
- Highly personalized responses without careful cache keys.
- Very short-lived data that changes every second.
- Sensitive content unless protected with signed URLs, tokens, or other controls.

## 8. Cache headers you should know
- `Cache-Control` controls how caching behaves.
- `max-age` tells caches how long a response stays fresh.
- `ETag` allows validation of whether the content changed.
- `Last-Modified` helps conditional fetching.
- Good cache headers are a big part of good CDN usage.

## 9. Invalidation and cache busting
- Invalidation removes or refreshes cached content before TTL ends.
- Cache busting usually changes the file name or URL when content changes, such as `app.v2.js`.
- Cache busting is often better than mass invalidation for static assets.

## 10. CDN vs Redis vs browser cache
| Question | CDN | Redis | Browser cache |
| --- | --- | --- | --- |
| Main location | Edge close to user | Near application/backend | User device |
| Best for | Static/global cacheable content | App-side hot data | Reusing previously fetched content |
| Typical use | Images, JS, CSS, downloads | Sessions, counters, DB result cache | Repeat page loads |

## 11. Dynamic content and CDN
- CDNs are best known for static content, but they can also help with dynamic or semi-dynamic content.
- Some API responses can be cached safely if the data is public and TTL is short.
- Edge logic and workers can sometimes transform requests near the user.

## 12. Security and control
- Use signed URLs or signed cookies for private content.
- Protect the origin so users cannot bypass the CDN easily.
- Use WAF and bot-protection features when available.
- Watch for cache poisoning risk if cache keys are poorly designed.

## 13. Best use cases
- Ecommerce images and static assets.
- Video and media delivery.
- Public web pages with global traffic.
- Software downloads.
- Public APIs with cacheable responses.

## 14. Pros
- Lower latency for global users.
- Lower bandwidth and CPU load at origin.
- Better scale for traffic spikes.
- Better resilience when origin is under pressure.

## 15. Cons
- Cache invalidation can be tricky.
- Wrong cache rules can serve stale or incorrect data.
- Personalized responses can be dangerous to cache.
- Adds operational thinking around TTL, keys, and origin behavior.

## 16. Common mistakes
- Serving dynamic personalized data with a bad cache key.
- Forgetting cache headers and then blaming the CDN.
- Using the same TTL for everything.
- Letting origin stay publicly open in a way that bypasses the CDN.
- Ignoring hit ratio and origin load metrics.

## 17. Real-world system design examples
- YouTube and Netflix use CDN heavily for media delivery.
- Ecommerce sites use CDN for images, product thumbnails, and static frontend bundles.
- News sites use CDN for article pages and assets during traffic spikes.

## 18. Tricky interview questions and answers

### Q1. Why use a CDN if I already have Redis?
- Redis helps the application access hot data faster, while CDN helps users receive cacheable content faster from edge locations.

### Q2. Can CDN cache APIs?
- Yes, if the responses are safe to cache and the cache key is designed carefully.

### Q3. What is a cache hit ratio?
- It is the percentage of requests served from cache instead of origin.

### Q4. Why is cache invalidation hard?
- Because you need to keep content fresh without destroying the performance value of caching.

### Q5. What happens on a cache miss?
- The CDN fetches the content from origin, returns it to the user, and may store it for future requests.

### Q6. What is the difference between CDN and load balancer?
- CDN caches and serves content near users; load balancer spreads traffic across backend servers.

### Q7. What is origin shield?
- A middle caching layer that reduces repeated origin fetches from many edge locations.

### Q8. What is the biggest beginner misunderstanding?
- Thinking CDN only matters for images when it can also strongly affect site performance, cost, and origin protection.

## 19. Quick revision checklist
- Can you explain cache hit and cache miss?
- Can you explain TTL, invalidation, and cache busting?
- Can you explain CDN vs Redis vs browser cache?
- Can you explain why personalized responses need careful cache keys?
- Can you explain why CDN is common in global systems?

## 20. One-line memory anchors
- CDN brings cacheable content closer to users.
- Cache headers matter as much as the CDN itself.
- CDN reduces origin load and improves latency.
- Wrong cache key design can create correctness bugs.

## 21. Senior-Level Deep Follow-up Questions

### DQ1. How does cache invalidation work across a global CDN? Why is it hard?
- CDNs cache content at hundreds or thousands of edge locations worldwide. Invalidating a cached asset means every edge must either purge or refresh it.
- **Purge/ban**: you send an API call to the CDN (e.g., CloudFront invalidation, Fastly purge). The CDN propagates the invalidation to all edge nodes. This can take seconds to minutes depending on the CDN.
- **TTL-based expiry**: set a short TTL so content refreshes frequently. Simpler but means more origin hits.
- **Cache busting via URL versioning**: change the URL when content changes (e.g., `style.v2.css` or `style.css?hash=abc123`). The old URL stays cached (harmless) and the new URL forces a fresh fetch. This is the most reliable method.
- Why it is hard:
  - Propagation delay: even after you send a purge, some edges may serve stale content for a brief period.
  - Eventual consistency: there is no global "instant invalidation" across all edges simultaneously.
  - Selective invalidation: purging a single URL is easy, but purging all URLs matching a pattern (e.g., all product images) may not be supported or may be slow.
  - Cost: some CDNs charge per invalidation request. Frequent purges can be expensive.
- Senior insight: the best CDN cache strategy often combines versioned URLs for static assets (never invalidate, just deploy new URLs) with short TTLs for dynamic or semi-dynamic content.

### DQ2. What is the Vary header and why can it cause cache problems?
- The `Vary` header tells the CDN (and any cache) that the response depends on certain request headers. The cache must store separate versions for each combination of those header values.
- Example: `Vary: Accept-Encoding` means the CDN stores separate cached copies for gzip, brotli, and uncompressed responses.
- Example: `Vary: Accept-Language` means separate cached copies per language.
- Problem: `Vary: Cookie` or `Vary: Authorization` effectively makes the response uncacheable because each user has a unique cookie/token. The cache key explodes and hit rates collapse.
- Pitfall: some frameworks add `Vary: Cookie` to all responses by default (Django does this). This destroys CDN caching for anonymous users.
- Senior advice: audit `Vary` headers carefully. For CDN-cached responses, strip unnecessary `Vary` directives. Serve personalized content separately from cacheable static content.

### DQ3. What is origin shield and when should you use it?
- Without origin shield: if your content is not cached at an edge, each edge location independently fetches from your origin. If 100 edges have a cache miss simultaneously, your origin gets 100 requests for the same content.
- With origin shield: a middle-tier cache layer sits between edges and origin. Edge misses go to the shield first. If the shield has it cached, it serves it. Only if the shield also misses does the request reach your origin.
- Benefits: dramatically reduces origin load, especially for content with moderate TTLs or during cache warming after deployments.
- Trade-off: adds one extra hop of latency for shield misses. The shield itself is usually in one region, so geographically distant edges have higher latency to the shield.
- When to use: when your origin is expensive to hit (slow, database-backed), when you have a large edge footprint, or when you deploy frequently and caches are cold.

### DQ4. How does CDN handle dynamic content? Can you cache API responses?
- CDNs were originally designed for static content, but modern CDNs (CloudFront, Fastly, Cloudflare) can cache dynamic content too.
- API response caching:
  - Cache GET requests with appropriate `Cache-Control` headers. For example, a product catalog API with `Cache-Control: public, max-age=60` can be cached for 60 seconds.
  - Use cache keys based on URL + query parameters + relevant headers. Be careful not to include per-user headers in the cache key.
  - Do NOT cache POST/PUT/DELETE requests or authenticated responses unless you know exactly what you are doing.
- **Edge compute** (Cloudflare Workers, CloudFront Functions, Fastly Compute): run code at the edge to customize responses, do A/B testing, personalize content, or aggregate APIs. This turns the CDN from a dumb cache into a programmable edge.
- **Stale-while-revalidate**: the CDN serves a stale cached response immediately while asynchronously fetching a fresh copy from origin. This gives low latency to the user and keeps content relatively fresh.

### DQ5. How does request collapsing (coalescing) work at the CDN edge?
- When a popular resource's cache expires, hundreds of concurrent requests may arrive at the edge simultaneously.
- Without collapsing: all of them are forwarded to the origin, causing a thundering herd.
- With request collapsing: the edge sends ONE request to the origin and makes all other concurrent requesters wait for that single response. Once the origin responds, the edge serves all waiting clients and caches the result.
- This is also called "request coalescing" or "origin shielding at the edge."
- Not all CDNs enable this by default. Some require configuration. Fastly calls it "request collapsing." CloudFront has limited support.
- Senior insight: request collapsing is critical for high-traffic sites. Without it, every cache expiration becomes an origin spike.

### DQ6. How does CDN handle HTTPS and certificate management at scale?
- CDN terminates TLS at the edge. The client's HTTPS connection ends at the nearest edge node, not at your origin.
- The CDN needs your TLS certificate (or provisions one for you). Options:
  - Upload your certificate to the CDN.
  - Use the CDN's managed certificates (e.g., AWS ACM for CloudFront, Cloudflare's Universal SSL). These auto-renew.
- **SNI (Server Name Indication)**: allows one edge IP to serve certificates for many domains. The client sends the hostname during the TLS handshake, and the edge selects the right certificate. This is how CDNs serve millions of domains on shared infrastructure.
- Edge-to-origin connection: can be HTTP (faster, simpler) or HTTPS (more secure). For sensitive data, use HTTPS end-to-end.
- HTTP/2 and HTTP/3 (QUIC): modern CDNs support these protocols at the edge, improving performance (multiplexing, header compression, 0-RTT connection resumption).

### DQ7. Push CDN vs Pull CDN — what is the real difference?
- **Pull CDN**: the CDN fetches content from your origin on the first request (cache miss). Subsequent requests are served from cache until TTL expires. This is the standard model used by CloudFront, Cloudflare, Fastly.
  - Pros: simple, automatic, no deployment step for CDN.
  - Cons: first request has higher latency (cache miss + origin fetch).
- **Push CDN**: you explicitly upload/push content to the CDN's storage. The CDN serves it directly without hitting your origin.
  - Pros: origin is never hit for cached content. Good for predictable content like software releases, video files, or build artifacts.
  - Cons: you must manage content uploads, versions, and cleanup.
  - Examples: S3 + CloudFront (origin is S3 — you push to S3), Azure Blob + CDN.
- Most modern architectures use pull CDN for web pages and APIs, and push CDN (via object storage origins) for large static files and media.

### DQ8. How do you measure and optimize CDN performance?
- Key metrics:
  - **Cache hit ratio**: percentage of requests served from cache. Target 90%+ for static content.
  - **Origin offload**: how much traffic the CDN saves your origin from handling.
  - **TTFB (Time To First Byte)**: from client to edge. Should be low for cached content.
  - **Bandwidth savings**: total bytes served from cache vs origin.
  - **Error rates**: 4xx/5xx from edge or origin.
- Optimization techniques:
  - Set appropriate TTLs (long for static assets, shorter for dynamic).
  - Use versioned URLs so static assets get long TTLs.
  - Enable compression (gzip/brotli) at the edge.
  - Use HTTP/2 or HTTP/3.
  - Pre-warm cache for known popular content (some CDNs support this).
  - Monitor and fix low-hit-ratio URLs.
- Senior insight: a CDN that is not well-configured can actually make things worse (adds a hop but never caches). Always verify cache hit headers (`X-Cache`, `CF-Cache-Status`) during testing.