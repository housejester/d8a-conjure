package io.d8a.conjure;

public class DoubleCardinalityNode extends CardinalityNode<Double>{

    public DoubleCardinalityNode(String name, int cardinality){
        super(name, cardinality, new Counter<Double>(cardinality){
            @Override
            protected Double convertValue(int counter){
                return counter + 0.1234d;
            }
        });
    }
}
