package io.d8a.conjure;

import com.google.common.base.Throwables;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

//Queue printer is used to print event objects from conjure directly to a queue. Prevents need for serializing and deserealizing.
public class QueuePrinter<T> implements Printer<T> {
    private final BlockingQueue<T> queue;
    private final long waitTime;
    private final TimeUnit unit;

    public QueuePrinter(BlockingQueue queue, long waitTime, TimeUnit unit) {
        this.waitTime = waitTime;
        this.unit = unit;
        this.queue = queue;
    }

    @Override
    public void print(T message) {
        try {
            queue.offer(message, waitTime, unit);
        } catch (InterruptedException e) {
            throw Throwables.propagate(e);
        }
    }

}
