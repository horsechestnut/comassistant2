package com.example.administrator.comassistant2.simulation.filesave;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.administrator.comassistant2.ApplicationController;
import com.example.administrator.comassistant2.simulation.Config;
import com.example.administrator.comassistant2.simulation.bean.PageFileIndexBean;
import com.example.administrator.comassistant2.simulation.bean.PageFileQueueBean;
import com.example.administrator.comassistant2.simulation.bean.PageQueueIndexData;
import com.example.administrator.comassistant2.simulation.tool.FileUtil;
import com.example.administrator.comassistant2.simulation.tool.IConstant;
import com.example.administrator.comassistant2.simulation.tool.LogUtil;
import com.example.administrator.comassistant2.simulation.tool.TimeStatisIt;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.example.administrator.comassistant2.simulation.tool.MyFunc.byteToInt;

public class TempFileQueueThread extends Thread implements IConstant {
    private int jjTempFileId = 1; //临时文件名，从1开始,当前实时数据的
    private List<Integer> tempList;
    private List<Integer> pageList; //用于处理临时数据

    private int THREE = 3;
    private Config jjConfig;
    private BlockingQueue queue;
    public TimeStatisIt jjTimeStatis;
    /*
     * 历史引导分页
     */
    public PageQueueIndexData jjPageIndex = new PageQueueIndexData();
    /**
     * 引导分页
     */
    public PageFileIndexBean jjPageIndex2 = new PageFileIndexBean();


    public TempFileQueueThread() {
        tempList = new ArrayList<>();
        pageList = new ArrayList<>();
        jjConfig = new Config();
        jjTimeStatis = new TimeStatisIt("TempFile");
        queue = new ArrayBlockingQueue(10000);
        init();
    }

    public List<Integer> getTempList() {
        return tempList;
    }


    //获取当前分页数据，当前文件的所有页数据
    public List<Integer> getAssignedPageList() {
        List<Integer> rlt = new ArrayList<>();

        //如果需要添加临时文件，则要每次都要刷新;而历史数据只装载一次
        rlt = genPageIndexDataList(jjPageIndex2.getFile_start_id());

        return rlt;
    }


    private int getLastestFile() {
        List<File> list = FileUtil.getSubFilesByModfiyTime(FileOpster.tempPath);
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

            Comparator cmp = Collections.reverseOrder();
            Collections.sort(fileNumList, cmp);

            return fileNumList.get(0);
        }

