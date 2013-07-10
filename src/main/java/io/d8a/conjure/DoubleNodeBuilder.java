package io.d8a.conjure;

public class DoubleNodeBuilder extends NodeBuilder
{
  public DoubleNodeBuilder(int defaultCardinality, String name)
  {
    super(defaultCardinality, name);
  }

  @Override
  public VariableWithCardinality build()
  {
    return new DoubleMetricNode(name,cardinality);
  }

}