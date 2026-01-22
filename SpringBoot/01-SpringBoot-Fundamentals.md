# Spring Boot Fundamentals — Interview Q&A

## Spring vs Spring Boot
- **Spring Framework**: core DI container + modules.
- **Spring Boot**: opinionated defaults + auto-configuration + starters + embedded server + production-ready tooling.

### Tricky questions
**Q: What does `@SpringBootApplication` do?**
- It’s a meta-annotation combining:
  - `@SpringBootConfiguration` (a specialized `@Configuration`)
  - `@EnableAutoConfiguration`
  - `@ComponentScan`

**Q: What is auto-configuration?**
- Boot tries to configure beans based on:
  - classpath (what dependencies exist)
  - properties (`application.properties/yml`)
  - existing beans (backing off if you define your own)

**Q: What is a “starter”?**
- A curated dependency set.
- Starters reduce dependency mismatch and bring sensible transitive dependencies.

**Q: How does Spring Boot choose an embedded server?**
- Based on classpath:
  - `spring-boot-starter-web` defaults to Tomcat.
  - You can switch to Jetty/Undertow by changing dependencies.

## Externalized configuration
### Property sources (common order concept)
- command line args
- environment variables
- `application.properties` / `application.yml`
- profile-specific files

**Tip:** In Boot 3, for config data you’ll often see `spring.config.import=` usage.

### Profiles
- Activate via `spring.profiles.active=dev` (or env var `SPRING_PROFILES_ACTIVE`).

**Trick:** If you have multiple profiles, learn conflict resolution and how property overriding works.

## Bean creation basics
- Component scanning discovers stereotype annotations (`@Component`, `@Service`, `@Repository`, `@Controller`).
- `@Bean` methods define beans explicitly.

**Q: Why prefer constructor injection?**
- Immutability
- easier testing
- detects missing dependencies at startup

## Common pitfalls
- Circular dependencies (especially with constructor injection)
- Misplaced component scan base package
- Using `@Autowired` on fields (hard to test)

## Quick practical tips
- Always check effective properties via Actuator `/actuator/env` (when enabled) or logs.
- Use `--debug` to see condition evaluation report for auto-config.
