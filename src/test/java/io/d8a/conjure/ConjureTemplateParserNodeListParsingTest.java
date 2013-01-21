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
        Conjurer conjurer = parser.parse(toInputStream("${type:\"cycle\"}\none\ntwo\nthree"));
        assertEquals(conjurer.next(), "one");
        assertEquals(conjurer.next(), "two");
        assertEquals(conjurer.next(), "three");

        assertEquals(conjurer.next(), "one");
        assertEquals(conjurer.next(), "two");
        assertEquals(conjurer.next(), "three");
    }

    public void stopsParsingNodeListAfterBlankLine() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        Conjurer conjurer = parser.parse(toInputStream("${type:\"cycle\"}\none\ntwo\nthree\n\nThis is not in the cycle."));
        assertEquals(conjurer.next(), "one\nThis is not in the cycle.");
        assertEquals(conjurer.next(), "two\nThis is not in the cycle.");
        assertEquals(conjurer.next(), "three\nThis is not in the cycle.");

        assertEquals(conjurer.next(), "one\nThis is not in the cycle.");
        assertEquals(conjurer.next(), "two\nThis is not in the cycle.");
        assertEquals(conjurer.next(), "three\nThis is not in the cycle.");
    }

    public void generatesValuesFromEachGroupAndCombinesWithNewlinesForEveryCall() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        Conjurer conjurer = parser.parse(toInputStream("${type:\"cycle\"}\n1\n2\n3\n\n${type:\"cycle\"}\na\nb\nc"));
        assertEquals(conjurer.next(), "1\na");
        assertEquals(conjurer.next(), "2\nb");
        assertEquals(conjurer.next(), "3\nc");

        assertEquals(conjurer.next(), "1\na");
        assertEquals(conjurer.next(), "2\nb");
        assertEquals(conjurer.next(), "3\nc");
    }

    public void canSpecifyCustomEndToken() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        Conjurer conjurer = parser.parse(toInputStream("${type:\"cycle\",endToken:\"---\"}\none\ntwo\nthree\n---\nThis is not in the cycle."));
        assertEquals(conjurer.next(), "one\nThis is not in the cycle.");
        assertEquals(conjurer.next(), "two\nThis is not in the cycle.");
        assertEquals(conjurer.next(), "three\nThis is not in the cycle.");

        assertEquals(conjurer.next(), "one\nThis is not in the cycle.");
        assertEquals(conjurer.next(), "two\nThis is not in the cycle.");
        assertEquals(conjurer.next(), "three\nThis is not in the cycle.");
    }

    public void canHaveWhitespaceBetweenCustomEndTokens() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        Conjurer conjurer = parser.parse(toInputStream("${type:\"cycle\",endToken:\"---\"}\none\n\n\ntwo\nthree\n---\nThis is not in the cycle."));
        assertEquals(conjurer.next(), "one\nThis is not in the cycle.");
        assertEquals(conjurer.next(), "two\nThis is not in the cycle.");
        assertEquals(conjurer.next(), "three\nThis is not in the cycle.");

        assertEquals(conjurer.next(), "one\nThis is not in the cycle.");
        assertEquals(conjurer.next(), "two\nThis is not in the cycle.");
        assertEquals(conjurer.next(), "three\nThis is not in the cycle.");
    }

    public void canSpecifyThePrimarySampleNode() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        Conjurer conjurer = parser.parse(toInputStream("${name:\"sample\",type:\"cycle\",endToken:\"---\"}\none\ntwo\nthree\n---\nThis is not in the cycle."));
        assertEquals(conjurer.next(), "one");
        assertEquals(conjurer.next(), "two");
        assertEquals(conjurer.next(), "three");

        assertEquals(conjurer.next(), "one");
        assertEquals(conjurer.next(), "two");
        assertEquals(conjurer.next(), "three");
    }

    private InputStream toInputStream(String text) {
        return new ByteArrayInputStream(text.getBytes());
    }
}
