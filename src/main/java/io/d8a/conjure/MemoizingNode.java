package io.d8a.conjure;

import java.util.Map;

public class MemoizingNode implements SampleNode{
    private SampleNode targetNode;
    private String name;
    private Map<String,String> cache;

    public MemoizingNode(SampleNode targetNode, String name, Map cache) {
        this.targetNode = targetNode;
        this.name = name;
        this.cache = cache;
    }

    @Override
    public StringBuilder generate(StringBuilder buff) {
        if(!cache.containsKey(name)){
            cache.put(name, targetNode.generate(new StringBuilder()).toString());
        }
        return buff.append(cache.get(name));
    }

    public SampleNode getTargetNode() {
        return targetNode;
    }
}
