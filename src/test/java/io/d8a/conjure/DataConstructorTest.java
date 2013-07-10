package io.d8a.conjure;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class DataConstructorTest
{

  @Test
  public void testCreateNodesBasic() throws Exception
  {
    int numColumns = 5;
    String defaultType = "int";
    int defaultCardinality = 10;
    Map<String, Integer> cardinalityRequirements = Maps.newHashMap();
    cardinalityRequirements.put("10", 2);
    cardinalityRequirements.put("1", 2);
    cardinalityRequirements.put("100", 1);
    List<SpecialColumn> specialColumns = Lists.newArrayList();

    Map<String, Integer> typeRequirements = Maps.newHashMap();
    List<String> aggregators = Lists.newArrayList();
    DataConstructor dc = new DataConstructor(
        numColumns,
        cardinalityRequirements,
        typeRequirements,
        aggregators,
        defaultCardinality,
        defaultType,
        specialColumns
    );
    CardinalityNodeList nodeList = dc.createNodes();
    Assert.assertTrue(
        checkNodeRequirements(
            numColumns,
            cardinalityRequirements,
            typeRequirements,
            nodeList,
            specialColumns
        )
    );
  }

  @Test
  public void testSpecialColumn() throws Exception
  {
    int numColumns = 5;
    String defaultType = "int";
    int defaultCardinality = 10;
    Map<String, Integer> cardinalityRequirements = Maps.newHashMap();
    cardinalityRequirements.put("10", 2);
    cardinalityRequirements.put("1", 2);
    cardinalityRequirements.put("100", 1);
    List<SpecialColumn> specialColumns = Lists.newArrayList();
    specialColumns.add(new SpecialColumn("Revenue", 500, "int"));
    Map<String, Integer> typeRequirements = Maps.newHashMap();
    List<String> aggregators = Lists.newArrayList();
    DataConstructor dc = new DataConstructor(
        numColumns,
        cardinalityRequirements,
        typeRequirements,
        aggregators,
        defaultCardinality,
        defaultType,
        specialColumns
    );
    CardinalityNodeList nodeList = dc.createNodes();
    Assert.assertTrue(
        checkNodeRequirements(
            numColumns,
            cardinalityRequirements,
            typeRequirements,
            nodeList,
            specialColumns
        )
    );
  }

  @Test
  public void testCardinalities() throws Exception
  {
    int numColumns = 100;
    String defaultType = "int";
    int defaultCardinality = 20;
    Map<String, Integer> cardinalityRequirements = Maps.newHashMap();
    cardinalityRequirements.put("15", 31);
    cardinalityRequirements.put("4", 12);
    cardinalityRequirements.put("103", 20);
    List<SpecialColumn> specialColumns = Lists.newArrayList();

    Map<String, Integer> typeRequirements = Maps.newHashMap();
    List<String> aggregators = Lists.newArrayList();
    DataConstructor dc = new DataConstructor(
        numColumns,
        cardinalityRequirements,
        typeRequirements,
        aggregators,
        defaultCardinality,
        defaultType,
        specialColumns
    );
    ;
    CardinalityNodeList nodeList = dc.createNodes();
    Assert.assertTrue(
        checkNodeRequirements(
            numColumns,
            cardinalityRequirements,
            typeRequirements,
            nodeList,
            specialColumns
        )
    );
  }

  @Test
  public void testBasicTypes() throws Exception
  {
    int numColumns = 10;
    String defaultType = "int";
    int defaultCardinality = 10;
    Map<String, Integer> cardinalityRequirements = Maps.newHashMap();
    cardinalityRequirements.put("200", 1);
    List<SpecialColumn> specialColumns = Lists.newArrayList();

    Map<String, Integer> typeRequirements = Maps.newHashMap();
    typeRequirements.put("string", 4);
    typeRequirements.put("double", 3);
    List<String> aggregators = Lists.newArrayList();
    DataConstructor dc = new DataConstructor(
        numColumns,
        cardinalityRequirements,
        typeRequirements,
        aggregators,
        defaultCardinality,
        defaultType,
        specialColumns
    );
    CardinalityNodeList nodeList = dc.createNodes();
    Assert.assertTrue(
        checkNodeRequirements(
            numColumns,
            cardinalityRequirements,
            typeRequirements,
            nodeList,
            specialColumns
        )
    );
  }

  @Test
  public void testTypesAndCardinality() throws Exception
  {
    int numColumns = 20;
    String defaultType = "int";
    int defaultCardinality = 10;
    Map<String, Integer> cardinalityRequirements = Maps.newHashMap();
    List<SpecialColumn> specialColumns = Lists.newArrayList();

    Map<String, Integer> typeRequirements = Maps.newHashMap();

    typeRequirements.put("string", 4);
    typeRequirements.put("double", 3);
    List<String> aggregators = Lists.newArrayList();
    DataConstructor dc = new DataConstructor(
        numColumns,
        cardinalityRequirements,
        typeRequirements,
        aggregators,
        defaultCardinality,
        defaultType,
        specialColumns
    );
    CardinalityNodeList nodeList = dc.createNodes();
    Assert.assertTrue(
        checkNodeRequirements(
            numColumns,
            cardinalityRequirements,
            typeRequirements,
            nodeList,
            specialColumns
        )
    );
  }


  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testFewerColumnsThanTypesSpecified() throws Exception
  {
    int numColumns = 2;
    String defaultType = "int";
    List<SpecialColumn> specialColumns = Lists.newArrayList();

    int defaultCardinality = 10;
    Map<String, Integer> cardinalityRequirements = Maps.newHashMap();
    Map<String, Integer> typeRequirements = Maps.newHashMap();
    typeRequirements.put("string", 4);
    typeRequirements.put("double", 3);
    List<String> aggregators = Lists.newArrayList();
    DataConstructor dc = new DataConstructor(
        numColumns,
        cardinalityRequirements,
        typeRequirements,
        aggregators,
        defaultCardinality,
        defaultType,
        specialColumns
    );
    ;
    CardinalityNodeList nodeList = dc.createNodes();
  }


  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testFewerColumnsThanCardinalitiesSpecified() throws Exception
  {
    int numColumns = 2;
    String defaultType = "int";
    List<SpecialColumn> specialColumns = Lists.newArrayList();

    int defaultCardinality = 10;
    Map<String, Integer> cardinalityRequirements = Maps.newHashMap();
    cardinalityRequirements.put("200", 2);
    cardinalityRequirements.put("1", 2);
    cardinalityRequirements.put("100", 1);
    Map<String, Integer> typeRequirements = Maps.newHashMap();
    List<String> aggregators = Lists.newArrayList();
    DataConstructor dc = new DataConstructor(
        numColumns,
        cardinalityRequirements,
        typeRequirements,
        aggregators,
        defaultCardinality,
        defaultType,
        specialColumns
    );
    ;
    CardinalityNodeList nodeList = dc.createNodes();
    Assert.assertTrue(
        checkNodeRequirements(
            numColumns,
            cardinalityRequirements,
            typeRequirements,
            nodeList,
            specialColumns
        )
    );
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testTypesAggregatorsMismatch() throws Exception
  {
    int numColumns = 1;
    List<SpecialColumn> specialColumns = Lists.newArrayList();

    String defaultType = "int";
    int defaultCardinality = 10;
    Map<String, Integer> cardinalityRequirements = Maps.newHashMap();
    Map<String, Integer> typeRequirements = Maps.newHashMap();
    List<String> aggregators = Lists.newArrayList();
    aggregators.add("doubleSumAggregator");
    DataConstructor dc = new DataConstructor(
        numColumns,
        cardinalityRequirements,
        typeRequirements,
        aggregators,
        defaultCardinality,
        defaultType,
        specialColumns
    );
    ;
    CardinalityNodeList nodeList = dc.createNodes();
    Assert.assertTrue(
        checkNodeRequirements(
            numColumns,
            cardinalityRequirements,
            typeRequirements,
            nodeList,
            specialColumns
        )
    );
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testInvalidCardinalitySpecified() throws Exception
  {
    int numColumns = 1;
    String defaultType = "int";
    List<SpecialColumn> specialColumns = Lists.newArrayList();

    int defaultCardinality = 10;
    Map<String, Integer> cardinalityRequirements = Maps.newHashMap();
    cardinalityRequirements.put("6.78", 5);
    Map<String, Integer> typeRequirements = Maps.newHashMap();
    List<String> aggregators = Lists.newArrayList();
    aggregators.add("doubleSumAggregator");
    DataConstructor dc = new DataConstructor(
        numColumns,
        cardinalityRequirements,
        typeRequirements,
        aggregators,
        defaultCardinality,
        defaultType,
        specialColumns
    );
    ;
    CardinalityNodeList nodeList = dc.createNodes();
    Assert.assertTrue(
        checkNodeRequirements(
            numColumns,
            cardinalityRequirements,
            typeRequirements,
            nodeList,
            specialColumns
        )
    );
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testInvalidCardinalityNumColumnsSpecified() throws Exception
  {
    int numColumns = 1;
    String defaultType = "int";
    List<SpecialColumn> specialColumns = Lists.newArrayList();

    int defaultCardinality = 10;
    Map<String, Integer> cardinalityRequirements = Maps.newHashMap();
    cardinalityRequirements.put("10", -5);
    Map<String, Integer> typeRequirements = Maps.newHashMap();
    List<String> aggregators = Lists.newArrayList();
    aggregators.add("doubleSumAggregator");
    DataConstructor dc = new DataConstructor(
        numColumns,
        cardinalityRequirements,
        typeRequirements,
        aggregators,
        defaultCardinality,
        defaultType,
        specialColumns
    );
    ;
    CardinalityNodeList nodeList = dc.createNodes();
    Assert.assertTrue(
        checkNodeRequirements(
            numColumns,
            cardinalityRequirements,
            typeRequirements,
            nodeList,
            specialColumns
        )
    );
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testInvalidTypeSpecified() throws Exception
  {
    int numColumns = 1;
    String defaultType = "int";
    int defaultCardinality = 10;
    List<SpecialColumn> specialColumns = Lists.newArrayList();

    Map<String, Integer> cardinalityRequirements = Maps.newHashMap();
    Map<String, Integer> typeRequirements = Maps.newHashMap();
    typeRequirements.put("mumbojumbo", 5);
    List<String> aggregators = Lists.newArrayList();
    aggregators.add("doubleSumAggregator");
    DataConstructor dc = new DataConstructor(
        numColumns,
        cardinalityRequirements,
        typeRequirements,
        aggregators,
        defaultCardinality,
        defaultType,
        specialColumns
    );
    ;
    CardinalityNodeList nodeList = dc.createNodes();
    Assert.assertTrue(
        checkNodeRequirements(
            numColumns,
            cardinalityRequirements,
            typeRequirements,
            nodeList,
            specialColumns
        )
    );
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testInvalidTypeNumColumnsSpecified() throws Exception
  {
    int numColumns = 1;
    String defaultType = "int";
    List<SpecialColumn> specialColumns = Lists.newArrayList();

    int defaultCardinality = 10;
    Map<String, Integer> cardinalityRequirements = Maps.newHashMap();
    Map<String, Integer> typeRequirements = Maps.newHashMap();
    typeRequirements.put("10", -3);
    List<String> aggregators = Lists.newArrayList();
    aggregators.add("doubleSumAggregator");
    DataConstructor dc = new DataConstructor(
        numColumns,
        cardinalityRequirements,
        typeRequirements,
        aggregators,
        defaultCardinality,
        defaultType,
        specialColumns
    );
    ;
    CardinalityNodeList nodeList = dc.createNodes();
    Assert.assertTrue(
        checkNodeRequirements(
            numColumns,
            cardinalityRequirements,
            typeRequirements,
            nodeList,
            specialColumns
        )
    );
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void invalidAggregatorSpecified() throws Exception
  {
    int numColumns = 1;
    String defaultType = "int";
    List<SpecialColumn> specialColumns = Lists.newArrayList();

    int defaultCardinality = 10;
    Map<String, Integer> cardinalityRequirements = Maps.newHashMap();
    Map<String, Integer> typeRequirements = Maps.newHashMap();
    List<String> aggregators = Lists.newArrayList();
    aggregators.add("mumbojumbo");
    DataConstructor dc = new DataConstructor(
        numColumns,
        cardinalityRequirements,
        typeRequirements,
        aggregators,
        defaultCardinality,
        defaultType,
        specialColumns
    );
    ;
    CardinalityNodeList nodeList = dc.createNodes();
    Assert.assertTrue(
        checkNodeRequirements(
            numColumns,
            cardinalityRequirements,
            typeRequirements,
            nodeList,
            specialColumns
        )
    );
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void testNegativeColumns() throws Exception
  {
    int numColumns = -5;
    String defaultType = "int";
    List<SpecialColumn> specialColumns = Lists.newArrayList();

    int defaultCardinality = 10;
    Map<String, Integer> cardinalityRequirements = Maps.newHashMap();
    Map<String, Integer> typeRequirements = Maps.newHashMap();
    typeRequirements.put("mumbojumbo", 5);
    List<String> aggregators = Lists.newArrayList();
    aggregators.add("doubleSumAggregator");
    DataConstructor dc = new DataConstructor(
        numColumns,
        cardinalityRequirements,
        typeRequirements,
        aggregators,
        defaultCardinality,
        defaultType,
        specialColumns
    );
    ;
    CardinalityNodeList nodeList = dc.createNodes();
    Assert.assertTrue(
        checkNodeRequirements(
            numColumns,
            cardinalityRequirements,
            typeRequirements,
            nodeList,
            specialColumns
        )
    );
  }


  private Boolean checkNodeRequirements(
      int numColumns,
      Map<String, Integer> cardinalityRequirements,
      Map<String, Integer> typeRequirements,
      CardinalityNodeList nodeList,
      List<SpecialColumn> specialColumns
  )
  {
    int actualColumns = nodeList.getNodes().size();
    Map<String, Integer> actualCardinalities = Maps.newHashMap();
    Map<String, Integer> actualTypes = Maps.newHashMap();
    for (SpecialColumn col : specialColumns) {
      String card = "" + col.getCardinality();
      if (cardinalityRequirements.get(card) == null) {
        cardinalityRequirements.put(card, 1);
      } else {
        cardinalityRequirements.put(card, cardinalityRequirements.get(card) + 1);
      }
      if (typeRequirements.get(col.getType()) == null) {
        typeRequirements.put(col.getType(), 1);
      } else {
        typeRequirements.put(col.getType(), typeRequirements.get(col.getType()) + 1);
      }
      numColumns++;
    }
    for (VariableWithCardinality node : nodeList.getNodes()) {
      String cardOfColumn = Integer.toString(node.getCardinality());
      Integer numColumnsWithCardinality = actualCardinalities.get(cardOfColumn);
      String typeOfColumn = node.getType();
      Integer numColumnsWithType = actualTypes.get(typeOfColumn);
      if (numColumnsWithCardinality == null) {
        actualCardinalities.put(cardOfColumn, new Integer(1));
      } else {
        actualCardinalities.put(cardOfColumn, new Integer(numColumnsWithCardinality.intValue() + 1));
      }
      if (numColumnsWithType == null) {
        actualTypes.put(typeOfColumn, new Integer(1));
      } else {
        actualTypes.put(typeOfColumn, new Integer(numColumnsWithType.intValue() + 1));
      }
    }
    for (String cardinality : cardinalityRequirements.keySet()) {
      if (actualCardinalities.get(cardinality) == null
          || actualCardinalities.get(cardinality).compareTo(cardinalityRequirements.get(cardinality)) == -1) {
        return false;
      }
    }

    for (String type : typeRequirements.keySet()) {
      if (actualTypes.get(type) == null || actualTypes.get(type).compareTo(typeRequirements.get(type)) == -1) {
        return false;
      }
    }
    return actualColumns == numColumns;
  }

}
