package io.d8a.conjure;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class ConjureTemplateParser {
    Conjurer conjurer;

    public ConjureTemplateParser(){
        this(Clock.SYSTEM_CLOCK);
    }

    public ConjureTemplateParser(Clock clock) {
        conjurer = new Conjurer(clock);
        registerStandardTypes();
    }

    private void registerStandardTypes() {
        conjurer.addNodeType("time", TimeNode.class);
        conjurer.addNodeType("minmax", MinMaxNode.class);
        conjurer.addNodeType("randomChoice", ChooseRandomNodeList.class);
        conjurer.addNodeType("cycle", ChooseInOrderNodeList.class);
        conjurer.addNodeType("combine", CombineNodeList.class);
        conjurer.addNodeType("weighted", ChooseByWeightNodeList.class);
    }

    public Conjurer parse(InputStream inputStream) throws IOException {
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
                SampleNode node = conjurer.parseNodes(line);
                NodeList nodeAsNodeList = unwrapNodeList(node);
                if(currentNodeList != null){
                    currentNodeList.add(node);
                }else if(nodeAsNodeList != null && nodeAsNodeList.isEmpty()){
                    currentNodeList = nodeAsNodeList;
                    endToken = parseEndToken(line, conjurer);
                    list.add(node);
                } else {
                    list.add(node);
                }
            }
            line = reader.readLine();
        }
        if(conjurer.getNode("sample") == null){
            conjurer.addNode("sample", list);
        }
        return conjurer;
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

    private String parseEndToken(String line, Conjurer conjurer) {
        Map config = conjurer.parseFirstConfig(line);
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
