package io.d8a.conjure;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CardinalityNodeList implements ConjureTemplateNode {
    protected List<CardinalityNode> cardinalityNodes = Lists.newArrayList();
    private Map<String, Object> event = Maps.newHashMap();

    public CardinalityNodeList(){
    }

    public CardinalityNodeList(List<CardinalityNode> nodes){
      cardinalityNodes.addAll(nodes);
    }


    public void add(CardinalityNode node){
        cardinalityNodes.add(node);
    }

    public List<CardinalityNode> getNodes(){
        return Collections.unmodifiableList(cardinalityNodes);
    }

    @Override
    public StringBuilder generate(StringBuilder buff){
        return buff.append(generateMap().toString());
    }

    public Map<String, Object> generateMap(){
        for(CardinalityNode variable : cardinalityNodes){
            event.put(variable.getName(), variable.getValue());
        }
        event.put("timestamp", Clock.SYSTEM_CLOCK.currentTimeMillis());
        return event;
    }

    public int size(){
        return cardinalityNodes.size();
    }

  @Override
  public int hashCode()
  {
    int result = cardinalityNodes.hashCode();
    return result;
  }

  @Override
    public boolean equals(Object list){
        return getNodes().equals(((CardinalityNodeList) list).getNodes());
    }
}
