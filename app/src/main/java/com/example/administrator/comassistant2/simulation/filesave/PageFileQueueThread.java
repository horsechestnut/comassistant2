package com.example.administrator.comassistant2.simulation.filesave;

import android.os.Message;

import com.example.administrator.comassistant2.simulation.ComAssistantActivity;
import com.example.administrator.comassistant2.simulation.Config;
import com.example.administrator.comassistant2.simulation.bean.LimitLineBean;
import com.example.administrator.comassistant2.simulation.bean.PageChartDataBean;
import com.example.administrator.comassistant2.simulation.bean.PageFileQueueBean;
import com.example.administrator.comassistant2.simulation.tool.FileUtil;
import com.example.administrator.comassistant2.simulation.tool.IConstant;
import com.example.administrator.comassistant2.simulation.tool.LogUtil;
import com.example.administrator.comassistant2.simulation.tool.TimeStatisIt;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.example.administrator.comassistant2.simulation.tool.MyFunc.byteToInt;

public class PageFileQueueThread extends Thread implements IConstant {
    private int THREE = 3;
    private Config jjConfig;
    private BlockingQueue queue;
    public TimeStatisIt jjTimeStatis;
    ComAssistantActivity.AveHandler jjHanlder;
    List<Float> hisFlieList = new ArrayList<>();
    int jjHisFileId = -1; //临时文件名，从1开始

