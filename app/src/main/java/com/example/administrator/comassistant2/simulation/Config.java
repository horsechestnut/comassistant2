package com.example.administrator.comassistant2.simulation;

public class Config {

    private int Chart_InitAdd_Num = 1000; //表初始化时添加的数据
    private int Page_InitAdd_Num = 11; //分页表初始化时添加的数据
    //Chart一次最多装载的数据
    private int UI_Chart_ShowLimit = 2000;
    //单个文件存储的数据数量，文件大小是其4倍，比如2000个数据是8K
    // 配置方式有两种，按照大约一分钟的数据量，大约234K;
    //也可以是Chart一次装载的整数倍
    private int File_MaxSize = 2000 * 2*4;
    //分页最大值
    private int Page_MaxSize = 2000 * 2*4;

//    private int File_MaxSize = 2000*10*2;
//    private int File_MaxSize= 1000*60*4;

    //默认第一次是true
    private static boolean isFirstStart = true;

    private int Timer_Statis = 1000;

    //概述数据一页加载的文件数量
    private int overview_page_filenum = 2;


    public static boolean isIsFirstStart() {
        return isFirstStart;
    }

    public static void setIsFirstStart(boolean isFirstStart) {
        Config.isFirstStart = isFirstStart;
    }


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


    public int getPage_MaxSize() {
        return Page_MaxSize;
    }

    public void setPage_MaxSize(int page_MaxSize) {
        Page_MaxSize = page_MaxSize;
    }

    public int getChart_InitAdd_Num() {
        return Chart_InitAdd_Num;
    }

    public void setChart_InitAdd_Num(int chart_InitAdd_Num) {
        Chart_InitAdd_Num = chart_InitAdd_Num;
    }

    public int getPage_InitAdd_Num() {
        return Page_InitAdd_Num;
    }

    public void setPage_InitAdd_Num(int page_InitAdd_Num) {
        Page_InitAdd_Num = page_InitAdd_Num;
    }
}
