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

## 23. Senior-Level Deep Follow-up Questions

### DQ1. How does the inverted index work internally?
- An inverted index is the core data structure behind full-text search. It maps terms (words) to the documents that contain them.
- Process:
  1. When you index a document, the `text` fields go through an **analyzer**: tokenizer (splits text into terms) → filters (lowercase, stemming, stop words removal).
  2. Each resulting term is added to the inverted index: `term → [doc1, doc5, doc12, ...]` (a posting list).
  3. Each entry in the posting list can also store: term frequency (how many times the term appears in the doc), positions (where in the text), and offsets (character positions).
- When you search for "quick brown fox":
  1. The search query is also analyzed (tokenized, lowercased, etc.).
  2. The inverted index is looked up for each term: "quick", "brown", "fox".
  3. The posting lists are intersected (for AND queries) or unioned (for OR queries).
  4. Relevance scoring (BM25) is applied using term frequency, document frequency, and field length.
- This is why full-text search is so fast: instead of scanning every document, it uses pre-built lookup tables.
- `keyword` fields are NOT analyzed. They are stored as-is in a different kind of index structure (doc values / sorted set) for exact matching and aggregations.

### DQ2. How does Elasticsearch's distributed search work (scatter-gather)?
- An Elasticsearch index is split into **shards**, distributed across nodes. Each shard is an independent Lucene index.
- When you send a search query:
  1. **Scatter phase (query phase)**: the coordinating node sends the query to all relevant shards (primary or replica). Each shard executes the query locally and returns the **top N document IDs and scores** (not the full documents).
  2. **Gather phase (merge)**: the coordinating node collects results from all shards, merges and re-sorts them globally, picks the final top N.
  3. **Fetch phase**: the coordinating node fetches the actual document content only for the final top N documents from the relevant shards.
- Why this matters:
  - Each shard does local work, so more shards = more parallelism.
  - But more shards = more network overhead, more merge work, and each shard's segment needs memory.
  - The coordinating node can become a bottleneck if it merges results from too many shards.
- Senior insight: the optimal shard count depends on data size and query patterns. Rule of thumb: each shard should be 10-50GB. Too many small shards waste resources; too few large shards limit parallelism.

