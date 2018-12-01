package com.example.administrator.comassistant2.simulation.bean;

import java.io.Serializable;

public class  LimitLineBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private Float highValue;
    private Float lowValue;
    private String label;


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Float getHighValue() {
        return highValue;
    }

    public void setHighValue(Float highValue) {
        this.highValue = highValue;
    }

    public Float getLowValue() {
        return lowValue;
    }

    public void setLowValue(Float lowValue) {
        this.lowValue = lowValue;
    }
}
