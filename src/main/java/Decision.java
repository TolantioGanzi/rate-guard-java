public record Decision(
    boolean allowed,
    long TokensRemaining,
    long nanosToWait
) {
    // Helper methods
    public static Decision allow(long remaining) {
        return new Decision(true, remaining, 0L);
    }
    public static Decision reject(long remaining, long nanosToWait) {
        return new Decision(false, remaining, nanosToWait);
    }
}