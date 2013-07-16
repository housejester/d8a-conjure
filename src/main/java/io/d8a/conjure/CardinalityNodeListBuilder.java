package io.d8a.conjure;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CardinalityNodeListBuilder{
    private final List<Spec> specList;

    @JsonCreator
    public CardinalityNodeListBuilder(
            @JsonProperty("specs") List<Spec> specList
    ){
        this.specList = specList;
    }

    public CardinalityNodeList build() throws IllegalArgumentException{
        CardinalityNodeList nodeList = new CardinalityNodeList();
        for(Spec spec : specList){
            spec.addNodes(nodeList);
        }
        return nodeList;
    }
}