package io.d8a.conjure;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
public class ChooseInOrderNodeListTest {

    private ChooseInOrderNodeList inOrder;

    public void generatesNodesInOrderAdded(){
        assertEquals(generate(), "one");
        assertEquals(generate(), "two");
        assertEquals(generate(), "three");
    }

    public void cyclesBackToBeginning(){
        assertEquals(generate(), "one");
        assertEquals(generate(), "two");
        assertEquals(generate(), "three");
        assertEquals(generate(), "one");
        assertEquals(generate(), "two");
        assertEquals(generate(), "three");
    }

    public void canBeReferencedInSampleGenerator(){
        ConjureTemplate generator = new ConjureTemplate();
        generator.addNode("order", inOrder);
        generator.addFragment("sample", "Answer is: ${order}.");
        assertEquals(generator.conjure(), "Answer is: one.");
        assertEquals(generator.conjure(), "Answer is: two.");
        assertEquals(generator.conjure(), "Answer is: three.");
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void generateFailsIfNoNodesAdded(){
        inOrder = new ChooseInOrderNodeList();
        assertEquals(generate(), "one");
    }

    public void canBeRegisteredAsType(){
        ConjureTemplate template = new ConjureTemplate();
        template.addNodeType("cycle", ChooseInOrderNodeList.class);
        template.addFragment("sample", "My favorite is [${type:\"cycle\", list:[\"a\",\"b\",\"c\"]}]");
        assertEquals(template.conjure(), "My favorite is [a]");
        assertEquals(template.conjure(), "My favorite is [b]");
        assertEquals(template.conjure(), "My favorite is [c]");

        assertEquals(template.conjure(), "My favorite is [a]");
        assertEquals(template.conjure(), "My favorite is [b]");
        assertEquals(template.conjure(), "My favorite is [c]");
    }

    private String generate() {
        return inOrder.generate(new StringBuilder()).toString();
    }

    @BeforeMethod
    public void setUp() throws Exception {
        inOrder = new ChooseInOrderNodeList();
        inOrder.add(new BareTextNode("one"), new BareTextNode("two"), new BareTextNode("three"));
    }
}
