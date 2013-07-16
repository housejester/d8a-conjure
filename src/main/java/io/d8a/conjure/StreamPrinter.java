package io.d8a.conjure;

import java.io.PrintStream;

/**
 * Created: 4/19/13 10:15 AM
 */
public class StreamPrinter implements Printer {
    private PrintStream out;

    public StreamPrinter(PrintStream out) {
        this.out = out;
    }

    @Override
    public void print(Object message) {
        out.println(message);
        out.checkError();
    }

    public void close() {
        out.flush();
        out.close();
    }

    @Override
    public String toString() {
        return "StreamPrinter";
    }
}
