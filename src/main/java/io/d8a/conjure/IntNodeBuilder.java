package io.d8a.conjure;

public class IntNodeBuilder extends NodeBuilder
{

  public IntNodeBuilder(int defaultCardinality, String name)
  {
    super(defaultCardinality,name);
  }

  @Override
  public VariableWithCardinality build()
  {
    return new IntMetricNode(name,cardinality);
  }


}
