package com.example.administrator.comassistant2.simulation.bean;

import java.util.ArrayList;
import java.util.List;

//基于阈值的数据分析，比如想找scale 为 60%以上或者 大于limitvalue的值
public class PageThresholdData {
    private int scale;
    private float limitValue;
    private float nums; //数量
    private float scaleValue; //临界值范围
    private int type; //1 scale型 2 limitevalue型
    private List<Float> chartData;

    public float getLimitValue() {
        return limitValue;
    }

    public void setLimitValue(float limitValue) {
        this.limitValue = limitValue;
    }

    public float getNums() {
        return nums;
    }

    public void setNums(float nums) {
        this.nums = nums;
    }

    public float getScaleValue() {
        return scaleValue;
    }

    public void setScaleValue(float scaleValue) {
        this.scaleValue = scaleValue;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public PageThresholdData() {
        chartData = new ArrayList<>();
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }


    public List<Float> getChartData() {
        return chartData;
    }

    public void setChartData(List<Float> chartData) {
        this.chartData = chartData;
    }
}