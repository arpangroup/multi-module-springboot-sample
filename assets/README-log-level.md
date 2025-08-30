## 📝 Should I Use warn or error Log Level for Exceptions?

### ✅ General Rule
- Use warn for expected or client-triggered issues.
- Use error for unexpected system failures or bugs.

### ⚠️ When to Use warn
- The exception is **caused by invalid client input** (e.g. validation errors).
- It’s a **recoverable** situation and doesn’t affect system stability.
- You want to log a warning for visibility, but **no immediate investigation is required**.


### Example
````java
@ExceptionHandler(ValidationException.class)
public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex, WebRequest request) {
    log.warn("ValidationException caught: errorCode={}, message={}, path={}",
            ex.getErrorCode(),
            ex.getMessage(),
            request.getDescription(false),
            ex
    );

    // Build response...
}
````

### ❌ When to Use error
- The exception is unexpected, possibly due to a bug or system failure.
- It indicates a dependency failure, null pointer, data corruption, etc.
- It requires attention or alerting (e.g. via monitoring systems).

### Example
````java
@ExceptionHandler(RuntimeException.class)
public ResponseEntity<ErrorResponse> handleUnexpected(RuntimeException ex, WebRequest request) {
    log.error("Unhandled exception occurred at path={}", request.getDescription(false), ex);

    // Build response...
}
````

### Ex1: MethodArgumentNotValidException
What should be the proper log level?
````java
handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    log.error("MethodArgumentNotValidException: ...", ex);
}
````
**Is** `error` **appropriate**?
- This is caused by invalid method arguments from client input (e.g., failed `@Valid` annotations).
- It's not a server error but a client mistake.
- Generally, these should be logged as `warn` (or even `info` depending on your policy), because it indicates invalid user input rather than a server failure.
- Using `error` here can flood error logs with client mistakes.


### Ex2: HttpMessageNotReadableException 
What should be the proper log level?
````java
handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    log.error("HttpMessageNotReadableException: ...", ex);
}
````
**Is** `error` **appropriate**?
- This means the client sent malformed JSON or missing body.
- Like the above, this is a client input error, not an internal server error.
- Logging it as `warn` is often better than `error` unless it happens so frequently or causes other failures.




### 🧠 Summary Table
| Scenario                       | Log Level | Reason                              |
|--------------------------------|-----------| ----------------------------------- |
| Client validation failure      | `warn`    | Expected, recoverable issue         |
| Business rule not satisfied    | `warn`    | Controlled rejection                |
| Null pointer, DB failure, etc. | `error`   | Internal error, needs investigation |
| External service unavailable   | `error`   | Unexpected system issue             |
| ValidationException            | `warn`    | Expected client-side validation error             |
| MethodArgumentNotValidException            | `warn` (not `error`)   | Client-side input validation error             |
| HttpMessageNotReadableException            | `warn` (not `error`)   | Malformed or missing request body from client             |


| Exception / Handler                                                | Log Level | Reasoning / Explanation                                                                                                        |
| ------------------------------------------------------------------ | --------- | ------------------------------------------------------------------------------------------------------------------------------ |
| **handleMethodArgumentNotValid (MethodArgumentNotValidException)** | `warn`    | Input validation error from client; expected and common, so warn to highlight but not error.                                   |
| **handleHttpMessageNotReadable (HttpMessageNotReadableException)** | `warn`    | Client sent malformed or missing body; expected client error, not server failure.                                              |
| **handleDataIntegrityViolation (DataIntegrityViolationException)** | `warn`    | Database constraint violation usually caused by client data issues (duplicates, nulls); important but not system failure.      |
| **handleAllExceptions (UserValidationException)**                  | `warn`    | User input validation failure; expected client error, so warn level is appropriate.                                            |
| **handleUserCreateException (UserCreateException)**                | `error`   | User creation failures may indicate internal bugs or logic errors; should be elevated to error to get attention and alerts.    |
| **handleIllegalArgumentException (IllegalArgumentException)**      | `warn`    | Usually due to invalid arguments passed by client; warn level appropriate for client-induced errors.                           |
| **handleAuthenticationException (AuthenticationException)**        | `warn`    | Authentication failures expected as part of normal flow (bad credentials); warn level to alert but not error.                  |
| **handleValidationException (ValidationException)**                | `warn`    | Business rule validation failure, expected but important; warn level fits.                                                     |
| **handleRuntimeException (RuntimeException)**                      | `error`   | Unexpected runtime exceptions often indicate server issues; log as error to alert ops and developers.                          |
| **handleGenericException (Exception)**                             | `error`   | Catch-all for any unhandled exceptions; logs unexpected server-side failures as error to ensure visibility and quick response. |

