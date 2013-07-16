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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo(
    use=JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property="type")
@JsonSubTypes({
                  @Type(value=LongSpec.class, name = "long"),
                  @Type(value=IntSpec.class, name = "int"),
                  @Type(value=DoubleSpec.class, name = "double"),
                  @Type(value=StringSpec.class, name = "string")
              }
)
public abstract class Spec
{
  protected int count;
  protected int cardinality;
  protected String type;
  protected String name = "column";

  public Spec(
      int count,
      int cardinality,
      String name
  )
  {
    this.count = count;
    this.cardinality = cardinality;
    if (name != null) {
      this.name = name;
    }
  }

  public abstract CardinalityNodeList addNodes(CardinalityNodeList nodeList) throws IllegalArgumentException;
}
