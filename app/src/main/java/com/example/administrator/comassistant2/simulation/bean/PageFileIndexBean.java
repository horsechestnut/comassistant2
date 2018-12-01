package com.example.administrator.comassistant2.simulation.bean;

import com.example.administrator.comassistant2.simulation.Config;
import com.example.administrator.comassistant2.simulation.tool.IConstant;
import com.example.administrator.comassistant2.simulation.tool.LogUtil;

//分页设置
public class PageFileIndexBean implements IConstant {
    private int file_start_id; //文件開始id
    private int file_end_id; //文件结束id
    private int file_start_index; //文件开始索引
    private int file_end_index; //文件结束索引
    private int page_id;


    public PageFileIndexBean() {
        page_id = Init_PageNum;
        file_end_id = Init_FileNum;
        file_start_index = Init_FileNum;
        file_end_index = Init_FileNum;
        file_end_id = Init_FileNum;

    }

    //生成文件索引值，依据pageid
    public void genFileIndex() {
        Config jjConfig = new Config();
        int start_num = (page_id - 1) * jjConfig.getPage_MaxSize();
        int end_num = (page_id) * jjConfig.getPage_MaxSize() - 1;
        file_start_id = start_num / jjConfig.getFile_MaxSize() + 1;
        file_start_index = start_num % jjConfig.getFile_MaxSize() ;
        file_end_id = end_num / jjConfig.getFile_MaxSize()+1;
        file_end_index = end_num % jjConfig.getFile_MaxSize();
        LogUtil.ii("pageid " + page_id + " start_id " + file_start_id + " end_id " + file_end_id + " start_index " + file_start_index + " end_index " + file_end_index);
    }


    public int getFile_start_id() {
        return file_start_id;
    }

    public void setFile_start_id(int file_start_id) {
        this.file_start_id = file_start_id;
    }

    public int getFile_end_id() {
        return file_end_id;
    }

    public void setFile_end_id(int file_end_id) {
        this.file_end_id = file_end_id;
    }

    public int getFile_start_index() {
        return file_start_index;
    }

    public void setFile_start_index(int file_start_index) {
        this.file_start_index = file_start_index;
    }

    public int getFile_end_index() {
        return file_end_index;
    }

    public void setFile_end_index(int file_end_index) {
        this.file_end_index = file_end_index;
    }

    public int getPage_id() {
        return page_id;
    }

    public void setPage_id(int page_id) {
        this.page_id = page_id;
    }


    public void initEarlyestStatus() {
        page_id = Earlyest_FileNum;
        file_end_id = Earlyest_FileNum;
        file_start_index = Earlyest_FileNum;
        file_end_index = Earlyest_FileNum;
        file_end_id = Earlyest_FileNum;
    }
    public void initLastestStatus() {
        page_id = Lastest_FileNum;
        file_end_id = Lastest_FileNum;
        file_start_index = Lastest_FileNum;
        file_end_index = Lastest_FileNum;
        file_end_id = Lastest_FileNum;
    }
}
