public interface RateLimiter {
    /**
     * Tries to consume requested tokens at given timestamp
     *
     * @param tokensRequested Number of tokens requested (usually 1)
     * @param currentNanos current timestamp in nanoseconds from NanoClock
     * @return Decision indicating wheather allowed, remaining tokens, and wait time
     */
    Decision tryConsume(long tokensRequested, long currentNanos);
}
