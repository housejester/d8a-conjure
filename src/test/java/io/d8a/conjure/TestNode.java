package io.d8a.conjure;

import java.util.Map;

class TestNode implements SampleNode {
    private Object text;

    TestNode(Object text) {
        this.text = text;
    }

    @Override
    public StringBuilder generate(StringBuilder buff) {
        return buff.append(text);
    }

    public static SampleNode createNode(final Map config, final ConjureTemplate generator){
        if(config.containsKey("value")){
            return new TestNode(config.get("value"));
        }
        return new SampleNode(){
            @Override
            public StringBuilder generate(StringBuilder buff) {
                generator.getNode((String)config.get("valueRef")).generate(buff);
                return buff;
            }
        };
    }

}
