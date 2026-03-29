# Spring Core (DI, Beans, Lifecycle) — Tricky Interview Topics

## Dependency Injection (DI)
**Q: What is IoC?**
- Inversion of Control means object creation and wiring are delegated to the container instead of being done manually throughout the codebase.

**Q: Constructor vs setter injection?**
- Constructor injection: best for mandatory dependencies.
- Setter injection: acceptable for optional dependencies, but often a design smell if overused.

**Q: Is `@Autowired` required on constructors?**
- No. With a single constructor, Spring autowires it automatically.

Good interview answer:
- Prefer constructor injection because it makes dependencies explicit, supports immutability, and fails fast.

## Bean scopes
- `singleton`: one bean instance per container.
- `prototype`: a new bean instance each time it is requested.
- Web scopes: `request`, `session`, `application`, `websocket`.

Trap:
- A singleton holding request-specific mutable state is a thread-safety bug.

Fix patterns:
- pass request state explicitly
- use request-scoped beans carefully
- inject providers or scoped proxies when necessary

## Bean lifecycle
Typical lifecycle flow:
1. Instantiate bean.
2. Inject dependencies.
3. Apply `Aware` callbacks if implemented.
4. Run `BeanPostProcessor` before-init hooks.
5. Run init callbacks such as `@PostConstruct`.
6. Run `BeanPostProcessor` after-init hooks.
7. Use bean normally.
8. Run destroy callbacks for singleton beans on shutdown.

Common lifecycle callbacks:
- `@PostConstruct`
- `InitializingBean#afterPropertiesSet`
- custom init method
- `@PreDestroy`
- custom destroy method

Important trap:
- Prototype beans are created by the container, but destruction is usually not managed automatically.

## `BeanPostProcessor` vs `BeanFactoryPostProcessor`
- `BeanFactoryPostProcessor` changes bean definitions before beans are instantiated.
- `BeanPostProcessor` intercepts actual bean instances before and after initialization.

Interview shortcut:
- If someone asks "where do proxies get attached?", `BeanPostProcessor` is the more relevant answer.

## Proxies (a huge interview area)
### JDK vs CGLIB
- JDK dynamic proxy proxies interfaces.
- CGLIB subclasses concrete classes.

Traps:
- `final` classes or `final` methods cannot be proxied effectively with subclass-based proxying.
- Private methods are not intercepted by Spring AOP proxies.

### AOP, `@Transactional`, `@Async`, caching
- These features usually work through proxies.

**Classic pitfall: self-invocation**
- Calling a proxied method from another method in the same class bypasses the proxy.
- Result: `@Transactional`, `@Async`, caching, or security advice may not run.

Fix patterns:
- move the method to another bean
- inject the proxied bean and call through it
- use `AopContext` only if you really understand the tradeoff

## `@Configuration` vs `@Component`
**Q: Why does `@Configuration` matter?**
- Full `@Configuration` classes are enhanced so repeated `@Bean` method calls still return the managed bean.

Example pitfall:
- A plain `@Component` with `@Bean` methods does not give the same configuration semantics when methods call each other directly.

## Dependency selection
- `@Primary`: default candidate.
- `@Qualifier`: choose a specific bean.
- `@Lazy`: defer creation or break some startup-time dependency issues.
- `ObjectProvider<T>`: retrieve beans lazily or optionally.

Interview tip:
- If you see `NoUniqueBeanDefinitionException`, explain both `@Qualifier` and `@Primary`, then mention `ObjectProvider` for optional or lazy access.

## Circular dependencies
- Constructor injection cycles fail fast, which is usually good.
- Setter/field injection may sometimes allow the app to start, but the design is still weak.

Good answer:
- The real fix is redesigning the dependency graph, not hiding the cycle.

## `ApplicationContext` vs `BeanFactory`
- `BeanFactory` is the minimal container.
- `ApplicationContext` adds events, internationalization, environment abstraction, resource loading, and better integration with Spring infrastructure.

In practice:
- Most real applications use `ApplicationContext`, not `BeanFactory` directly.

## Events
- `ApplicationEventPublisher` publishes events.
- `@EventListener` consumes them.
- `@TransactionalEventListener` allows phases such as `AFTER_COMMIT`.

Why `AFTER_COMMIT` matters:
- You usually do not want to publish "order created" side effects before the database transaction is actually committed.

## Advanced but common follow-ups
- `FactoryBean`: a bean that creates another object for the container.
- `@DependsOn`: enforce initialization ordering when really necessary.
- `SmartLifecycle`: useful for start/stop coordination in infrastructure beans.
- Injecting prototype into singleton directly does not create a fresh prototype per call unless you use a provider pattern.
