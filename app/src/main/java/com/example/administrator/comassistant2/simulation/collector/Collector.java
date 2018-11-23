package com.example.administrator.comassistant2.simulation.collector;

import android.os.Message;
import android.util.Log;

import com.example.administrator.comassistant2.simulation.ComAssistantActivity;
import com.example.administrator.comassistant2.simulation.bean.ComBean;
import com.example.administrator.comassistant2.simulation.bean.StatusOPS;
import com.example.administrator.comassistant2.simulation.tool.StatisIt;
import com.example.administrator.comassistant2.simulation.tool.TimeStatisIt;

import java.util.Random;

public class Collector {
    ComAssistantActivity jjAcitivity;
    String TAG = "Collector";
    boolean jjIsOpen = false;


    int Time_Interval = 200; //时间间隔，
    int Nums_Once = 1; //一次采集的数量,可以生成200个数据

    public StatisIt jjStatis;
    public  TimeStatisIt jjTimeStatis;


    public Collector(ComAssistantActivity in_activity) {
        jjAcitivity = in_activity;
        jjStatis = new StatisIt();
        jjTimeStatis = new TimeStatisIt("Collector");
    }

    public int getStatis() {
        return jjStatis.getStatus_index();
    }

    public void clearStatis() {
        jjStatis.ops(StatusOPS.Clear);
    }

    public boolean isOpen() {
        return jjIsOpen;
    }


    /*
     * 10: 20K/s
     * 50: 4K/s
     * 100: 2K/s
     * 200: 1K/s : 缺省
     * 400: 0.5K/s
     * 1000: 0.2K/S
     * */
    public void setTimeInterval(int in_time) {
        Time_Interval = in_time;
    }

    public int getTime_Interval() {
        return Time_Interval;
    }

    //开始采集
    public void open() {
        //生成
        jjIsOpen = true;
        jjhandler.sendEmptyMessageDelayed(1, Time_Interval);
    }

    public void sendTxt(String sTxt) {
        Log.d(TAG, "sendTxt: " + sTxt);
    }

    String sPort = "/dev/ttyS3";
    private android.os.Handler jjhandler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case 1:
                        if (!isOpen()) {
                            return;
                        }

                        jjTimeStatis.startIt();
                        for (int i = 0; i < Nums_Once; i++) {
                            //一次生成200个数据点，byte数605
                            byte recBuf[] = new byte[2048];
                            initData(recBuf);
                            int minPkgLen = 605;
                            ComBean item = new ComBean(sPort, recBuf, minPkgLen);
                            jjStatis.ops(StatusOPS.AddOne);
                            jjAcitivity.jjBufferManager.drawChartUI(item);
                        }

                        jjhandler.sendEmptyMessageDelayed(1, Time_Interval);
                        jjTimeStatis.endIt();
                        break;

                }
            } catch (Exception e) {
                Log.e(TAG, "handleMessage: ", e);
            }
        }
    };

    Random random = new Random();

    private void initData(byte[] recBuf) {
        for (int i = 0; i < 605; i++) {
            if (i == 0) {
                recBuf[i] = 0x02;
                continue;
            }
            if (i == 603) {
                recBuf[i] = 0x0d;
                continue;
            }
            if (i == 604) {
                recBuf[i] = 0x0a;
                continue;
            }

//            recBuf[i] = 75;

            recBuf[i] = (byte) random.nextInt(125);
        }
    }

    public void stopSend() {
        jjhandler.removeMessages(1);
    }

    public void close() {
        jjhandler.removeMessages(1);
        jjIsOpen = false;
    }
}
