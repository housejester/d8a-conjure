package io.d8a.conjure;

import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
public class ChooseByWeightNodeListTest {
    @Test(expectedExceptions = IllegalStateException.class)
    public void diesWhenRenderingNoNodes(){
        ChooseByWeightNodeList node = new ChooseByWeightNodeList();
        node.generate(new StringBuilder());
    }

    public void generatesLoneNodeRepeatedlyWhenOnlyOneAdded(){
        ChooseByWeightNodeList node = new ChooseByWeightNodeList();
        node.add(new BareTextNode("Foo"));
        for(int i=0; i<5; i++){
            assertEquals(node.generate(new StringBuilder()).toString(), "Foo");
        }
    }

    public void generatesAllNodesOfSameWeightEventually(){
        ChooseByWeightNodeList node = new ChooseByWeightNodeList();
        int numNodes = 10;
        Set<String> expected = new HashSet<String>();
        for(int i=1; i<=numNodes; i++){
            String str = ""+i;
            expected.add(str);
            node.add(new BareTextNode(str));
        }

        Set<String> generated = new HashSet<String>();
        for(int i=0; generated.size() != numNodes && i<numNodes * 10; i++){
            generated.add(node.generate(new StringBuilder()).toString());
        }

        assertTrue(generated.containsAll(expected));
    }

    public void generatesHigherWeightedNodesMoreOften(){
        ChooseByWeightNodeList node = new ChooseByWeightNodeList();
        node.add(new BareTextNode("99"), 99);
        node.add(new BareTextNode("1"), 1);

        int count99 = 0;
        int count1 = 0;
        for(int i=0; i<10; i++){
            String val = node.generate(new StringBuilder()).toString();
            if("99".equals(val)){
                ++count99;
            }else if("1".equals(val)){
                ++count1;
            }
        }
        assertTrue(count99 > count1);
    }

    public void nodeDistributionReflectsWeights() {
        ChooseByWeightNodeList node = new ChooseByWeightNodeList();
        node.add(new BareTextNode("a"), 2);
        node.add(new BareTextNode("b"), 10);
        node.add(new BareTextNode("c"), 12);
        node.add(new BareTextNode("d"), 18);
        node.add(new BareTextNode("e"), 25);
        node.add(new BareTextNode("f"), 33);

        // sum of all "Weights" is 100 in this example.  That means the weights are just the expected percentage
        // of times the target node to be generated

        int numGenerations = 30000;
        Map<String, Integer> counts = runGenerations(node, numGenerations);

        assertPct(counts.get("a"), numGenerations, 2);
        assertPct(counts.get("b"), numGenerations, 10);
        assertPct(counts.get("c"), numGenerations, 12);
        assertPct(counts.get("d"), numGenerations, 18);
        assertPct(counts.get("e"), numGenerations, 25);
        assertPct(counts.get("f"), numGenerations, 33);
    }

    public void nodesOfDifferentWeightsCanBeAddedInAnyOrder() {
        ChooseByWeightNodeList node = new ChooseByWeightNodeList();
        node.add(new BareTextNode("b"), 10);
        node.add(new BareTextNode("a"), 2);
        node.add(new BareTextNode("f"), 33);
        node.add(new BareTextNode("d"), 18);
        node.add(new BareTextNode("c"), 12);
        node.add(new BareTextNode("e"), 25);

        int numGenerations = 30000;
        Map<String, Integer> counts = runGenerations(node, numGenerations);

        assertPct(counts.get("a"), numGenerations, 2);
        assertPct(counts.get("b"), numGenerations, 10);
        assertPct(counts.get("c"), numGenerations, 12);
        assertPct(counts.get("d"), numGenerations, 18);
        assertPct(counts.get("e"), numGenerations, 25);
        assertPct(counts.get("f"), numGenerations, 33);
    }

