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

public class SpecTest
{
  @Test
  public void testAddRequirementsSingleCount()
  {
    Spec spec = new Spec(1, 10, "string", "column");
    ArrayList<NodeBuilder> builders = new ArrayList<NodeBuilder>();
    spec.addRequirements(builders);
    Assert.assertEquals(builders.size(),1);
    Assert.assertEquals(builders.get(0).getCardinality(),10);
    Assert.assertEquals(builders.get(0).getType(),"string");
    Assert.assertEquals(builders.get(0).getName(),"column");
  }

  @Test
  public void testAddRequirementsMultipleCount()
  {
    Spec spec = new Spec(5, 10, "string", "column");
    ArrayList<NodeBuilder> builders = new ArrayList<NodeBuilder>();
    spec.addRequirements(builders);
    Assert.assertEquals(builders.size(),5);
    for (int i=0;i<builders.size();i++){
      Assert.assertEquals(builders.get(i).getCardinality(),10);
      Assert.assertEquals(builders.get(i).getType(),"string");
      Assert.assertEquals(builders.get(i).getName(),"column");
    }

  }
  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testMissingType(){
    Spec spec = new Spec(1, 10, null, "column");
    ArrayList<NodeBuilder> builders = new ArrayList<NodeBuilder>();
    spec.addRequirements(builders);

  }

}
