package io.d8a.conjure;

import org.testng.Assert;
import org.testng.annotations.Test;

public class StringSpecTest {
    @Test
    public void testAddNodes() throws Exception {
        CardinalityNodeList nodeList = new CardinalityNodeList();
        int numColumns = 3;
        int cardinality = 10;
        String name = "intcolumn";
        StringSpec spec = new StringSpec(numColumns, cardinality, name);
        spec.addNodes(nodeList);

        CardinalityNodeList expectedList = new CardinalityNodeList();
        for(int i = 0; i < numColumns; i++){
            expectedList.addNode(new StringCardinalityNode(name+i, cardinality));
        }
        Assert.assertEquals(nodeList, expectedList);
    }

}
