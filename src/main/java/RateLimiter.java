public interface RateLimiter {
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
