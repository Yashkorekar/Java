# Spring Boot Annotations & Mappings — Cheat Sheet (Interview)

This is the high-frequency annotation list plus what it does and the common trap behind it.

## 1) Core stereotypes
- `@Component`: generic scanned bean.
- `@Service`: service-layer bean.
- `@Repository`: persistence bean; also participates in exception translation.
- `@Controller`: MVC controller for views.
- `@RestController`: controller whose return values are written to the response body.

## 2) Configuration and bean creation
- `@Configuration`: configuration class with `@Bean` methods.
- `@Bean`: explicit bean factory method.
- `@Import`: include other configuration classes.
- `@Profile("dev")`: bean/config active for a profile.
- `@ConfigurationProperties(prefix = "app")`: bind grouped config.
- `@EnableConfigurationProperties`: register config properties classes.
- `@ConfigurationPropertiesScan`: scan for config properties classes.
- `@Value("${key}")`: inject a single property.

Classic trap:
- `@Configuration` has stronger semantics than a plain component with `@Bean` methods.

## 3) Boot and auto-configuration
- `@SpringBootApplication`: Boot main meta-annotation.
- `@EnableAutoConfiguration`: turn on Boot auto-config.
- `@ConditionalOnClass`: activate when a class exists.
- `@ConditionalOnMissingBean`: activate only if no bean already exists.
- `@ConditionalOnProperty`: activate based on a property value.

## 4) Injection and selection
- `@Autowired`: inject dependency.
- `@Qualifier("beanName")`: pick a specific bean.
- `@Primary`: mark a default candidate.
- `@Lazy`: lazy-init bean or dependency.

Interview trap:
- Prefer constructor injection over field injection.

## 5) Web mappings
- `@RequestMapping`: generic mapping.
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`: method-specific mappings.
- `@PathVariable`: bind path segment.
- `@RequestParam`: bind query parameter.
- `@RequestBody`: bind request body.
- `@RequestHeader`: bind header.
- `@CookieValue`: bind cookie.
- `@RequestPart`: bind multipart part.
- `@ModelAttribute`: bind form/query data to an object.
- `@CrossOrigin`: configure CORS at controller or method level.

## 6) Response and exception handling
- `@ResponseBody`: write return value to response.
- `@ResponseStatus`: set status code.
- `@ControllerAdvice`: global controller advice.
- `@RestControllerAdvice`: advice that writes response bodies.
- `@ExceptionHandler`: handle exception types.

## 7) Validation
- `@Valid`: trigger Jakarta validation.
- `@Validated`: enable method validation or validation groups.
- Common constraints: `@NotNull`, `@NotBlank`, `@Size`, `@Email`, `@Positive`.

## 8) JPA and persistence
- `@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`
- `@OneToMany`, `@ManyToOne`, `@ManyToMany`, `@OneToOne`, `@JoinColumn`
- `@Version`: optimistic locking version field.

Rule:
- Do not return entities directly from controllers unless you really understand the consequences.

## 9) Transactions and async behavior
- `@Transactional`: transaction boundary.
- `@EnableAsync` and `@Async`: asynchronous execution.
- `@EnableScheduling` and `@Scheduled`: scheduled jobs.
- `@EnableCaching`, `@Cacheable`, `@CachePut`, `@CacheEvict`: cache behavior.

Trap:
- Proxy-based features often fail on self-invocation.

## 10) Security
- `@EnableMethodSecurity`: enable method security.
- `@PreAuthorize`: authorize before method call.
- `@PostAuthorize`: authorize after method call.

## 11) Testing
- `@SpringBootTest`: full context.
- `@WebMvcTest`: MVC slice.
- `@DataJpaTest`: JPA slice.
- `@JsonTest`: JSON slice.
- `@RestClientTest`: REST client slice.
- `@MockBean`: replace a bean in the Spring context.
- `@SpyBean`: spy on a Spring bean.
- `@AutoConfigureMockMvc`: configure `MockMvc` in a full context test.
- `@ActiveProfiles("test")`: activate test profile.
- `@DynamicPropertySource`: register dynamic properties for tests.

## 12) Handy rules of thumb
- Prefer specific mapping annotations over generic `@RequestMapping` when possible.
- Prefer DTOs for request and response boundaries.
- Keep controllers thin.
- Keep transaction boundaries in the service layer.
- Use `@ConfigurationProperties` for grouped settings.
- If a feature feels magical, ask whether it is proxy-based.
