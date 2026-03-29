# Spring Security — Interview and Practical Notes

## Why this topic matters
Spring Security is one of the highest-frequency Spring Boot interview areas because it forces you to understand filters, configuration, stateless APIs, and authorization decisions.

## Authentication vs authorization
- Authentication answers: who are you?
- Authorization answers: what are you allowed to do?

Good interview answer:
- Authentication happens first.
- Authorization is evaluated after identity is established.

## What happens when you add `spring-boot-starter-security`?
- Spring Boot secures the app by default.
- Most endpoints require authentication.
- A default generated password is logged for the default user unless you define your own setup.

Interview trap:
- People add the starter and then wonder why every endpoint suddenly returns `401` or redirects to login.

## Modern Boot 3 configuration style
Old approach:
- `WebSecurityConfigurerAdapter` is deprecated and removed from normal modern usage.

Modern approach:
- Define a `SecurityFilterChain` bean.

Example shape:

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/health", "/public/**").permitAll()
            .anyRequest().authenticated()
        )
        .httpBasic(Customizer.withDefaults())
        .build();
}
```

## Filter chain mental model
Important idea:
- Spring Security works mostly through a servlet filter chain.

What to say:
- Requests pass through security filters before controller code runs.
- Authentication, session handling, CSRF checks, exception translation, and authorization all happen in that chain.

## Stateful vs stateless security
### Session-based
- Server stores security context in session.
- Common in browser apps with login forms.

### Stateless token-based
- Server does not rely on HTTP session for each request.
- Common for REST APIs using JWT or opaque bearer tokens.

Interview trap:
- Stateless does not mean no security context exists. It means it is rebuilt per request rather than loaded from an HTTP session.

## JWT basics
What a good answer includes:
- JWT is a signed token containing claims.
- The server typically validates signature, expiration, issuer, and audience.
- JWT is not automatically secure just because it is a token.

Common pitfalls:
- Storing too much sensitive data in the token.
- Not validating expiration or issuer.
- Treating logout as trivial in purely stateless setups.

## OAuth2 / OIDC high-level interview answer
- OAuth2 is an authorization framework.
- OpenID Connect adds authentication on top.
- In Spring Boot, common real setups are OAuth2 login for browser apps and resource server mode for bearer token validation.

If asked what the app is:
- client app
- authorization server
- resource server

## Password encoding
Rule:
- Never store plain text passwords.
- Use a `PasswordEncoder`, usually BCrypt in common examples.

Interview trap:
- Hashing without a proper adaptive password encoder is not enough.

## CSRF
What it is:
- Cross-Site Request Forgery is mainly a browser/session concern.

Good answer:
- CSRF protection matters when the browser automatically sends credentials such as session cookies.
- For stateless token-based APIs that do not rely on browser cookies, CSRF is often disabled deliberately.

Trap:
- Do not say "always disable CSRF". That is wrong.

## CORS and security
- CORS is a browser-enforced cross-origin policy.
- It is not the same thing as authentication or authorization.

Common confusion:
- Fixing CORS does not make the API secure.
- Failing CORS does not mean the user is unauthorized.

## Authorization
Common styles:
- URL/path-based rules in the `SecurityFilterChain`
- method security with `@PreAuthorize`, `@PostAuthorize`, and similar annotations

Boot 3 direction:
- `@EnableMethodSecurity` is the common method security entry point.

## Method security
Examples to know:
- `@PreAuthorize("hasRole('ADMIN')")`
- `@PreAuthorize("#id == authentication.name")`

Interview warning:
- Method security is powerful, but hidden SpEL-heavy rules can become hard to maintain.

## Exception behavior
- `401 Unauthorized`: user is not authenticated or token is invalid.
- `403 Forbidden`: user is authenticated but lacks permission.

This distinction is asked often.

## Common Spring Security interview pitfalls
- Confusing authentication with authorization.
- Disabling CSRF without understanding why.
- Exposing actuator endpoints without restriction.
- Forgetting password encoding.
- Mixing session-based and stateless expectations in the same API design.
- Writing security rules only at the controller layer and forgetting service-level access rules.

## Good practical defaults
- Keep public endpoints explicit.
- Secure everything else by default.
- Use method security for sensitive business operations.
- Keep tokens small and validated strictly.
- Avoid logging credentials, tokens, or personal data.