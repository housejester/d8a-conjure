package io.d8a.conjure;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.testng.Assert.assertEquals;

@Test
public class ConjureTemplateParserFixedDataTest {
    public void singleFixedLineRepeats() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse( toInputStream("single line") );
        assertEquals(template.next(), "single line");
        assertEquals(template.next(), "single line");
        assertEquals(template.next(), "single line");
    }

    public void multipleFixedLinesAllCombineEachCall() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse( toInputStream("line 1\nline 2\nline 3") );

        assertEquals(template.next(), "line 1\nline 2\nline 3");
        assertEquals(template.next(), "line 1\nline 2\nline 3");
    }

    public void ignoresBlankLines() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse( toInputStream("\n\n\nline 1\n   \nline 2\n\t\t\nline 3") );

        assertEquals(template.next(), "line 1\nline 2\nline 3");
        assertEquals(template.next(), "line 1\nline 2\nline 3");

    }

    public void ignoresHashCommentLines() throws IOException {
        ConjureTemplateParser parser = new ConjureTemplateParser();
        ConjureTemplate template = parser.parse( toInputStream("line 1\n# Ignore me\nline 2\n\t\t# Ignore me too\nline 3") );

        assertEquals(template.next(), "line 1\nline 2\nline 3");
        assertEquals(template.next(), "line 1\nline 2\nline 3");
    }

    private InputStream toInputStream(String text) {
        return new ByteArrayInputStream(text.getBytes());
    }
}
