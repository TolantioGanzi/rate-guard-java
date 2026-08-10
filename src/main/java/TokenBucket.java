import lombok.Getter;
import lombok.Setter;

/**
 * Thread-safe implementation of the Token Bucket algorithm.
 * <p>
 * Tokens accumulate lazily over time up to a maximum {@code capacity}.
 * Refill calculations preserve leftover nanoseconds across calls to prevent
 * sub-token time truncation under high-frequency polling.
 */
public class TokenBucket {
    private final long capacity;
    private final long nanosPerToken;
    private long tokens;
    private long lastRefillNanos;
    /**
     * Constructs a new TokenBucket initialized to full capacity.
     *
     * @param capacity      maximum number of tokens the bucket can hold
     * @param nanosPerToken time required to generate a single token, in nanoseconds
     * @param nowNanos      current timestamp in nanoseconds from a monotonic clock
     */
    public TokenBucket(long capacity, long nanosPerToken, long nowNanos) {
        this.capacity = capacity;
        this.nanosPerToken = nanosPerToken;
        this.lastRefillNanos = nowNanos;
        this.tokens = capacity;
    }
    /**
     * Attempts to consume the specified number of permits from the bucket.
     *
     * @param permits  number of tokens required for the action
     * @param nowNanos current timestamp in nanoseconds
     * @return {@code true} if tokens were available and consumed; {@code false} otherwise
     */
    public synchronized boolean tryConsume(long permits, long nowNanos) {
        refill(nowNanos);
        if(tokens >= permits) {
            tokens -= permits;
            return true;
        }
        return false;
    }
    /**
     * Refills tokens based on elapsed time since the last refill event.
     */
    private void refill(long nowNanos) {
        long elapsed = nowNanos - lastRefillNanos;
        if(elapsed <= 0) {
            return;
        }
        long newTokens = elapsed / nanosPerToken;
        if (newTokens > 0) {
            tokens = Math.min(capacity, tokens + newTokens);
            // Advance lastRefillNanos
            lastRefillNanos += newTokens * nanosPerToken;
        }
    }
    public synchronized long getTokens(long nowNanos) {
        refill(nowNanos);
        return tokens;
    }
}
