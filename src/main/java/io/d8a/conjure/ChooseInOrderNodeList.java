package io.d8a.conjure;

public class ChooseInOrderNodeList extends NodeList {
    private int next = 0;

    @Override
    protected void generateNonEmpty(StringBuilder buff){
        if(next >= nodes.size()){
            next = 0;
        }
        nodes.get(next++).generate(buff);
    }
}
