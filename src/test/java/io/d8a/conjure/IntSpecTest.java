package io.d8a.conjure;

import org.testng.Assert;
import org.testng.annotations.Test;

public class IntSpecTest
{
  @Test
  public void testAddNodes() throws Exception
  {
    CardinalityNodeList nodeList = new CardinalityNodeList();
    int numColumns=3;
    int cardinality=10;
    String name = "intcolumn";
    IntSpec spec = new IntSpec(numColumns,cardinality,name);
    spec.addNodes(nodeList);

    CardinalityNodeList expectedList = new CardinalityNodeList();
    for (int i=0;i<numColumns;i++){
      expectedList.addNode(new IntCardinalityNode(name,cardinality));
    }
    Assert.assertEquals(nodeList,expectedList);
  }
}
