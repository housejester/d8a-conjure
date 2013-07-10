package io.d8a.conjure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created: 4/19/13 10:19 AM
 */
public class FilePrinter extends StreamPrinter
{
  private final String filePath;

  public FilePrinter(File out) throws FileNotFoundException
  {
    super(new PrintStream(new FileOutputStream(out, true), false));
    filePath = out.getAbsolutePath();
  }

  public String toString()
  {
    return "FilePrinter:" + filePath;
  }

}
