# Spring Boot Annotations & Mappings — Cheat Sheet (Interview)

This is the “most asked” annotation list + what it really does + common traps.

## 1) Core stereotypes (component scanning)
- `@Component`
  - Generic bean discovered by component scan.
- `@Service`
  - Same as `@Component`, but semantically for service layer.
- `@Repository`
  - Same as `@Component` + adds exception translation for persistence exceptions.
- `@Controller`
  - MVC controller (views).
- `@RestController`
  - `@Controller` + `@ResponseBody` (return value becomes HTTP response body, typically JSON).

**Trick:** These stereotypes matter for readability and some infrastructure features (`@Repository`). DI is identical.

## 2) Configuration & bean creation
- `@Configuration`
  - Marks a class as a source of bean definitions.
  - **Important:** Full `@Configuration` is proxied so calling a `@Bean` method returns the managed singleton.
- `@Bean`
  - Declares a bean in a `@Configuration` class.
- `@Import`
  - Imports other config classes.
- `@Profile("dev")`
  - Bean active only for specific profiles.
- `@Conditional...` (Boot/Framework)
  - `@ConditionalOnClass`, `@ConditionalOnMissingBean`, `@ConditionalOnProperty`, etc.

**Classic trap:** `@Transactional`, `@Async`, caching etc. often work via proxies → self-invocation bypasses them.

## 3) Dependency injection selection
- `@Autowired`
  - Inject dependency.
  - If class has a single constructor, it’s autowired even without `@Autowired` (Spring 4.3+).
- `@Qualifier("beanName")`
  - Choose a specific bean when multiple candidates.
- `@Primary`
  - Makes one bean the default candidate.
- `@Lazy`
  - Delay bean creation until first use.

**Trick:** Prefer constructor injection; `@Qualifier` is more explicit than relying on `@Primary`.

## 4) Spring Boot “main” annotations
- `@SpringBootApplication`
  - Meta: `@SpringBootConfiguration` + `@EnableAutoConfiguration` + `@ComponentScan`.
- `@EnableAutoConfiguration`
  - Turns on Boot auto-config.

## 5) Externalized configuration
- `@ConfigurationProperties(prefix = "app")`
  - Binds properties to POJO (preferred over many `@Value`).
- `@EnableConfigurationProperties`
  - Enables/configures `@ConfigurationProperties` classes.
- `@Value("${key}")`
  - Inject single property.

**Tricks:**
- Prefer `@ConfigurationProperties` + validation (`@Validated`) for groups of settings.
- In Boot 3, many apps use `spring.config.import=` for extra config files.

## 6) Web mappings (Spring MVC)
### Mapping annotations
- `@RequestMapping("/path")`
  - Base mapping for any HTTP method; can specify `method=...`, `consumes`, `produces`.
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`
  - Shortcuts for method-specific mappings.

### Parameter binding
- `@PathVariable`
  - Pull value from URL path: `/users/{id}`.
- `@RequestParam`
  - Query parameter: `/users?id=1`.
- `@RequestBody`
  - Body (JSON) → object via message converters.
- `@RequestHeader`
  - Read a header.
- `@CookieValue`
  - Read a cookie.
- `@RequestPart`
  - Multipart part (file uploads).

### Response shaping
- `@ResponseBody`
  - Return value is written to response.
- `@ResponseStatus(HttpStatus.X)`
  - Force HTTP status.

**Common pitfalls:**
- `415 Unsupported Media Type` → wrong/missing `Content-Type` OR missing converter.
- `400 Bad Request` → binding/validation failed.

## 7) Validation
- `@Valid` (Jakarta)
  - Triggers validation for a parameter (e.g., `@RequestBody`).
- `@Validated` (Spring)
  - Enables method-level validation and validation groups.

**Trick:** `@Valid` on `@RequestBody` is the common controller pattern.

## 8) Exception handling
- `@ControllerAdvice`
  - Global exception handler for controllers.
- `@RestControllerAdvice`
  - `@ControllerAdvice` + `@ResponseBody`.
- `@ExceptionHandler`
  - Handles specific exception types.

## 9) Persistence (JPA) annotations (common)
- `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`
- Relationships: `@OneToMany`, `@ManyToOne`, `@ManyToMany`, `@JoinColumn`

**Trick:** Don’t return entities from controllers; use DTOs to avoid lazy-loading/serialization issues.

## 10) Transactions
- `@Transactional`
  - Defines transaction boundary (commonly at service layer).

**Tricks:**
- Proxy-based → self-invocation issue.
- Default rollback is RuntimeException/Error.

## 11) Async, Scheduling, Caching
- `@EnableAsync` + `@Async`
- `@EnableScheduling` + `@Scheduled`
- `@EnableCaching` + `@Cacheable` / `@CacheEvict`

**Trick:** These also commonly use proxies.

## 12) Security (high-level)
- `@EnableWebSecurity` (often via config)
- Method security: `@PreAuthorize`, `@PostAuthorize`

## 13) Testing annotations
- `@SpringBootTest` (full context)
- `@WebMvcTest` (MVC slice)
- `@DataJpaTest` (JPA slice)
- `@MockBean` (replace bean in context)
- `@ActiveProfiles("test")`

---

## Request mapping “rules of thumb” (interview-ready)
- Prefer `@GetMapping`/`@PostMapping` etc. over generic `@RequestMapping` for clarity.
- Put a base path on the class: `@RequestMapping("/api/users")`.
- Use `produces`/`consumes` when you must enforce content types.
- Prefer DTOs for request/response.
- Keep controller thin; move business logic to `@Service`.