**Why not error for all?** <br/>
Using `error` for every exception would overwhelm your logs with noise, especially from common client errors (validation, auth failures). `warn` is a good middle ground that highlights issues needing attention without flooding error monitoring.


---


## Log Structure
### ✅ Current Log (Baseline)
````java
log.warn("ValidationException caught: errorCode={}, message={}, path={}",
        ex.getErrorCode(),
        ex.getMessage(),
        request.getDescription(false),
        ex
);
````

## 🔧 Suggested Improvements
### 1. Add Request Context or Trace ID (if available)
If you use a **logging context (MDC)** or tracing (e.g., OpenTelemetry, Sleuth), include a **trace ID** or **request ID** for easier correlation across services.
````java
log.warn("ValidationException: traceId={}, errorCode={}, message={}, path={}",
        MDC.get("traceId"),
        ex.getErrorCode(),
        ex.getMessage(),
        request.getDescription(false),
        ex
);
````
> ✅ Requires MDC context to be populated at request entry (e.g., via filter/interceptor).

### 2. Use Structured Logging (Optional but Recommended)
If you're sending logs to a platform like **ELK**, **Datadog**, or **Splunk**, structured logging (key-value pairs) improves queryability:

````java
log.warn("ValidationException caught",
    kv("traceId", MDC.get("traceId")),
    kv("errorCode", ex.getErrorCode()),
    kv("message", ex.getMessage()),
    kv("path", request.getDescription(false)),
    kv("exception", ex)
);
````
> Replace kv(...) with your logging lib’s support or use a JSON logger.

### 3. Avoid Duplicating Info in Stack Trace
If you're logging the full exception (`ex`) as the **last argument**, the stack trace is already included — avoid re-logging message fields unnecessarily.

So this is preferred:
````java
log.warn("ValidationException: errorCode={}, path={}", ex.getErrorCode(), request.getDescription(false), ex);
````

### 4. Add User/Request Metadata (Optional)
If you can extract user ID, IP, or session info, include that too:
````java
log.warn("ValidationException for userId={}, errorCode={}, path={}", userId, ex.getErrorCode(), request.getDescription(false), ex);
````

---

## ✨ Final Recommended Version
Assuming trace ID is available in MDC and you want clarity with minimal duplication:

````java
log.warn("ValidationException: traceId={}, errorCode={}, path={}",
        MDC.get("traceId"),
        ex.getErrorCode(),
        request.getDescription(false),
        ex
);
````

## 🧠 TL;DR
| Improvement Area         | Why It Helps                                         |
| ------------------------ | ---------------------------------------------------- |
| Add trace/request ID     | Correlate logs across services                       |
| Use structured logging   | Makes logs queryable in observability tools          |
| Avoid redundant details  | Don’t log message+exception when stack trace logs it |
| Add user/request context | Helps debug user-specific issues faster              |


---

## How to create structured logging
Creating **structured logging** means logging in a consistent, machine-parsable format — usually **key-value pairs**, often as **JSON** — to make it easier to search, filter, and analyze logs using tools like ELK Stack (Elasticsearch + Logstash + Kibana), Datadog, Splunk, etc.

### ✅ 1. Why Structured Logging?
- Easier filtering: e.g., errorCode=INVALID_INPUT
- Better correlation across services (via traceId, userId)
- Cleaner dashboards and alerts
- Works seamlessly with centralized logging systems


## ⚙️ 2. How to Implement Structured Logging (Java / SLF4J + Logback)
### 🔸 Option A: Use Key-Value in Log Message (Basic Structured Format)
You can manually format key-values:
````java
log.warn("ValidationException occurred: errorCode={}, path={}, userId={}", 
    ex.getErrorCode(), 
    request.getDescription(false), 
    userId, 
    ex
);
````
This isn't full JSON but is semi-structured and usable by many log parsers.

