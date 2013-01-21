package io.d8a.conjure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.cli.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ConjurerFirehose implements Runnable{
    private static final Random RAND = new Random();

    private final Clock clock;
    private final long stopTime;
    private final Printer printer;
    private final int linesPerSec;
    private final long maxLines;
    private String filePath;
    private long count = 0;
    private Conjurer conjurer;

    public ConjurerFirehose(long stopTime, Printer printer, int linesPerSec, String filePath){
        this(-1, stopTime, printer, linesPerSec, Long.MAX_VALUE, filePath);
    }

    public ConjurerFirehose(long startTime, long stopTime, Printer printer, int linesPerSec, String filePath) {
        this(startTime, stopTime, printer, linesPerSec, Long.MAX_VALUE, filePath);
    }

    public ConjurerFirehose(long startTime, long stopTime, Printer printer, int linesPerSec, long maxLines, String filePath) {
        this.stopTime = stopTime;
        this.printer = printer;
        this.linesPerSec = linesPerSec;
        this.maxLines = maxLines;
        this.filePath = filePath;
        if(startTime < 0){ //-1 means generate data moving forward.
            clock = Clock.SYSTEM_CLOCK;
        }else{
            clock = new SimulatedClock(startTime);
        }
        ConjureTemplateParser parser = new ConjureTemplateParser(clock);
        try {
            this.conjurer = parser.parse(new FileInputStream(filePath));
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not create Conjurer from "+filePath, e);
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        Options options = new Options();
        options.addOption("zk", true, "Zookeeper connection string for kafka");
        options.addOption("topic", true, "Kafka topic to send data to");
        options.addOption("template", true, "Path to the Conjure Template file that describes the data to generate");
        options.addOption("rate", true, "Lines per second");
        options.addOption("cap", true, "Total lines to generate");
        options.addOption("out", true, "Where to write the generated samples.  [kafka|console|none]");
        options.addOption("startTime", true, "For historical data generation.  What epoch time to start generating data from.");
        options.addOption("stopTime", true, "For historical data generation.  What epoch time to stop generating data.");

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse(options, args);

        long stopTime = Long.MAX_VALUE;
        if (cmd.hasOption("stopTime")) {
            stopTime = new Long(cmd.getOptionValue("stopTime"));
        }

        long startTime = -1;
        if (cmd.hasOption("startTime")) {
            startTime = new Long(cmd.getOptionValue("startTime"));
            if (stopTime == Long.MAX_VALUE) {
                stopTime = System.currentTimeMillis();
            }
        }

        Printer printer = consolePrinter();
        if ("kafka".equals(cmd.getOptionValue("out"))) {
            if (cmd.hasOption("zk") && cmd.hasOption("topic")) {
                printer = kafkaPrinter(cmd.getOptionValue("zk"), cmd.getOptionValue("topic"));
            } else {
                throw new IllegalArgumentException("Must specify zookeeper connection string ('zk') and kafka topic ('topic') to write to kafka.");
            }
        } else if ("none".equals(cmd.getOptionValue("out"))) {
            printer = nonePrinter();
        }

        int linesPerSec = 10;
        if (cmd.hasOption("rate")) {
            linesPerSec = new Integer(cmd.getOptionValue("rate"));
        }

        long numLines = Long.MAX_VALUE;
        if (cmd.hasOption("cap")) {
            numLines = new Long(cmd.getOptionValue("cap"));
        }
        long start = System.currentTimeMillis();
        String filePath = cmd.getOptionValue("template");
        ConjurerFirehose conjurerFirehose = new ConjurerFirehose(startTime, stopTime, printer, linesPerSec, numLines, filePath);
        conjurerFirehose.exhaust();
        long duration = System.currentTimeMillis() - start;
        System.out.println("ConjurerFirehose finished.  Took " + duration + "ms to conjure up " + conjurerFirehose.getCount()+" samples.");
    }

    public void exhaust(){
        Thread thread = new Thread(this);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
        }
    }

    public void run(){
        System.err.println("Conjuring data to "+ printer.getName() +" at a rate of "+linesPerSec+" lines per second.");
        double linesPerMs = (double)linesPerSec / 1000;

        long start = clock.currentTimeMillis();
        long lastReport = start;
        long bytesWritten = 0L;
        String lastLinePrinted = "";

        for(long i=0 ;i<maxLines && clock.currentTimeMillis()<stopTime; i++){
            throttle(start, i, linesPerMs);
            if(Thread.currentThread().isInterrupted()){
                return;
            }
            String str = genLine();
            bytesWritten += str.length();
            printer.print(str);
            lastLinePrinted = str;
            ++count;
            if(System.currentTimeMillis() - lastReport > 5000){
                report(start, count, lastLinePrinted, bytesWritten);
                lastReport = System.currentTimeMillis();
            }
        }
        report(start, count, lastLinePrinted, bytesWritten);
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    private String genLine() {
        return conjurer.next();
    }

    private String genLineHardcode() {
        Map<String, Object> map = new HashMap<String, Object>();
        long now = clock.currentTimeMillis();
        map.put("install_timestamp", now - randInt(9, 1000000));
        map.put("event_timestamp", now);
        map.put("publisherId", randInt(1, 130));
        map.put("campaignId", randInt(1, 3000));
        map.put("adgroupId", randInt(1, 25));
        map.put("appId", randInt(1, 280));
        map.put("adId", randInt(1, 44000));
        map.put("country", randString("US", "AR", "BE", "BR", "CA", "CH", "CN", "DE"));
        map.put("event", randString("__appuse", "click", "install", "log+in+with+facebook", "bought+token"));
        map.put("event_value", 1);
        map.put("days_from_install", randInt(0, 365));
        map.put("engagement", randInt(0, 600));
        map.put("retention", randInt(0, 88));
        map.put("revenue", randInt(0, 210));
        return toJson(mapper, map);
    }

    private String toJson(ObjectMapper mapper, Map<String, Object> map){
        try {
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Problem writing map as json.", e);
        }
    }

    private static Printer nonePrinter() {
        return new Printer(){
            @Override
            public void print(String message) {
            }
            public String getName(){
                return "Blackhole";
            }
        };
    }

    private void report(long start, long linesPrinted, String lastLinePrinted, long bytesPrinted) {
        long now = clock.currentTimeMillis();
        long duration = now - start;
        long ratePerSec = (long)(1000 * ((double)linesPrinted)/duration);
        long bytesPerSec = (long)(1000 * ((double)bytesPrinted)/duration);
        long bytesPerMin = (long)(60000 * ((double)bytesPrinted)/duration);

        System.out.println("generated "+linesPrinted+" lines in "+duration+"ms (using the "+clock.getName()+"), "+ratePerSec+"/s.  ");
        System.out.println("bytes/sec: "+bytesPerSec+", bytes/min: "+bytesPerMin);
        System.out.println("Last: "+lastLinePrinted);
    }

    private void throttle(long start, long lineNumber, double linesPerMs){
        while(checkThrottle(start, lineNumber, linesPerMs)){
            clock.sleep(1);
        }
    }

    private boolean checkThrottle(long start, long lineNumber, double linesPerMSec) {
        long elapsedMs = clock.currentTimeMillis() - start;
        long expectedLines = (long)(elapsedMs * linesPerMSec);
        if(lineNumber < expectedLines){
            return false;
        }
        return true;
    }

    private static String randString(String...values) {
        return values[RAND.nextInt(values.length)];
    }

    private static long randInt(int min, int max) {
        if(min > max){
            int a = min;
            min = max;
            max = a;
        }
        if(min == max){
            return 0;
        }
        return min + RAND.nextInt(max - min);
    }

    public static Printer kafkaPrinter(String zkString, String topic){
        return new KafkaPrinter(zkString, topic);
    }

    public static Printer consolePrinter(){
        return new ConsolePrinter();
    }

    public long getCount() {
        return count;
    }
}