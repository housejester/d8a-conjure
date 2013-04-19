package io.d8a.conjure;

import java.util.Map;

/**
 * A simple node that generates an ever increasing value,
 * starts with value and changes by step (defaults to 1).
 */
public class IncrementNode implements ConjureTemplateNode {
    private long value;
    private long step;

    public IncrementNode(long value, long step) {
        this.value = value;
        this.step = step;
    }

    @Override
    public StringBuilder generate(StringBuilder buff) {
        long currentValue = value;
        value += step;
        return buff.append(currentValue);
    }

    public static IncrementNode createNode(Map config)
    {
        Number startValue = (Number)config.get("value");
        Number step = (Number)config.get("step");
        if(startValue == null){
            throw new IllegalArgumentException("value must be specified.");
        }
        if(step == null){
            step = 1;
        }
        return new IncrementNode(startValue.longValue(), step.longValue());
    }
}