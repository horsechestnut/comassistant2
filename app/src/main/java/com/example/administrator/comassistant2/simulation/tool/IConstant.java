package com.example.administrator.comassistant2.simulation.tool;

public interface IConstant {
    int Event_OpenCollector = 4;//打开采集器
    int Event_TimingStatisLog = 10;//定时日志
    int Event_DrawPageChart = 16;//绘制page图表
    int Type_His = 2;
    int Type_Temp = 1;
    int Result_SetActivity = 24;
    int Type_PageIndex_AllPage = -15; //Prepage和NextPage时的取向
    String Broad_DoPageChart = "Broad_DoPageChart"; //绘制page图标
    String IntentKey_PageChart_FileNum = "IntentKey_PageChart_FileNum"; //文件索引
    String IntentKey_PageChart_PageIndex = "IntentKey_PageChart_PageIndex"; //文件索引

}
