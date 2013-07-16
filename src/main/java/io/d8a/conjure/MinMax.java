package io.d8a.conjure;

import java.util.Random;

public class MinMax implements Jitter{
    private long base;
    private long count;
    private static final Random RAND = new Random();

    public MinMax(long min, long max){
        base = min;
        count = (max - min) + 1;
    }

    public long nextValue(){
        return base + (long) (RAND.nextDouble() * count);
    }

}
