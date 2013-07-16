package io.d8a.conjure;

import com.beust.jcommander.internal.Maps;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

public class StringCardinalityNodeTest
{
  StringCardinalityNode node;
  int cardinality;
  String name;

  @BeforeClass
  public void setUp(){
      cardinality=100;
      name="test";
      node = new StringCardinalityNode(name, cardinality);
  }

  @Test
  public void testGetCardinality() throws Exception
  {
    Assert.assertEquals(100,node.getCardinality());
  }

  public void testSetName() throws Exception
  {
    node.setName("test1");
    Assert.assertEquals("test1",node.getName());
  }

  public void testGetValue() throws Exception
  {
    Map<String,Integer> cardinalityChecker = Maps.newHashMap();
    for (int i=0;i<node.getCardinality()*10; i++) {
      cardinalityChecker.put(node.getValue(),1);
    }
    Assert.assertEquals(cardinalityChecker.keySet().size(),node.getCardinality());
  }
}
