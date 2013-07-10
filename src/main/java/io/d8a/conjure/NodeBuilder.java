package io.d8a.conjure;

public class NodeBuilder
{
  private int cardinality;
  private String name;
  private String type;

  public NodeBuilder(String type, int cardinality, String name){
    this.cardinality=cardinality;
    this.name=name;
    this.type=type;
  }

  public VariableWithCardinality build(){
    VariableWithCardinality variable;
    if (type.equals("long")) {
      variable = new LongMetricNode(name,cardinality);
    } else if (type.equals("int")) {
      variable = new IntMetricNode(name,cardinality);
    } else if (type.equals("double")) {
      variable = new DoubleMetricNode(name,cardinality);
    } else if (type.equals("string")) {
      variable = new StringNode(name,cardinality);
    } else {
      throw new IllegalArgumentException("Incorrect type specified: " + type);
    }
    return variable;
  }

  public void setCardinality(int newCardinality) throws Exception
  {
    if (newCardinality > 0) {
      cardinality = newCardinality;
    } else {
      throw new Exception("Cardinality can't be less than 0");
    }
  }

  public void setType(String type){
    this.type=type;
  }
}
