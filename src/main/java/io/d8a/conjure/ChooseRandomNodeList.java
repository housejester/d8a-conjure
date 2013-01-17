package io.d8a.conjure;

import java.util.List;
import java.util.Random;

public class ChooseRandomNodeList extends NodeList {
    private static final Random RAND = new Random();

    protected void generateNonEmpty(StringBuilder buff, List<SampleNode> nodes){
        nodes.get(RAND.nextInt(nodes.size())).generate(buff);
    }
}
