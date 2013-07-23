package io.d8a.conjure;

public abstract class Counter<T>
{
  private final int cardinality;
  private int counter;

  public Counter(int cardinality)
  {
    this.cardinality = cardinality;
  }

  public T nextValue()
  {
    counter++;
    if (counter >= cardinality) {
      counter = 0;
    }
    return convertValue(counter);
  }

  protected abstract T convertValue(int counter);

}
