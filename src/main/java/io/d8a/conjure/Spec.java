package io.d8a.conjure;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @Type(value = LongSpec.class, name = "long"),
        @Type(value = IntSpec.class, name = "int"),
        @Type(value = DoubleSpec.class, name = "double"),
        @Type(value = StringSpec.class, name = "string")
}
)
public abstract class Spec {
    protected int count;
    protected int cardinality;
    protected String type;
    protected String name = "column";

    public Spec(
            int count,
            int cardinality,
            String name
    ){
        this.count = count;
        this.cardinality = cardinality;
        if(name != null){
            this.name = name;
        }
    }

    public abstract CardinalityNodeList addNodes(CardinalityNodeList nodeList) throws IllegalArgumentException;
}
