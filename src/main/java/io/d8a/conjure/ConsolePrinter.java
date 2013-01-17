package io.d8a.conjure;

public class ConsolePrinter implements Printer{
    @Override
    public void print(String message) {
        System.out.println(message);
    }

    @Override
    public String getName() {
        return "Console";
    }
}
