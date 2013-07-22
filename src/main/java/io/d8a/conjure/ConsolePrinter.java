package io.d8a.conjure;

public class ConsolePrinter extends StreamPrinter
{
  public ConsolePrinter()
  {
    super(System.out);
  }

  @Override
  public String toString()
  {
    return "ConsolePrinter";
  }
}
