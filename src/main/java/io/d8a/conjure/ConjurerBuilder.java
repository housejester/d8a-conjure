/*
 * Druid - a distributed column store.
 * Copyright (C) 2012  Metamarkets Group Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.d8a.conjure;

public class ConjurerBuilder
{
  private long startTime = -1;
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
  )
  {

    if (startTime != null) {
      this.startTime = startTime;
    }
    if (stopTime != null) {
      this.stopTime = stopTime;
    }
    if (printer != null) {
      this.printer = printer;
    }
    if (linesPerSec != null) {
      this.linesPerSec = linesPerSec;
    }
    if (maxLines != null) {
      this.maxLines = maxLines;
    }
    this.filePath = filePath;
    this.customCardinalityVariablesMode = customCardinalityVariablesMode;
  }

  public void setPrinter(Printer printer)
  {
    this.printer = printer;
  }

  public Conjurer build()
  {
    return new Conjurer(startTime, stopTime, printer, linesPerSec, maxLines, filePath, customCardinalityVariablesMode);
  }
}
