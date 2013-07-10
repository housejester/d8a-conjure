package io.d8a.conjure;


public class StringNode implements VariableWithCardinality
{
  private String[] stringPossibilities;
  private String name;
  private int cardinality;
  private Counter counter;

  public StringNode(String name, int cardinality)
  {
    this.name = name;
    this.cardinality = cardinality;
    this.counter = new Counter(cardinality);
  }

  @Override
  public StringBuilder generate(StringBuilder buff)
  {
    for (String str : stringPossibilities) {
      buff.append(str);
    }
    return buff;
  }

  @Override
  public int getCardinality()
  {
    return cardinality;
  }

  @Override
  public String getType()
  {
    return "string";
  }

  @Override
  public void setName(String name)
  {
    this.name = name;
  }

  @Override
  public String getName()
  {
    return name;
  }

  public String getValue()
  {
    return "value" + Integer.toString(counter.nextValue());
  }
}
