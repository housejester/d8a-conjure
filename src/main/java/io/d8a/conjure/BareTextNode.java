package io.d8a.conjure;

public class BareTextNode implements ConjureTemplateNode
{
  private String text;

  public BareTextNode(String text)
  {
    this.text = text;
  }

  @Override
  public StringBuilder generate(StringBuilder buff)
  {
    buff.append(text);
    return buff;
  }

  public String getText()
  {
    return text;
  }
}
