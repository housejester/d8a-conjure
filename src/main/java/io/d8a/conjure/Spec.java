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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Spec
{
  private int count;
  private int cardinality;
  private String type;
  private String name = "column";

  @JsonCreator
  public Spec(@JsonProperty("count") int count,
              @JsonProperty("cardinality") int cardinality,
              @JsonProperty("type") String type,
              @JsonProperty("name") String name)
  {
    this.count = count;
    this.cardinality = cardinality;
    if (type==null){
      throw new IllegalArgumentException("Type is missing");
    }
    this.type = type;
    if (name!=null){
      this.name = name;
    }
  }

  public List<NodeBuilder> addRequirements(ArrayList<NodeBuilder> builderList) throws IllegalArgumentException{
    for (int i =0;i<count;i++){
      builderList.add(new NodeBuilder(type,cardinality,name));
    }
    return builderList;
  }
}
