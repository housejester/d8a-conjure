package io.d8a.conjure;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class IncrementNodeTest {
    private ConjureTemplate samples;

    @BeforeMethod
    public void setUp() throws Exception {
        samples = new ConjureTemplate();
        samples.addNodeType("increment", IncrementNode.class);
    }

    public void generatesIncrementalNumbers(){
        samples.addFragment("sample", "${type:\"increment\", value:1, step:3}");
        assertEquals(conjureNext(), 1);
        assertEquals(conjureNext(), 4);
    }

    public void generatesIncrementalNumbersDefaultStep(){
        samples.addFragment("sample", "${type:\"increment\", value:42}");
        assertEquals(conjureNext(), 42);
        assertEquals(conjureNext(), 43);
    }

    public void generatesIncrementalNegativeValue(){
        samples.addFragment("sample", "${type:\"increment\", value:-1}");
        assertEquals(conjureNext(), -1);
        assertEquals(conjureNext(), 0);
    }

    public void generatesIncrementalNegativeStep(){
        samples.addFragment("sample", "${type:\"increment\", value:12, step:-2}");
        assertEquals(conjureNext(), 12);
        assertEquals(conjureNext(), 10);
    }

    public void generatesIncrementalNegativeValueAndStep(){
        samples.addFragment("sample", "${type:\"increment\", value:-1, step:-3}");
        assertEquals(conjureNext(), -1);
        assertEquals(conjureNext(), -4);
    }

    public void rollsNumbersAfterSpecifiedNumberOfSteps(){
        samples.addFragment("sample", "${type:\"increment\", value:42, rollAfterSteps:2}");
        assertEquals(conjureNext(), 42);
        assertEquals(conjureNext(), 43);

        assertEquals(conjureNext(), 42);
    }

    public void generatesIncrementingValuesAfterRoll(){
        samples.addFragment("sample", "${type:\"increment\", value:42, rollAfterSteps:2}");
        assertEquals(conjureNext(), 42);
        assertEquals(conjureNext(), 43);

        assertEquals(conjureNext(), 42);
        assertEquals(conjureNext(), 43);
    }

    private long conjureNext() {
        return Long.valueOf(samples.conjure());
    }
}
