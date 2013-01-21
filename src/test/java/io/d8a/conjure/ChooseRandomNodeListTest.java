package io.d8a.conjure;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
public class ChooseRandomNodeListTest {

    private ChooseRandomNodeList randomOrder;

    @BeforeMethod
    public void setUp() throws Exception {
        randomOrder = new ChooseRandomNodeList();
        for(int i=100;i<120;i++){
            randomOrder.add(new BareTextNode("" + i));
        }
    }

    public void generatesNodesFromProvidedList(){
        for(int i=0;i<10;i++){
            int value = new Integer(generate());
            assertTrue(value >=100 && value < 120);
        }
    }

    public void eventuallyGeneratesAllNodes(){
        Set<Integer> found = new HashSet<Integer>();
        int size = randomOrder.getNodes().size();
        int count = 0;
        while(found.size() < size && count < size * 10){
            ++count;
            found.add(new Integer(generate()));
        }
        assertEquals(found.size(), size);
        for(SampleNode node : randomOrder.getNodes()){
            assertTrue(found.contains(new Integer(((BareTextNode)node).getText())));
        }
    }

    public void repeatsValuesOverTime(){
        Map<String,Integer> found = new HashMap<String,Integer>();
        int size = randomOrder.getNodes().size();
        int count = 0;
        while(found.size() < size){
            ++count;
            String val = generate();
            Integer foundCount = found.get(val);
            if(foundCount == null){
                foundCount = 0;
            }
            foundCount += 1;
            found.put(val, foundCount);
        }
        assertTrue(count > size);

        boolean repeatedOne = false;
        for(Map.Entry<String,Integer> entry : found.entrySet()){
            if(entry.getValue() > 1){
                repeatedOne = true;
            }
        }
        assertTrue(repeatedOne);
    }

    public void canBeReferencedInSampleGenerator(){
        ConjureTemplate generator = new ConjureTemplate();
        generator.addNode("random", randomOrder);
        generator.addNodeTemplate("sample", "Answer is: [${random}].");
        Set<Integer> values = new HashSet<Integer>();
        for(SampleNode node : randomOrder.getNodes()){
            values.add(new Integer(((BareTextNode)node).getText()));
        }
        for(int i=0;i<20;i++){
            int embedded = parseNumber(generator.next());
            assertTrue(values.contains(Integer.valueOf(embedded)));
        }
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void generateFailsIfNoNodesAdded(){
        randomOrder = new ChooseRandomNodeList();
        assertEquals(generate(), "one");
    }

    public void canBeRegisteredAsType(){
        ConjureTemplate template = new ConjureTemplate();
        template.addNodeType("randomChoice", ChooseRandomNodeList.class);
        template.addNodeTemplate("sample", "My favorite is [${type:\"randomChoice\", list:[\"a\",\"b\",\"c\"]}]");
        String text = template.next();
        String value = text.substring(text.indexOf('[')+1, text.indexOf(']'));
        assertTrue(Arrays.asList("a", "b", "c").contains(value));
    }

    private String generate() {
        return randomOrder.generate(new StringBuilder()).toString();
    }

    private int parseNumber(String text) {
        return Integer.valueOf(text.substring(text.indexOf('[')+1, text.indexOf(']')));
    }
}
