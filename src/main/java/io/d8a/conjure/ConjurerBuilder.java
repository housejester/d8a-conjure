package io.d8a.conjure;

import com.google.common.base.Preconditions;

public class ConjurerBuilder {
    private long startTime = - 1;
    private long stopTime = Long.MAX_VALUE;
    private Printer printer = Conjurer.nonePrinter();
    private int linesPerSec = 10;
    private long maxLines = Long.MAX_VALUE;
    private String filePath=null;
    private boolean customSchema = false;

    public ConjurerBuilder withStartTime(long startTime){
        this.startTime=startTime;
      return this;
    }

    public ConjurerBuilder withPrinter(Printer printer){
      this.printer=printer;
      return this;
    }

    public ConjurerBuilder withLinesPerSec(int linesPerSec){
      this.linesPerSec = linesPerSec;
      return this;
    }

    public ConjurerBuilder withMaxLines(long maxLines){
      this.maxLines=maxLines;
      return this;
    }

    public ConjurerBuilder withFilePath(String filePath){
      this.filePath=filePath;
      return this;
    }

    public ConjurerBuilder withCustomSchema(boolean customSchema){
      this.customSchema=customSchema;
      return this;
    }
    public Conjurer build(){
      Preconditions.checkArgument(filePath!=null,"Must specify filepath");
        return new Conjurer(startTime, stopTime, printer, linesPerSec, maxLines, filePath, customSchema);
    }
}
