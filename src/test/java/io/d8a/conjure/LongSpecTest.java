package io.d8a.conjure;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LongSpecTest
{
  @Test
  public void testAddNodes() throws Exception
  {
    CardinalityNodeList nodeList = new CardinalityNodeList();
    int numColumns=3;
    int cardinality=10;
    String name = "longcolumn";
    LongSpec spec = new LongSpec(numColumns,cardinality,name);
    spec.addNodes(nodeList);

    CardinalityNodeList expectedList = new CardinalityNodeList();
    for (int i=0;i<numColumns;i++){
      expectedList.addNode(new LongCardinalityNode(name+i,cardinality));
    }
    Assert.assertEquals(nodeList, expectedList);
  }

}
