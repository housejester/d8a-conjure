package io.d8a.conjure;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class NodeList implements ConjureTemplateNode{
    private boolean allowsGenerateOnEmpty = false;
    protected List<ConjureTemplateNode> nodes = Lists.newArrayList();

    public NodeList(){
        this(false);
    }

    public NodeList(boolean allowsGenerateOnEmpty){
        this.allowsGenerateOnEmpty = allowsGenerateOnEmpty;
    }


    public void add(ConjureTemplateNode... nodes){
        add(Arrays.asList(nodes));
    }

    public void add(Collection<ConjureTemplateNode> nodes){
        this.nodes.addAll(nodes);
    }

    public List<ConjureTemplateNode> getNodes(){
        return Collections.unmodifiableList(nodes);
    }

    @Override
    public StringBuilder generate(StringBuilder buff){
        if(! nodes.isEmpty()){
            generateNonEmpty(buff);
            return buff;
        }
        if(allowsGenerateOnEmpty){
            generateEmpty(buff);
            return buff;
        }
        throw new IllegalStateException(
                "Nodes must first be added to "
                        + getClass().getSimpleName()
                        + " before calling generate."
        );
    }

    protected void generateEmpty(StringBuilder buff){
    }

    protected abstract void generateNonEmpty(StringBuilder buff);

    public boolean isEmpty(){
        return nodes == null || nodes.isEmpty();
    }
}
