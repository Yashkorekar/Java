# Spring Boot Testing — Interview Q&A

## Testing strategy
The best answer is not "write `@SpringBootTest` everywhere".

Good testing layers:
- unit tests for pure business logic
- slice tests for focused framework integration
- integration tests for full wiring and infrastructure behavior

Rule of thumb:
- Use the smallest test style that proves the behavior you care about.

## Unit tests
- No Spring context.
- Fastest feedback.
- Best for service logic, validators, mappers, and utility code.

Common tools:
- JUnit 5
- Mockito
- AssertJ

## Slice tests
Goal:
- Load only a focused part of the application.

Common slices:
- `@WebMvcTest`: controller layer and MVC infrastructure.
- `@DataJpaTest`: repository/JPA layer.
- `@JsonTest`: JSON serialization/deserialization.
- `@RestClientTest`: client-side HTTP components.

Strong interview answer:
- Slice tests are a middle ground between pure unit tests and full integration tests.

## Full integration tests
- `@SpringBootTest` loads the full application context.
- It is useful when you want real wiring, multiple layers together, or full infrastructure integration.

Common web modes:
- `MOCK`
- `RANDOM_PORT`
- `DEFINED_PORT`

Trap:
- If you use `@SpringBootTest` for every test, the suite becomes slow and noisy.

## Common annotations you should know
- `@SpringBootTest`
- `@WebMvcTest`
- `@DataJpaTest`
- `@JsonTest`
- `@RestClientTest`
- `@MockBean`
- `@SpyBean`
- `@AutoConfigureMockMvc`
- `@ActiveProfiles("test")`
- `@DynamicPropertySource`

## `@MockBean` vs Mockito `@Mock`
- `@Mock`: plain Mockito mock, not registered in Spring context.
- `@MockBean`: replaces a bean inside the Spring context.

Why it matters:
- If the controller/service under test is created by Spring, a plain `@Mock` does not automatically replace the dependency inside the context.

## MockMvc
What it is:
- A way to test Spring MVC controllers without starting a real HTTP server.

Good use cases:
- controller mapping
- validation behavior
- JSON shape
- exception handling
- security rules at controller boundary

## `TestRestTemplate` and `WebTestClient`
- `TestRestTemplate` is useful with real HTTP tests in servlet-based apps.
- `WebTestClient` is common in WebFlux and can also be useful for reactive-oriented testing scenarios.

Interview angle:
- Choose the client based on the web stack and how close to real HTTP behavior you need to be.

## Testcontainers
Why it matters:
- Testcontainers gives you real dependencies such as PostgreSQL, Redis, or Kafka in containers.

Strong interview answer:
- It gives better production parity than H2 or fake infrastructure for many integration scenarios.

## `@DynamicPropertySource`
Use case:
- Feed runtime container values such as JDBC URLs and ports into the Spring test context.

Why it matters:
- Container ports are often dynamic, so hard-coded test properties do not work well.

## Security tests
Useful annotations/patterns:
- `@WithMockUser`
- request post-processors for roles, users, or CSRF

What to verify:
- unauthenticated calls get `401`
- unauthorized calls get `403`
- authorized users can access the endpoint

## Transaction behavior in tests
- Many data-layer tests run in a transaction and roll back after the test.

Pitfall:
- Tests can pass because the rollback hides transaction/flush behavior that production will actually hit.

## Context caching
Important idea:
- Spring caches application contexts across tests when it can.

Why it matters:
- Excessive context variation makes the suite slower.
- `@DirtiesContext` is useful sometimes, but expensive.

## Common mistakes
- Overusing `@SpringBootTest`.
- Using H2 when production behavior depends on PostgreSQL/MySQL specifics.
- Asserting too much internal implementation instead of observable behavior.
- Ignoring security in controller tests.
- Letting tests depend on order or shared mutable state.

## Strong interview close
If asked how to test a Boot app well:
- keep most tests small and fast
- use slice tests intentionally
- use full integration tests for wiring and infrastructure
- use Testcontainers for critical external systems
- test security and error handling explicitly
