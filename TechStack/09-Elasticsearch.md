# Elasticsearch Interview Prep and Deep Guide

## 1. What Elasticsearch is
- Elasticsearch is a distributed search and analytics engine.
- It is built for full-text search, filtering, aggregations, and fast query experience over large datasets.
- It is commonly used for product search, log search, autocomplete, and analytics dashboards.

## 2. 30-second answer
- Elasticsearch is used when users need powerful search, relevance ranking, faceted filtering, or fast log exploration.
- Its core strength comes from inverted indexes, analyzers, distributed shards, and flexible query capabilities.
- It is usually not the primary transactional source of truth for an application.

## 3. Beginner-friendly example: ecommerce search bar
- A user types `wireless noise cancelling headphones` into an ecommerce search bar.
- The product database alone may not give a great search experience.
- Elasticsearch indexes product title, brand, description, category, and tags.
- It can match words quickly, rank better results higher, and support filters like price range or brand.
- The important beginner idea is this: Elasticsearch is built to answer search questions, not just exact-value lookups.

### Beginner memory trick
- Elasticsearch is like a super-fast searchable catalog that knows how words relate, not just how exact IDs match.

## 4. Core concepts
| Concept | Meaning |
| --- | --- |
| Index | A logical collection of searchable documents |
| Document | One searchable record |
| Shard | Partition of an index |
| Replica | Copy of a shard for availability and read scale |
| Mapping | Schema definition for fields |
| Analyzer | Breaks text into searchable tokens |
| Inverted index | Maps terms to documents containing them |
| Aggregation | Summary query such as counts, buckets, or metrics |

## 5. Why Elasticsearch is useful
- Very fast full-text search.
- Relevance ranking.
- Autocomplete and search suggestions.
- Filtering and faceted navigation.
- Log and event exploration.
- Aggregations for dashboards and analytics.

## 6. Query vs filter
- Query is usually about matching text and scoring relevance.
- Filter is usually about exact conditions such as `brand = Apple` or `price < 1000`.
- Filters are often cacheable and do not affect score.
- Good interview answers explain both together.

## 7. Analyzers and tokenization
- Elasticsearch does not just store raw strings for search.
- It analyzes text into tokens.
- Different analyzers can lowercase, remove stop words, stem words, or split text differently.
- Search quality depends heavily on analyzer design.

## 8. Near real-time behavior
- Elasticsearch is near real-time, not instant in the strictest sense.
- Newly indexed documents become searchable after refresh.
- This is usually fast, but not always immediate.

## 9. Shards and scaling
- Indexes are split into shards.
- Shards allow data and query work to be distributed across nodes.
- Replicas improve availability and can help read throughput.
- Too many shards can create operational overhead.

## 10. Mapping design
- `text` fields are analyzed for full-text search.
- `keyword` fields are usually for exact match, sorting, and aggregations.
- Wrong field type causes bad query behavior.
- Mapping mistakes are a very common beginner problem.

## 11. Best use cases
- Product search.
- Log search and observability.
- Article or content search.
- Autocomplete and discovery.
- Faceted search with filters.
- Operational dashboards using aggregations.

## 12. When Elasticsearch is not the best fit
- Primary transactional storage.
- Complex financial transaction systems.
- Exact relational querying with joins as the main workload.
- Small systems that only need simple `LIKE` queries once in a while.

## 13. How it fits in real systems
- Primary data is often stored in SQL or MongoDB.
- Searchable views of that data are indexed into Elasticsearch.
- Updates may arrive through batch jobs, CDC pipelines, or events from Kafka.
- Elasticsearch serves search traffic, while the source database remains the durable truth.

## 14. Reindexing and schema evolution
- Sometimes mappings or analyzers must change.
- In that case, reindexing into a new index is common.
- This is a normal operational reality and a frequent interview discussion point.

## 15. Pros
- Strong full-text search.
- Good relevance ranking.
- Fast filtering and aggregations.
- Great for search-heavy user experiences.
- Strong fit for logs and observability.

## 16. Cons
- Not the best primary transactional database.
- Operational tuning can be significant.
- Mapping mistakes can hurt search quality badly.
- Reindexing can be expensive.
- Search correctness is more nuanced than simple exact-match lookups.

## 17. Elasticsearch vs SQL
- SQL databases are the system of record for many business systems.
- Elasticsearch is usually a specialized search layer.
- SQL may support basic text search, but Elasticsearch is stronger for ranking, autocomplete, stemming, and faceting at scale.

## 18. Elasticsearch vs MongoDB
- MongoDB stores primary document data.
- Elasticsearch specializes in making that data searchable in rich ways.
- Many systems use both together.

## 19. Common mistakes
- Using Elasticsearch as the only source of truth for core business data.
- Not understanding `text` vs `keyword` fields.
- Creating too many shards.
- Ignoring analyzer choice.
- Expecting strict immediate consistency after every write.

## 20. Tricky interview questions and answers

### Q1. What makes Elasticsearch good for full-text search?
- Inverted indexes, analyzers, tokenization, and relevance scoring.

### Q2. Is Elasticsearch a database?
- It stores data, but it is usually best thought of as a search and analytics engine rather than the main transactional database.

### Q3. Why not just search directly in SQL?
- SQL can do basic text search, but advanced ranking, autocomplete, typo tolerance, and faceted filtering are much stronger in Elasticsearch.

### Q4. What is the difference between `text` and `keyword` fields?
- `text` is analyzed for full-text search; `keyword` is used for exact matching, filtering, sorting, and aggregations.

### Q5. Why can reindexing be necessary?
- Because mapping or analyzer changes often require building a new index structure.

### Q6. What is near real-time search?
- Newly indexed data usually becomes searchable after a refresh, so visibility is quick but not always instantaneous.

### Q7. What are shards and replicas?
- Shards split the data; replicas copy shards for availability and read scaling.

### Q8. What is the biggest beginner misunderstanding?
- Treating Elasticsearch like a drop-in replacement for the primary application database.

## 21. Quick revision checklist
- Can you explain inverted index, analyzer, mapping, shard, and replica?
- Can you explain query vs filter?
- Can you explain `text` vs `keyword`?
- Can you explain why Elasticsearch usually sits beside a primary database?
- Can you explain why search relevance is different from exact lookup?

## 22. One-line memory anchors
- Elasticsearch is search-first, not transaction-first.
- Search quality depends heavily on analyzers and mappings.
- `text` is for search; `keyword` is for exactness.
- Keep the primary source of truth elsewhere.