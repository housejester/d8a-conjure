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
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

public class StringNodeTest
{
  StringNode node;
  int cardinality;
  String name;

  @BeforeClass
  public void setUp(){
      cardinality=100;
      name="test";
      node = new StringNode(name, cardinality);
  }

  @Test
  public void testGetCardinality() throws Exception
  {
    Assert.assertEquals(100,node.getCardinality());
  }

  public void testGetType() throws Exception
  {
    Assert.assertEquals("string",node.getType());
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
