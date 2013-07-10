package io.d8a.conjure;

public abstract class NodeBuilder
{
  protected int cardinality;
  protected String name;

  public NodeBuilder(int defaultCardinality, String name){
    this.cardinality=defaultCardinality;
    this.name=name;
  }

  public abstract VariableWithCardinality build();

  public void setCardinality(int newCardinality) throws Exception
  {
    if (newCardinality > 0) {
      this.cardinality = newCardinality;
    } else {
      throw new Exception("Cardinality can't be less than 0");
    }
  }
}
