import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TokenBucketTest {
    private TokenBucket tokenBucket;
    private long now;

    @BeforeEach
    void setUp() {
        now = System.nanoTime();
        tokenBucket = new TokenBucket(10, 1_000_000_000L, now);
    }

    @Test
    void tokenBucketBurstTest() {
        for(int i = 0; i < 10; i++) {
            assertTrue(tokenBucket.tryConsume(1, now), "The request should be allowed");
        }
        assertFalse(tokenBucket.tryConsume(1, now));
    }
}
