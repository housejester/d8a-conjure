package io.d8a.conjure;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import scala.actors.threadpool.Arrays;

import java.util.HashSet;
import java.util.Random;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
public class ConjureTemplateTest {
    private static final Random RAND = new Random();

    private ConjureTemplate samples;
    private long rand;

    @BeforeMethod
    public void setUp() {
        samples = new ConjureTemplate();
        rand = RAND.nextLong();
    }

    public void shouldReturnBareValueForTemplatesWithoutReferences(){
        samples.addNodeTemplate("sample", "foo");

        assertEquals(samples.next("sample"), "foo");
        assertEquals(samples.next("sample"), "foo");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void diesWhenAskedForMissingTemplates(){
        samples.addNodeTemplate("sample", "foo");
        samples.next("bad");
    }

    public void defaultsToTemplateNamedSample(){
        samples.addNodeTemplate("sample", "foo");

        assertEquals(samples.next(), "foo");
        assertEquals(samples.next(), "foo");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void diesWhenAskedForDefaultTemplateIfNotYetAdded(){
        samples.addNodeTemplate("other", "foo");
        samples.next();
    }

    public void allowsTemplatesToReferenceOthers(){
        samples.addNodeTemplate("other", "World");
        samples.addNodeTemplate("sample", "Hello, ${other}!");

        assertEquals(samples.next(), "Hello, World!");
    }

    public void templateReferencesCanBeAtTheEndOfTemplates(){
        samples.addNodeTemplate("other", "World");
        samples.addNodeTemplate("sample", "Hello, ${other}");

        assertEquals(samples.next(), "Hello, World");
    }

    public void templateReferencesCanBeAtTheBeginningOfTemplates(){
        samples.addNodeTemplate("other", "Hello");
        samples.addNodeTemplate("sample", "${other}, World!");

        assertEquals(samples.next(), "Hello, World!");
    }

    public void templateReferencesCanBeTheWholeTemplate(){
        samples.addNodeTemplate("other", "Hello");
        samples.addNodeTemplate("sample", "${other}");

        assertEquals(samples.next(), "Hello");
    }

    public void allowsMultipleTemplateReferences(){
        samples.addNodeTemplate("greeting", "Hello");
        samples.addNodeTemplate("name", "World");
        samples.addNodeTemplate("sample", "${greeting}, ${name}!");

        assertEquals(samples.next(), "Hello, World!");
    }

    public void doesNotCareWhatOrderTemplatesAreAdded(){
        samples.addNodeTemplate("sample", "${greeting}, ${name}!");
        samples.addNodeTemplate("greeting", "Hello");
        samples.addNodeTemplate("name", "World");

        assertEquals(samples.next(), "Hello, World!");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void rejectsDuplicateSampleNames(){
        samples.addNodeTemplate("sample", "one");
        samples.addNodeTemplate("sample", "two");
    }

    public void allowsCustomSampleNodes(){
        samples.addNode("test", new BareTextNode("" + rand));

        samples.addNodeTemplate("sample", "The winner is ${test}!");
        assertEquals(samples.next(), "The winner is "+rand+"!");
    }

    public void allowsRegisteredNodeTemplateTypes(){
        samples.addNodeType("test", TestNode.class);
        samples.addNodeTemplate("sample", "The winner is ${type:\"test\",value:\""+rand+"\"}!");
        assertEquals(samples.next(), "The winner is "+rand+"!");
    }

    public void configRefsCanHaveNamesToo(){
        samples.addNodeType("test", TestNode.class);

        samples.addNodeTemplate("foo", "The winning number is ${name:\"lottery\",type:\"test\",value:\""+rand+"\"}!");
        samples.addNodeTemplate("sample", "that was ${lottery}!");
        assertEquals(samples.next(), "that was " + rand + "!");
    }

    public void setsTheFullTemplateToCustomNodeWhenTemplateContainsTheConfigOnly(){
        samples.addNodeType("test", TestNode.class);

        samples.addNodeTemplate("sample", "${type:\"test\",value:\""+rand+"\"}");

        assertTrue(samples.getNode("sample") instanceof TestNode);
    }

    public void customTypesCanResolveReferences(){
        samples.addNodeType("test", TestNode.class);

        samples.addNodeTemplate("sample", "The winner is ${type:\"test\", valueRef:\"randValue\"}!");
        samples.addNodeTemplate("randValue", "" + rand);
        assertEquals(samples.next(), "The winner is "+rand+"!");
    }

    public void customTypesCanBeFullClassNames(){
        samples.addNodeTemplate("sample", "The winner is ${type:\"io.d8a.conjure.TestNode\", value:\""+rand+"\"}!");
        assertEquals(samples.next(), "The winner is "+rand+"!");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void failsWhenCustomTypeByClassNameNotFound(){
        samples.addNodeTemplate("sample", "The winner is ${type:\"io.d8a.conjure.FooTestNode\", value:\""+rand+"\"}!");
    }

    public void supportsCustomRefDelimiters(){
        samples = new ConjureTemplate(Clock.SYSTEM_CLOCK, "[[[", "]]]");
        samples.addNodeTemplate("sample", "Hello, [[[other]]]!");
        samples.addNodeTemplate("other", "World");

        assertEquals(samples.next(), "Hello, World!");
    }

    public void allowsDirectParseOfNodes(){
        samples.addNodeType("test", TestNode.class);
        SampleNode parsedNode = samples.parseNodes("The winner is ${type:\"test\", value:" + rand + "}!");
        assertEquals(parsedNode.generate(new StringBuilder()).toString(), "The winner is "+rand+"!");
    }

    public void allowsRegisteringNodeTypesByClassName(){
        samples.addNodeType("registerTest", TestNode.class);
        samples.addNodeTemplate("sample", "The winner is ${type:\"registerTest\", value:\"" + rand + "\"}!");
        assertEquals(samples.next(), "The winner is " + rand + "!");
    }

    @Test(groups = "Issues",
          description = "Want to eventually refactor around so the embedded nodes don't get registered as a side-effect.")
    public void registersEmbeddedNodesWhenParsed(){
        samples.addNodeType("test", TestNode.class);
        SampleNode parsedNode = samples.parseNodes("The winner is ${name:\"testNode\",type:\"test\", value:" + rand + "}!");

        samples.addNodeTemplate("sample", "I can see ${testNode}!");

        assertEquals(samples.next(), "I can see "+rand+"!");

    }

    public void templateRefsCanBeLongHand(){
        samples.addNodeTemplate("other", "World");
        samples.addNodeTemplate("sample", "Hello, ${ref:\"other\"}!");

        assertEquals(samples.next(), "Hello, World!");
    }

    public void refIgnoredIfTypeDetailsSpecified(){
        samples.addNodeTemplate("other", "World");
        samples.addNodeTemplate("sample", "Hello, ${ref:\"other\",type:\"io.d8a.conjure.CombineNodeList\",list:[\"TypedWorld\"]}!");

        assertEquals(samples.next(), "Hello, TypedWorld!");
    }

    public void referencedValuesAreRememberedByDefaultWithinSingleRun(){
        samples.addNodeType("minmax", MinMaxNode.class);
        samples.addNodeTemplate("lotteryTemplate", "${name:\"lottery\",type:\"minmax\",min:0,max:999}");
        samples.addNodeTemplate("sample", "Lottery Numbers:${lottery},${lottery},${lottery},${lottery},${lottery}");
        String text = samples.next();
        String numbersPart = text.substring(text.indexOf(':')+1);
        assertEquals((new HashSet<Number>(Arrays.asList(numbersPart.split(",")))).size(), 1);
    }

    public void referencedValuesCanBeConfiguredToNotRememberValues(){
        samples.addNodeType("minmax", MinMaxNode.class);
        samples.addNodeTemplate("lotteryTemplate", "${name:\"lottery\",type:\"minmax\",min:0,max:999,remember:false}");
        samples.addNodeTemplate("sample", "Lottery Numbers:${lottery},${lottery},${lottery},${lottery},${lottery}");
        String text = samples.next();
        String numbersPart = text.substring(text.indexOf(':')+1);
        assertEquals((new HashSet<Number>(Arrays.asList(numbersPart.split(",")))).size(), 5);
    }
}

