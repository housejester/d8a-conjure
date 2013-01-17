package io.d8a.conjure;

public class JitterClock implements Clock {
    private Clock targetClock;
    private Jitter jitter;

    public JitterClock(Clock targetClock, int jitterLow, int jitterHigh) {
        this(targetClock, new MinMax(jitterLow, jitterHigh));
    }

    public JitterClock(Clock targetClock, Jitter jitter) {
        this.targetClock = targetClock;
        this.jitter = jitter;
    }

    @Override
    public long currentTimeMillis() {
        return targetClock.currentTimeMillis() + jitter.nextValue();
    }

    @Override
    public void sleep(long millis) {
        targetClock.sleep(millis);
    }

    @Override
    public String getName() {
        return "Jitter";
    }
}
