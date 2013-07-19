package io.d8a.conjure;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SpecTest
{
  int numColumns=3;
  int cardinality=10;
  String name = "column";
  IntSpec intSpec = new IntSpec(numColumns,cardinality,name);
  DoubleSpec doubleSpec = new DoubleSpec(numColumns,cardinality,name);
  StringSpec stringSPec = new StringSpec(numColumns,cardinality,name);
  LongSpec longSpec = new LongSpec(numColumns,cardinality,name);

  @Test
  public void intSpecTest(){
    CardinalityNodeList nodeList = new CardinalityNodeList();
    intSpec.addNodes(nodeList);
    CardinalityNodeList expectedList = new CardinalityNodeList();
    for (int i=0;i<numColumns;i++){
      expectedList.add(new IntCardinalityNode(name + i, cardinality));
    }
    Assert.assertEquals(nodeList, expectedList);
  }
  @Test
  public void doubleSpecTest(){
    CardinalityNodeList nodeList = new CardinalityNodeList();
    doubleSpec.addNodes(nodeList);
    CardinalityNodeList expectedList = new CardinalityNodeList();
    for (int i=0;i<numColumns;i++){
      expectedList.add(new DoubleCardinalityNode(name + i, cardinality));
    }
    Assert.assertEquals(nodeList, expectedList);
  }
  @Test
  public void LongSpecTest(){
    CardinalityNodeList nodeList = new CardinalityNodeList();
    longSpec.addNodes(nodeList);
    CardinalityNodeList expectedList = new CardinalityNodeList();
    for (int i=0;i<numColumns;i++){
      expectedList.add(new LongCardinalityNode(name + i, cardinality));
    }
    Assert.assertEquals(nodeList, expectedList);
  }
  @Test
  public void stringSpecTest(){
    CardinalityNodeList nodeList = new CardinalityNodeList();
    stringSPec.addNodes(nodeList);
    CardinalityNodeList expectedList = new CardinalityNodeList();
    for (int i=0;i<numColumns;i++){
      expectedList.add(new StringCardinalityNode(name + i, cardinality));
    }
    Assert.assertEquals(nodeList, expectedList);
  }

  @Test (expectedExceptions = IllegalArgumentException.class)
  public void negativeCountTest(){
    Spec spec = new Spec(-5,10,"column")
    {
      @Override
      public CardinalityNode createNewNode(String name, int cardinality)
      {
        return new IntCardinalityNode("test",4);
      }
    };
  }


}
