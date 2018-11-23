package com.example.administrator.comassistant2.simulation.chart;

import android.util.Log;

import com.example.administrator.comassistant2.simulation.ComAssistantActivity;
import com.example.administrator.comassistant2.simulation.Config;
import com.example.administrator.comassistant2.simulation.bean.ComBean;
import com.example.administrator.comassistant2.simulation.bean.StatusOPS;
import com.example.administrator.comassistant2.simulation.tool.LogUtil;
import com.example.administrator.comassistant2.simulation.tool.MyFunc;
import com.example.administrator.comassistant2.simulation.tool.StatisIt;
import com.example.administrator.comassistant2.simulation.tool.TimeStatisIt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static android.content.ContentValues.TAG;

public class ChartQueueThread extends Thread {
    private BlockingQueue queue;
    private int minPkgLen = 605;

    ComAssistantActivity jjActivity;
    public StatisIt jjStatis;
    Config jjConfig;
    List<Integer> recDataDis;
    List<Float> list = new ArrayList<>();
    public TimeStatisIt jjTimeStatis;

    public ChartQueueThread(ComAssistantActivity in_activity) {
        jjConfig = new Config();
        queue = new ArrayBlockingQueue<ComBean>(10000);
        jjActivity = in_activity;
        jjStatis = new StatisIt();
        recDataDis = new ArrayList<>();
        jjTimeStatis = new TimeStatisIt("chartQueue");
    }

    public int getStatis() {
        return jjStatis.getStatus_index();
    }

    public void clearStatis() {
        jjStatis.ops(StatusOPS.Clear);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                ComBean t = (ComBean) queue.take();
                jjTimeStatis.startIt();
                hadleQueue(t);
                jjTimeStatis.endIt();

//                if (jjActivity.isScopeRunning) {
//                    LogUtil.ii("时间 ChartQueue " + (time2 - time1) + " chartqueue " + Thread.currentThread().getId());
//                }


            } catch (Exception e) {
                LogUtil.ee(e);
            }
        }
    }

    private void hadleQueue(ComBean in_data) {
        if (!jjActivity.isScopeRunning) {
            //不点击开始，不运行数据
            return;
        }

        //解析数据，并且可以传送到显示队列
        int i;
        //将当前的buf拷贝到公共变量中
        List<Byte> recDataBuf = jjActivity.recDataBuf;
        for (i = 0; i < in_data.bRec.length; i++) {
            recDataBuf.add(in_data.bRec[i]);
        }

        //如果公共变量超过阈值时，开始绘制图表
        while (recDataBuf.size() > minPkgLen) {
            //找到有效数据段，以02开始，0d0a结尾
            if (recDataBuf.get(0) == 0x02 && recDataBuf.get(minPkgLen - 2) == 0x0d && recDataBuf.get(minPkgLen - 1) == 0x0a) {
                byte[] dbyte = new byte[minPkgLen];
                for (i = 0; i < minPkgLen; i++) {
                    {
                        dbyte[i] = recDataBuf.get(0);
                        recDataBuf.remove(0);
                    }
                }
                AddDataToDisList(dbyte);
                break;
            } else {
                recDataBuf.remove(0);
            }
        }
    }


    public void AddDataToDisList(byte[] dbyte) {
        synchronized (jjActivity.recDataDis) {
            if (!jjActivity.isScopeRunning) {
                if (jjActivity.recDataDis.size() > 1) jjActivity.recDataDis.clear();
                return;
            }

            //605个byte值转换成200个chart表数据
            recDataDis.clear();
            for (int i = 0; i < (dbyte.length - 5) / 3; i++) {
                double data = (double) (MyFunc.byte3ToInt(dbyte, 3 + i * 3)) * 250000.0 / 131071;
                data = data / 2000;
                recDataDis.add((int) data);
            }

            //判断逻辑
            if (recDataDis.size() < 1 || recDataDis == null) {
                Log.d(TAG, "RefreshCurve: Rec Zero Data");
                return;
            }

            //数据处理
            for (int i = 0; i < recDataDis.size(); i++) {
                float rcv = 0;

                int ircv = recDataDis.get(i);
                if (ircv < 0) {
                    continue;
                }

                jjActivity.jjBufferManager.writeToTemp(ircv); //临时文件保存
                rcv = jjActivity.cmpRelaData(ircv);

                jjActivity.AddDataCounter++;
                jjActivity.rec++;
                jjActivity.recf = true;


                if (jjActivity.rlv > rcv) {
                    jjActivity.rlvf = true;
                    jjActivity.rlv = rcv;
                }

                if (jjActivity.rhv < rcv) {
                    jjActivity.rhvf = true;
                    jjActivity.rhv = rcv;
                }

                int rccv = jjActivity.jjBufferManager.readHisData(); //读取一个历史数据
                float ccv = jjActivity.cmpRelaData(rccv);

                if (jjActivity.clv > ccv) {
                    jjActivity.clvf = true;
                    jjActivity.clv = ccv;
                }
                if (jjActivity.chv < ccv) {
                    jjActivity.chvf = true;
                    jjActivity.chv = ccv;
                }

                list.clear();
                list.add(rcv);
                list.add(ccv);

                if (list.get(0) > list.get(1)) {
                    jjActivity.wcounter++;
                    jjActivity.warFlag = true;
                    jjActivity.wcof = true;
                }

                jjActivity.dynamicLineChartManager2.addEntry(list, 100);
                list.clear();
            }

            recDataDis.clear();
            jjActivity.avgHandler.sendEmptyMessage(15);

        }
    }

    public int getQueueSize() {
        return queue.size();
    }

    public void addQueue(ComBean ComData) {
        queue.add(ComData);
    }
}
