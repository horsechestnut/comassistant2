package com.example.administrator.comassistant2.simulation.bean;


import java.util.ArrayList;
import java.util.List;

//分页索引： 用于temp队列和his队列的处理
public class PageQueueIndexData {
    /**
     * 一页也可以分很多，
     * 默认是-1；
     * 该值 = 当前文件的list总数%Config.page_threshold_num
     */
    private int pageIndex = -1;
    private List<Integer> dataList; //当前页的所有数据
    /**
     * 用户点击下一页上一页时调用该状态量，如果
     * -1 未初始化的状态
     * -2 已经是第一页了
     * -3 最右边的temp列表
     * -4 已经是最后一页了
     */
    private int fileNum = -1; //文件序列号


    public PageQueueIndexData() {
        pageIndex = -1;
        fileNum = -1;
        dataList = new ArrayList<>();
    }


    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public List<Integer> getDataList() {
        return dataList;
    }

    public void setDataList(List<Integer> dataList) {
        this.dataList = dataList;
    }

    public int getFileNum() {
        return fileNum;
    }

    public void setFileNum(int fileNum) {
        this.fileNum = fileNum;
    }

}
