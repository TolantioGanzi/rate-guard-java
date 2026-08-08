public record LimitPolicy(
    String name,
    long capacity,
    java.time.Duration refillPeriod
) {
    /**
     *
     * @return how many nanoseconds it takes to produce one token
     */
    public long nanosPerToken() {
        return refillPeriod.toNanos() / capacity;
    }
}
