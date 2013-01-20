package io.d8a.conjure;

import java.util.List;

public class AppendAllNodeList extends NodeList {
    public AppendAllNodeList(){
        super(true);
    }

    public AppendAllNodeList(List<SampleNode> nodes) {
        this();
        add(nodes);
    }

    @Override
    protected void generateNonEmpty(StringBuilder buff){
        for(SampleNode node : nodes){
            node.generate(buff);
        }
    }
}
