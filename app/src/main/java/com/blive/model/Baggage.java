package com.blive.model;

public class Baggage {

    private String baggageURL,is_this_baggage_applied,name;

    public String getBaggageURL() {
        return baggageURL;
    }

    public void setBaggageURL(String baggageURL) {
        this.baggageURL = baggageURL;
    }

    public String getIs_this_baggage_applied() {
        return is_this_baggage_applied;
    }

    public void setIs_this_baggage_applied(String is_this_baggage_applied) {
        this.is_this_baggage_applied = is_this_baggage_applied;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Baggage{" +
                "baggageURL='" + baggageURL + '\'' +
                ", is_this_baggage_applied='" + is_this_baggage_applied + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
