/*
 * Druid - a distributed column store.
 * Copyright (C) 2012  Metamarkets Group Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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
