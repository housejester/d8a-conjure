package io.d8a.conjure;


import com.google.common.base.Preconditions;

public abstract class CardinalityNode<T> implements ConjureTemplateNode {
    public Counter<T> counter;
    private String name;
    private int cardinality;

    public CardinalityNode(String name, int cardinality, Counter<T> counter){
        Preconditions.checkArgument(cardinality>0, "must be positive: %s", cardinality);

        this.name = name;
        this.cardinality = cardinality;
        this.counter = counter;
    }

    public int getCardinality(){
        return cardinality;
    }

    public void setName(String name){
        this.name = name;
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
    public boolean equals(Object other){
        CardinalityNode otherObj = (CardinalityNode) other;
        if(getName().equals(otherObj.getName()) && getCardinality() == (otherObj.getCardinality()) && getValue().equals(otherObj.getValue())){
            return true;
        }
        return false;
    }

}
