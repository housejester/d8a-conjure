package io.d8a.conjure;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CardinalityNodeList implements ConjureTemplateNode
{
  protected List<CardinalityNode> cardinalityNodes = Lists.newArrayList();
  private Map<String, Object> event = Maps.newHashMap();
  private final Clock clock;

  public CardinalityNodeList(Clock clock)
  {
    this.clock = clock;
  }

  public CardinalityNodeList(List<CardinalityNode> nodes, Clock clock)
  {
    cardinalityNodes.addAll(nodes);
    this.clock = clock;
  }


  public void add(CardinalityNode node)
  {
    cardinalityNodes.add(node);
  }

  public List<CardinalityNode> getNodes()
  {
    return Collections.unmodifiableList(cardinalityNodes);
  }

  @Override
  public StringBuilder generate(StringBuilder buff)
  {
    return buff.append(generateEvent().toString());
  }

  public Map<String, Object> generateEvent()
  {
    for (CardinalityNode variable : cardinalityNodes) {
      event.put(variable.getName(), variable.getValue());
    }
    event.put("timestamp", clock.currentTimeMillis());
    return event;
  }

  public int size()
  {
    return cardinalityNodes.size();
  }

  @Override
  public int hashCode()
  {
    int result = cardinalityNodes.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object list)
  {
    return getNodes().equals(((CardinalityNodeList) list).getNodes());
  }
}
