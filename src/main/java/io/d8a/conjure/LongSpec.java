package io.d8a.conjure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LongSpec extends Spec {
    @JsonCreator
    public LongSpec(
            @JsonProperty("count") int count,
            @JsonProperty("cardinality") int cardinality,
            @JsonProperty("name") String name
    ){
        super(count, cardinality, name);
    }

    @Override
    public CardinalityNodeList addNodes(CardinalityNodeList list) throws IllegalArgumentException{
        for(int i = 0; i<count; i++){
            list.addNode(new LongCardinalityNode(name+i, cardinality));
        }
        return list;
    }
}
