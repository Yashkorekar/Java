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