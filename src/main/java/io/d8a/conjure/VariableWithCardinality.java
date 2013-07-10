package io.d8a.conjure;

public interface VariableWithCardinality extends ConjureTemplateNode
{
  public int getCardinality();

  public String getType();

  public void setName(String name);

  public String getName();

  public Object getValue();
}
