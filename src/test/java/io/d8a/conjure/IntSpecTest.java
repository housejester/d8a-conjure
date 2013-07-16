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
