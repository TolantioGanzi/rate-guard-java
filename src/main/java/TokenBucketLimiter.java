import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketLimiter implements RateLimiter{
    private ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final LimitPolicy policy;
    private final NanoClock clock;

    public TokenBucketLimiter(LimitPolicy policy, NanoClock clock) {
        this.policy = policy;
        this.clock = clock;
    }
    public TokenBucketLimiter(LimitPolicy policy) {
        this(policy, NanoClock.SYSTEM);
    }
    @Override
    public Decision tryAcquire(String key, int permits) {
        long now = clock.nanos();
        TokenBucket bucket = buckets.computeIfAbsent(key, k -> new TokenBucket(policy.capacity(), policy.nanosPerToken(), now));
        boolean consume = bucket.tryConsume(permits, now);
        if(consume) {
            return Decision.allow(bucket.getTokens(now));
        }
        return Decision.reject(bucket.getTokens(now), policy.nanosPerToken());
    }
}
