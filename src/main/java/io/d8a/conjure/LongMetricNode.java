package io.d8a.conjure;

public class LongMetricNode extends MetricNode implements ConjureTemplateNode
{

  public LongMetricNode(String name, int cardinality)
  {
    super(name, cardinality);
  }

  @Override
  public StringBuilder generate(StringBuilder buff)
  {
    return buff.append(getValue());
  }

  @Override
  public Long getValue()
  {
    return counter.nextValue()+10000000000L;
  }

  @Override
  public String getType()
  {
    return "long";
  }
}
