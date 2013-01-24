package io.d8a.conjure;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.testng.Assert.assertEquals;

@Test
public class ConjureTemplateParserNodeListParsingTest {
    public void parsesNodeListLines() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse(toInputStream("${type:\"cycle\"}\none\ntwo\nthree"));
        assertEquals(template.conjure(), "one");
        assertEquals(template.conjure(), "two");
        assertEquals(template.conjure(), "three");

        assertEquals(template.conjure(), "one");
        assertEquals(template.conjure(), "two");
        assertEquals(template.conjure(), "three");
    }

    public void stopsParsingNodeListAfterBlankLine() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse(toInputStream("${type:\"cycle\"}\none\ntwo\nthree\n\nThis is not in the cycle."));
        assertEquals(template.conjure(), "one\nThis is not in the cycle.");
        assertEquals(template.conjure(), "two\nThis is not in the cycle.");
        assertEquals(template.conjure(), "three\nThis is not in the cycle.");

        assertEquals(template.conjure(), "one\nThis is not in the cycle.");
        assertEquals(template.conjure(), "two\nThis is not in the cycle.");
        assertEquals(template.conjure(), "three\nThis is not in the cycle.");
    }

    public void generatesValuesFromEachGroupAndCombinesWithNewlinesForEveryCall() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse(toInputStream("${type:\"cycle\"}\n1\n2\n3\n\n${type:\"cycle\"}\na\nb\nc"));
        assertEquals(template.conjure(), "1\na");
        assertEquals(template.conjure(), "2\nb");
        assertEquals(template.conjure(), "3\nc");

        assertEquals(template.conjure(), "1\na");
        assertEquals(template.conjure(), "2\nb");
        assertEquals(template.conjure(), "3\nc");
    }

    public void canSpecifyCustomEndToken() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse(toInputStream("${type:\"cycle\",endToken:\"---\"}\none\ntwo\nthree\n---\nThis is not in the cycle."));
        assertEquals(template.conjure(), "one\nThis is not in the cycle.");
        assertEquals(template.conjure(), "two\nThis is not in the cycle.");
        assertEquals(template.conjure(), "three\nThis is not in the cycle.");

        assertEquals(template.conjure(), "one\nThis is not in the cycle.");
        assertEquals(template.conjure(), "two\nThis is not in the cycle.");
        assertEquals(template.conjure(), "three\nThis is not in the cycle.");
    }

    public void canHaveWhitespaceBetweenCustomEndTokens() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse(toInputStream("${type:\"cycle\",endToken:\"---\"}\none\n\n\ntwo\nthree\n---\nThis is not in the cycle."));
        assertEquals(template.conjure(), "one\nThis is not in the cycle.");
        assertEquals(template.conjure(), "two\nThis is not in the cycle.");
        assertEquals(template.conjure(), "three\nThis is not in the cycle.");

        assertEquals(template.conjure(), "one\nThis is not in the cycle.");
        assertEquals(template.conjure(), "two\nThis is not in the cycle.");
        assertEquals(template.conjure(), "three\nThis is not in the cycle.");
    }

    public void canSpecifyThePrimarySampleNode() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse(toInputStream("${name:\"sample\",type:\"cycle\",endToken:\"---\"}\none\ntwo\nthree\n---\nThis is not in the cycle."));
        assertEquals(template.conjure(), "one");
        assertEquals(template.conjure(), "two");
        assertEquals(template.conjure(), "three");

        assertEquals(template.conjure(), "one");
        assertEquals(template.conjure(), "two");
        assertEquals(template.conjure(), "three");
    }

    public void canSpecifyLinesShouldBeTrimmed() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse(toInputStream("${type:\"cycle\",trim:true}\n   one   \n   two  \n\t three\t"));
        assertEquals(template.conjure(), "one");
        assertEquals(template.conjure(), "two");
        assertEquals(template.conjure(), "three");

        assertEquals(template.conjure(), "one");
        assertEquals(template.conjure(), "two");
        assertEquals(template.conjure(), "three");
    }

    public void canSpecifyLinesShouldBeTrimmedForCombineNodes() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse(toInputStream("${type:\"combine\",trim:true,separator:\",\"}\n   one   \n   two  \n\t three\t"));
        assertEquals(template.conjure(), "one,two,three");

        assertEquals(template.conjure(), "one,two,three");
    }

    private InputStream toInputStream(String text) {
        return new ByteArrayInputStream(text.getBytes());
    }
}
