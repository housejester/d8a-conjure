package io.d8a.conjure;

import java.util.Arrays;
import java.util.Collection;

public class MultiPrinter implements Printer<String>
{
  private Collection<Printer> printers;

  public MultiPrinter(Collection<Printer> printers)
  {
    this.printers = printers;
  }

  public MultiPrinter(Printer... printers)
  {
    this(Arrays.asList(printers));
  }

  @Override
  public void print(String message)
  {
    for (Printer printer : printers) {
      printer.print(message);
    }
  }

  @Override
  public String toString()
  {
    return "MultiPrinter{" +
           "printers=" + printers +
           '}';
  }
}
