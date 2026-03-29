# Web / REST / Validation / Exception Handling — Interview Q&A

## Request flow you should be able to explain
Typical MVC flow:
1. Request reaches the servlet container.
2. `DispatcherServlet` receives it.
3. `HandlerMapping` selects the controller method.
4. `HandlerAdapter` invokes the method.
5. Arguments are resolved from path, query, headers, body, and model.
6. Return value is converted by `HttpMessageConverter` or view resolution.
7. Response is written back.

Interview advantage:
- If you can explain this flow calmly, many follow-up questions become much easier.

## Controllers
- `@Controller` is usually for MVC views.
- `@RestController` is `@Controller` + `@ResponseBody`, so return values become HTTP response bodies.

**Trick:** Content negotiation and request/response conversion depend on message converters on the classpath.

## Request mapping edge cases
- `@RequestParam` is for query parameters.
- `@PathVariable` is for path segments.
- Optional params can use `required = false` or `Optional<T>`.
- `@RequestBody` needs a matching `HttpMessageConverter` such as Jackson for JSON.
- `@ModelAttribute` binds form-style request data to an object.

### Important mapping annotations
- `@RequestMapping`: generic mapping with `method`, `consumes`, and `produces`.
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`: method-specific shortcuts.

### Important binding annotations
- `@PathVariable`: bind `/users/{id}`.
- `@RequestParam`: bind query params like `?page=0&size=10`.
- `@RequestBody`: bind JSON/XML body.
- `@RequestHeader`: bind a header.
- `@CookieValue`: bind a cookie.
- `@RequestPart`: bind multipart request parts.

### Response shaping
- `@ResponseStatus`: fixed HTTP status.
- `ResponseEntity<T>`: explicit status + headers + body.

Tip:
- See [../Advanced/03-Annotations-And-Mappings-CheatSheet.md](../Advanced/03-Annotations-And-Mappings-CheatSheet.md) for the quick lookup sheet.

## `HttpMessageConverter`
What it does:
- Converts request bodies into Java objects and Java objects into response bodies.

Common example:
- Jackson handles JSON serialization/deserialization when the proper dependencies are present.

Interview trap:
- `415 Unsupported Media Type` often means the `Content-Type` does not match what the controller expects, or the required converter is missing.

## Common HTTP errors to explain well
- `400 Bad Request`: binding failed, validation failed, or the body is unreadable.
- `401 Unauthorized`: authentication is missing or invalid.
- `403 Forbidden`: the user is authenticated but not allowed.
- `404 Not Found`: route or resource is missing.
- `405 Method Not Allowed`: route exists but the HTTP method is wrong.

## Validation
- Use `jakarta.validation` annotations such as `@NotNull`, `@Size`, `@Email`, and `@Positive`.
- Trigger validation with:
  - `@Valid` on request objects
  - `@Validated` for method-level validation or validation groups

Important detail:
- `@Valid` on `@RequestBody` validates payload fields.
- `BindingResult` can capture validation errors without throwing immediately.

## Exception handling
- Use `@ControllerAdvice` or `@RestControllerAdvice` with `@ExceptionHandler`.
- A common base class is `ResponseEntityExceptionHandler`.

Boot 3 note:
- `ProblemDetail` is the modern built-in error representation worth knowing.

**Good error response structure:**
- timestamp
- path
- error code
- message
- validation details
- trace id if you use distributed tracing

Pitfalls:
- Returning `200 OK` with an error message in the body.
- Leaking stack traces or internal implementation details.

## CORS
What it is:
- Cross-Origin Resource Sharing controls whether browsers can call your API from another origin.

Interview traps:
- CORS is enforced by browsers, not by server-to-server calls.
- `OPTIONS` preflight requests are part of the flow for many cross-origin requests.

## Filters vs Interceptors vs AOP
- Filter: servlet layer, before Spring MVC dispatch.
- Interceptor: Spring MVC handler execution layer.
- AOP: method-level cross-cutting around beans.

Rule of thumb:
- Authentication and raw request wrapping often belong in filters.
- Controller-specific request timing or correlation often fits interceptors.
- Service-level cross-cutting concerns fit AOP.

## Serialization pitfalls
- Returning JPA entities directly can trigger lazy-loading issues, deep object graphs, or unstable response shapes.

Fix patterns:
- DTOs
- fetch joins or projections
- explicit mapping layer

## `OpenEntityManagerInView`
- Historically, it keeps the persistence context open into the web layer.

Interview perspective:
- It can hide lazy-loading issues.
- It can also cause N+1 queries and surprise database access in controllers.
- A strong answer is to prefer clear transaction boundaries and DTO-based responses.

## Strong interview close
If asked how to build a good Spring Boot API:
- keep controllers thin
- validate inputs explicitly
- return DTOs, not entities
- centralize exception handling
- use consistent error responses
- understand the request pipeline instead of treating MVC as black box magic
