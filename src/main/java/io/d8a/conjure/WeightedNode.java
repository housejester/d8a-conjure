package io.d8a.conjure;

public class WeightedNode implements ConjureTemplateNode {
    private ConjureTemplateNode target;
    private int weight;

    public WeightedNode(ConjureTemplateNode target, int weight) {
        this.target = target;
        this.weight = weight;
    }

    @Override
    public StringBuilder generate(StringBuilder buff) {
        return target.generate(buff);
    }

    public int getWeight() {
        return weight;
    }
}
