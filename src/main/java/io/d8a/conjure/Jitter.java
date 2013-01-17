package io.d8a.conjure;

public interface Jitter {
    Jitter NO_JITTER = new Jitter() {
        @Override
        public long nextValue() {
            return 0;
        }
    };

    long nextValue();
}
