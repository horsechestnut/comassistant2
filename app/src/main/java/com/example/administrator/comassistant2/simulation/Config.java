package com.example.administrator.comassistant2.simulation;

public class Config {
    //Chart一次最多装载的数据
    private int UI_Chart_ShowLimit = 2000;
    //单个文件存储的数据数量，文件大小是其4倍，比如2000个数据是8K
    // 配置方式有两种，按照大约一分钟的数据量，大约234K;
    //也可以是Chart一次装载的整数倍
    private int File_MaxSize = 2000 * 2;
    //分页最大值
    private  int Page_MaxSize = 4000;

//    private int File_MaxSize = 2000*10*2;
//    private int File_MaxSize= 1000*60*4;


    private int Timer_Statis = 1000;

    //概述数据一页加载的文件数量
    private int overview_page_filenum = 2;

    //添加的pagechart按照每1000个点中的超限个数显示吧，这样没有的话就显示0，使用的时候比较好定位哪里有超过设定值
    private int page_threshold_num = 4000;


    public int getUI_Chart_ShowLimit() {
        return UI_Chart_ShowLimit;
    }

    public void setUI_Chart_ShowLimit(int UI_Chart_ShowLimit) {
        this.UI_Chart_ShowLimit = UI_Chart_ShowLimit;
    }

    public int getFile_MaxSize() {
        return File_MaxSize;
    }

    public void setFile_MaxSize(int file_MaxSize) {
        File_MaxSize = file_MaxSize;
    }

    public int getTimer_Statis() {
        return Timer_Statis;
    }

    public void setTimer_Statis(int timer_Statis) {
        Timer_Statis = timer_Statis;
    }

    public int getOverview_page_filenum() {
        return overview_page_filenum;
    }

    public void setOverview_page_filenum(int overview_page_filenum) {
        this.overview_page_filenum = overview_page_filenum;
    }

    public int getPage_threshold_num() {
        return page_threshold_num;
    }

    public void setPage_threshold_num(int page_threshold_num) {
        this.page_threshold_num = page_threshold_num;
    }

    public int getPage_MaxSize() {
        return Page_MaxSize;
    }

    public void setPage_MaxSize(int page_MaxSize) {
        Page_MaxSize = page_MaxSize;
    }
}
