package com.example.administrator.comassistant2.simulation.filesave;

import com.example.administrator.comassistant2.simulation.Config;
import com.example.administrator.comassistant2.simulation.bean.PageQueueIndexData;
import com.example.administrator.comassistant2.simulation.tool.FileUtil;
import com.example.administrator.comassistant2.simulation.tool.IConstant;
import com.example.administrator.comassistant2.simulation.tool.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.example.administrator.comassistant2.simulation.tool.MyFunc.byteToInt;

public class HisFileQueueThread extends Thread implements IConstant {
    private int jjHisFileIndex = -1; //从0开始
    private List<Integer> curHisList = new ArrayList<>(); //历史文件的临时缓冲数据
    private int THREE = 3;
    private BlockingQueue queue = new ArrayBlockingQueue(100000);
    private Status jjStatus = Status.Init;
    List<String> jjFileNameList = new ArrayList<>(); //所有的历史文件
    Config jjConfig = new Config();
    int wantAddNum = 2000; //当队列容量低于这个数时，边进行插入操作
    public PageQueueIndexData jjPageIndex = new PageQueueIndexData();

    public int getOne() {
        try {
            int size = queue.size();
            if (size > 0) {
                int rlt = (int) queue.take();
                return rlt;
            }

        } catch (Exception e) {
            LogUtil.ee(e);
        }
        return 0;
    }

    public List<Integer> getPageList(int in_pageindex) {
        List<Integer> rlt = new ArrayList<>();
        if (in_pageindex == Earlyest_FileNum || in_pageindex == Lastest_FileNum) {
            return rlt;
        }

        if (in_pageindex > 0) {
            rlt = getFileData(in_pageindex);
        }

        return rlt;
    }

    //依据文件数获取历史数据
    @Deprecated
    public List<Integer> getPageList(int in_filenum, int pageindex) {
        if (jjPageIndex == null || jjPageIndex.getFileNum() != in_filenum || jjPageIndex.getDataList() == null || jjPageIndex.getDataList().size() <= 0) {
            //更新文件数据
            jjPageIndex.setFileNum(in_filenum);
            List<Integer> fileData = getFileData(jjPageIndex.getFileNum());
            jjPageIndex.setDataList(fileData);
        }


        List<Integer> rlt = new ArrayList<>();

        if (jjPageIndex.getDataList() == null || jjPageIndex.getDataList().size() <= 0) {
            return rlt;
        }

        if (pageindex >= 0) {
            jjPageIndex.setPageIndex(pageindex);
            int start = (jjPageIndex.getPageIndex() - 1) * jjConfig.getPage_threshold_num();
            int end = start + jjConfig.getPage_threshold_num();

            for (int i = start; i < end; i++) {
                if (i < jjPageIndex.getDataList().size()) {
                    rlt.add(jjPageIndex.getDataList().get(i));
                }
            }
        }
        return rlt;
    }

    private List<Integer> getFileData(int in_filenum) {
        String hisName = "Hdb" + in_filenum + ".bin";
        List<Integer> rlt = new ArrayList<>();
        String fileName = FileOpster.hisPath + hisName;
        File file = new File(fileName);
        if (file.exists()) {
            byte[] rb = FileUtil.readFileBytes(file);
            for (int i = 0; i < rb.length / 4; i++) {
                try {
                    int data = byteToInt(rb, i * 4);
                    rlt.add(data);
                } catch (Exception e) {
                    LogUtil.ee(e);
                }
            }
            return rlt;
        } else {
            return null;
        }
    }

    public enum Status {
        Init,//初始状态
        Work,
        EMPTYHIS, //历史数据为空
        EndHis //遍历完毕所有的历史文件
    }

    public HisFileQueueThread() {
        wantAddNum = jjConfig.getUI_Chart_ShowLimit() / 3;//三分之一时
    }

    public void init() {
        boolean isHasHis = initHisFileList();
        if (isHasHis) {
            //装载第一个数据
            addOneFile();
        }

    }

    private boolean initHisFileList() {
        jjFileNameList.clear();
        jjHisFileIndex = -1;
        setStatus(Status.Init);

        //第一次加载一个数据
        List<File> list = FileUtil.getSubFilesByModfiyTime(FileOpster.hisPath);
        if (list != null && list.size() > 0) {
            List<Integer> fileNumList = new ArrayList<>();
            //按照字母进行排序
            for (File item : list
                    ) {
                String srcName = item.getName();
                int endPos = srcName.indexOf(".");
                int num = Integer.valueOf(srcName.substring(3, endPos));
                fileNumList.add(num);
            }
            Collections.sort(fileNumList);

            for (Integer item : fileNumList
                    ) {
                String hisName = "Hdb" + item + ".bin";
                jjFileNameList.add(hisName);
            }
            LogUtil.ii("HIS wantAddNum " + wantAddNum + " 历史文件数 " + jjFileNameList.size());
            setStatus(Status.Work);
            return true;
        } else {
            setStatus(Status.EMPTYHIS);
        }
        return false;
    }

    public List<Integer> getCurHisList() {
        return curHisList;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                try {
                    int sleepTime = 200;
                    if (jjStatus == Status.Init || jjStatus == Status.EMPTYHIS || jjStatus == Status.EndHis) {
                        Thread.sleep(sleepTime * 3);
                        continue;
                    }

                    //判断当前队列的size，如果小于10000时，则填充缓冲队列
                    if (jjStatus == Status.Work && queueSize() < wantAddNum) {
                        addOneFile();
                    }

                    Thread.sleep(sleepTime);
                } catch (Exception e) {
                    LogUtil.ee(e);
                }
            }
        } catch (Exception e) {
            LogUtil.ee(e);
        }

    }

    private void setStatus(Status in_status) {
        jjStatus = in_status;
        LogUtil.ii("HIS状态 " + jjStatus.name());
    }

    private void addOneFile() {
        jjHisFileIndex++;
        if (jjHisFileIndex > (jjFileNameList.size() - 1)) {
            setStatus(Status.EndHis);
            return;
        }

        String fileName = FileOpster.hisPath + jjFileNameList.get(jjHisFileIndex);
        File file = new File(fileName);
        if (file.exists()) {
            int oldSize = queue.size();
            long time1 = System.currentTimeMillis();
            byte[] rb = FileUtil.readFileBytes(file);
            for (int i = 0; i < rb.length / 4; i++) {
                try {
                    int data = byteToInt(rb, i * 4);
                    queue.offer(data, THREE, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    LogUtil.ee(e);
                }
            }
            long time2 = System.currentTimeMillis();
            LogUtil.ii("His填充 " + jjFileNameList.get(jjHisFileIndex) + " 填充前 " + oldSize + " 填充后 " + queueSize() + " 耗时 " + (time2 - time1));
        }
    }

    public int queueSize() {
        return queue.size();
    }


}
