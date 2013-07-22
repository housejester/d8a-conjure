package io.d8a.conjure;

import java.util.List;
import java.util.Map;

public class ChooseInOrderNodeList extends NodeList
{
  private int next = 0;

  @Override
  protected void generateNonEmpty(StringBuilder buff)
  {
    if (next >= nodes.size()) {
      next = 0;
    }
    nodes.get(next++).generate(buff);
  }

  public static ChooseInOrderNodeList createNode(Map config, ConjureTemplate template)
  {
    ChooseInOrderNodeList nodes = new ChooseInOrderNodeList();
    List list = (List) config.get("list");
    if (list != null) {
      for (Object obj : list) {
        nodes.add(template.parseNodes(String.valueOf(obj)));
      }
    }
    return nodes;
  }
}
