package io.d8a.conjure;

public class DoubleMetricNode extends MetricNode implements ConjureTemplateNode
{

  public DoubleMetricNode(String name, int cardinality)
  {
    super(name, cardinality);
  }

  @Override
  public StringBuilder generate(StringBuilder buff)
  {
    return buff.append(getValue());
  }

  @Override
  public Double getValue()
  {
    return (double)counter.nextValue()+0.1234d;
  }

  @Override
  public String getType()
  {
    return "double";
  }

}
