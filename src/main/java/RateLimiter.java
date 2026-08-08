public interface RateLimiter {
    /**
     * Tries to consume requested tokens at given timestamp
     *
     * @param tokensRequested Number of tokens requested (usually 1)
     * @param currentNanos current timestamp in nanoseconds from NanoClock
     * @return Decision indicating whether allowed, remaining tokens, and wait time
     */
    Decision tryConsume(long tokensRequested, long currentNanos);
    /**
     *
     * @param key unique identifier of user making request
     * @param permits how many tokens requested
     * @return a Decision object indicating if the user request was allowed or denied
     */
    Decision tryAcquire(String key, int permits);

    /**
     *
     * @param key unique identifier of user making request
     * @return a Decision object indicating if the user request was allowed or denied
     */
    default Decision tryAcquire(String key) {
        return tryAcquire(key, 1);
    }
}
