package com.example.administrator.comassistant2.simulation;

import android.widget.Toast;

import com.example.administrator.comassistant2.simulation.bean.ComBean;
import com.example.administrator.comassistant2.simulation.bean.PageFileQueueBean;
import com.example.administrator.comassistant2.simulation.bean.PageQueueIndexData;
import com.example.administrator.comassistant2.simulation.chart.ChartQueueThread;
import com.example.administrator.comassistant2.simulation.filesave.FileOpster;
import com.example.administrator.comassistant2.simulation.filesave.PageFileQueueThread;
import com.example.administrator.comassistant2.simulation.tool.LogUtil;

import java.util.ArrayList;
import java.util.List;

import static com.example.administrator.comassistant2.simulation.tool.DataSwitcher.switchToKM;

//总控制器，协调采集器、文件读写、UI显示的速度协调
public class BufferManager {
    FileOpster jjFileOpster;
    ComAssistantActivity jjActivity;
    ChartQueueThread jjChartQueue;
    PageFileQueueThread jjPageChartQueue;
    long jjStartTime = 0;

    public BufferManager(ComAssistantActivity in_activity) {
        jjActivity = in_activity;
        jjFileOpster = new FileOpster();
        jjChartQueue = new ChartQueueThread(in_activity);
        jjPageChartQueue = new PageFileQueueThread(jjActivity.avgHandler);
        jjStartTime = System.currentTimeMillis();
    }

    public void init() {
        jjChartQueue.start();
        jjFileOpster.init();
        jjPageChartQueue.start();
    }


    public void status() {
        try {
            //发送速度，Chart队列，Temp队列，His队列，Temp文件数，His文件数，累计运行时间，累计采集数据
            int collectNum = jjActivity.ComA.jjStatis.getStatus_index();

            StringBuffer jjBuffer = new StringBuffer();
            jjBuffer.append("GXTStatis : 采集速度 " + switchToKM(collectNum * 200));

            int chartQueueNum = jjChartQueue.getQueueSize();
            jjBuffer.append(" Chart队列: " + chartQueueNum);

            int tempQueueNum = jjFileOpster.getTempQueueSize();
            jjBuffer.append(" 临时文件队列: " + tempQueueNum);

            int hisQueueNum = jjFileOpster.getHisQueueNum();
            jjBuffer.append(" 历史文件队列: " + hisQueueNum);

            jjBuffer.append(" 采集详情 [ " + jjActivity.ComA.jjTimeStatis.showIt()).append(" ]");
            jjBuffer.append(" Chart队列 [ " + jjChartQueue.jjTimeStatis.showIt()).append(" ]");
            jjBuffer.append(" Temp队列 [ " + jjFileOpster.jjTempQueueThread.jjTimeStatis.showIt()).append(" ]");
//            LogUtil.ii(jjBuffer.toString());


            jjActivity.ComA.jjTimeStatis.clear();
            jjActivity.ComA.clearStatis();
            jjChartQueue.jjTimeStatis.clear();
            jjFileOpster.jjTempQueueThread.jjTimeStatis.clear();
        } catch (Exception e) {
            LogUtil.ee(e);
        }
    }


    //实时采集的一个数据点，进行保存记录
    public void writeToTemp(int in_num) {
        jjFileOpster.addTempWriteQueue(in_num);
    }

    //保存历史数据
    public void saveToHis() {
        int num = jjFileOpster.saveToHis();
        Toast.makeText(jjActivity.getApplicationContext(), "保存数据成功，文件数: " + num, Toast.LENGTH_SHORT).show();
    }

    public int readHisData() {
        return jjFileOpster.readHisData();
    }

    public void drawChartUI(ComBean item) {
        jjChartQueue.addQueue(item);
    }


    //获取左侧分页的实时数据，每调用一次则自动左移一次，同时变更jjPageIndex数据
    public List<Integer> getLeftPageTempDataList() {
        return jjFileOpster.jjTempQueueThread.getPrePageList();
    }

    public List<Integer> getAssignedPageList() {
        return jjFileOpster.jjTempQueueThread.getAssignedPageList();
    }

    //设置分页数据的文件index
    public void setPageFileIndex(int in_file, int in_page) {
        jjFileOpster.jjTempQueueThread.setPageFileIndex(in_file, in_page);
    }

    //获取左侧分页的历史数据，依据jjPageIndex，如果his文件存在，则认为也有
    public List<Integer> getPageHisDataList() {
        int tempindex = getPageFileIndex();
        int pageindex = jjFileOpster.jjTempQueueThread.jjPageIndex.getPageIndex();
        return jjFileOpster.jjHisQueueThread.getPageList(tempindex, pageindex);

    }

    //获取分页数据的文件index
    public int getPageFileIndex() {
        return jjFileOpster.jjTempQueueThread.jjPageIndex.getFileNum();
    }


    //由文件数据转换成图表显示数据
    public List<Float> genChartList(List<Integer> tempList) {
        if (tempList == null || tempList.size() < 0) {
            return null;
        }

        List<Float> rlt = new ArrayList<>();
        for (int i = 0; i < tempList.size(); i++) {
            float dt = tempList.get(i) / 1000;
            rlt.add(dt);
        }
        return rlt;
    }

    //用户切换到实时监控模式时，此时分页模式会自动回到原来位置
    public void initPageIndex() {
        jjFileOpster.jjTempQueueThread.jjPageIndex = new PageQueueIndexData();
    }

    //获取右侧下一页分页的实时数据，每调用一次则自动右移一次，同时变更jjPageIndex数据
    public List<Integer> getRightPageTempDataList() {
        return jjFileOpster.jjTempQueueThread.getNextPageList();
    }


    //绘制分页数据
    public void do4BroadPageChart(PageFileQueueBean fileNum) {
        jjPageChartQueue.addQueue(fileNum);
    }
}
