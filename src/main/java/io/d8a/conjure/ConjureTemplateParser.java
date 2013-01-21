package io.d8a.conjure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class ConjureTemplateParser {
    ConjureTemplate template;

    public ConjureTemplateParser(){
        this(Clock.SYSTEM_CLOCK);
    }

    public ConjureTemplateParser(Clock clock) {
        template = new ConjureTemplate(clock);
        registerStandardTypes();
    }

    private void registerStandardTypes() {
        template.addNodeType("time", TimeNode.class);
        template.addNodeType("minmax", MinMaxNode.class);
        template.addNodeType("randomChoice", ChooseRandomNodeList.class);
        template.addNodeType("cycle", ChooseInOrderNodeList.class);
        template.addNodeType("combine", CombineNodeList.class);
        template.addNodeType("weighted", ChooseByWeightNodeList.class);
    }

    public ConjureTemplate parse(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = reader.readLine();
        NodeList currentNodeList = null;
        String endToken = "";
        CombineNodeList list = new CombineNodeList("\n");
        while(line != null){
            if( isEndToken(line, endToken) ){
                currentNodeList = null;
                endToken = "";
            }else if(!isBlank(line)){
                SampleNode node = template.parseNodes(line);
                NodeList nodeAsNodeList = unwrapNodeList(node);
                if(currentNodeList != null){
                    currentNodeList.add(node);
                }else if(nodeAsNodeList != null && nodeAsNodeList.isEmpty()){
                    currentNodeList = nodeAsNodeList;
                    endToken = parseEndToken(line, template);
                    list.add(node);
                } else {
                    list.add(node);
                }
            }
            line = reader.readLine();
        }
        if(template.getNode("sample") == null){
            template.addNode("sample", list);
        }
        return template;
    }

    private NodeList unwrapNodeList(SampleNode node) {
        SampleNode unwrapped = unwrapNode(node);
        if(unwrapped instanceof NodeList){
            return (NodeList) unwrapped;
        }
        return null;
    }

    private boolean isNodeList(SampleNode node) {
        return unwrapNode(node) instanceof NodeList;
    }

    private SampleNode unwrapNode(SampleNode node){
        if(node instanceof MemoizingNode){
            return ((MemoizingNode)node).getTargetNode();
        }
        return node;
    }

    private String parseEndToken(String line, ConjureTemplate template) {
        Map config = template.parseFirstConfig(line);
        if(config.containsKey("endToken")){
            return (String)config.get("endToken");
        }
        return "";
    }

    private boolean isEndToken(String line, String endToken) {
        return line.equals(endToken);
    }

    private boolean isBlank(String line) {
        String trimmed = line.trim();
        return trimmed.isEmpty() || trimmed.startsWith("#");
    }
}
