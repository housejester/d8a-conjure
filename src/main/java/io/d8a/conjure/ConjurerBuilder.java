package io.d8a.conjure;

public class ConjurerBuilder {
    private long startTime = - 1;
    private long stopTime = Long.MAX_VALUE;
    private Printer printer = Conjurer.nonePrinter();
    private int linesPerSec = 10;
    private long maxLines = Long.MAX_VALUE;
    private final String filePath;
    private boolean customCardinalityVariablesMode = false;


    public ConjurerBuilder(
            Long startTime,
            Long stopTime,
            Printer printer,
            Integer linesPerSec,
            Long maxLines,
            String filePath,
            Boolean customCardinalityVariablesMode
    ){

        if(startTime != null){
            this.startTime = startTime;
        }
        if(stopTime != null){
            this.stopTime = stopTime;
        }
        if(printer != null){
            this.printer = printer;
        }
        if(linesPerSec != null){
            this.linesPerSec = linesPerSec;
        }
        if(maxLines != null){
            this.maxLines = maxLines;
        }
        this.filePath = filePath;
        this.customCardinalityVariablesMode = customCardinalityVariablesMode;
    }

    public void setPrinter(Printer printer){
        this.printer = printer;
    }

    public Conjurer build(){
        return new Conjurer(startTime, stopTime, printer, linesPerSec, maxLines, filePath, customCardinalityVariablesMode);
    }
}
