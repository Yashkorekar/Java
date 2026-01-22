# Web / REST / Validation / Exception Handling — Interview Q&A

## Controllers
- `@Controller` returns views (MVC)
- `@RestController` = `@Controller` + `@ResponseBody`

**Trick:** content negotiation (JSON/XML) depends on message converters on classpath.

## Request mapping edge cases
- `@RequestParam` vs `@PathVariable`
- Optional params: `required=false` or `Optional<T>`
- `@RequestBody` requires a converter (Jackson for JSON)

### Important mapping annotations (must-know)
- `@RequestMapping`: generic mapping; can set `method`, `consumes`, `produces`
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`: method-specific shortcuts

### Important binding annotations (must-know)
- `@PathVariable`: bind `/users/{id}`
- `@RequestParam`: bind query string `?page=0&size=10`
- `@RequestBody`: bind JSON/XML body (via message converters)
- `@RequestHeader`: bind headers
- `@CookieValue`: bind cookies
- `@RequestPart`: multipart/form-data parts

### Response shaping
- `@ResponseStatus`: set the HTTP status
- `ResponseEntity<T>`: set status + headers + body explicitly

Tip: See `08-Annotations-And-Mappings-CheatSheet.md` for the full list.

Pitfall:
- `415 Unsupported Media Type` usually means wrong `Content-Type` or missing converter.

Other common HTTP errors to explain in interviews:
- `400 Bad Request`: binding failed / validation failed / unreadable JSON
- `401 Unauthorized`: missing/invalid authentication
- `403 Forbidden`: authenticated but not allowed
- `404 Not Found`: no matching route or resource missing
- `405 Method Not Allowed`: route exists but wrong HTTP method

## Validation
- Use `jakarta.validation` annotations (`@NotNull`, `@Size`, ...)
- Trigger:
  - `@Valid` on controller method argument
  - `@Validated` on class/bean (for method-level validation)

**Trick:** `@Valid` on `@RequestBody` validates request payload.

## Exception handling
- Use `@ControllerAdvice` + `@ExceptionHandler`.

**Good answer structure:**
- Define a consistent error response structure:
  - timestamp
  - path
  - error code
  - message
  - validation details

Pitfalls:
- Swallowing exceptions and returning 200 OK with error string.
- Leaking stack traces or internal details.

## Filters vs Interceptors vs AOP
- Filter: servlet level; runs before dispatcher.
- Interceptor: Spring MVC, per handler.
- AOP: method-level cross-cutting.

Trick:
- For request correlation id, filter/interceptor often best.

## Serialization pitfalls
- Jackson + Hibernate lazy-loaded entities => `LazyInitializationException` or serialization issues.
- Fix patterns:
  - DTOs
  - fetch joins
  - avoid returning entities directly

## `OpenEntityManagerInView`
- Default (historically) keeps persistence context open during view rendering.

Interview perspective:
- It hides lazy loading problems but can cause N+1 and unexpected DB access in controllers.
- A strong answer: return DTOs and close transaction boundaries appropriately.
