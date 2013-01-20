package io.d8a.conjure;

import java.util.Random;

public class ChooseRandomNodeList extends NodeList {
    private static final Random RAND = new Random();

    protected void generateNonEmpty(StringBuilder buff){
        nodes.get(RAND.nextInt(nodes.size())).generate(buff);
    }
}
