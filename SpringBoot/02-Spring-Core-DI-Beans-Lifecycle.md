# Spring Core (DI, Beans, Lifecycle) — Tricky Interview Topics

## Dependency Injection (DI)
**Q: What’s IoC?**
- Inversion of Control: container controls object creation + wiring.

**Q: Constructor vs Setter injection?**
- Constructor injection: mandatory deps, immutable.
- Setter injection: optional deps, circular refs sometimes possible (but usually a smell).

**Q: `@Autowired` required by default?**
- In Spring Framework 4.3+, a single constructor is auto-wired even without `@Autowired`.

## Bean scopes
- `singleton` (default): one bean per container.
- `prototype`: new instance each request from container.
- web scopes: `request`, `session` (web apps)

**Trick:** Singleton bean holding request-scoped state is a bug. Use scoped proxies or pass state explicitly.

## Bean lifecycle
Lifecycle callbacks:
- `@PostConstruct`
- `InitializingBean#afterPropertiesSet`
- custom init method
- `@PreDestroy`

**Trick:** Order matters; also, not all callbacks run for `prototype` destruction.

## Proxies (a huge interview area)
### JDK vs CGLIB
- JDK dynamic proxy: proxies interfaces.
- CGLIB: subclasses concrete classes.

**Trick:** `final` classes/methods can’t be proxied with CGLIB.

### AOP, `@Transactional`, `@Async`
- These features often work via proxies.

**Classic pitfall: self-invocation**
- Calling a proxied method from another method in the same class bypasses the proxy.
- Result: `@Transactional`/`@Async` might not run.

Fix patterns:
- move method to another bean
- inject the proxy (e.g., interface) and call through it
- use `AopContext` (less preferred)

## `@Configuration` vs `@Component`
**Q: Why does `@Configuration` matter?**
- Full `@Configuration` classes are proxied so `@Bean` methods return the same singleton instance.

Example pitfall:
- If you use a plain `@Component` with `@Bean` methods, calling the `@Bean` method directly can create new instances.

## `@Primary` vs `@Qualifier`
- `@Primary` sets default when multiple candidates.
- `@Qualifier` selects specific bean.

Interview tip:
- If you see “NoUniqueBeanDefinitionException”, explain both approaches.

## Circular dependencies
- Constructor injection circular refs usually fail fast.
- Field/setter injection may allow resolution but is fragile.

Good answer:
- break cycles by redesigning dependencies.

## `ApplicationContext` vs `BeanFactory`
- `BeanFactory` is minimal container.
- `ApplicationContext` adds features (events, i18n, AOP integration, etc.).

## Events
- `ApplicationEventPublisher` + `@EventListener`.
- Transactional events: `@TransactionalEventListener`.

Trick:
- Know phases like AFTER_COMMIT to avoid publishing events before DB commit.
