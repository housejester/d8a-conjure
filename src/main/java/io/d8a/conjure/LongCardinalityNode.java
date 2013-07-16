package io.d8a.conjure;

public class LongCardinalityNode extends CardinalityNode<Long> {

    public LongCardinalityNode(String name, int cardinality) {
        super(name, cardinality, new Counter<Long>(cardinality) {
            @Override
            protected Long convertValue(int counter) {
                return counter + 10000000000L;
            }
        });
    }


}