### DQ3. What is the deep pagination problem and how do you solve it?
- Using `from` + `size` (like SQL's `OFFSET` + `LIMIT`): to get page 100 with size 10 (`from: 990, size: 10`), EACH shard must retrieve and sort the top 1000 documents, send them to the coordinating node, which merges 1000 × N_shards results to pick the final 10.
- As `from` increases, this becomes extremely expensive. Elasticsearch has a default limit of `from + size ≤ 10,000`.
- Solutions:
  - **`search_after`**: uses the sort values of the last document from the previous page as a cursor. Each page only needs to fetch `size` documents from each shard. Very efficient for "next page" navigation but does not support jumping to arbitrary pages.
  - **Scroll API**: creates a consistent snapshot and returns pages sequentially. Designed for bulk data export, not user-facing pagination. Deprecated in favor of Point in Time (PIT) + `search_after`.
  - **Point in Time (PIT)**: creates a lightweight snapshot of the index state. Combined with `search_after`, it provides consistent pagination even as the index changes.
- Senior tip: for user-facing search, most products do not need deep pagination. Google shows ~10 pages max. Use `search_after` for "load more" patterns and PIT for data export.

### DQ4. How does Elasticsearch handle near-real-time (NRT) search?
- When a document is indexed, it is NOT immediately searchable. It is written to an in-memory buffer.
- Every 1 second (default `refresh_interval`), the buffer is flushed to a new **Lucene segment** (an immutable mini-index on disk). Only after this "refresh" is the document searchable.
- This is why Elasticsearch is called "near-real-time" — there is a ~1 second delay between indexing and searchability.
- You can:
  - Force a refresh with `_refresh` API (useful in tests, bad in production due to overhead).
  - Change `refresh_interval` to a longer value for write-heavy workloads (reduces segment creation overhead).
  - Set `refresh_interval: -1` to disable automatic refresh (for bulk loading).
- **Segments are immutable**. Deleting a document does not remove it from the segment; it marks it as deleted in a bitmap. The actual data is removed during segment merging.

### DQ5. What is segment merging and why does it matter?
- Each refresh creates a new segment. Over time, you get many small segments.
- Searching across many segments is slower because each segment must be searched independently and results merged.
- **Segment merging**: Elasticsearch (Lucene) periodically merges small segments into larger ones in the background. This:
  - Removes deleted documents.
  - Reduces the number of segments.
  - Improves search performance.
- Merging is I/O and CPU intensive. During heavy merging, search latency can increase.
- **Force merge** (`_forcemerge`): manually triggers merging to a target number of segments. Useful for read-only indexes (e.g., time-based indexes that are no longer being written to). Never force merge on actively written indexes.
- Senior insight: segment management is one of the reasons Elasticsearch performance tuning is nuanced. Too many small segments = slow searches. Merging = CPU/IO cost. The balance is managed automatically but understanding it helps with performance debugging.

### DQ6. How does Elasticsearch relevance scoring work (BM25)?
- BM25 (Best Match 25) is the default relevance scoring algorithm since Elasticsearch 5.0 (replaced TF-IDF).
- Factors:
  - **Term Frequency (TF)**: how many times the search term appears in the document field. More occurrences = higher score, but with diminishing returns (BM25 has a saturation curve, unlike raw TF).
  - **Inverse Document Frequency (IDF)**: how rare the term is across all documents. Rare terms (e.g., "kubernetes") score higher than common terms (e.g., "the").
  - **Field length normalization**: shorter fields that contain the term score higher than longer fields. A match in a 5-word title is more relevant than the same match in a 5000-word body.
- BM25 parameters: `k1` (controls term frequency saturation, default 1.2) and `b` (controls field length normalization, default 0.75).
- You can customize scoring with:
  - `boost`: increase the weight of certain fields (e.g., title matches are 3x more important than body matches).
  - `function_score`: apply custom scoring functions based on document attributes (e.g., boost newer documents, boost by popularity).
  - `script_score`: write custom scoring logic in Painless script.
- Senior tip: understanding BM25 helps you explain why search results are ordered the way they are. Use the `_explain` API to see the scoring breakdown for a specific query.

### DQ7. How does Elasticsearch handle cluster state and master election?
- The **cluster state** is the metadata that describes the cluster: indexes, mappings, shard allocation, node membership. Every node holds a copy.
- One node is the **master node**, responsible for:
  - Managing cluster state changes (creating/deleting indexes, shard allocation decisions).
  - Handling node join/leave events.
  - Propagating cluster state updates to all nodes.
- Master election uses a quorum-based approach. In modern Elasticsearch (7.0+), the cluster coordination module uses a Raft-like protocol:
  - Eligible master nodes vote.
  - A node needs votes from a majority of master-eligible nodes to become master.
  - `discovery.seed_hosts` and `cluster.initial_master_nodes` configure the initial bootstrapping.
- If the master node fails, a new election happens. During election (usually seconds), the cluster cannot make metadata changes but existing searches and indexing to already-allocated shards continue.
- **Dedicated master nodes**: in production, use 3 dedicated master-eligible nodes that do NOT hold data. This prevents data operations from impacting cluster management.
- Senior insight: large cluster state (thousands of indexes/shards) can slow down cluster state propagation. This is why "shard count discipline" is important.

### DQ8. How do you do zero-downtime reindexing in Elasticsearch?
- Unlike databases, you cannot alter the mapping of existing fields in Elasticsearch. If you need to change an analyzer, add a new field type, or restructure mappings, you must reindex.
- Zero-downtime approach:
  1. Create a new index (`products-v2`) with the desired mappings.
  2. Use the `_reindex` API to copy data from the old index (`products-v1`) to the new index.
  3. Use an **alias** (`products`) that points to the current live index. Applications always use the alias, not the index name directly.
  4. After reindex completes, atomically switch the alias from `products-v1` to `products-v2` using the `_aliases` API (remove old, add new in one request).
  5. Delete the old index when ready.
- During reindexing, the old index still serves live traffic. New writes can be dual-written to both indexes or only to the old index (then re-synced after switch).
- Senior tip: aliases are fundamental to Elasticsearch operations. Always use aliases for application access, never raw index names.

### DQ9. What is the difference between query context and filter context?
- **Query context**: "How well does this document match?" Calculates a relevance score. Used for full-text search where ranking matters.
  - Example: `match` query for "wireless headphones" — returns results sorted by relevance.
- **Filter context**: "Does this document match yes or no?" No score calculation. Used for exact conditions.
  - Example: `term` filter for `status: "published"` or `range` filter for `price < 100`.
- Why it matters for performance:
  - Filters are cached by Elasticsearch in a bitset cache. Repeated filters are very fast.
  - Queries with scoring cannot be cached as easily because scores depend on the full index state (IDF changes as documents are added/removed).
- Best practice: use `bool` query with `must` (scored), `should` (scored, optional), `filter` (not scored, cached), and `must_not` (not scored, cached).
- Example: search for "wireless headphones" (scored `must`), filter by `category: "electronics"` and `price < 100` (unscored `filter`). This gives relevant ranking while efficiently filtering.
- Senior insight: putting conditions in `filter` instead of `must` when you do not need scoring dramatically improves performance on large indexes.

### DQ10. How does Elasticsearch handle text analysis at index time vs query time?
- **Index-time analysis**: when a document is indexed, `text` fields pass through the configured analyzer (tokenizer + filters). The resulting terms are stored in the inverted index. This happens once per document.
- **Query-time analysis**: when a search query is run, the search terms also pass through an analyzer (by default, the same one used at index time). The resulting terms are looked up in the inverted index.
- If index-time and query-time analyzers do not match, searches may miss documents. Example: if the index analyzer lowercases terms but the query analyzer does not, searching for "Redis" would not match the indexed term "redis".
- You can set different analyzers for index and search using `search_analyzer`. This is useful for:
  - **Autocomplete**: index with `edge_ngram` analyzer (generates prefixes: "r", "re", "red", "redi", "redis") but search with a `standard` analyzer (user types "red" and it matches the "red" prefix in the index).
  - **Synonym expansion**: expand synonyms at index time or search time depending on your strategy.
- Senior tip: always test your analysis chain using the `_analyze` API before deploying. Mismatched analysis is one of the most common causes of "why doesn't my search find this document?"