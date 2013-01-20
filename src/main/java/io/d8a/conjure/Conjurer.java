package io.d8a.conjure;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;
import java.util.*;

public class Conjurer {
    private Map<String, SampleNode> nodes;
    private Map<String, Method> typeRegistry;
    private Clock clock;
    private String refOpenToken = "${";
    private String refCloseToken = "}";

    private static final ObjectMapper json = new ObjectMapper();
    static{
        json.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

    public Conjurer() {
        this(Clock.SYSTEM_CLOCK);
    }

    public Conjurer(Clock clock) {
        this(clock, "${", "}");
    }

    public Conjurer(Clock clock, String openToken, String closeToken) {
        this.clock = clock;
        nodes = new HashMap<String, SampleNode>();
        typeRegistry = new HashMap<String, Method>();
        this.refOpenToken = openToken;
        this.refCloseToken = closeToken;
    }

    public Clock getClock() {
        return clock;
    }

    public void addNodeTemplate(String name, String template) {
        addNode(name, parseNodes(template));
    }

    public SampleNode parseNodes(String text){
        List<SampleNode> nodes = compileToNodeList(text);
        if(nodes.size() == 1){
            return nodes.get(0);
        }
        return new CombineNodeList(nodes);
    }

    public String next(String templateName) {
        if(nodes.containsKey(templateName)){
            return nodes.get(templateName).generate(new StringBuilder()).toString();
        }
        throw new IllegalArgumentException("Node '"+templateName+"' not found in the sample generator.");
    }

    public String next() {
        return next("sample");
    }

    public SampleNode getNode(String name) {
        return nodes.get(name);
    }

    public void addNode(String name, SampleNode node) {
        if(nodes.containsKey(name)){
            throw new IllegalArgumentException("Node '"+name+"' already added to this generator.");
        }
        this.nodes.put(name, node);
    }

    public void addNodeType(final String typeName, Class nodeType) {
        final Method creator;
        try {
            creator = lookupCreatorMethod(nodeType);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Could not find creator method for class '"+nodeType+"'.  Needs to have a static method called 'createNode' that takes Map,Conjurer, or just a Map.");
        }
        typeRegistry.put(typeName, creator);
    }

    private Method lookupCreatorMethod(Class nodeType) throws NoSuchMethodException {
        try {
            return nodeType.getMethod("createNode", Map.class, Conjurer.class);
        } catch (NoSuchMethodException e) {
        }
        return nodeType.getMethod("createNode", Map.class);
    }

    private List<SampleNode> compileToNodeList(String text) {
        List<SampleNode> nodes = new ArrayList<SampleNode>();
        Snippet refSnip = findRef(text);
        while(refSnip != null){
            if(refSnip.start > 0){
                nodes.add(new BareTextNode(text.substring(0, refSnip.start)));
            }

            String ref = text.substring(refSnip.start + refOpenToken.length(), refSnip.stop).trim();
            SampleNode refNode = resolveNodeFromRef(ref);
            nodes.add(refNode);

            text = text.substring(refSnip.stop + refCloseToken.length());
            refSnip = findRef(text);
        }
        if(!text.isEmpty()){
            nodes.add(new BareTextNode(text));
        }
        return nodes;
    }

    private SampleNode resolveNodeFromRef(String ref) {
        Map config;
        try{
            config = json.readValue("{"+ref+"}", Map.class);
        }catch(Exception ex){
            SampleNode node = this.nodes.get(ref);
            if(node == null){
                node = new LazyRefSampleNode(ref, this);
            }
            return node;
        }
        String typeName = (String) config.get("type");
        Method nodeCreator = resolveNodeCreator(typeName);

        SampleNode node = createNodeFromMethod(typeName, nodeCreator, config, this);
        String name = (String)config.get("name");
        if(name != null){
            //TODO: don't like how this happens under the hood as a side-effect of parsing nodes from text
            this.nodes.put(name, node);
        }
        return node;
    }

    private Method resolveNodeCreator(String typeName) {
        Method nodeCreator = typeRegistry.get(typeName);
        if(nodeCreator != null){
            return nodeCreator;
        }
        Class clazz;
        try {
            clazz = Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unknown sample node nodeCreator '"+typeName+"'.");
        }
        addNodeType(typeName, clazz);
        return typeRegistry.get(typeName);
    }

    private SampleNode createNodeFromMethod(String typeName, Method creator, Map config, Conjurer generator) {
        Object[] args = new Object[creator.getParameterTypes().length];
        args[0] = config;
        if(args.length > 1){
            args[1] = generator;
        }
        try {
            return (SampleNode)creator.invoke(null, args);
        } catch (Exception e) {
            throw new IllegalStateException("Problem creating the '"+typeName+"' node.", e);
        }
    }

    private Snippet findRef(String text) {
        int refOpen = text.indexOf(refOpenToken);
        if(refOpen < 0){
            return null;
        }
        int refClose = text.indexOf(refCloseToken, refOpen);
        if(refClose < 0){
            return null;
        }
        return new Snippet(refOpen, refClose);
    }

    private static class Snippet{
        final int start;
        final int stop;
        Snippet(int start, int stop){
            this.start = start;
            this.stop = stop;
        }
    }
}
