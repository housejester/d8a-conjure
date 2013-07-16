package io.d8a.conjure;

public class LazyRefNode implements ConjureTemplateNode {
    private String ref;
    private ConjureTemplateNode refNode;
    private ConjureTemplate template;

    public LazyRefNode(String ref, ConjureTemplate template) {
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
