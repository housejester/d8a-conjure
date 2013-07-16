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

import com.google.common.collect.ImmutableList;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class CardinalityNodeListBuilderTest
{
  private final Spec intSpec = new IntSpec(5,10,"intcolumn");
  private final Spec longSpec = new LongSpec(3,100,"longcolumn");
  private final Spec doubleSpec = new DoubleSpec(5,10,"doublecolumn");
  private final Spec stringSPec = new StringSpec(5,10,"stringcolumn");

  @Test
  public void testBasicBuild() throws Exception
  {
    List<Spec> specList = ImmutableList.<Spec>of(intSpec);
    CardinalityNodeListBuilder list = new CardinalityNodeListBuilder(specList);
    CardinalityNodeList expectedList = new CardinalityNodeList();
    for (int i=0;i<5;i++)
    {
      expectedList.addNode(new IntCardinalityNode("intcolumn",10));
    }
    Assert.assertEquals(expectedList, list.build());
  }
  @Test
  public void addMultipleSpecsBuild() throws Exception
  {
    List<Spec> specList = ImmutableList.<Spec>of(intSpec,longSpec);
    CardinalityNodeListBuilder list = new CardinalityNodeListBuilder(specList);
    CardinalityNodeList expectedList = new CardinalityNodeList();
    for (int i=0;i<5;i++)
    {
      expectedList.addNode(new IntCardinalityNode("intcolumn",10));
    }
    for (int i=0;i<3;i++)
    {
      expectedList.addNode(new LongCardinalityNode("longcolumn",100));
    }
    Assert.assertEquals(list.build(),expectedList);

  }
}