        return -1;
    }


    private boolean isLastPage(int in_curFileNum) {
        List<File> list = FileUtil.getSubFilesByModfiyTime(FileOpster.tempPath);
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

            Comparator cmp = Collections.reverseOrder();
            Collections.sort(fileNumList, cmp);

            if (in_curFileNum == fileNumList.get(0) || (in_curFileNum - 1) == fileNumList.get(0)) {
                //已经是最后一页了
                return true;
            }
        } else {
            //空目录，则认为是ture
            return true;
        }
        return false;
    }


    public List<Integer> getPrePageList_V2() {
        List<Integer> rlt = new ArrayList<>();

        if (jjPageIndex2.getPage_id() == IConstant.Lastest_FileNum) {
            int lastestFile = getLastestFile();
            if (lastestFile == -1) {
                return rlt;
            } else {
                if (lastestFile == 1) {
                    //只有一页
                    jjPageIndex2.initEarlyestStatus();
                    return rlt;
                }

                jjPageIndex2.setPage_id(lastestFile - 1);
                jjPageIndex2.genFileIndex();
                rlt = genPageIndexDataList(jjPageIndex2.getFile_start_id());
                return rlt;
            }
        }

        if (jjPageIndex2.getPage_id() > 1) {
            jjPageIndex2.setPage_id(jjPageIndex2.getPage_id() - 1);
            jjPageIndex2.genFileIndex();

            rlt = genPageIndexDataList(jjPageIndex2.getFile_start_id());
            return rlt;
        }

        if (jjPageIndex2.getPage_id() == 1) {
            jjPageIndex2.initEarlyestStatus();
        }


        return rlt;
    }

    //获取前一个文件的所有分页数据
    @Deprecated
    public List<Integer> getPrePageList() {
        List<Integer> rlt = new ArrayList<>();
        boolean isNeedTemp = false;
        if (jjPageIndex.getFileNum() == -1 || jjPageIndex.getFileNum() == -3) {
            isNeedTemp = true;
        }

        LogUtil.ii("临时数据数量 " + tempList.size());
        genPLIndex();

        if (jjPageIndex.getFileNum() > 0) {
            //读取一页数据，然后添加之
            String fileName = "Rdb" + String.valueOf(jjPageIndex) + ".bin";
            String fileAllName = FileOpster.tempPath + fileName;
            File file = new File(fileAllName);
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
                LogUtil.ii("添加文档数据 " + fileName + "  " + rlt.size());
            }

            //
            if (isNeedTemp && tempList != null && tempList.size() > 0) {
                rlt.addAll(tempList);
                LogUtil.ii("添加临时数据 " + fileName + "  " + rlt.size());
            }
        }
        return rlt;
    }

    //获取当前需要展示的路径信息
    private void genPLIndex() {
        List<File> list = FileUtil.getSubFilesByModfiyTime(FileOpster.tempPath);
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
            //倒序排列
            Comparator cmp = Collections.reverseOrder();
            Collections.sort(fileNumList, cmp);

            if (jjPageIndex.getFileNum() == -1 || jjPageIndex.getFileNum() == -3) {
                jjPageIndex.setFileNum(fileNumList.get(0));
                List<Integer> rlt = genPageIndexDataList(fileNumList.get(0));
                jjPageIndex.setDataList(rlt);
            } else {
                if (jjPageIndex.getFileNum() == fileNumList.get(fileNumList.size() - 1)) {
                    //已经是最后一页了
                    jjPageIndex.setFileNum(-2);
                    List<Integer> rlt = genPageIndexDataList(fileNumList.get(fileNumList.size() - 1));
                    jjPageIndex.setDataList(rlt);
                } else {
                    //找到最后一个数据
                    for (int i = 0; i < fileNumList.size(); i++) {
                        if (fileNumList.get(i) < jjPageIndex.getFileNum()) {
                            jjPageIndex.setFileNum(fileNumList.get(i));
                            List<Integer> rlt = genPageIndexDataList(fileNumList.get(i));
                            jjPageIndex.setDataList(rlt);
                            break;
                        }
                    }
                }
            }
        } else {
            jjPageIndex.setFileNum(-1);
        }

        LogUtil.ii("当前跟踪路径 " + jjPageIndex);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                Integer t = (Integer) queue.take();
                hadleQueue(t);
            } catch (Exception e) {
                LogUtil.ee(e);
            }
        }
    }

    public void addQueue(int in_num) {
        try {
            queue.offer(in_num, THREE, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LogUtil.ee(e);
        }
    }

    public int queueSize() {
        return queue.size();
    }

    private void hadleQueue(Integer in_num) {
        tempList.add(in_num);


        do4PageChart(in_num); //Page表处理

        if (tempList.size() >= jjConfig.getFile_MaxSize()) {
            jjTimeStatis.startIt();
            doWriteIt();
            jjTimeStatis.endIt();
        }
    }

    //处理Chart表数据
    private void do4PageChart(Integer in_num) {
        pageList.add(in_num);

        if (pageList.size() >= jjConfig.getPage_MaxSize()) {
            jjPageIndex2.setPage_id(jjPageIndex2.getPage_id() + 1);//自动加1
            jjPageIndex2.genFileIndex();
            LogUtil.ii("pageid " + jjTempFileId);
            //拷贝数据
            List<Integer> tempList = new ArrayList(Arrays.asList(new Integer[pageList
                    .size()]));
            Collections.copy(tempList, pageList);

            Intent intent = new Intent();
            PageFileQueueBean intentbean = new PageFileQueueBean();
            intentbean.pageIndex = jjPageIndex2.getPage_id();
            intentbean.fileNum = jjTempFileId;
            intentbean.dataList = tempList;
            intent.setAction(Broad_DoPageChart);
            intent.putExtra(IntentKey_PageChart_PageIndex, intentbean);

            LocalBroadcastManager.getInstance(ApplicationController.getInstance().getApplicationContext()).sendBroadcast(intent);
            pageList.clear();
        }

        if (pageList.size() >= jjConfig.getPage_threshold_num()) {
            //每隔1000，边进行计算统计数据，并清零
            int pageindex = tempList.size() / jjConfig.getPage_threshold_num();

            Intent intent = new Intent();
            PageFileQueueBean intentbean = new PageFileQueueBean();
            intentbean.pageIndex = pageindex;
            intentbean.fileNum = jjTempFileId;


            List<Integer> tempList = new ArrayList(Arrays.asList(new Integer[pageList
                    .size()]));
            Collections.copy(tempList, pageList);

            intentbean.dataList = tempList;
            intent.setAction(Broad_DoPageChart);
            intent.putExtra(IntentKey_PageChart_PageIndex, intentbean);

//            LocalBroadcastManager.getInstance(ApplicationController.getInstance().getApplicationContext()).sendBroadcast(intent);
            pageList.clear();
        }
    }


    //将templist的数据写入
    public void doWriteIt() {
        int lastSize = queue.size();
        int alen = tempList.size();
        if (alen <= 0) {
            return;
        }
        long time1 = System.currentTimeMillis();
        byte[] rd = new byte[alen * 4];
        for (int ii = 0; ii < alen; ii++) {
            int temp = tempList.get(ii);
            for (int i = 0; i < 4; i++) {
                rd[i + ii * 4] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
                temp = temp >> 8;// 向右移8位
            }
        }

        String fileName = "Rdb" + String.valueOf(jjTempFileId) + ".bin";
        String fileAllName = FileOpster.tempPath + fileName;
        FileUtil.writeFile(rd, fileAllName);

        tempList.clear();
        jjTempFileId++;
        long time2 = System.currentTimeMillis();
        LogUtil.ii("Temp写入 " + fileName + " 耗时 " + (time2 - time1) + " 数据量 " + alen + " 当前队列 " + queue.size() + " 之前队列 " + lastSize);
        LogUtil.ii("Page写入 " + fileName + " 耗时 " + (time2 - time1) + " 数据量 " + alen + " 当前队列 " + queue.size() + " 之前队列 " + lastSize);
    }

    public void init() {
        jjTempFileId = 1;
        FileOpster.clearDis(FileOpster.tempPath);
        LogUtil.ii("TempInit " + queue.size() + " " + tempList.size());
        if (queue.size() > 0) {
            queue.clear();
        }
        if (tempList.size() > 0) {
            tempList.clear();
        }

        if (pageList.size() > 0) {
            pageList.clear();
        }
    }


    //---------------pageindex-------------------------------------------------------
    public List<Integer> getNextPageList_V2() {
        List<Integer> rlt = new ArrayList<>();
        if (jjPageIndex2.getPage_id() == Earlyest_FileNum) {
            jjPageIndex2.setPage_id(2);
            jjPageIndex2.genFileIndex();
            return genPageIndexDataList(jjPageIndex2.getPage_id());
        }

        if (jjPageIndex2.getPage_id() == Lastest_FileNum) {
            return rlt;
        }

        if (isLastPage(jjPageIndex2.getPage_id())) {
            jjPageIndex2.initLastestStatus();
            return rlt;
        }

        jjPageIndex2.setPage_id(jjPageIndex2.getPage_id() + 1);
        rlt = genPageIndexDataList(jjPageIndex2.getPage_id());
        return rlt;
    }

    //获取下一页的数据pageindex
    @Deprecated
    public List<Integer> getNextPageList() {
        genPLNextIndex(); //漂移指向

        if (jjPageIndex.getFileNum() == -1 || jjPageIndex.getFileNum() == -4) {
            return null; //已经右侧底部了
        }

        if (jjPageIndex.getFileNum() == -2) {
            return null; //已经左侧底部了，此时加载第一页就可以了,理论上不存在这个状态
        }

        List<Integer> rlt = new ArrayList<>();

        LogUtil.ii("临时数据数量 " + tempList.size());

        if (jjPageIndex.getFileNum() == -3) {
            if (tempList != null && tempList.size() > 0) {
                rlt.addAll(tempList);
                LogUtil.ii("添加临时数据    " + rlt.size());
                return rlt;
            } else {
                return null;
            }
        }


        if (jjPageIndex.getFileNum() > 0) {
            //读取一页数据，然后添加之
            String fileName = "Rdb" + String.valueOf(jjPageIndex) + ".bin";
            String fileAllName = FileOpster.tempPath + fileName;
            File file = new File(fileAllName);
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
                LogUtil.ii("添加文档数据 " + fileName + "  " + rlt.size());
            }
        }
        return rlt;
    }

    //返回值告知是不是最右侧一页
    private boolean genPLNextIndex() {
        if (jjPageIndex.getFileNum() == -1 || jjPageIndex.getFileNum() == -4) {
            LogUtil.ii("当前跟踪路径 " + jjPageIndex);
            return false;
        }

        if (jjPageIndex.getFileNum() == -3) {
            jjPageIndex.setFileNum(-4);
            jjPageIndex.setPageIndex(1);
            return true;
        }


        List<File> list = FileUtil.getSubFilesByModfiyTime(FileOpster.tempPath);
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
            //正序排列
//            Comparator cmp = Collections.reverseOrder();
            Collections.sort(fileNumList);

            if (jjPageIndex.getFileNum() == -2) {
                //最初的一页
                jjPageIndex.setFileNum(fileNumList.get(0));
                jjPageIndex.setPageIndex(1);
                List<Integer> rlt = genPageIndexDataList(jjPageIndex.getFileNum());
                jjPageIndex.setDataList(rlt);
                return false;
            }

            if (jjPageIndex.getFileNum() == fileNumList.get(fileNumList.size() - 1) && tempList.size() > 0) {
                //把temp列表填充进去
                jjPageIndex.setFileNum(-3);
                jjPageIndex.setPageIndex(1);
                List<Integer> rlt = genPageIndexDataList(fileNumList.get(fileNumList.size() - 1));
                jjPageIndex.setDataList(rlt);
                return false;
            }


            //找到最后一个数据
            for (int i = 0; i < fileNumList.size(); i++) {
                if (fileNumList.get(i) > jjPageIndex.getFileNum()) {
                    jjPageIndex.setFileNum(fileNumList.get(i));
                    jjPageIndex.setPageIndex(1);
                    List<Integer> rlt = genPageIndexDataList(jjPageIndex.getFileNum());
                    jjPageIndex.setDataList(rlt);
                    break;
                }
            }

            return false;
        }

        LogUtil.ii("当前跟踪路径 " + jjPageIndex);
        return false;
    }

    public void setPageFileIndex(int in_page) {
        PageFileIndexBean temp = new PageFileIndexBean();
        temp.setPage_id(in_page);
        temp.genFileIndex();
        jjPageIndex2 = temp;
    }


    public void setPageFileIndex(int in_file, int in_page) {
        jjPageIndex.setPageIndex(in_page);

        //如果需要添加临时文件，则要每次都要刷新;而历史数据只装载一次
        String fileName = "Rdb" + String.valueOf(in_file) + ".bin";
        String fileAllName = FileOpster.tempPath + fileName;
        File file = new File(fileAllName);

        if (jjPageIndex.getFileNum() != in_file || file == null || !file.exists()) {
            jjPageIndex.setFileNum(in_file);
            List<Integer> rlt = genPageIndexDataList(in_file);
            jjPageIndex.setDataList(rlt);
        }
    }


    private List<Integer> genPageIndexDataList(int in_file) {
        List<Integer> rlt = new ArrayList<>();
        if (in_file < 0) {
            if (in_file == -4) {
                if (tempList != null && tempList.size() > 0) {
                    rlt.addAll(tempList);
                    LogUtil.ii("添加临时数据   " + rlt.size());
                }
            }
            return rlt;
        }


        boolean isNeedTemp = isLastPage(in_file);
        String fileName = "Rdb" + String.valueOf(in_file) + ".bin";
        String fileAllName = FileOpster.tempPath + fileName;
        File file = new File(fileAllName);
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
            LogUtil.ii("添加文档数据 " + fileName + "  " + rlt.size());
        } else {
            if (isNeedTemp && tempList != null && tempList.size() > 0) {
                rlt.addAll(tempList);
                LogUtil.ii("添加临时数据 " + fileName + "  " + rlt.size());
            }
        }

        return rlt;
    }
}
