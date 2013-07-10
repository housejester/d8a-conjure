package io.d8a.conjure;

public class StringNodeBuilder extends NodeBuilder
{

  public StringNodeBuilder(int defaultCardinality, String name)
  {
    super(defaultCardinality,name);
  }

  @Override
  public VariableWithCardinality build()
  {
    return new StringNode(name,cardinality);
  }

}