### 🔸 Option B: Use JSON Logging via Logback
1. **Add dependency: Use [Logstash Logback Encoder](https://github.com/logfurniture/logstash-logback-encoder)**
````xml
<!-- In your pom.xml -->
<dependency>
  <groupId>net.logstash.logback</groupId>
  <artifactId>logstash-logback-encoder</artifactId>
  <version>7.4</version>
</dependency>
````
2. **Configure Logback** (`logback-spring.xml` or `logback.xml`):
````xml
<configuration>
  <appender name="JSON_CONSOLE" class="net.logstash.logback.appender.LoggingEventCompositeJsonEncoder">
    <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
      <providers>
        <timestamp />
        <loggerName />
        <threadName />
        <logLevel />
        <mdc /> <!-- Include MDC (e.g. traceId, userId) -->
        <message />
        <stackTrace />
      </providers>
    </encoder>
    <target>System.out</target>
  </appender>

  <root level="INFO">
    <appender-ref ref="JSON_CONSOLE"/>
  </root>
</configuration>
````

Now, logs are emitted in structured JSON like:

````json
{
  "timestamp": "2025-08-03T12:00:00Z",
  "level": "WARN",
  "logger": "com.example.MyClass",
  "message": "ValidationException occurred",
  "mdc": {
    "traceId": "abc-123",
    "userId": "42"
  },
  "stack_trace": "..."
}
````

3. 🔑 **Add Context with MDC (Mapped Diagnostic Context)**
MDC allows you to attach context (e.g. `userId`, `traceId`) to all logs within a thread/request.
````java
import org.slf4j.MDC;

MDC.put("userId", user.getId());
MDC.put("traceId", UUID.randomUUID().toString());
log.warn("Validation failed", ex);
MDC.clear(); // Important to clear after request completes
````
> If you use Spring Boot, you can configure this automatically with a filter/interceptor.


4. 🧪 **Structured Logging Libraries (Alternatives)**

| Library                       | Purpose                              |
| ----------------------------- | ------------------------------------ |
| `logstash-logback-encoder`    | JSON logs with Logback (most common) |
| `log4j2-layout-template-json` | JSON logs with Log4j2                |
| `Tinylog`                     | Lightweight structured logging       |
| `Flogger` (Google)            | Flexible structured logging          |


---

## 🧠 Example: Final Logging Line with MDC
````java
MDC.put("userId", userId);
MDC.put("traceId", traceId);
log.warn("ValidationException: errorCode={}, path={}", ex.getErrorCode(), request.getDescription(false), ex);
MDC.clear();
````



---

## A Sample Spring Boot project

### Step 1: Add MDC Filter
**🔸 Create LoggingMdcFilter.java**
````java
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Component
public class LoggingMdcFilter implements Filter {

    private static final String TRACE_ID = "traceId";
    private static final String USER_ID = "userId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            // Generate or extract traceId
            String traceId = UUID.randomUUID().toString();
            MDC.put(TRACE_ID, traceId);

            // Optional: Extract userId from request or security context
            String userId = extractUserId((HttpServletRequest) request);
            if (userId != null) {
                MDC.put(USER_ID, userId);
            }

            chain.doFilter(request, response);
        } finally {
            MDC.clear(); // Prevent MDC leak across threads
        }
    }

    private String extractUserId(HttpServletRequest request) {
        // Example: extract from JWT, header, or session
        // return request.getHeader("X-User-Id");
        return null; // Replace with real extraction logic
    }
}
````

**🧪 Example Log Output (with JSON encoder)**
````json
{
  "timestamp": "2025-08-03T13:12:00Z",
  "level": "WARN",
  "message": "ValidationException caught",
  "mdc": {
    "traceId": "1b6f1e8c-3f25-45d7-8f3f-24cf3fda3b6c",
    "userId": "42"
  },
  "stack_trace": "..."
}
````

### 📌 Notes
- This filter ensures **every log in the request lifecycle** includes `traceId` and optionally `userId`.
- If you're using Spring **Security with JWT**, you can extract user ID from the token using `SecurityContextHolder`.

### 🧰 Optional: Extract userId from Spring Security
If you use Spring Security (e.g., with JWT), modify `extractUserId(...)`:
````java
private String extractUserId(HttpServletRequest request) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.isAuthenticated()) {
        return auth.getName(); // Or extract from JWT claims
    }
    return null;
}
````






















