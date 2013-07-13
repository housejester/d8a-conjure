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

import java.util.ArrayList;
import java.util.List;

public class ColumnSpecTest
{
  @Test
  public void testBasicGetBuilderList() throws Exception
  {
    DefaultSpec defaultSpec = new DefaultSpec(5,"int");
    ColumnSpec columnSpec = new ColumnSpec(1,null);
    List<NodeBuilder> builderList= columnSpec.getBuilderList(defaultSpec);
    Assert.assertEquals(builderList.size(),1);
    Assert.assertEquals(builderList.get(0).getCardinality(),5);
    Assert.assertEquals(builderList.get(0).getType(),"int");
  }
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testOverSpecifiedBuilderList() throws Exception
  {
    DefaultSpec defaultSpec = new DefaultSpec(5,"int");
    List<Spec> specList = new ArrayList<Spec>();
    specList.add(new Spec(1,5,"int",null));
    ColumnSpec columnSpec = new ColumnSpec(0,specList);
    List<NodeBuilder> builderList= columnSpec.getBuilderList(defaultSpec);
  }

  @Test
  public void testValidSpecInput() throws Exception
  {
    DefaultSpec defaultSpec = new DefaultSpec(5, "int");
    List<Spec> specList = new ArrayList<Spec>();
    specList.add(new Spec(1,5,"int",null));
    ColumnSpec columnSpec = new ColumnSpec(1,specList);
    List<NodeBuilder> builderList= columnSpec.getBuilderList(defaultSpec);
    Assert.assertEquals(builderList.size(),specList.size());
    Assert.assertEquals(builderList.get(0).getCardinality(),5);
    Assert.assertEquals(builderList.get(0).getType(),"int");
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNegativeColumns() throws Exception
  {
    ColumnSpec columnSpec = new ColumnSpec(-5,null);
  }

}
