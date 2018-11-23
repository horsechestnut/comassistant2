package com.example.administrator.comassistant2.simulation.bean;

import java.util.ArrayList;
import java.util.List;

//分页历史数据
public class PageChartDataBean {
    private int fileIndex; //文件对应点
    private int pageIndex; //分页对应点
    private int type; //是历史数据还是实时数据
    private List<Float> chartData; //具体的数据，暂时取一个
    private float midValue; //中位数值
    private float averageValue; //均值

    public PageChartDataBean() {
        chartData = new ArrayList<>();
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(int fileIndex) {
        this.fileIndex = fileIndex;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Float> getChartData() {
        return chartData;
    }

    public void setChartData(List<Float> chartData) {
        this.chartData = chartData;
    }


    public float getAverageValue() {
        return averageValue;
    }

    public void setAverageValue(float averageValue) {
        this.averageValue = averageValue;
    }


    public float getMidValue() {
        return midValue;
    }

    public void setMidValue(float midValue) {
        this.midValue = midValue;
    }


    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }
}
