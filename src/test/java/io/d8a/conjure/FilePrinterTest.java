package io.d8a.conjure;

import org.testng.annotations.Test;

import java.io.*;
import java.util.Random;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
public class FilePrinterTest {
    private static final Random RAND = new Random();

    public void shouldPrintToFile() throws IOException {
        File tmp = File.createTempFile(FilePrinterTest.class.getSimpleName(), ".txt");
        FilePrinter printer = new FilePrinter(tmp);
        String[] generated = genRandomStrings(10);
        for(String str : generated){
            printer.print(str);
        }
        printer.close();

        BufferedReader in = new BufferedReader(new FileReader(tmp));
        for(int i=0; i<generated.length; i++){
            assertEquals(in.readLine(), generated[i]);
        }
    }

    @Test(expectedExceptions = FileNotFoundException.class)
    public void shouldDieOnFileNotWritable() throws FileNotFoundException {
        FilePrinter bad = new FilePrinter(new File("/foo.txt"));
    }

    public void shouldSilentlyFailOnWriteAfterClose() throws IOException {
        // Just behave like PrintStream here.
        File tmp = File.createTempFile(FilePrinterTest.class.getSimpleName(), ".txt");
        FilePrinter printer = new FilePrinter(tmp);
        printer.print("test");
        printer.close();
        printer.print("test2");
        printer.close();

        BufferedReader in = new BufferedReader(new FileReader(tmp));
        assertEquals(in.readLine(), "test");
        assertTrue( in.readLine() == null );
    }

    private String[] genRandomStrings(int count) {
        String[] strings = new String[count];
        for(int i=0;i<count;i++){
            strings[i] = "" + RAND.nextInt(Integer.MAX_VALUE);
        }
        return strings;
    }
}
