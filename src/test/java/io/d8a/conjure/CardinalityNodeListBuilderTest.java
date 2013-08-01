package io.d8a.conjure;

import com.google.common.collect.ImmutableList;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class CardinalityNodeListBuilderTest
{
  private final Spec intSpec = new IntSpec(5,10,"intcolumn");
  private final Spec longSpec = new LongSpec(3,100,"longcolumn");
  private final Spec doubleSpec = new DoubleSpec(5,10,"doublecolumn");
  private final Spec stringSPec = new StringSpec(5,10,"stringcolumn");
  private final Clock clock = Clock.SYSTEM_CLOCK;

  @Test
  public void testBasicBuild() throws Exception
  {
    List<Spec> specList = ImmutableList.<Spec>of(intSpec);
    CardinalityNodeListBuilder list = new CardinalityNodeListBuilder(specList);
    CardinalityNodeList expectedList = new CardinalityNodeList(clock);
    for (int i=0;i<5;i++)
    {
      expectedList.add(new IntCardinalityNode("intcolumn" + i, 10));
    }
    Assert.assertEquals(expectedList, list.build());
  }
  @Test
  public void addMultipleSpecsBuild() throws Exception
  {
    List<Spec> specList = ImmutableList.<Spec>of(intSpec,longSpec);
    CardinalityNodeListBuilder list = new CardinalityNodeListBuilder(specList);
    CardinalityNodeList expectedList = new CardinalityNodeList(clock);
    for (int i=0;i<5;i++)
    {
      expectedList.add(new IntCardinalityNode("intcolumn" + i, 10));
    }
    for (int i=0;i<3;i++)
    {
      expectedList.add(new LongCardinalityNode("longcolumn" + i, 100));
    }
    Assert.assertEquals(list.build(),expectedList);

  }
}
