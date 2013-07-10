package io.d8a.conjure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataConstructor
{
  private int numcolumns;
  private int numcolumnsCreated = 0;
  private Map<String, Integer> cardinalityRequirements;
  private Map<String, Integer> typeRequirements;
  private List<String> aggregators;
  List<NodeBuilder> builderList = Lists.newArrayList();
  private final int defaultCardinality;
  private final String defaultType;
  private Map<String, Integer> typeNodesCreated = new HashMap<String, Integer>();
  private List<SpecialColumn> specialColumnList;

  @JsonCreator
  public DataConstructor(
      @JsonProperty("columns") int numcolumns,
      @JsonProperty("cardinalityRequirements") Map<String, Integer> cardinalityRequirements,
      @JsonProperty("typeRequirements") Map<String, Integer> typeRequirements,
      @JsonProperty("aggregators") List<String> aggregators,
      @JsonProperty("defaultCardinality") int defaultCardinality,
      @JsonProperty("defaultType") String defaultType,
      @JsonProperty("special column list") List<SpecialColumn> specialColumnList
  )
  {
    this.numcolumns = numcolumns;
    this.cardinalityRequirements = cardinalityRequirements;
    this.typeRequirements = typeRequirements;
    this.aggregators = aggregators;
    this.defaultCardinality = defaultCardinality;
    this.defaultType = defaultType;
    this.specialColumnList = specialColumnList;
  }

  public CardinalityNodeList createNodes() throws Exception
  {
    CardinalityNodeList nodeList = new CardinalityNodeList();
    try {
      validateArguments();
      addTypeRequirements();
      addMetricRequirements();
      addAllcolumnBuilders();
      addCardinalityRequirements();
      addSpecialColumns();
      for (NodeBuilder builder : builderList) {
        nodeList.addNode(builder.build());
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      Throwables.propagate(e);
    }
    return nodeList;
  }


  private void addTypeRequirements()
  {
    for (String type : typeRequirements.keySet()) {
      for (int i = 0; i < typeRequirements.get(type); i++) {
        addRelevantBuilder(type);
      }
    }
  }

  private void addSpecialColumns() throws IllegalArgumentException
  {
    for (SpecialColumn col : specialColumnList) {
      NodeBuilder builder = getCorrectTypeBuilder(col.getType(), col.getName());
      try {
        builder.setCardinality(col.getCardinality());
        builderList.add(builder);
      }
      catch (Exception e) {
        e.printStackTrace();
        Throwables.propagate(e);
      }
    }
  }

  private void addCardinalityRequirements()
  {
    int j = 0;
    for (String cardinality : cardinalityRequirements.keySet()) {
      for (int k = 0; k < cardinalityRequirements.get(cardinality).intValue(); k++) {
        try {
          builderList.get(j).setCardinality(Integer.parseInt(cardinality));
        }
        catch (Exception e) {
          e.printStackTrace();
          Throwables.propagate(e);
        }
        j++;
      }
    }
  }

  private void addMetricRequirements() throws IllegalArgumentException
  {
    for (String aggregator : aggregators) {
      if (aggregator.equals("longsum")) {
        if (typeNodesCreated.get("long").intValue() == 0) {
          addRelevantBuilder("long");
          System.out.println("Creating column of type long for the longsum aggregator");
        }
      } else if (aggregator.equals("doublesum")) {
        if (typeNodesCreated.get("double").intValue() == 0) {
          addRelevantBuilder("double");
          System.out.println("Creating column of type long for the doublesum aggregator");
        }
      } else if (aggregator.equals("min") || aggregator.equals("max")) {
        if (typeNodesCreated.get("int") == 0
            && typeNodesCreated.get("double") == 0
            && typeNodesCreated.get("long") == 0) {
          addRelevantBuilder("long");
        }
      } else {
        throw new IllegalArgumentException("Invalid Aggregator");
      }
    }
  }

  private void addAllcolumnBuilders()
  {
    for (int i = numcolumnsCreated; i < numcolumns; i++) {
      addRelevantBuilder(defaultType);
    }
  }


  public void addRelevantBuilder(String type) throws IllegalArgumentException
  {
    try {
      builderList.add(getCorrectTypeBuilder(type, "column" + numcolumnsCreated));
      updateTypesCreated(type);
      numcolumnsCreated++;
    }
    catch (Exception e) {
      Throwables.propagate(e);
    }
  }

  public NodeBuilder getCorrectTypeBuilder(String type, String name) throws IllegalArgumentException
  {
    NodeBuilder builder;
    if (type.equals("long")) {
      builder = new LongNodeBuilder(defaultCardinality, name);
    } else if (type.equals("int")) {
      builder = new IntNodeBuilder(defaultCardinality, name);
    } else if (type.equals("double")) {
      builder = new DoubleNodeBuilder(defaultCardinality, name);
    } else if (type.equals("string")) {
      builder = new StringNodeBuilder(defaultCardinality, name);
    } else {
      throw new IllegalArgumentException("Incorrect type specified: " + type);
    }
    return builder;
  }

  private void updateTypesCreated(String type)
  {
    Integer numberOfNodes = typeNodesCreated.get(type);
    if (numberOfNodes != null) {
      typeNodesCreated.put(type, new Integer(numberOfNodes.intValue() + 1));
    } else {
      typeNodesCreated.put(type, new Integer(1));
    }
  }

  public void validateArguments() throws IllegalArgumentException
  {
    int cardinalityColumnsSpecified = 0;
    int typeColumnsSpecified = 0;

    for (String card : cardinalityRequirements.keySet()) {
      try {
        int cols = Integer.parseInt(card);
      }
      catch (Exception e) {
        throw new IllegalArgumentException("Invalid number of Columns specified under the cardinality requirements", e);
      }
      if (cardinalityRequirements.get(card).intValue() < 0) {
        throw new IllegalArgumentException(
            "Can't specify a negative number of columns under the cardinality requirements. Number specified : "
            + cardinalityRequirements.get(card)
        );
      }
      cardinalityColumnsSpecified = cardinalityColumnsSpecified + cardinalityRequirements.get(card);
    }
    for (String type : typeRequirements.keySet()) {
      if (typeRequirements.get(type).intValue() < 0) {
        throw new IllegalArgumentException(
            "Can't specify a negative number of columns under the type requirement. Number specified: "
            + typeRequirements.get(type)
        );
      }

      typeColumnsSpecified = typeColumnsSpecified + typeRequirements.get(type);
    }
    if (numcolumns < typeColumnsSpecified + cardinalityColumnsSpecified) {
      throw new IllegalArgumentException(
          "Number of columns specified less than the number of specifications provided through cardinality and type"
      );
    }

    if (defaultCardinality < 0) {
      throw new IllegalArgumentException("Default cardinality can't be negative");
    }
  }

}
