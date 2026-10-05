# Audit Logging Spring Boot Starter

A custom Spring Boot Starter for automatic audit logging of incoming HTTP requests. It intercepts requests, measures response times, logs request details (like URI, method, and status), and can warn you if requests exceed a specified latency threshold.

## Requirements
- Java 25 (as configured in the project)
- Spring Boot 4.1.1+

## How to Install and Download

Since this project is a custom starter, you need to build and install it to your local Maven repository before using it in other projects.

1. Clone or download this repository.
2. Navigate to the root directory of the starter (`audit-logging-spring-boot-starter`).
3. Run the following Maven command to install it locally:
   ```bash
   mvn clean install
   ```

4. Once installed, add the dependency to the `pom.xml` of your target Spring Boot application:
   ```xml
   <dependency>
       <groupId>com.help-bridge</groupId>
       <artifactId>audit-logging-spring-boot-starter</artifactId>
       <version>0.0.1-SNAPSHOT</version>
   </dependency>
   ```

*(Note: If you plan to host this project on GitHub, users can also use [JitPack](https://jitpack.io/) to import it directly without a local installation.)*

## Properties and Settings

You can customize the behavior of the audit logger by adding properties to your `application.yml` or `application.properties` file. All properties are prefixed with `helpbridge.audit`.

| Property | Type | Default | Description |
| :--- | :--- | :--- | :--- |
| `helpbridge.audit.enabled` | `boolean` | `false` | Enables or disables the audit logging interceptor. |
| `helpbridge.audit.slow-threshold-ms` | `long` | none | The threshold in milliseconds. If the response takes longer than this value, a warning is logged. |
| `helpbridge.audit.include.client-ip` | `boolean` | `false` | Includes the client IP address in the log output. |
| `helpbridge.audit.include.user-agent` | `boolean` | `false` | Includes the User-Agent header in the log output. |
| `helpbridge.audit.include.handler-method` | `boolean` | `false` | Includes the executed controller method name. |
| `helpbridge.audit.include.query-params` | `boolean` | `false` | Includes request query parameters. |
| `helpbridge.audit.include.headers` | `boolean` | `false` | Includes all HTTP request headers. |
| `helpbridge.audit.include.thread-name` | `boolean` | `false` | Includes the name of the executing thread. |

### Example `application.yml`:
```yaml
helpbridge:
  audit:
    enabled: true
    slow-threshold-ms: 500
    include:
      client-ip: true
      user-agent: true
      query-params: true
```

## Output Format

The audit logger uses SLF4J to log the results. The default log level for standard audit logs is `INFO`, and `WARN` for slow responses.

The log format consists of an `[AUDIT]` prefix followed by a map (key-value pairs) of the context attributes.

**Standard Log Example (`INFO`):**
```text
[AUDIT] Audit results: {responseTime=42, method=GET, uri=/api/users, status=200, clientIp=127.0.0.1, userAgent=Mozilla/5.0...}
```

**Slow Response Log Example (`WARN`):**
```text
Response time is slower than threshold set [threshold = 500, actual = 650]
[AUDIT] Audit results: {responseTime=650, method=POST, uri=/api/data, status=201, clientIp=192.168.1.10}
```

**Error Log Example (`INFO`):**
If an exception occurs during request processing, the error details are automatically included in the map:
```text
[AUDIT] Audit results: {responseTime=15, error=IllegalArgumentException, errorMessage=Invalid ID, method=GET, uri=/api/items/invalid, status=500}
```
