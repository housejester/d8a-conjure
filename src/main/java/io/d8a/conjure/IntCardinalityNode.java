package io.d8a.conjure;

public class IntCardinalityNode extends CardinalityNode<Integer>
{

  public IntCardinalityNode(String name, int cardinality)
  {
    super(
        name, cardinality, new Counter<Integer>(cardinality)
    {
      @Override
      protected Integer convertValue(int counter)
      {
        return counter;
      }
    }
    );
  }

}
