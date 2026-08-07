# RateGuard Java

A lightweight, zero-dependency Java rate limiting library implementing the Token Bucket algorithm with a Spring Web adapter.

## Key Features
* **Zero-Dependency Core (`ratelimiter-core`)**: Pure Java implementation. Lightning-fast unit tests running in milliseconds without booting a Spring context.
* **Deterministic Time Control (`NanoClock`)**: Injected monotonic clock abstractions enabling fast-forward millisecond-level concurrency testing without `Thread.sleep()`.
* **Thread-Safe**: Safe for concurrent execution under multi-threaded loads.
* **Spring Web Integration**: Simple `OncePerRequestFilter` returning HTTP `429 Too Many Requests` with RFC-compliant HTTP headers (`Retry-After`, `X-RateLimit-Limit`, `X-RateLimit-Remaining`).

## Architecture & Module Split

| Module | Description | Dependencies |
| :--- | :--- | :--- |
| `ratelimiter-core` | Core algorithms, clock abstraction, decisions | Zero dependencies |
| `ratelimiter-spring` | Servlet filter, Spring Web configuration | `spring-boot-starter-web` |

## Quick Start

```java
// Initialize clock and token bucket (10 permits capacity, refills 1 token per 100ms)
NanoClock clock = NanoClock.SYSTEM;
TokenBucket bucket = new TokenBucket(10, 100_000_000L, clock.nanos());

// Acquire a permit
Decision decision = bucket.tryConsume(1, clock.nanos());
if (decision.isAllowed()) {
    // Process request
} else {
    // Reject request
}