    public PageFileQueueThread(ComAssistantActivity.AveHandler in_handler) {
        jjConfig = new Config();
        jjTimeStatis = new TimeStatisIt("PageQueue");
        queue = new ArrayBlockingQueue(10000);
        jjHanlder = in_handler;
        init();
    }


    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                PageFileQueueBean t = (PageFileQueueBean) queue.take();
                hadleQueue(t);
            } catch (Exception e) {
                LogUtil.ee(e);
            }
        }
    }

    public void addQueue(PageFileQueueBean in_num) {
        try {
            queue.offer(in_num, THREE, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LogUtil.ee(e);
        }
    }

    public int queueSize() {
        return queue.size();
    }

    private void hadleQueue(PageFileQueueBean in_num) {
        jjTimeStatis.startIt();
        doIt(in_num);
        jjTimeStatis.endIt();

    }

    private void doIt(PageFileQueueBean in_bean) {
        doIt4TempFile(in_bean);
    }


    private void genHisFileList(int in_filenum) {
        hisFlieList = new ArrayList<>();
        String fileAllName = (FileOpster.hisPath + ("Hdb" + String.valueOf(in_filenum) + ".bin"));
        File file = new File(fileAllName);
        if (file.exists()) {
            byte[] rb = FileUtil.readFileBytes(file);
            for (int i = 0; i < rb.length / 4; i++) {
                try {
                    int data = byteToInt(rb, i * 4);
                    Float cc = ComAssistantActivity.cmpRelaData(data);
                    hisFlieList.add(cc);
                } catch (Exception e) {
                    LogUtil.ee(e);
                }
            }
        }
    }

    private void doIt4TempFile(PageFileQueueBean in_bean) {
        LimitLineBean item = LocalSeter.getLimitLine();
        float value = 0.0f;
        if (item == null || (item.getLowValue() == null && item.getHighValue() == null)) {
            value = 0.0f;
        } else {
            //获取策略值计算
            float highvalue = (item.getHighValue() != null && item.getHighValue() >= 0) ? item.getHighValue() : 0.0f;
            float lowvalue = (item.getLowValue() != null && item.getLowValue() >= 0) ? item.getLowValue() : 0.0f;

            List<Integer> datalist = in_bean.dataList;
            int num = 0;
            for (int i = 0; i < datalist.size(); i++) {
                Float cc = ComAssistantActivity.cmpRelaData(datalist.get(i));
                if (cc >= lowvalue && cc <= highvalue) {
                    num++;
                }
            }
            //离差标准归一化
            value = get3Float(Float.valueOf(num) / Float.valueOf(jjConfig.getPage_MaxSize()));
        }


        //PageChartDataBean
        PageChartDataBean pageChartDataBean = new PageChartDataBean();
        pageChartDataBean.setFileIndex(in_bean.fileNum);
        pageChartDataBean.setPageIndex(in_bean.pageIndex);
        pageChartDataBean.getChartData().add(value);
        pageChartDataBean.setType(Type_Temp);

//        int allNum = jjConfig.getFile_MaxSize() / jjConfig.getPage_MaxSize();
//        int startX = allNum * (pageChartDataBean.getFileIndex() - 1) + pageChartDataBean.getPageIndex();
        int startX = pageChartDataBean.getPageIndex();

        LogUtil.ii("Page展示: startX " + startX + " -> " + in_bean.fileNum + " -> " + in_bean.pageIndex + " -> " + value);
        Message msg = new Message();
        msg.what = Event_DrawPageChart;
        msg.obj = pageChartDataBean;
        jjHanlder.sendMessage(msg);
    }

    /*
    private void doIt4File(PageFileQueueBean in_bean, int in_type) {
        PageChartDataBean pageChartDataBean = new PageChartDataBean();
        pageChartDataBean.setFileIndex(in_bean.fileNum);
        pageChartDataBean.setPageIndex(in_bean.pageIndex);
        pageChartDataBean.setType(in_type);

        String fileAllName = in_type == Type_Temp ? (FileOpster.tempPath + ("Rdb" + String.valueOf(in_num) + ".bin")) : (FileOpster.hisPath + ("Hdb" + String.valueOf(in_num) + ".bin"));
//        String fileAllName = FileOpster.tempPath + fileName;
        File file = new File(fileAllName);
        List<Float> rlt = new ArrayList<>();
        if (file.exists()) {
            byte[] rb = FileUtil.readFileBytes(file);
            for (int i = 0; i < rb.length / 4; i++) {
                try {
                    int data = byteToInt(rb, i * 4);
                    Float cc = ComAssistantActivity.cmpRelaData(data);
                    rlt.add(cc);
                } catch (Exception e) {
                    LogUtil.ee(e);
                }
            }
        }

        if (rlt != null || rlt.size() > 0) {
            //降序排列
            Comparator cmp = Collections.reverseOrder();
            Collections.sort(rlt, cmp);

            //求平均值
            float sum = 0;
            for (int i = 0; i < rlt.size(); i++) {
                sum = sum + rlt.get(i);
            }
            float aver = sum / (rlt.size());
            aver = get3Float(aver);
            pageChartDataBean.setAverageValue(aver);

            //求中位数
            int mid_pos = rlt.size() / 2;
            float mid_value = rlt.get(mid_pos);
            pageChartDataBean.setMidValue(mid_value);

            //评判标识基于下面规则，先查看是否有Limit值，如果有则按照其进行判断； 如果没有，则按照均值进行划分
            pageChartDataBean.getChartData().add(aver); //均值作为其指标
            LogUtil.ii("第" + in_num + "页 : " + mid_value + " sum: " + sum + " aver: " + aver + " mid_pos: " + mid_pos + " type: " + in_type);

            //调用图表进行绘制

            if (in_type == Type_Temp) {
                jjTempPageList.add(pageChartDataBean);
            }

            if (in_type == Type_His) {
                jjHisPageList.add(pageChartDataBean);
            }

            Message msg = new Message();
            msg.what = Event_DrawPageChart;
            msg.obj = pageChartDataBean;
            jjHanlder.sendMessage(msg);
        }
    }
*/

    public void init() {
        if (queue.size() > 0) {
            queue.clear();
        }

    }

    public static float get3Float(float a) {
        DecimalFormat df = new DecimalFormat("0.000");
        return Float.valueOf(df.format(a).toString());
    }


}
