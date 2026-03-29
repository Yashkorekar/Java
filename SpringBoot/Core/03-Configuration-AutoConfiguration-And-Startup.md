# Configuration, Auto-Configuration, and Startup — Interview Deep Dive

## Externalized configuration model
Spring Boot expects configuration to live outside the code as much as possible.

Common sources to remember:
- command line arguments
- environment variables
- system properties
- `application.properties` / `application.yml`
- profile-specific config files
- imported config via `spring.config.import`

Good interview answer:
- Boot builds an `Environment` from multiple property sources and resolves final values by precedence.

## `application.properties` vs `application.yml`
- Properties are flat and explicit.
- YAML is easier for nested structures.

Pitfall:
- YAML indentation mistakes can silently bind the wrong structure.

## Profiles and config data
- `application-dev.yml`, `application-prod.yml`, and similar files specialize config by profile.
- `spring.profiles.active` decides which profiles are active.
- `spring.config.import` is often used for additional config files or external sources.

Interview traps:
- Multiple profiles can override the same property.
- Teams sometimes misuse profiles for feature flags instead of environment separation.

## Relaxed binding
Spring Boot can bind values across naming styles:
- `my.service.timeout`
- `my.service-timeout`
- `MY_SERVICE_TIMEOUT`

Why it matters:
- It makes environment variable mapping practical in containerized deployments.

## `@Value` vs `@ConfigurationProperties`
- `@Value` is good for one-off scalar values.
- `@ConfigurationProperties` is better for grouped, typed, validated configuration.

Strong answer:
- Prefer `@ConfigurationProperties` for anything non-trivial because it is cleaner, testable, and safer.

## Configuration validation
- Combine `@ConfigurationProperties` with Bean Validation and `@Validated`.
- Fail fast when required settings are missing or malformed.

Modern style:
- Immutable configuration objects or records are common in Boot 3 codebases.

## Auto-configuration internals
**Q: How does Boot decide what to configure?**
- It uses conditional configuration, often with annotations such as:
  - `@ConditionalOnClass`
  - `@ConditionalOnMissingBean`
  - `@ConditionalOnProperty`
  - `@ConditionalOnWebApplication`
  - `@ConditionalOnBean`

**Q: What does “back off” mean?**
- Boot supplies a default bean only if you have not already defined one yourself.

Example:
- If Boot can create a default `ObjectMapper` but you provide your own bean, Boot usually backs off.

## Debugging auto-configuration
- Start with `--debug` to see the condition evaluation report.
- Check whether the classpath contains the expected starter.
- Check active profiles and property values.
- Check whether your own bean caused the default configuration to back off.

## Excluding auto-configuration
Ways to disable specific auto-configurations:
- `@SpringBootApplication(exclude = ...)`
- `spring.autoconfigure.exclude=...`

Use carefully:
- Excluding the wrong auto-configuration can remove more than you expected.

## Startup lifecycle hooks
Useful extension points:
- `ApplicationRunner`
- `CommandLineRunner`
- `ApplicationListener`
- `ApplicationContextInitializer`
- `EnvironmentPostProcessor`

Rule of thumb:
- If you need to change configuration before the context is fully created, think earlier than a runner.

## Lazy initialization
- `spring.main.lazy-initialization=true` can speed startup but shifts failures to runtime.

Interview view:
- It is useful in some cases, but it can hide misconfiguration until traffic hits the code path.

## Common startup events
- `ApplicationStartingEvent`
- `ApplicationEnvironmentPreparedEvent`
- `ApplicationPreparedEvent`
- `ApplicationStartedEvent`
- `ApplicationReadyEvent`
- `ApplicationFailedEvent`

What to say:
- If the question is "when is the app really ready?", `ApplicationReadyEvent` is the closest answer.

## AOT and native image awareness
Boot 3 supports AOT processing and native image workflows.

Interview-safe answer:
- Native images can improve startup and memory behavior, but they change reflection and proxy assumptions, so app compatibility must be verified.

## Common “why is this not working” checklist
- Wrong profile is active.
- Property name is wrong or not bound as expected.
- Bean is not created because a condition failed.
- Component scan does not include your package.
- Your own bean caused Boot to back off.
- The starter dependency is missing or the wrong one is present.
