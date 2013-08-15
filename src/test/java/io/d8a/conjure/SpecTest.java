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
  StringSpec stringSpec = new StringSpec(numColumns,cardinality,name);
  LongSpec longSpec = new LongSpec(numColumns,cardinality,name);
  private final Clock clock= Clock.SYSTEM_CLOCK;

  @Test
  public void intSpecTest(){

    CardinalityNodeList nodeList = new CardinalityNodeList(clock);
    nodeList.add(intSpec.getNodesToAdd());
    CardinalityNodeList expectedList = new CardinalityNodeList(clock);
    for (int i=0;i<numColumns;i++){
      expectedList.add(new IntCardinalityNode(name + i, cardinality));
    }
    Assert.assertEquals(nodeList, expectedList);
  }
  @Test
  public void doubleSpecTest(){

    CardinalityNodeList nodeList = new CardinalityNodeList(clock);
    nodeList.add(doubleSpec.getNodesToAdd());
    CardinalityNodeList expectedList = new CardinalityNodeList(clock);
    for (int i=0;i<numColumns;i++){
      expectedList.add(new DoubleCardinalityNode(name + i, cardinality));
    }
    Assert.assertEquals(nodeList, expectedList);
  }
  @Test
  public void LongSpecTest(){
    CardinalityNodeList nodeList = new CardinalityNodeList(clock);
    nodeList.add(longSpec.getNodesToAdd());
    CardinalityNodeList expectedList = new CardinalityNodeList(clock);
    for (int i=0;i<numColumns;i++){
      expectedList.add(new LongCardinalityNode(name + i, cardinality));
    }
    Assert.assertEquals(nodeList, expectedList);
  }
  @Test
  public void stringSpecTest(){
    CardinalityNodeList nodeList = new CardinalityNodeList(clock);
    nodeList.add(stringSpec.getNodesToAdd());
    CardinalityNodeList expectedList = new CardinalityNodeList(clock);
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
