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

  @BeforeClass
  public void setUp(){

  }
  @Test
  public void testDoubleValue() throws Exception
  {
    Map<Double,Integer> testMap = Maps.newHashMap();
    for (int i=0;i<doubleNode.getCardinality()*10;i++){
      testMap.put(doubleNode.getValue(),1);
    }
    Assert.assertEquals(doubleNode.getCardinality(),testMap.keySet().size());
  }

  @Test
  public void testLongValue() throws Exception
  {
    Map<Long,Integer> testMap = Maps.newHashMap();
    for (int i=0;i<longNode.getCardinality()*10;i++){
      testMap.put(longNode.getValue(),1);
    }
    Assert.assertEquals(longNode.getCardinality(),testMap.keySet().size());
  }

  @Test
  public void testIntValue() throws Exception
  {
    Map<Integer,Integer> testMap = Maps.newHashMap();
    for (int i=0;i<intNode.getCardinality()*10;i++){
      testMap.put(intNode.getValue(),1);
    }
    Assert.assertEquals(longNode.getCardinality(),testMap.keySet().size());
  }


}
