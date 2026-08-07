@FunctionalInterface
public interface NanoClock {

    /**
     *
     * @return current time in nanoseconds;
     */
    long nanos();

    NanoClock SYSTEM = System::nanoTime;
}
