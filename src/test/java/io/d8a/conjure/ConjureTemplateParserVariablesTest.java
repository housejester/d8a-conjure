package io.d8a.conjure;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
public class ConjureTemplateParserVariablesTest {
    public void linesCanHaveVariablesAndStillCombinesAllEachCall() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse(toInputStream(
            "First value ${type:\"io.d8a.conjure.MinMaxNode\",min:20,max:20}.\n"+
            "Second value ${type:\"io.d8a.conjure.MinMaxNode\",min:30,max:30}.\n"
        ));
        assertEquals(template.conjure(), "First value 20.\nSecond value 30.");
        assertEquals(template.conjure(), "First value 20.\nSecond value 30.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void failsForUnregisteredVariableTypes() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse(toInputStream("The current time is ${type:\"badname\"}"));
    }

    public void succeedsForFullyQualifiedTypes() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse(toInputStream("The current time is ${type:\"io.d8a.conjure.MinMaxNode\",\"min\":10,\"max\":10}"));
        assertEquals(template.conjure(), "The current time is 10");
    }

    public void timeRegisteredByDefault() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        long start = System.currentTimeMillis();
        ConjureTemplate template = parser.parse(toInputStream("The current time is [${type:\"time\"}]"));
        long stop = System.currentTimeMillis();
        String text = template.conjure();
        long time = parseLong(text);

        assertTrue( time >= start);
        assertTrue( time <= stop);
    }

    private static final Random RAND = new Random();

    public void canSpecifyCustomClock() throws IOException {
        long rand = RAND.nextLong();
        ConjureTemplateParser parser = new ConjureTemplateParser(new SimulatedClock(rand));
        ConjureTemplate template = parser.parse(toInputStream("The current time is [${type:\"time\"}]"));
        assertEquals(template.conjure(), "The current time is ["+rand+"]");
    }

    public void minMaxRegisteredByDefault() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse(toInputStream("The current time is ${type:\"minmax\",\"min\":10,\"max\":10}"));
        assertEquals(template.conjure(), "The current time is 10");
    }

    public void randomChoiceRegisteredByDefault() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse(toInputStream("My favorite is [${type:\"randomChoice\", list:[\"a\",\"b\",\"c\"]}]"));
        String text = template.conjure();
        String value = text.substring(text.indexOf('[') + 1, text.indexOf(']'));
        assertTrue(Arrays.asList("a", "b", "c").contains(value));
    }

    public void cycleRegisteredByDefault() throws IOException{
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse(toInputStream("My favorite is [${type:\"cycle\", list:[\"a\",\"b\",\"c\"]}]"));
        assertEquals(template.conjure(), "My favorite is [a]");
        assertEquals(template.conjure(), "My favorite is [b]");
        assertEquals(template.conjure(), "My favorite is [c]");

        assertEquals(template.conjure(), "My favorite is [a]");
        assertEquals(template.conjure(), "My favorite is [b]");
        assertEquals(template.conjure(), "My favorite is [c]");

    }

    public void combineRegisteredByDefault() throws IOException{
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse(toInputStream("My favorite is [${type:\"combine\", list:[\"a\",\"b\",\"c\"]}]"));
        assertEquals(template.conjure(), "My favorite is [abc]");
        assertEquals(template.conjure(), "My favorite is [abc]");
    }

    public void weightedRegisteredByDefault() throws IOException{
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse(toInputStream("My favorite is [${type:\"weighted\", list:[\"10:a\",\"20:b\",\"70:c\"]}]"));
        String text = template.conjure();
        String fav = text.substring(text.indexOf('[')+1, text.indexOf(']'));
        assertTrue(Arrays.asList("a", "b", "c").contains(fav));
    }

    private long parseLong(String text) {
        int index = text.indexOf('[');
        return new Long(text.substring(index + 1, text.indexOf(']', index)));
    }

    private InputStream toInputStream(String text) {
        return new ByteArrayInputStream(text.getBytes());
    }
}
