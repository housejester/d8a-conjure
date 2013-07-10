package io.d8a.conjure;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConjureTemplate
{
  private Map<String, ConjureTemplateNode> nodes;
  private CardinalityNodeList variableList;
  private Map<String, Method> typeRegistry;
  private Clock clock;
  private String refOpenToken = "${";
  private String refCloseToken = "}";
  private Map<String, String> namedNodeValueCache;

  private static final ObjectMapper json = new ObjectMapper();

  static {
    json.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
  }

  public ConjureTemplate()
  {
    this(Clock.SYSTEM_CLOCK);
  }

  public ConjureTemplate(Clock clock)
  {
    this(clock, "${", "}");
  }

  public ConjureTemplate(Clock clock, String openToken, String closeToken)
  {
    this.clock = clock;
    nodes = new HashMap<String, ConjureTemplateNode>();
    typeRegistry = new HashMap<String, Method>();
    this.refOpenToken = openToken;
    this.refCloseToken = closeToken;
    this.namedNodeValueCache = new HashMap<String, String>();
    this.variableList = new CardinalityNodeList();
  }

  public Clock getClock()
  {
    return clock;
  }


  public void addFragment(String name, String template)
  {
    addNode(name, parseNodes(template));
  }

  public ConjureTemplateNode parseNodes(String text)
  {
    List<ConjureTemplateNode> nodes = compileToNodeList(text);
    if (nodes.size() == 1) {
      return nodes.get(0);
    }
    return new CombineNodeList(nodes);
  }

  public String conjure(String templateName)
  {
    if (nodes.containsKey(templateName)) {
      try {
        return nodes.get(templateName).generate(new StringBuilder()).toString();
      }
      finally {
        namedNodeValueCache.clear();
      }
    }
    throw new IllegalArgumentException("Node '" + templateName + "' not found in the sample generator.");
  }

  public String conjure()
  {
    return conjure("sample");
  }

  public Map<String,Object> conjureMapData()
  {
    return variableList.generateMap();
  }

  public ConjureTemplateNode getNode(String name)
  {
    return nodes.get(name);
  }

  public void addNode(String name, ConjureTemplateNode node)
  {
    //when adding nodes directly via the api, mamoization will not happen.  The assumption is that the caller can
    //have full control over that behavior.
    if (nodes.containsKey(name)) {
      throw new IllegalArgumentException("Node '" + name + "' already added to this generator.");
    }
    this.nodes.put(name, node);
  }

  public void addNodeType(final String typeName, Class nodeType)
  {
    final Method creator;
    try {
      creator = lookupCreatorMethod(nodeType);
    }
    catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(
          "Could not find creator method for class '"
          + nodeType
          + "'.  Needs to have a static method called 'createNode' that takes Map,ConjureTemplate, or just a Map."
      );
    }
    typeRegistry.put(typeName, creator);
  }

  public void setVariableList(CardinalityNodeList list)
  {
    this.variableList = list;
  }

  private Method lookupCreatorMethod(Class nodeType) throws NoSuchMethodException
  {
    try {
      return nodeType.getMethod("createNode", Map.class, ConjureTemplate.class);
    }
    catch (NoSuchMethodException e) {
    }
    return nodeType.getMethod("createNode", Map.class);
  }

  public Map parseFirstConfig(String text)
  {
    Snippet refSnip = findRef(text);
    if (refSnip == null) {
      return Collections.emptyMap();
    }
    String ref = text.substring(refSnip.start + refOpenToken.length(), refSnip.stop).trim();
    try {
      return json.readValue("{" + ref + "}", Map.class);
    }
    catch (Exception ex) {
    }
    return Collections.emptyMap();
  }

  private List<ConjureTemplateNode> compileToNodeList(String text)
  {
    List<ConjureTemplateNode> nodes = new ArrayList<ConjureTemplateNode>();
    Snippet refSnip = findRef(text);
    while (refSnip != null) {
      if (refSnip.start > 0) {
        nodes.add(new BareTextNode(text.substring(0, refSnip.start)));
      }

      String ref = text.substring(refSnip.start + refOpenToken.length(), refSnip.stop).trim();
      ConjureTemplateNode refNode = resolveNodeFromRef(ref);
      nodes.add(refNode);

      text = text.substring(refSnip.stop + refCloseToken.length());
      refSnip = findRef(text);
    }
    if (!text.isEmpty()) {
      nodes.add(new BareTextNode(text));
    }
    return nodes;
  }

  private ConjureTemplateNode resolveNodeFromRef(String ref)
  {
    Map config;
    try {
      config = json.readValue("{" + ref + "}", Map.class);
    }
    catch (Exception ex) {
      ConjureTemplateNode node = this.nodes.get(ref);
      if (node == null) {
        node = new LazyRefNode(ref, this);
      }
      return node;
    }
    ConjureTemplateNode node = null;
    String typeName = (String) config.get("type");
    if (typeName != null) {
      Method nodeCreator = resolveNodeCreator(typeName);

      node = createNodeFromMethod(typeName, nodeCreator, config, this);
    } else if (config.containsKey("ref")) {
      node = new LazyRefNode((String) config.get("ref"), this);
    } else {
      throw new IllegalArgumentException("Must specify either 'type' or 'ref'.");
    }
    String name = (String) config.get("name");
    if (name != null) {
      //TODO: don't like how this happens under the hood as a side-effect of parsing nodes from text
      Boolean remember = (Boolean) config.get("remember");
      if (remember == null || remember) {
        node = new MemoizingNode(node, name, namedNodeValueCache);
      }
      this.addNode(name, node);
    }
    return node;
  }

  private Method resolveNodeCreator(String typeName)
  {
    Method nodeCreator = typeRegistry.get(typeName);
    if (nodeCreator != null) {
      return nodeCreator;
    }
    Class clazz;
    try {
      clazz = Class.forName(typeName);
    }
    catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("Unknown sample node nodeCreator '" + typeName + "'.");
    }
    addNodeType(typeName, clazz);
    return typeRegistry.get(typeName);
  }

  private ConjureTemplateNode createNodeFromMethod(
      String typeName,
      Method creator,
      Map config,
      ConjureTemplate generator
  )
  {
    Object[] args = new Object[creator.getParameterTypes().length];
    args[0] = config;
    if (args.length > 1) {
      args[1] = generator;
    }
    try {
      return (ConjureTemplateNode) creator.invoke(null, args);
    }
    catch (Exception e) {
      throw new IllegalStateException("Problem creating the '" + typeName + "' node.", e);
    }
  }

  private Snippet findRef(String text)
  {
    int refOpen = text.indexOf(refOpenToken);
    if (refOpen < 0) {
      return null;
    }
    int refClose = text.indexOf(refCloseToken, refOpen);
    if (refClose < 0) {
      return null;
    }
    return new Snippet(refOpen, refClose);
  }

  private static class Snippet
  {
    final int start;
    final int stop;

    Snippet(int start, int stop)
    {
      this.start = start;
      this.stop = stop;
    }
  }
}
