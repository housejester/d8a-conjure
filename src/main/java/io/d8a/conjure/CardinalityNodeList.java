package io.d8a.conjure;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CardinalityNodeList implements ConjureTemplateNode{
    protected List<CardinalityNode> cardinalityNodes = Lists.newArrayList();
    private Map<String, Object> jsonMap = Maps.newHashMap();

    public CardinalityNodeList(){
    }

    public CardinalityNodeList(List<CardinalityNode> nodes){
        for(CardinalityNode node : nodes){
            cardinalityNodes.add(node);
        }
    }


    public void addNode(CardinalityNode node){
        cardinalityNodes.add(node);
    }

    public void addAll(List<CardinalityNode> nodes){
        for(CardinalityNode node : nodes){
            addNode(node);
        }
    }

    public List<CardinalityNode> getNodes(){
        return Collections.unmodifiableList(cardinalityNodes);
    }

    @Override
    public StringBuilder generate(StringBuilder buff){
        return buff.append(generateMap().toString());
    }

    public Map<String, Object> generateMap(){
        for(CardinalityNode variable : cardinalityNodes){
            jsonMap.put(variable.getName(), variable.getValue());
        }
        jsonMap.put("timestamp", System.currentTimeMillis());
        return jsonMap;
    }

    public int size(){
        return cardinalityNodes.size();
    }

    @Override
    public boolean equals(Object list){
        return getNodes().equals(((CardinalityNodeList) list).getNodes());
    }
}
