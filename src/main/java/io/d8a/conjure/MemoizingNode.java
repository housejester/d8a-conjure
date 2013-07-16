package io.d8a.conjure;

import java.util.Map;

public class MemoizingNode implements ConjureTemplateNode {
    private ConjureTemplateNode targetNode;
    private String name;
    private Map<String,String> cache;

    public MemoizingNode(ConjureTemplateNode targetNode, String name, Map cache) {
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

    public ConjureTemplateNode getTargetNode() {
        return targetNode;
    }
}
