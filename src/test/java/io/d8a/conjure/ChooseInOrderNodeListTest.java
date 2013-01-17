package io.d8a.conjure;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

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
        Conjurer generator = new Conjurer();
        generator.addNode("order", inOrder);
        generator.addNodeTemplate("sample", "Answer is: ${order}.");
        assertEquals(generator.next(), "Answer is: one.");
        assertEquals(generator.next(), "Answer is: two.");
        assertEquals(generator.next(), "Answer is: three.");
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void generateFailsIfNoNodesAdded(){
        inOrder = new ChooseInOrderNodeList();
        assertEquals(generate(), "one");
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
