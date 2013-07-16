package io.d8a.conjure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DoubleSpec extends Spec {
    @JsonCreator
    public DoubleSpec(
            @JsonProperty("count") int count,
            @JsonProperty("cardinality") int cardinality,
            @JsonProperty("name") String name
    ){
        super(count, cardinality, name);
    }

    @Override
    public CardinalityNodeList addNodes(CardinalityNodeList list) throws IllegalArgumentException{
        for(int i = 0; i<count; i++){
            list.addNode(new DoubleCardinalityNode(name+i, cardinality));
        }
        return list;
    }
}
