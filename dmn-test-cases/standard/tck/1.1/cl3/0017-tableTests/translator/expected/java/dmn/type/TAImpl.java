package type;

import java.util.*;

@javax.annotation.Generated(value = {"itemDefinition.ftl", "tA"})
@com.fasterxml.jackson.annotation.JsonPropertyOrder(alphabetic = true)
public class TAImpl implements TA {
        private String name;
        private java.lang.Number price;

    public TAImpl() {
    }

    public TAImpl(String name, java.lang.Number price) {
        this.setName(name);
        this.setPrice(price);
    }

    @com.fasterxml.jackson.annotation.JsonGetter("name")
    public String getName() {
        return this.name;
    }

    @com.fasterxml.jackson.annotation.JsonSetter("name")
    public void setName(String name) {
        this.name = name;
    }

    @com.fasterxml.jackson.annotation.JsonGetter("price")
    public java.lang.Number getPrice() {
        return this.price;
    }

    @com.fasterxml.jackson.annotation.JsonSetter("price")
    public void setPrice(java.lang.Number price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        return equalTo(o);
    }

    @Override
    public int hashCode() {
        return hash();
    }

    @Override
    public String toString() {
        return asString();
    }
}