    public void nodesOfSameWeightsCanBeAddedInAnyOrder() {
        ChooseByWeightNodeList node = new ChooseByWeightNodeList();
        node.add(new BareTextNode("a"), 10);
        node.add(new BareTextNode("b"), 20);
        node.add(new BareTextNode("c"), 10);
        node.add(new BareTextNode("d"), 20);
        node.add(new BareTextNode("e"), 10);
        node.add(new BareTextNode("f"), 20);
        node.add(new BareTextNode("g"), 10);

        int numGenerations = 30000;
        Map<String, Integer> counts = runGenerations(node, numGenerations);

        assertPct(counts.get("a"), numGenerations, 10);
        assertPct(counts.get("b"), numGenerations, 20);
        assertPct(counts.get("c"), numGenerations, 10);
        assertPct(counts.get("d"), numGenerations, 20);
        assertPct(counts.get("e"), numGenerations, 10);
        assertPct(counts.get("f"), numGenerations, 20);
        assertPct(counts.get("g"), numGenerations, 10);
    }

    public void weightsCanSumToMoreThan100() {
        ChooseByWeightNodeList node = new ChooseByWeightNodeList();
        node.add(new BareTextNode("a"), 40);
        node.add(new BareTextNode("b"), 80);
        node.add(new BareTextNode("c"), 100);
        node.add(new BareTextNode("d"), 30);
        node.add(new BareTextNode("e"), 70);
        node.add(new BareTextNode("f"), 50);
        node.add(new BareTextNode("g"), 250);

        int numGenerations = 30000;
        Map<String, Integer> counts = runGenerations(node, numGenerations);

        // see the javadoc, but:  ( count / num generations ) SHOULD roughly equal ( weight / sum of weights )
        assertPct(counts.get("a"), numGenerations, getPct(40, node.getSumOfWeights()));
        assertPct(counts.get("b"), numGenerations, getPct(80, node.getSumOfWeights()));
        assertPct(counts.get("c"), numGenerations, getPct(100, node.getSumOfWeights()));
        assertPct(counts.get("d"), numGenerations, getPct(30, node.getSumOfWeights()));
        assertPct(counts.get("e"), numGenerations, getPct(70, node.getSumOfWeights()));
        assertPct(counts.get("f"), numGenerations, getPct(50, node.getSumOfWeights()));
        assertPct(counts.get("g"), numGenerations, getPct(250, node.getSumOfWeights()));
    }

    public void canBeRegisteredAsType(){
        ConjureTemplate template = new ConjureTemplate();
        template.addNodeType("weighted", ChooseByWeightNodeList.class);
        template.addNodeTemplate("sample", "My favorite is [${type:\"weighted\", list:[\"10:a\",\"20:b\",\"70:c\"], separator:\",\"}]");
        CombineNodeList sampleNodes = (CombineNodeList) template.getNode("sample");
        ChooseByWeightNodeList weightedNodes = (ChooseByWeightNodeList)sampleNodes.getNodes().get(1);
        assertEquals(((WeightedNode)weightedNodes.getNodes().get(0)).getWeight(), 10);
        assertEquals(((WeightedNode)weightedNodes.getNodes().get(1)).getWeight(), 20);
        assertEquals(((WeightedNode) weightedNodes.getNodes().get(2)).getWeight(), 70);

        String text = template.next();
        String fav = text.substring(text.indexOf('[')+1, text.indexOf(']'));
        assertTrue(Arrays.asList("a", "b", "c").contains(fav));
    }

    private void assertPct(int count, int total, double expectedPct){
        // this is frustratingly flaky.  Vast majority is within tolerance of .3, but fails here and there.
        assertEquals(getPct(count, total), expectedPct, 1);
    }

    private Map<String, Integer> runGenerations(ChooseByWeightNodeList node, int numGenerations) {
        Map<String,Integer> counts = new HashMap<String,Integer>();
        for(int i=0; i<numGenerations; i++){
            String generatedVal = node.generate(new StringBuilder()).toString();
            Integer count = counts.get(generatedVal);
            if(count == null){
                count = 0;
            }
            counts.put(generatedVal, count+1);
        }
        return counts;
    }

    private double getPct(Integer count, int total) {
        return 100*(count/(double)total);
    }
}
