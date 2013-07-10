package io.d8a.conjure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public class DataConstructor
{
  private Logger log;
  private int numColumns;
  private int numColumnsCreated = 0;
  private Map<String, Integer> cardinalityRequirements;
  private Map<String, Integer> typeRequirements;
  private List<String> aggregators;
  ArrayList<NodeBuilder> builderList = Lists.newArrayList();
  private final int defaultCardinality;
  private final String defaultType;
  private Map<String, Integer> numNodesOfEachType = new HashMap<String, Integer>();
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
    this.numColumns = numcolumns;
    this.cardinalityRequirements = cardinalityRequirements;
    this.typeRequirements = typeRequirements;
    this.aggregators = aggregators;
    this.defaultCardinality = defaultCardinality;
    this.defaultType = defaultType;
    this.specialColumnList = specialColumnList;
  }

  public CardinalityNodeList createNodes()
  {
    CardinalityNodeList nodeList = new CardinalityNodeList();
    validateArguments();
    addAllColumnBuilders();
    addTypeRequirements();
    addCardinalityRequirements();
    addMetricRequirements();
    addSpecialColumns();
    for (NodeBuilder builder : builderList) {
      nodeList.addNode(builder.build());
    }
    return nodeList;
  }


  private void addTypeRequirements()
  {
    int j=0;
    for (String type : typeRequirements.keySet()) {
      int numColumnsToCreate = typeRequirements.get(type);
      for (int i = 0; i < numColumnsToCreate; i++) {
        //builderList is always an ArrayList so this inner loop should be O(n)
        builderList.get(j+i).setType(type);
      }
      j+=numColumnsToCreate;
    }
  }

  private void addCardinalityRequirements()
  {
    int j = 0;
    for (String cardinality : cardinalityRequirements.keySet()) {
      int numColumnsToCreate = cardinalityRequirements.get(cardinality).intValue();
      for (int k = 0; k < numColumnsToCreate; k++) {
        try {
          builderList.get(j+k).setCardinality(Integer.parseInt(cardinality));
        }
        catch (Exception e) {
          Throwables.propagate(e);
        }
      }
      j+=numColumnsToCreate;
    }
  }


  private void addSpecialColumns() throws IllegalArgumentException
  {
    for (SpecialColumn col : specialColumnList) {
      addBuilder(col.getType(),col.getCardinality(),col.getName());
    }
  }

  public void addBuilder(String type, int cardinality, String name){
    builderList.add(new NodeBuilder(type,cardinality,name));
    numColumnsCreated++;
    updateTypesCreated(type);
  }


  private void addMetricRequirements() throws IllegalArgumentException
  {
    for (String aggregator : aggregators) {
      if (aggregator.equals("longsum")) {
        if (numNodesOfEachType.get("long").intValue() == 0) {
          addBuilder("long",defaultCardinality,"column"+numColumnsCreated);
          log.info("Creating column of type long for the longsum aggregator");
        }
      } else if (aggregator.equals("doublesum")) {
        if (numNodesOfEachType.get("double").intValue() == 0) {
          addBuilder("double", defaultCardinality, "column" + numColumnsCreated);
          log.info("Creating column of type long for the doublesum aggregator");
        }
      } else if (aggregator.equals("min") || aggregator.equals("max")) {
        if (numNodesOfEachType.get("int") == 0
            && numNodesOfEachType.get("double") == 0
            && numNodesOfEachType.get("long") == 0) {
          addBuilder("int", defaultCardinality, "column" + numColumnsCreated);
        }
      } else {
        throw new IllegalArgumentException("Invalid Aggregator");
      }
    }
  }

  private void addAllColumnBuilders()
  {
    for (int i = numColumnsCreated; i < numColumns; i++) {
      addBuilder(defaultType, defaultCardinality, "column" + numColumnsCreated);
    }
  }


  private void updateTypesCreated(String type)
  {
    Integer numberOfNodes = numNodesOfEachType.get(type);
    if (numberOfNodes != null) {
      numNodesOfEachType.put(type, new Integer(numberOfNodes.intValue() + 1));
    } else {
      numNodesOfEachType.put(type, new Integer(1));
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
    if (numColumns < typeColumnsSpecified + cardinalityColumnsSpecified) {
      throw new IllegalArgumentException(
          "Number of columns specified less than the number of specifications provided through cardinality and type"
      );
    }

    if (defaultCardinality < 0) {
      throw new IllegalArgumentException("Default cardinality can't be negative");
    }
  }

}