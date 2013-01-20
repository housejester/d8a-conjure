package io.d8a.conjure;

import java.util.*;

public abstract class NodeList implements SampleNode {
    protected List<SampleNode> nodes = new ArrayList<SampleNode>();
    private boolean allowsGenerateOnEmpty = false;

    public NodeList(){
        this(false);
    }

    public NodeList(boolean allowsGenerateOnEmpty){
        this.allowsGenerateOnEmpty = allowsGenerateOnEmpty;
    }

    public void add(SampleNode...nodes){
        add(Arrays.asList(nodes));
    }

    public void add(Collection<SampleNode> nodes){
        this.nodes.addAll(nodes);
    }

    public List<SampleNode> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    @Override
    public StringBuilder generate(StringBuilder buff) {
        if(!nodes.isEmpty()){
            generateNonEmpty(buff);
            return buff;
        }
        if(allowsGenerateOnEmpty){
            generateEmpty(buff);
            return buff;
        }
        throw new IllegalStateException("Nodes must first be added to "+getClass().getSimpleName()+" before calling generate.");
    }

    protected void generateEmpty(StringBuilder buff){
    }

    protected abstract void generateNonEmpty(StringBuilder buff);
}
