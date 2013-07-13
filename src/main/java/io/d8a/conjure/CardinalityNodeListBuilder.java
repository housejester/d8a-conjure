package io.d8a.conjure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.logging.Logger;


public class CardinalityNodeListBuilder
{
  private Logger log;
  private final JsonSchema schema;

  @JsonCreator
  public CardinalityNodeListBuilder(
      @JsonProperty("schema") JsonSchema schema
  )
  {
    this.schema=schema;
  }

  public CardinalityNodeList build() throws IllegalArgumentException
  {
    CardinalityNodeList nodeList = new CardinalityNodeList();
    List<NodeBuilder> builderList = schema.getColumnSpec().getBuilderList(schema.getDefaults());
    for (NodeBuilder builder: builderList){
      nodeList.addNode(builder.build());
    }
    return nodeList;
  }
}