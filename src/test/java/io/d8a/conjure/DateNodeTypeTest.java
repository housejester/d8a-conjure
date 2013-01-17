package io.d8a.conjure;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Random;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
public class DateNodeTypeTest {
    private static final Random RAND = new Random();
    private Conjurer samples;
    private long randTime;

    @BeforeMethod
    public void setUp() throws Exception {
        randTime = Math.abs(RAND.nextLong());
        Clock clock = new SimulatedClock(randTime);

        samples = new Conjurer(clock);
        samples.addNodeType("date", DateNode.class);
    }

    public void generatesSystemTimestamp(){
        samples = new Conjurer();
        samples.addNodeType("date", DateNode.class);
        samples.addNodeTemplate("sample", "The current time is [${type:\"date\"}].");

        long start = System.currentTimeMillis();
        String text = samples.next();
        long stop = System.currentTimeMillis();

        long timestamp = parseNumber(text);

        assertInRange(timestamp, start, stop);
    }

    public void usesClockFromSampleGenerator(){
        samples.addNodeTemplate("sample", "The current time is [${type:\"date\"}].");
        long timestamp = parseNumber(samples.next());
        assertEquals(timestamp, randTime);
    }

    public void allowsFormat(){
        samples.addNodeTemplate("sample", "The current time is ${type:\"date\",format:\"yyyyMMDD hh:mm:ss\"}.");
        DateTimeFormatter utc = DateTimeFormat.forPattern("yyyyMMDD hh:mm:ss").withZone(DateTimeZone.getDefault());

        String timeFormatted = utc.print(randTime);
        assertEquals(samples.next(), "The current time is "+timeFormatted+".");
    }

    public void allowsTimezone(){
        samples.addNodeTemplate("sample", "The current time is ${type:\"date\",format:\"yyyyMMDD hh:mm:ss\", timezone:\"UTC\"}.");
        DateTimeFormatter utc = DateTimeFormat.forPattern("yyyyMMDD hh:mm:ss").withZone(DateTimeZone.forID("UTC"));
        String timeFormatted = utc.print(randTime);
        assertEquals(samples.next(), "The current time is " + timeFormatted + ".");
    }

    public void allowsDateJitter(){
        samples.addNodeTemplate("sample", "The current time is [${type:\"date\",min:10, max:100}].");
        long value = parseNumber(samples.next());
        assertInRange(value, randTime + 10, randTime + 100);
    }

    public void allowsNegativeLowJitter(){
        samples.addNodeTemplate("sample", "The current time is [${type:\"date\",min:-110, max:100}].");
        long value = parseNumber(samples.next());
        assertInRange(value, randTime - 110, randTime + 100);
    }

    public void allowsNegativeHighJitter(){
        samples.addNodeTemplate("sample", "The current time is [${type:\"date\",min:-110, max:-10}].");
        long value = parseNumber(samples.next());
        assertInRange(value, randTime - 110, randTime - 10);
    }

    public void fixesTransposedLowHigh(){
        samples.addNodeTemplate("sample", "The current time is [${type:\"date\",min:100, max:10}].");
        long value = parseNumber(samples.next());
        assertInRange(value, randTime + 10, randTime + 100);
    }

    public void canHaveSameJitterLowAndHigh(){
        samples.addNodeTemplate("sample", "The current time is [${type:\"date\",min:100, max:100}].");
        long value = parseNumber(samples.next());
        assertEquals(value, randTime + 100);
    }

    public void canReferenceMinMaxForJitter(){
        samples.addNodeType("minmax",MinMaxNode.class);
        samples.addNodeTemplate("sample", "The current time is [${type:\"date\",minmaxRef:\"jitter\"}].");
        samples.addNodeTemplate("jitter", "${type:\"minmax\",min:200,max:200}");
        long value = parseNumber(samples.next());
        assertEquals(value, randTime + 200);
    }

    private long parseNumber(String text) {
        return Long.valueOf(text.substring(text.indexOf('[')+1, text.indexOf(']')));
    }

    private void assertInRange(long value, long start, long stop) {
        assertTrue(value >= start && value <= stop, "Value '" + value + "' not contained in the range [" + start + "," + stop + "]");
    }
}
