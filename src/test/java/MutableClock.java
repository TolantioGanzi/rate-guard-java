public class MutableClock implements NanoClock{
    private long currentNanos;



    public MutableClock(long initialNanos) {
        this.currentNanos = initialNanos;
    }
    @Override
    public long nanos() {
        return currentNanos;
    }
    public void advance(java.time.Duration duration) {
        this.currentNanos += duration.toNanos();
    }
}
