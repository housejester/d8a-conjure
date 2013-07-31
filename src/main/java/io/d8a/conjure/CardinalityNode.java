package io.d8a.conjure;


import com.google.common.base.Preconditions;

/**
 * A cardinality node is a conjure template node that contains a cardinality, name, and a counter. It
 * generates a different value on each call of getValue().
 * This makes sure it outputs possible values from 0 to cardinality-1.
 * @param <T>  - This is the type of value the node will output on getValue().
 */
public abstract class CardinalityNode<T> implements ConjureTemplateNode{
    private final Counter<T> counter;
    private final String name;
    private final int cardinality;

    public CardinalityNode(String name, int cardinality, Counter<T> counter){
        Preconditions.checkArgument(cardinality > 0, "must be positive: %s", cardinality);

        this.name = name;
        this.cardinality = cardinality;
        this.counter = counter;
    }

    public int getCardinality(){
        return cardinality;
    }

    public T getValue(){
        return counter.nextValue();
    }

    public String getName(){
        return name;
    }


    @Override
    public StringBuilder generate(StringBuilder buff){
        return buff.append(getValue());
    }

    @Override
    public int hashCode(){
        int result = counter != null ? counter.hashCode() : 0;
        result = 31 * result+(name != null ? name.hashCode() : 0);
        result = 31 * result+cardinality;
        return result;
    }

    @Override
    public boolean equals(Object other){
        CardinalityNode otherObj = (CardinalityNode)other;
        if(getName().equals(otherObj.getName()) && getCardinality() == (otherObj.getCardinality()) && getValue().equals(
                otherObj.getValue()
        )){
            return true;
        }
        return false;
    }

}
