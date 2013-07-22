package io.d8a.conjure;

public class SimulatedClock implements Clock
{
  private long currentTimeMillis;

  public SimulatedClock(long currentTimeMillis)
  {
    this.currentTimeMillis = currentTimeMillis;
  }

  @Override
  public long currentTimeMillis()
  {
    return currentTimeMillis;
  }

  @Override
  public void sleep(long millis)
  {
    currentTimeMillis += millis;
  }

  @Override
  public String toString()
  {
    return "Simulated Clock";
  }
}
