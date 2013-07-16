package io.d8a.conjure;

import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class Conjurer implements Runnable {
    private static final Random RAND = new Random();

    private final static long waitTime = 15L;
    private final static TimeUnit unit = TimeUnit.SECONDS;
    private final Clock clock;
    private final long stopTime;
    private final Printer printer;
    private final int linesPerSec;
    private final long maxLines;
    private String filePath;
    private long count = 0;
    private ConjureTemplate template;
    private boolean customCardinalityVariablesMode = true;
    private final Thread thread = new Thread(this);


    public Conjurer(long stopTime, Printer printer, int linesPerSec, String filePath) {
        this(- 1, stopTime, printer, linesPerSec, Long.MAX_VALUE, filePath);
    }

    public Conjurer(long startTime, long stopTime, Printer printer, int linesPerSec, String filePath) {
        this(startTime, stopTime, printer, linesPerSec, Long.MAX_VALUE, filePath);
    }

    public Conjurer(long startTime, long stopTime, Printer printer, int linesPerSec, long maxLines, String filePath) {
        this(startTime, stopTime, printer, linesPerSec, maxLines, filePath, true);
    }


    public Conjurer(
            long startTime,
            long stopTime,
            Printer printer,
            int linesPerSec,
            long maxLines,
            String filePath,
            Boolean customCardinalityMode
    ) {
        this.customCardinalityVariablesMode = customCardinalityMode;
        this.stopTime = stopTime;
        this.printer = printer;
        this.linesPerSec = linesPerSec;
        this.maxLines = maxLines;
        this.filePath = filePath;
        if(startTime < 0){ //-1 means generate data moving forward.
            clock = Clock.SYSTEM_CLOCK;
        } else{
            clock = new SimulatedClock(startTime);
        }
        ConjureTemplateParser parser = new ConjureTemplateParser(clock);
        try {
            if(FilenameUtils.getExtension(filePath).equals("json")){
                this.template = parser.jsonParse(filePath);
            }
            this.template = parser.parse(new FileInputStream(filePath));
        } catch(IOException e) {
            throw new IllegalArgumentException("Could not create ConjureTemplate from " + filePath, e);
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        Options options = new Options();
        options.addOption("zk", true, "Zookeeper connection string for kafka");
        options.addOption("topic", true, "Kafka topic to send data to");
        options.addOption("template", true, "Path to the Conjure Template file that describes the data to generate");
        options.addOption("rate", true, "Lines per second");
        options.addOption("cap", true, "Total lines to generate");
        options.addOption("out", true, "Where to write the generated samples.  [file|console|kafka|none]");
        options.addOption("file", true, "Filename to write generated samples to.");
        options.addOption(
                "startTime",
                true,
                "For historical data generation.  What epoch time to start generating data from."
        );
        options.addOption("stopTime", true, "For historical data generation.  What epoch time to stop generating data.");
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse(options, args);

        String filePath = null;
        String[] appArgs = cmd.getArgs();
        if(appArgs != null && appArgs.length > 0){
            filePath = appArgs[0];
        } else{
            filePath = cmd.getOptionValue("template");
        }
        if(filePath == null){
            throw new IllegalArgumentException("Must specify a template that describes the data to be generated.");
        }

        long stopTime = Long.MAX_VALUE;
        if(cmd.hasOption("stopTime")){
            stopTime = new Long(cmd.getOptionValue("stopTime"));
        }

        long startTime = - 1;
        if(cmd.hasOption("startTime")){
            startTime = new Long(cmd.getOptionValue("startTime"));
            if(stopTime == Long.MAX_VALUE){
                stopTime = System.currentTimeMillis();
            }
        }

        Printer printer = consolePrinter();
        if(cmd.hasOption("out")){
            Set<String> outs = new HashSet<String>(Arrays.asList(cmd.getOptionValue("out").split(",")));
            List<Printer> printers = new ArrayList<Printer>();
            for(String out : outs){
                printers.add(createPrinter(out, cmd));
            }
            if(printers.size() == 1){
                printer = printers.get(0);
            } else{
                printer = new MultiPrinter(printers.toArray(new Printer[printers.size()]));
            }
        }

        int linesPerSec = 10;
        if(cmd.hasOption("rate")){
            linesPerSec = new Integer(cmd.getOptionValue("rate"));
        }

        long numLines = Long.MAX_VALUE;
//    long numLines = 5000000;

        if(cmd.hasOption("cap")){
            numLines = new Long(cmd.getOptionValue("cap"));
        }
        long start = System.currentTimeMillis();


        Conjurer conjurer = new Conjurer(startTime, stopTime, printer, linesPerSec, numLines, filePath);
        conjurer.exhaust();
        long duration = System.currentTimeMillis() - start;
        System.err
                .println("Conjurer finished.  Took " + duration + "ms to conjure up " + conjurer.getCount() + " samples.");
    }

    private static Printer createPrinter(String type, CommandLine cmd) {
        if("kafka".equals(type)){
            if(cmd.hasOption("zk") && cmd.hasOption("topic")){
                return kafkaPrinter(cmd.getOptionValue("zk"), cmd.getOptionValue("topic"));
            }
            throw new IllegalArgumentException(
                    "Must specify zookeeper connection string ('zk') and kafka topic ('topic') to write to kafka."
            );
        } else if("file".equals(type)){
            if(cmd.hasOption("file")){
                try {
                    return filePrinter(cmd.getOptionValue("file"));
                } catch(FileNotFoundException e) {
                    throw new IllegalArgumentException("Could not create file printer.", e);
                }
            }
            throw new IllegalArgumentException("Must specify file path to write to a file.");
        } else if("console".equals(type)){
            return consolePrinter();
        } else if("none".equals(type)){
            return nonePrinter();
        }
        throw new IllegalArgumentException("Printer type '" + type + "' not supported.");
    }

    private static Printer filePrinter(String fileName) throws FileNotFoundException {
        return new FilePrinter(new File(fileName));
    }

    public void exhaust() {
        thread.setDaemon(true);
        thread.start();
        try {
            thread.join();
        } catch(InterruptedException e) {
            thread.interrupt();
        }
    }

    public void run() {
        System.err.println("Conjuring data to " + printer + " at a rate of " + linesPerSec + " lines per second.");
        double linesPerMs = (double) linesPerSec / 1000;
        long start = clock.currentTimeMillis();
        long lastReport = start;
        long bytesWritten = 0L;
        String lastLinePrinted = "";
        Iterator<String> linesIterator = null;

        for(int i = 0; i < maxLines && clock.currentTimeMillis() < stopTime; i++){
            throttle(start, i, linesPerMs);
            if(Thread.currentThread().isInterrupted()){
                return;
            }
            Object event;
            if(customCardinalityVariablesMode){
                event = template.conjureMapData();
            } else{
                if(linesIterator == null || ! linesIterator.hasNext()){
                    linesIterator = conjureNextBatch();
                }
                event = linesIterator.next();
            }
            printer.print(event);
            ++ count;
            if(System.currentTimeMillis() - lastReport > 5000){
                report(start, count, lastLinePrinted, bytesWritten);
                lastReport = System.currentTimeMillis();
            }
        }
        report(start, count);

    }

    private Iterator<String> conjureNextBatch() {
        String lineVal = template.conjure();
        String[] conjureList = lineVal.split("\n");
        return Arrays.asList(conjureList).iterator();
    }


    public static Printer nonePrinter() {
        return new Printer() {
            @Override
            public void print(Object message) {
            }

            public String toString() {
                return "Blackhole";
            }
        };
    }

    private void report(long start, long linesPrinted) {
        long now = clock.currentTimeMillis();
        long duration = now - start;
        long ratePerSec = (long) (1000 * ((double) linesPrinted) / duration);
        System.err
                .println(
                        "generated "
                                + linesPrinted
                                + " lines in "
                                + duration
                                + "ms (using the "
                                + clock
                                + "), "
                                + ratePerSec
                                + "/s.  "
                );
    }

    private void report(long start, long linesPrinted, String lastLinePrinted, long bytesPrinted) {
        long now = clock.currentTimeMillis();
        long duration = now - start;
        long ratePerSec = (long) (1000 * ((double) linesPrinted) / duration);
        long bytesPerSec = (long) (1000 * ((double) bytesPrinted) / duration);
        long bytesPerMin = (long) (60000 * ((double) bytesPrinted) / duration);

        System.err
                .println(
                        "generated "
                                + linesPrinted
                                + " lines in "
                                + duration
                                + "ms (using the "
                                + clock
                                + "), "
                                + ratePerSec
                                + "/s.  "
                );
        System.err.println("bytes/sec: " + bytesPerSec + ", bytes/min: " + bytesPerMin);
        System.err.println("Last: " + lastLinePrinted);
    }

    private void throttle(long start, long lineNumber, double linesPerMs) {
        while(checkThrottle(start, lineNumber, linesPerMs)){
            clock.sleep(1);
        }
    }

    private boolean checkThrottle(long start, long lineNumber, double linesPerMSec) {
        long elapsedMs = clock.currentTimeMillis() - start;
        long expectedLines = (long) (elapsedMs * linesPerMSec);
        if(lineNumber < expectedLines){
            return false;
        }
        return true;
    }

    public static Printer kafkaPrinter(String zkString, String topic) {
        return new KafkaPrinter(zkString, topic);
    }

    public static Printer queuePrinter(BlockingQueue<Object> queue) {
        return new QueuePrinter(queue, waitTime, unit);
    }

    public static Printer consolePrinter() {
        return new ConsolePrinter();
    }

    public long getCount() {
        return count;
    }
}