package io.d8a.conjure;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertEquals;

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
        long firstValue = Long.valueOf(samples.conjure());
        assertEquals(firstValue, 1);
        long secondValue = Long.valueOf(samples.conjure());
        assertEquals(secondValue, 4);
    }

    public void generatesIncrementalNumbersDefaultStep(){
        samples.addFragment("sample", "${type:\"increment\", value:42}");
        long firstValue = Long.valueOf(samples.conjure());
        assertEquals(firstValue, 42);
        long secondValue = Long.valueOf(samples.conjure());
        assertEquals(secondValue, 43);
    }

    public void generatesIncrementalNegativeValue(){
        samples.addFragment("sample", "${type:\"increment\", value:-1}");
        long firstValue = Long.valueOf(samples.conjure());
        assertEquals(firstValue, -1);
        long secondValue = Long.valueOf(samples.conjure());
        assertEquals(secondValue, 0);
    }

    public void generatesIncrementalNegativeStep(){
        samples.addFragment("sample", "${type:\"increment\", value:12, step:-2}");
        long firstValue = Long.valueOf(samples.conjure());
        assertEquals(firstValue, 12);
        long secondValue = Long.valueOf(samples.conjure());
        assertEquals(secondValue, 10);
    }

    public void generatesIncrementalNegativeValueAndStep(){
        samples.addFragment("sample", "${type:\"increment\", value:-1, step:-3}");
        long firstValue = Long.valueOf(samples.conjure());
        assertEquals(firstValue, -1);
        long secondValue = Long.valueOf(samples.conjure());
        assertEquals(secondValue, -4);
    }

    public void rollsNumbersAfterSpecifiedNumberOfSteps(){
        samples.addFragment("sample", "${type:\"increment\", value:42, rollAfterSteps:2}");
        long firstValue = Long.valueOf(samples.conjure());
        assertEquals(firstValue, 42);
        long secondValue = Long.valueOf(samples.conjure());
        assertEquals(secondValue, 43);
        long thirdValue = Long.valueOf(samples.conjure());
        assertEquals(thirdValue, 42);
    }

}
