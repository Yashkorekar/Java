# Spring Boot Fundamentals — Interview Q&A

## Spring vs Spring Boot
- Spring Framework gives you the core container, DI, AOP, MVC, data, and integration modules.
- Spring Boot builds on top of Spring Framework and adds opinionated defaults, auto-configuration, starter dependencies, executable packaging, embedded servers, and production tooling.

Good interview line:
- Spring is the framework.
- Spring Boot is the opinionated application bootstrap layer on top of it.

## What Boot actually adds
- Auto-configuration based on classpath, properties, and existing beans.
- Starter dependencies to keep versions compatible.
- Embedded servers so the app runs as a standalone process.
- Externalized configuration.
- Actuator and production-ready integrations.
- Simple packaging for JAR-first deployment.

## What happens during `SpringApplication.run()`?
High-level startup sequence:
1. Create and prepare `SpringApplication`.
2. Build the `Environment` and load config data.
3. Create the `ApplicationContext`.
4. Register configuration classes and scanned components.
5. Apply auto-configuration conditionally.
6. Run bean factory and bean post-processors.
7. Instantiate non-lazy singletons.
8. Start the embedded web server if this is a web app.
9. Run `ApplicationRunner` / `CommandLineRunner`.
10. Publish ready events.

Interview trap:
- If the app fails before `ApplicationReadyEvent`, startup logic that depends on a fully ready application may never run.

## `@SpringBootApplication`
**Q: What does `@SpringBootApplication` do?**
- It combines:
  - `@SpringBootConfiguration`
  - `@EnableAutoConfiguration`
  - `@ComponentScan`

**Q: Is it mandatory?**
- No. You can compose the same behavior manually, but almost all Boot apps use it.

**Q: Why does package placement matter?**
- `@ComponentScan` starts from the package of the main class, so the main class is usually placed at the root package.

## Starters and dependency management
**Q: What is a starter?**
- A curated dependency set such as `spring-boot-starter-web` or `spring-boot-starter-data-jpa`.

**Q: Why is it useful?**
- It reduces version mismatch and pulls the integrations Boot expects.

**Q: Why do we often omit dependency versions?**
- Boot manages them through a BOM so the ecosystem versions stay aligned.

Pitfall:
- Overriding versions casually can break Boot-managed compatibility.

## Embedded server model
**Q: How does Spring Boot choose an embedded server?**
- `spring-boot-starter-web` uses Tomcat by default.
- You can switch to Jetty or Undertow by changing dependencies.
- `spring-boot-starter-webflux` commonly uses Netty.

**Q: Does Boot require an external Tomcat installation?**
- No. The server is usually embedded in the application process.

**Q: JAR or WAR?**
- Executable JAR is the default and simplest deployment model.
- WAR is mainly for deployment into an external servlet container, which is less common in modern Boot apps.

## Externalized configuration basics
Common property source idea:
- command line arguments
- environment variables
- system properties
- `application.properties` / `application.yml`
- profile-specific config files
- defaults inside code

Important ideas:
- Environment variables usually map with uppercase and underscores, for example `SPRING_DATASOURCE_URL`.
- YAML is more compact for nested config.
- Boot 3 projects often use `spring.config.import=` for extra config data.

## Profiles
- Activate profiles with `spring.profiles.active=dev`, environment variables, or command line arguments.
- Profiles usually represent environment differences, not business logic.

Interview traps:
- Multiple active profiles can override each other in confusing ways.
- If profile-specific files are wrong, the app may still start but behave differently than expected.

## Bean creation basics
- Component scanning discovers stereotypes such as `@Component`, `@Service`, `@Repository`, and `@Controller`.
- `@Bean` methods define beans explicitly.

**Q: Why prefer constructor injection?**
- Dependencies are explicit.
- The object can be immutable.
- Missing dependencies fail fast at startup.
- Testing is easier because you can instantiate the class directly.

## Startup hooks you should know
- `CommandLineRunner`: receives raw `String[]`-style arguments.
- `ApplicationRunner`: receives parsed `ApplicationArguments` and is often cleaner.
- `ApplicationStartedEvent`: context refreshed, but runners not finished yet.
- `ApplicationReadyEvent`: application is fully ready to serve traffic.

Rule of thumb:
- Use runners for startup tasks.
- Use `ApplicationReadyEvent` when the logic should happen only after the app is truly ready.

## Common pitfalls
- Main class is in the wrong package, so components are not scanned.
- Circular dependencies, especially with constructor injection.
- Field injection makes testing harder.
- Adding broad dependencies accidentally changes auto-configuration.
- Assuming "Spring Boot did magic" instead of tracing the actual bean and condition path.

## Fast interview answers
**Why use Spring Boot?**
- Faster setup, fewer configuration files, consistent dependency management, embedded runtime, and production tooling.

**Why is Boot popular for microservices?**
- Easy bootstrap, strong ecosystem, externalized configuration, embedded servers, and good observability/testing support.

## Quick practical tips
- Use `--debug` to see the auto-configuration condition report.
- Check effective properties with Actuator or startup logs.
- If a bean is missing, first check scan path, profile, and conditional configuration.
