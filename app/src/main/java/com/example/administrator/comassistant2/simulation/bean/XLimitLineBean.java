package com.example.administrator.comassistant2.simulation.bean;

import java.io.Serializable;

public class XLimitLineBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer absValue;
    private String label;


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getAbsValue() {
        return absValue;
    }

    public void setAbsValue(Integer absValue) {
        this.absValue = absValue;
    }
}
