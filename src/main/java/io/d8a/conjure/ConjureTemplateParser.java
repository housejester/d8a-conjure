package io.d8a.conjure;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class ConjureTemplateParser
{
  ConjureTemplate template;

  public ConjureTemplateParser()
  {
    this(Clock.SYSTEM_CLOCK);
  }

  public ConjureTemplateParser(Clock clock)
  {
    template = new ConjureTemplate(clock);
    registerStandardTypes();
  }

  private void registerStandardTypes()
  {
    template.addNodeType("time", TimeNode.class);
    template.addNodeType("minmax", MinMaxNode.class);
    template.addNodeType("randomChoice", ChooseRandomNodeList.class);
    template.addNodeType("cycle", ChooseInOrderNodeList.class);
    template.addNodeType("combine", CombineNodeList.class);
    template.addNodeType("weighted", ChooseByWeightNodeList.class);
    template.addNodeType("increment", IncrementNode.class);
  }

  public ConjureTemplate jsonParse(String filePath) throws IOException
  {
    ObjectMapper mapper = new ObjectMapper();
    File file = new File(filePath);
    DataConstructor dc = mapper.readValue(file, DataConstructor.class);
    template.setVariableList(dc.createNodes());
    return template;
  }

  public ConjureTemplate parse(InputStream inputStream) throws IOException
  {
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    String line = reader.readLine();
    NodeList currentNodeList = null;
    String endToken = "";
    String delimiter = "\n";
    boolean shouldTrim = false;
    CombineNodeList list = new CombineNodeList("\n");
    while (line != null) {
      if (isEndToken(line, endToken)) {
        currentNodeList = null;
        endToken = "";
        shouldTrim = false;
        delimiter = "\n";
      } else if (!isBlank(line)) {
        if (shouldTrim) {
          line = line.trim();
        }
        if (currentNodeList != null) {
          String[] items = new String[]{line};
          if (!delimiter.equals("\n")) {
            items = line.split(delimiter);
          }
          for (String item : items) {
            ConjureTemplateNode node = null;
            if (currentNodeList instanceof ChooseByWeightNodeList) {
              node = ChooseByWeightNodeList.parseWeightedNode(item, template);
            } else {
              node = template.parseNodes(item);
            }
            currentNodeList.add(node);
          }
        } else {
          ConjureTemplateNode node = template.parseNodes(line);
          NodeList nodeAsNodeList = unwrapNodeList(node);
          if (nodeAsNodeList != null && nodeAsNodeList.isEmpty()) {
            currentNodeList = nodeAsNodeList;
            endToken = parseEndToken(line, template);
            shouldTrim = parseTrim(line, template);
            delimiter = parseDelimiter(line, template);
            list.add(node);
          } else {
            list.add(node);
          }
        }
      }
      line = reader.readLine();
    }
    if (template.getNode("sample") == null) {
      template.addNode("sample", list);
    }
    return template;
  }

  private boolean parseTrim(String line, ConjureTemplate template)
  {
    Map config = template.parseFirstConfig(line);
    if (config.containsKey("trim")) {
      return (Boolean) config.get("trim");
    }
    return false;
  }

  private String parseDelimiter(String line, ConjureTemplate template)
  {
    Map config = template.parseFirstConfig(line);
    if (config.containsKey("delimiter")) {
      return (String) config.get("delimiter");
    }
    return "\n";
  }

  private NodeList unwrapNodeList(ConjureTemplateNode node)
  {
    ConjureTemplateNode unwrapped = unwrapNode(node);
    if (unwrapped instanceof NodeList) {
      return (NodeList) unwrapped;
    }
    return null;
  }

  private ConjureTemplateNode unwrapNode(ConjureTemplateNode node)
  {
    if (node instanceof MemoizingNode) {
      return ((MemoizingNode) node).getTargetNode();
    }
    return node;
  }

  private String parseEndToken(String line, ConjureTemplate template)
  {
    Map config = template.parseFirstConfig(line);
    if (config.containsKey("endToken")) {
      return (String) config.get("endToken");
    }
    return "";
  }

  private boolean isEndToken(String line, String endToken)
  {
    return line.equals(endToken);
  }

  private boolean isBlank(String line)
  {
    String trimmed = line.trim();
    return trimmed.isEmpty() || trimmed.startsWith("#");
  }
}
