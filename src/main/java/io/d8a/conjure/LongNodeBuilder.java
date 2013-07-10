package io.d8a.conjure;

public class LongNodeBuilder extends NodeBuilder
{
  public LongNodeBuilder(int defaultCardinality, String name)
  {
    super(defaultCardinality,name);
  }

  @Override
  public VariableWithCardinality build()
  {
    return new LongMetricNode(name,cardinality);
  }


}
