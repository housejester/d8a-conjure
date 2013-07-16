package io.d8a.conjure;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DoubleSpecTest
{
  @Test
  public void testAddNodes() throws Exception
  {
    CardinalityNodeList nodeList = new CardinalityNodeList();
    int numColumns=3;
    int cardinality=10;
    String name = "doublecolumn";
    DoubleSpec spec = new DoubleSpec(numColumns,cardinality,name);
    spec.addNodes(nodeList);

    CardinalityNodeList expectedList = new CardinalityNodeList();
    for (int i=0;i<numColumns;i++){
      expectedList.addNode(new DoubleCardinalityNode(name+i,cardinality));
    }
    Assert.assertEquals(nodeList, expectedList);
  }

}
