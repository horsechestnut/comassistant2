package com.example.administrator.comassistant2.simulation.bean;

import java.io.Serializable;

public class LimitLineBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private Float value;
    private String label;

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
