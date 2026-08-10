import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TokenBucketLimiterTest {

    @Test
    void testIndependentKeys() {
        MutableClock clock = new MutableClock(System.nanoTime());
        LimitPolicy policy = new LimitPolicy("test", 2, Duration.ofSeconds(10));
        TokenBucketLimiter limiter = new TokenBucketLimiter(policy, clock);

        // User A consumes all 2 tokens
        assertTrue(limiter.tryAcquire("userA").allowed());
        assertTrue(limiter.tryAcquire("userA").allowed());
        assertFalse(limiter.tryAcquire("userA").allowed());

        // User B should still get their tokens
        assertTrue(limiter.tryAcquire("userB").allowed());
        assertTrue(limiter.tryAcquire("userB").allowed());
    }

    @Test
    void testTubTokenAccumulationUnder99msPolling() {
        MutableClock clock = new MutableClock(1_000_000_000L);
        // Capacity 1, 1 token per 100ms (100,000,000 ms)
        LimitPolicy policy = new LimitPolicy("test", 1, Duration.ofMillis(100));
        TokenBucketLimiter limiter = new TokenBucketLimiter(policy, clock);

        // Empty the bucket
        assertTrue(limiter.tryAcquire("user").allowed());

        int allowedCount = 0;
        // Poll every 99ms for 10 seconds (100 steps of 99ms)
        for(int i = 0; i < 100; i++) {
            clock.advance(Duration.ofMillis(99));
            if(limiter.tryAcquire("user").allowed()) {
                allowedCount++;
            }
        }
        assertTrue(allowedCount >= 99, "Expected around 99 tokens, but got " + allowedCount);
    }
    @Test
    void testOneYearIdleRefillDoesNotOverflow() {
        MutableClock clock = new MutableClock(1_000_000_000L);
        LimitPolicy policy = new LimitPolicy("test", 10, Duration.ofSeconds(1));
        TokenBucketLimiter limiter = new TokenBucketLimiter(policy, clock);

        // Drain bucket
        for(int i = 0; i < 10; i++) {
            limiter.tryAcquire("test");
        }
        clock.advance(Duration.ofDays(365));

        for(int i = 0; i < 10; i++) {
            assertTrue(limiter.tryAcquire("test").allowed());
        }
        assertFalse(limiter.tryAcquire("test").allowed());
    }
    @Test
    void test16ThreadsExactPermitCounts() throws InterruptedException{
        int capacity = 100;
        LimitPolicy policy = new LimitPolicy("test", capacity, Duration.ofMinutes(1));
        TokenBucketLimiter limiter = new TokenBucketLimiter(policy, NanoClock.SYSTEM);

        int threadCount = 16;
        int requestPerThread = 20; // 16 * 20 = 320 requests total against capacity 100
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger allowedCount = new AtomicInteger(0);

        CountDownLatch latch = new CountDownLatch(1);

        for(int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    latch.await();
                    for (int j = 0; j < requestPerThread; j++) {
                        if (limiter.tryAcquire("concurrentUser").allowed()) {
                            allowedCount.incrementAndGet();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        latch.countDown(); // Release all 16 threads simultaneously
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(capacity, allowedCount.get());
    }
}
