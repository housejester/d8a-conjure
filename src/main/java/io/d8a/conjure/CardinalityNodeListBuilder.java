package io.d8a.conjure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import java.util.List;

public class CardinalityNodeListBuilder{
    private final List<Spec> specList;
    private Clock clock = Clock.SYSTEM_CLOCK;

    @JsonCreator
    public CardinalityNodeListBuilder(
            @JsonProperty("specs") List<Spec> specList
    ){
        this.specList = specList;
    }

    public CardinalityNodeList build() throws IllegalArgumentException{
        List<CardinalityNode> nodesToAdd = Lists.newArrayList();
        for(Spec spec : specList){
            nodesToAdd.addAll(spec.getNodesToAdd());
        }
        return new CardinalityNodeList(nodesToAdd, clock);
    }

    public CardinalityNodeListBuilder withClock(Clock clock){
        this.clock = clock;
        return this;
    }
}