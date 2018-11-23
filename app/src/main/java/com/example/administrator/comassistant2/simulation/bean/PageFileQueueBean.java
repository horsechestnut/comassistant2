package com.example.administrator.comassistant2.simulation.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageFileQueueBean implements Serializable {
    private static final long serialVersionUID = 1L;
    public int fileNum;
    public int pageIndex;
    public List<Integer> dataList;

    public PageFileQueueBean() {
        dataList = new ArrayList<>();
        fileNum = -1;
        pageIndex = -1;
    }
}
