package io.d8a.conjure;

public class LazyRefSampleNode implements SampleNode{
    private String ref;
    private SampleNode refNode;
    private Conjurer conjurer;

    public LazyRefSampleNode(String ref, Conjurer conjurer) {
        this.ref = ref;
        this.conjurer = conjurer;
    }

    @Override
    public StringBuilder generate(StringBuilder buff) {
        if(refNode == null){
            refNode = conjurer.getNode(ref);
            if(refNode == null){
                throw new IllegalArgumentException("Referenced node '"+ref+"' not found.");
            }
        }
        return refNode.generate(buff);
    }
}
