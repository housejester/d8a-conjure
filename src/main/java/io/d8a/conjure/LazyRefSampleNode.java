package io.d8a.conjure;

public class LazyRefSampleNode implements SampleNode{
    private String ref;
    private SampleNode refNode;
    private ConjureTemplate template;

    public LazyRefSampleNode(String ref, ConjureTemplate template) {
        this.ref = ref;
        this.template = template;
    }

    @Override
    public StringBuilder generate(StringBuilder buff) {
        if(refNode == null){
            refNode = template.getNode(ref);
            if(refNode == null){
                throw new IllegalArgumentException("Referenced node '"+ref+"' not found.");
            }
        }
        return refNode.generate(buff);
    }
}
