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
        ChooseInOrderNodeList list = new ChooseInOrderNodeList();
        while(line != null){
            if(!isBlank(line) && !isEndToken(line, endToken)){
                SampleNode node = conjurer.parseNodes(line);
                if(currentNodeList != null){
                    currentNodeList.add(node);
                }else if(node instanceof NodeList){
                    currentNodeList = (NodeList)node;
                    endToken = parseEndToken(line, conjurer);
                    list.add(node);
                } else {
                    list.add(node);
                }
            }else{
                currentNodeList = null;
                endToken = "";
            }
            line = reader.readLine();
        }
        if(conjurer.getNode("sample") == null){
            conjurer.addNode("sample", list);
        }
        return conjurer;
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
