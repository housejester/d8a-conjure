package io.d8a.conjure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CardinalityNodeList implements ConjureTemplateNode
{
  protected List<VariableWithCardinality> cardinalityNodes = Lists.newArrayList();
  private Map<String, Object> jsonMap = Maps.newHashMap();
  private ObjectMapper mapper = new ObjectMapper();
  public CardinalityNodeList(List<VariableWithCardinality> nodes)
  {
    for (VariableWithCardinality node : nodes) {
      cardinalityNodes.add(node);
    }
  }

  public CardinalityNodeList()
{
}

  public void addNode(VariableWithCardinality node){
    cardinalityNodes.add(node);
  }

  public int getSize()
  {
    return cardinalityNodes.size();
  }

  public List<VariableWithCardinality> getNodes(){
    return Collections.unmodifiableList(cardinalityNodes);
  }

  @Override
  public StringBuilder generate(StringBuilder buff)
  {
    return buff.append(generateMap().toString());
  }

  public Map<String,Object> generateMap(){
    for (VariableWithCardinality variable : cardinalityNodes) {
      jsonMap.put(variable.getName(), variable.getValue());
    }
    jsonMap.put("time", System.currentTimeMillis());
    return jsonMap;
  }
}
