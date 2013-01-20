package io.d8a.conjure;

import java.util.List;
import java.util.Map;

public class CombineNodeList extends NodeList {
    private static final String DEFAULT_SEPARATOR = "";
    private String separator;

    public CombineNodeList(){
        this(DEFAULT_SEPARATOR);
    }

    public CombineNodeList(List<SampleNode> nodes) {
        this();
        add(nodes);
    }

    public CombineNodeList(String separator) {
        super(true);
        this.separator = separator;
        if(this.separator == null){
            this.separator = DEFAULT_SEPARATOR;
        }
    }

    @Override
    protected void generateNonEmpty(StringBuilder buff){
        boolean first = true;
        for(SampleNode node : nodes){
            if(first){
                first = false;
            }else{
                buff.append(separator);
            }
            node.generate(buff);
        }
    }

    public static CombineNodeList createNode(Map config, Conjurer conjurer) {
        String separator = (String)config.get("separator");
        CombineNodeList nodes = new CombineNodeList(separator);
        List list = (List)config.get("list");
        if(list != null){
            for(Object obj : list){
                nodes.add(conjurer.parseNodes(String.valueOf(obj)));
            }
        }
        return nodes;
    }
}
