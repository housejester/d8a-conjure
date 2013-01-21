package io.d8a.conjure;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class CombineNodeListTest {
    public void appendsAllNodesInSingleGenerate(){
        CombineNodeList node = new CombineNodeList();
        node.add(new BareTextNode("a"), new BareTextNode("b"), new BareTextNode("c"));
        assertEquals(node.generate(new StringBuilder()).toString(), "abc");
    }

    public void canSpecifySeparator(){
        CombineNodeList node = new CombineNodeList("\n");
        node.add(new BareTextNode("a"), new BareTextNode("b"), new BareTextNode("c"));
        assertEquals(node.generate(new StringBuilder()).toString(), "a\nb\nc");
    }

    public void canBeRegisteredAsType(){
        ConjureTemplate template = new ConjureTemplate();
        template.addNodeType("combine", CombineNodeList.class);
        template.addFragment("sample", "My favorite is [${type:\"combine\", list:[\"a\",\"b\",\"c\"]}]");
        assertEquals(template.conjure(), "My favorite is [abc]");
        assertEquals(template.conjure(), "My favorite is [abc]");
    }

    public void canSetSeparatorInConfig(){
        ConjureTemplate template = new ConjureTemplate();
        template.addNodeType("combine", CombineNodeList.class);
        template.addFragment("sample", "My favorite is [${type:\"combine\", list:[\"a\",\"b\",\"c\"], separator:\",\"}]");
        assertEquals(template.conjure(), "My favorite is [a,b,c]");
        assertEquals(template.conjure(), "My favorite is [a,b,c]");
    }
}
