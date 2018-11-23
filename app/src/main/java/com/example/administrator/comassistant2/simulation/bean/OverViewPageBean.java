package com.example.administrator.comassistant2.simulation.bean;

import com.example.administrator.comassistant2.simulation.Config;

import java.util.List;

/**
 * 概述目录，用于指示一次加载页面数据对应的起始位置和结束位置
 */
public class OverViewPageBean {
    private int startPos;
    private int endPos;

    public OverViewPageBean() {
        startPos = -1;
        endPos = -1;
    }

    //是否是第一次使用
    public boolean isFirstUsed() {
        if (startPos == endPos && startPos == -1) {
            return true;
        }
        return false;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    public void genStartPos(List<Integer> fileNumList) {
        //已经倒序排列好，且结束位置已经确定好
        if (fileNumList == null || fileNumList.size() <= 0) {
            startPos = -1;
            endPos = -1;
            return;
        }

        Config config = new Config();
        //
        if (isFirstUsed()) {
            endPos = fileNumList.get(0);
            int expected_startpos = endPos - config.getOverview_page_filenum();

            if (expected_startpos <= 0) {
                //预期开始值小于0，则认为已经文件已经遍历完毕，此时文件
            }
        }


    }
}
