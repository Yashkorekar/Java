# MongoDB Interview Prep and Deep Guide

## 1. What MongoDB is
- MongoDB is a document-oriented NoSQL database.
- It stores data in flexible JSON-like documents called BSON documents.
- It is often chosen when the data model is naturally document-shaped and the schema may evolve over time.

## 2. 30-second answer
- MongoDB is good for applications where records look like documents rather than heavily normalized relational rows.
- It supports indexing, replication, aggregation, and sharding.
- It is commonly used for product catalogs, user profiles, content systems, event metadata, and rapidly evolving application data.

## 3. Beginner-friendly example: ecommerce product catalog
- An ecommerce product can have different attributes.
- A phone may have `screenSize`, `battery`, and `storage`.
- A shoe may have `size`, `color`, and `material`.
- In MongoDB, both can be stored as product documents in the same collection without forcing every product to fit one rigid table structure.
- The important beginner idea is this: MongoDB is useful when records are naturally stored as documents with varying fields.

### Beginner memory trick
- MongoDB is like storing one complete product card in a folder instead of splitting that product into many rigid spreadsheet tables.

## 4. Core concepts
| Concept | Meaning |
| --- | --- |
| Document | JSON-like record |
| Collection | Group of documents, similar to a table at a high level |
| BSON | Binary JSON format used internally |
| Index | Data structure for faster queries |
| Replica set | Primary plus replicas for availability |
| Sharding | Splitting data across servers |
| Aggregation pipeline | Stage-based data processing and transformation |

## 5. Why people choose MongoDB
- Flexible schema.
- Natural fit for nested JSON-like application data.
- Good developer productivity for evolving products.
- Easy to store arrays and nested objects.
- Good scaling options with replication and sharding.

## 6. When MongoDB is a good fit
- Product catalogs.
- User profiles and preferences.
- CMS and content metadata.
- Event metadata or logs with varying structure.
- Applications where schema changes often.

## 7. When MongoDB is not the best fit
- Heavy multi-row transactional workloads with strict relational constraints.
- Systems that depend heavily on complex joins.
- Financial systems where relational consistency and mature SQL semantics are central.

## 8. Embedding vs referencing
- Embedding means storing related data inside one document.
- Referencing means storing separate documents and linking them by ID.
- Embed when the related data is small, usually read together, and belongs tightly to one parent.
- Reference when related data grows independently or becomes too large.

## 9. Indexing
- MongoDB supports single-field and compound indexes.
- It also supports text, TTL, and geospatial indexes.
- Good indexes matter as much in MongoDB as in SQL systems.
- Too many indexes slow writes and consume memory.

## 10. Replication and replica sets
- A replica set has one primary and one or more secondary nodes.
- Writes usually go to the primary.
- Secondaries replicate changes.
- If the primary fails, a new primary can be elected.

## 11. Sharding
- Sharding splits data across multiple servers.
- A shard key decides how data is distributed.
- Good shard-key choice matters a lot.
- Bad shard keys can create hotspots and uneven growth.

## 12. Read concern and write concern
- Write concern controls how safely a write must be acknowledged.
- Read concern controls what consistency level the read expects.
- These settings affect durability, latency, and consistency behavior.

## 13. Transactions
- MongoDB supports transactions, including multi-document transactions.
- They are useful but should not be used carelessly everywhere.
- If your design requires constant large relational transactions, a relational database may still be the more natural fit.

## 14. Aggregation pipeline
- Aggregation pipeline processes documents in stages.
- It is powerful for filtering, grouping, projecting, and transforming data.
- It is one of MongoDB's most important features for analytics-like operations inside the database.

## 15. Best use cases
- Catalog and content management.
- User profiles and settings.
- Nested data models.
- Rapidly changing product requirements.
- APIs that naturally produce and consume JSON.

## 16. Pros
- Flexible schema.
- Good fit for document-shaped data.
- Easy to model nested objects and arrays.
- Good developer productivity.
- Replication and sharding support.

## 17. Cons
- Less natural than SQL for relationship-heavy systems.
- Poor schema discipline can create messy data over time.
- Bad shard key choice can hurt scale badly.
- Complex joins are weaker than in relational databases.

## 18. MongoDB vs SQL
- MongoDB is usually stronger when the data is document-shaped and evolving.
- SQL is usually stronger when transactions, joins, and relational integrity are central.
- This is not about one being modern and the other being old.
- It is about the shape of data and the workload.

## 19. MongoDB vs Elasticsearch
- MongoDB is a primary database.
- Elasticsearch is usually a search engine, not the main source of truth for transactional application data.
- Many systems store primary data in MongoDB or SQL and then index searchable views into Elasticsearch.

## 20. Common mistakes
- Treating schema flexibility as if no data discipline is needed.
- Embedding huge unbounded arrays inside documents.
- Choosing a bad shard key.
- Forgetting indexes for common queries.
- Using MongoDB when the workload is deeply relational.

## 21. Tricky interview questions and answers

### Q1. Is MongoDB schema-less?
- Not exactly. It is schema-flexible, but your application still has an effective schema whether you manage it well or not.

### Q2. When should I embed instead of reference?
- Embed when data belongs together and is usually read together. Reference when the data grows independently or becomes too large.

### Q3. What is a replica set?
- A primary-replica configuration for availability and failover.

### Q4. What is sharding?
- Horizontal partitioning of data across servers.

### Q5. Can MongoDB do transactions?
- Yes, but if strong multi-row relational transactions dominate the workload, SQL may still be a better fit.

### Q6. Why can schema flexibility be dangerous?
- Because different services or developers may store inconsistent document shapes over time.

### Q7. Why is shard key choice important?
- It controls data distribution and hotspot risk.

### Q8. Is MongoDB a search engine?
- No. It can do some text search, but full-text search systems like Elasticsearch are stronger for advanced search use cases.

## 22. Quick revision checklist
- Can you explain document, collection, index, replica set, and shard?
- Can you explain embedding vs referencing?
- Can you explain when MongoDB is better than SQL?
- Can you explain why shard key choice matters?
- Can you explain why schema flexibility still needs discipline?

## 23. One-line memory anchors
- MongoDB is document-first.
- Flexible schema is useful, but not a free pass to store chaos.
- Embed for togetherness, reference for independent growth.
- Shard key choice can make or break scale.