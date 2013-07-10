package io.d8a.conjure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("special column")
public class SpecialColumn
{
  private final String name;
  private final int cardinality;
  private final String type;

  @JsonCreator
  public SpecialColumn(
      @JsonProperty("name") String name,
      @JsonProperty("cardinality") int cardinality,
      @JsonProperty("type") String type
  )
  {
    this.name = name;
    this.cardinality = cardinality;
    this.type = type;
  }

  public String getName()
  {
    return name;
  }

  public int getCardinality()
  {
    return cardinality;
  }

  public String getType()
  {
    return type;
  }
}
