package io.d8a.conjure;

import java.util.List;

public class ChooseInOrderNodeList extends NodeList {
    private int next = 0;

    @Override
    protected void generateNonEmpty(StringBuilder buff, List<SampleNode> nodes){
        if(next >= nodes.size()){
            next = 0;
        }
        nodes.get(next++).generate(buff);
    }
}
