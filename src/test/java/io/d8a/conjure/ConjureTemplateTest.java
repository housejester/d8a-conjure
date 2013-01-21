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
        samples.addFragment("sample", "foo");

        assertEquals(samples.conjure("sample"), "foo");
        assertEquals(samples.conjure("sample"), "foo");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void diesWhenAskedForMissingTemplates(){
        samples.addFragment("sample", "foo");
        samples.conjure("bad");
    }

    public void defaultsToTemplateNamedSample(){
        samples.addFragment("sample", "foo");

        assertEquals(samples.conjure(), "foo");
        assertEquals(samples.conjure(), "foo");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void diesWhenAskedForDefaultTemplateIfNotYetAdded(){
        samples.addFragment("other", "foo");
        samples.conjure();
    }

    public void allowsTemplatesToReferenceOthers(){
        samples.addFragment("other", "World");
        samples.addFragment("sample", "Hello, ${other}!");

        assertEquals(samples.conjure(), "Hello, World!");
    }

    public void templateReferencesCanBeAtTheEndOfTemplates(){
        samples.addFragment("other", "World");
        samples.addFragment("sample", "Hello, ${other}");

        assertEquals(samples.conjure(), "Hello, World");
    }

    public void templateReferencesCanBeAtTheBeginningOfTemplates(){
        samples.addFragment("other", "Hello");
        samples.addFragment("sample", "${other}, World!");

        assertEquals(samples.conjure(), "Hello, World!");
    }

    public void templateReferencesCanBeTheWholeTemplate(){
        samples.addFragment("other", "Hello");
        samples.addFragment("sample", "${other}");

        assertEquals(samples.conjure(), "Hello");
    }

    public void allowsMultipleTemplateReferences(){
        samples.addFragment("greeting", "Hello");
        samples.addFragment("name", "World");
        samples.addFragment("sample", "${greeting}, ${name}!");

        assertEquals(samples.conjure(), "Hello, World!");
    }

    public void doesNotCareWhatOrderTemplatesAreAdded(){
        samples.addFragment("sample", "${greeting}, ${name}!");
        samples.addFragment("greeting", "Hello");
        samples.addFragment("name", "World");

        assertEquals(samples.conjure(), "Hello, World!");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void rejectsDuplicateSampleNames(){
        samples.addFragment("sample", "one");
        samples.addFragment("sample", "two");
    }

    public void allowsCustomSampleNodes(){
        samples.addNode("test", new BareTextNode("" + rand));

        samples.addFragment("sample", "The winner is ${test}!");
        assertEquals(samples.conjure(), "The winner is "+rand+"!");
    }

    public void allowsRegisteredNodeTemplateTypes(){
        samples.addNodeType("test", TestNode.class);
        samples.addFragment("sample", "The winner is ${type:\"test\",value:\"" + rand + "\"}!");
        assertEquals(samples.conjure(), "The winner is "+rand+"!");
    }

    public void configRefsCanHaveNamesToo(){
        samples.addNodeType("test", TestNode.class);

        samples.addFragment("foo", "The winning number is ${name:\"lottery\",type:\"test\",value:\"" + rand + "\"}!");
        samples.addFragment("sample", "that was ${lottery}!");
        assertEquals(samples.conjure(), "that was " + rand + "!");
    }

    public void setsTheFullTemplateToCustomNodeWhenTemplateContainsTheConfigOnly(){
        samples.addNodeType("test", TestNode.class);

        samples.addFragment("sample", "${type:\"test\",value:\"" + rand + "\"}");

        assertTrue(samples.getNode("sample") instanceof TestNode);
    }

    public void customTypesCanResolveReferences(){
        samples.addNodeType("test", TestNode.class);

        samples.addFragment("sample", "The winner is ${type:\"test\", valueRef:\"randValue\"}!");
        samples.addFragment("randValue", "" + rand);
        assertEquals(samples.conjure(), "The winner is "+rand+"!");
    }

    public void customTypesCanBeFullClassNames(){
        samples.addFragment("sample", "The winner is ${type:\"io.d8a.conjure.TestNode\", value:\"" + rand + "\"}!");
        assertEquals(samples.conjure(), "The winner is "+rand+"!");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void failsWhenCustomTypeByClassNameNotFound(){
        samples.addFragment("sample", "The winner is ${type:\"io.d8a.conjure.FooTestNode\", value:\"" + rand + "\"}!");
    }

    public void supportsCustomRefDelimiters(){
        samples = new ConjureTemplate(Clock.SYSTEM_CLOCK, "[[[", "]]]");
        samples.addFragment("sample", "Hello, [[[other]]]!");
        samples.addFragment("other", "World");

        assertEquals(samples.conjure(), "Hello, World!");
    }

    public void allowsDirectParseOfNodes(){
        samples.addNodeType("test", TestNode.class);
        ConjureTemplateNode parsedNode = samples.parseNodes("The winner is ${type:\"test\", value:" + rand + "}!");
        assertEquals(parsedNode.generate(new StringBuilder()).toString(), "The winner is "+rand+"!");
    }

    public void allowsRegisteringNodeTypesByClassName(){
        samples.addNodeType("registerTest", TestNode.class);
        samples.addFragment("sample", "The winner is ${type:\"registerTest\", value:\"" + rand + "\"}!");
        assertEquals(samples.conjure(), "The winner is " + rand + "!");
    }

    @Test(groups = "Issues",
          description = "Want to eventually refactor around so the embedded nodes don't get registered as a side-effect.")
    public void registersEmbeddedNodesWhenParsed(){
        samples.addNodeType("test", TestNode.class);
        ConjureTemplateNode parsedNode = samples.parseNodes("The winner is ${name:\"testNode\",type:\"test\", value:" + rand + "}!");

        samples.addFragment("sample", "I can see ${testNode}!");

        assertEquals(samples.conjure(), "I can see "+rand+"!");

    }

    public void templateRefsCanBeLongHand(){
        samples.addFragment("other", "World");
        samples.addFragment("sample", "Hello, ${ref:\"other\"}!");

        assertEquals(samples.conjure(), "Hello, World!");
    }

    public void refIgnoredIfTypeDetailsSpecified(){
        samples.addFragment("other", "World");
        samples.addFragment("sample", "Hello, ${ref:\"other\",type:\"io.d8a.conjure.CombineNodeList\",list:[\"TypedWorld\"]}!");

        assertEquals(samples.conjure(), "Hello, TypedWorld!");
    }

    public void referencedValuesAreRememberedByDefaultWithinSingleRun(){
        samples.addNodeType("minmax", MinMaxNode.class);
        samples.addFragment("lotteryTemplate", "${name:\"lottery\",type:\"minmax\",min:0,max:999}");
        samples.addFragment("sample", "Lottery Numbers:${lottery},${lottery},${lottery},${lottery},${lottery}");
        String text = samples.conjure();
        String numbersPart = text.substring(text.indexOf(':')+1);
        assertEquals((new HashSet<Number>(Arrays.asList(numbersPart.split(",")))).size(), 1);
    }

    public void referencedValuesCanBeConfiguredToNotRememberValues(){
        samples.addNodeType("minmax", MinMaxNode.class);
        samples.addFragment("lotteryTemplate", "${name:\"lottery\",type:\"minmax\",min:0,max:999,remember:false}");
        samples.addFragment("sample", "Lottery Numbers:${lottery},${lottery},${lottery},${lottery},${lottery}");
        String text = samples.conjure();
        String numbersPart = text.substring(text.indexOf(':')+1);
        assertEquals((new HashSet<Number>(Arrays.asList(numbersPart.split(",")))).size(), 5);
    }
}

