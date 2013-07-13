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
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

@JsonTypeName ("ColumnSpec")
public class ColumnSpec
{
  private final int count;
  private final List<Spec> specList;

  @JsonCreator
  public ColumnSpec(
      @JsonProperty("count") int count,
      @JsonProperty("specs") List<Spec> specList
  ){
    if (count<0){
      throw new IllegalArgumentException("column count can't be negative");
    }
    this.count = count;
    if (specList!=null){
      this.specList=specList;
    }
    else{
      this.specList=new ArrayList<Spec>();
    }
  }

  public List<NodeBuilder> getBuilderList(DefaultSpec defaultSpec) throws IllegalArgumentException{
    ArrayList<NodeBuilder> builderList = Lists.newArrayList();
    for(Spec spec:specList){
      spec.addRequirements(builderList);
    }
    if (builderList.size()>count){
      throw new IllegalArgumentException("More specified columns than total columns");
    }
    for (int i=builderList.size();i<count;i++){
      builderList.add(new NodeBuilder(defaultSpec.getType(),defaultSpec.getCardinality(),"column"));
    }
    return builderList;
  }

  public int getCount()
  {
    return count;
  }
}
