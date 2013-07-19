package io.d8a.conjure;

import com.beust.jcommander.internal.Maps;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

public class CardinalityNodeTest
{
  ObjectMapper mapper = new ObjectMapper();
  DoubleCardinalityNode doubleNode = new DoubleCardinalityNode("test1",100);
  LongCardinalityNode longNode = new LongCardinalityNode("test2",100);
  IntCardinalityNode intNode = new IntCardinalityNode("test3",100);
  StringCardinalityNode stringNode = new StringCardinalityNode("test4",100);

  @BeforeClass
  public void setUp(){

  }
  @Test
  public void testDoubleCardinality() throws Exception
  {
    Map<Double,Integer> valuesSeen = Maps.newHashMap();
    for (int i=0;i<doubleNode.getCardinality();i++){
      valuesSeen.put(doubleNode.getValue(),1);
    }
    Assert.assertEquals(doubleNode.getCardinality(),valuesSeen.keySet().size());
  }

  @Test
  public void testLongCardinality() throws Exception
  {
    Map<Long,Integer> valuesSeen = Maps.newHashMap();
    for (int i=0;i<longNode.getCardinality();i++){
      valuesSeen.put(longNode.getValue(),1);
    }
    Assert.assertEquals(longNode.getCardinality(),valuesSeen.keySet().size());
  }

  @Test
  public void testIntCardinality() throws Exception
  {
    Map<Integer,Integer> valuesSeen = Maps.newHashMap();
    for (int i=0;i<intNode.getCardinality();i++){
      valuesSeen.put(intNode.getValue(),1);
    }
    Assert.assertEquals(longNode.getCardinality(),valuesSeen.keySet().size());
  }

  @Test
  public void testStringCardinality() throws Exception
  {
    Map<String,Integer> valuesSeen = Maps.newHashMap();
    for (int i=0;i<stringNode.getCardinality();i++){
      valuesSeen.put(stringNode.getValue(),1);
    }
    Assert.assertEquals(stringNode.getCardinality(),valuesSeen.keySet().size());
  }

  @Test (expectedExceptions = IllegalArgumentException.class)
  public void checkNegativeCardinality() throws Exception
  {
    CardinalityNode<Integer> node = new CardinalityNode<Integer>("test5",-5, new Counter<Integer>(-5){

      @Override
      protected Integer convertValue(int counter)
      {
        return 4;
      }
    })
    {
    };
  }


}
