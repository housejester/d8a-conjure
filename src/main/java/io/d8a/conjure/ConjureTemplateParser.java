package io.d8a.conjure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
        ChooseInOrderNodeList list = new ChooseInOrderNodeList();
        while(line != null){
            if(!isBlank(line)){
                list.add(conjurer.parseNodes(line));
            }
            line = reader.readLine();
        }
        conjurer.addNode("sample", list);
        return conjurer;
    }

    private boolean isBlank(String line) {
        String trimmed = line.trim();
        return trimmed.isEmpty() || trimmed.startsWith("#");
    }
}
