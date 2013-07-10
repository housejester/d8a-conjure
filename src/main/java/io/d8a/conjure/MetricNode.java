package io.d8a.conjure;


public abstract class MetricNode implements VariableWithCardinality
{
  public Counter counter;
  private String name;
  private int cardinality;

  public MetricNode(String name,int cardinality)
  {
    this.name=name;
    this.cardinality=cardinality;
    counter = new Counter(cardinality);
  }

  public int getCardinality()
  {
    return cardinality;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public abstract Object getValue();

  public String getName()
  {
    return name;
  }

}
