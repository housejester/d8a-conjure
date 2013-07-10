package io.d8a.conjure;

public class IntMetricNode extends MetricNode implements ConjureTemplateNode
{

  public IntMetricNode(String name,int cardinality)
  {
    super(name, cardinality);
  }

  @Override
  public StringBuilder generate(StringBuilder buff)
  {
    return buff.append(getValue());
  }

  @Override
  public Integer getValue()
  {
    return counter.nextValue();
  }

  @Override
  public String getType()
  {
    return "int";
  }
}
