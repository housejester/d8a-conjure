package io.d8a.conjure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IntSpec extends Spec {
    @JsonCreator
    public IntSpec(
            @JsonProperty("count") int count,
            @JsonProperty("cardinality") int cardinality,
            @JsonProperty("name") String name
    ) {
        super(count, cardinality, name);
    }

    @Override
    public CardinalityNode createNewNode(String name, int cardinality) {
        return new IntCardinalityNode(name, cardinality);
    }

}
