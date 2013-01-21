package io.d8a.conjure;

import java.util.Map;

class TestNode implements ConjureTemplateNode {
    private Object text;

    TestNode(Object text) {
        this.text = text;
    }

    @Override
    public StringBuilder generate(StringBuilder buff) {
        return buff.append(text);
    }

    public static ConjureTemplateNode createNode(final Map config, final ConjureTemplate generator){
        if(config.containsKey("value")){
            return new TestNode(config.get("value"));
        }
        return new ConjureTemplateNode(){
            @Override
            public StringBuilder generate(StringBuilder buff) {
                generator.getNode((String)config.get("valueRef")).generate(buff);
                return buff;
            }
        };
    }

}
