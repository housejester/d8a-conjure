package io.d8a.conjure;


public class StringCardinalityNode extends CardinalityNode<String> {
    public StringCardinalityNode(String name, int cardinality){
        super(name, cardinality, new Counter<String>(cardinality) {
            @Override
            protected String convertValue(int counter){
                return String.format("value%s",Integer.toString(counter));
            }
        });
    }

}
