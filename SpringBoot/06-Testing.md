# Spring Boot Testing — Interview Q&A

## Test types
- Unit tests: no Spring context.
- Slice tests: part of context (fast).
- Integration tests: full context (slower).

## Common annotations
- `@SpringBootTest`: loads full application context.
- `@WebMvcTest`: MVC slice (controller + MVC infra).
- `@DataJpaTest`: JPA slice.
- `@MockBean`: replace bean in Spring context.

Tricks:
- Prefer unit tests when possible.
- Use slices for faster feedback.

## MockMvc
- Tests controllers without starting server.

## Testcontainers
- Real DB in container for integration tests.

Interview point:
- Explaining why Testcontainers is better than H2 for production parity.

## Profiles for tests
- Use `@ActiveProfiles("test")`.

## Transaction in tests
- Many Spring tests run in a transaction and roll back by default.

Pitfall:
- Tests passing due to rollback behavior, but production behaves differently.
