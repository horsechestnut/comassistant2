package com.example.administrator.comassistant2.simulation.filesave;

import android.content.Context;
import android.text.TextUtils;

import com.example.administrator.comassistant2.simulation.Config;
import com.example.administrator.comassistant2.simulation.tool.FileUtil;
import com.example.administrator.comassistant2.simulation.tool.LogUtil;

import java.io.File;
import java.util.List;

//文件读写器
public class FileOpster {
    public static String sdPath;
    public static String fileSeparator;
    public static String projectName = "com.example.comassistant2";//项目制定文件目录
    public static String sdRootPath; // 外置存储根目录 ::   /storage/emulated/0/com.example.comassistant2/
    public static String tempPath; //实时采集数据临时存放点
    public static String hisPath; //历史数据存放点

    static {
        // 初始化文件目录
        fileSeparator = System.getProperty("file.separator");
        //sd卡目录
        sdPath = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        //wyt根目录
        sdRootPath = sdPath + fileSeparator + projectName + fileSeparator;
        tempPath = sdRootPath + ".temp" + fileSeparator;
        hisPath = sdRootPath + "his" + fileSeparator;
    }

    public static void init(Context context) {
        //初始化数据库，数据，图片文件夹,日志文件夹
        checkAndCreatePrivateDirectory();

        //初始化缓存文件夹
        checkAndCreateSdDirectory();
    }

    public TempFileQueueThread jjTempQueueThread;
    public HisFileQueueThread jjHisQueueThread;


    Config jjConfig;

    public FileOpster() {
        jjConfig = new Config();
        jjTempQueueThread = new TempFileQueueThread(); //队列启动
        jjHisQueueThread = new HisFileQueueThread();

    }

    public void init() {
        jjHisQueueThread.init();
        jjTempQueueThread.start();
        jjHisQueueThread.start();
    }

    //=------------------------------------------------------
    //临时文件写操作
    public void addTempWriteQueue(int in_num) {
        jjTempQueueThread.addQueue(in_num);
    }


    /**
     * 检查并创建私有文件夹
     */
    private static void checkAndCreatePrivateDirectory() {
        // 创建私有文件夹
        String[] path = {sdRootPath, hisPath, tempPath};
        //这里创建目录要先创建sd/layou/  再创建sd/layou/image/  否则创建失败
        for (String x : path) {
            File fi = new File(x);
            if (!fi.exists()) {
                fi.mkdir();
            }
        }
    }

    /**
     * 每次启动需要删除的旧文件夹
     */
    private static void checkAndCreateSdDirectory() {

        if ((!TextUtils.isEmpty(sdPath) && new File(sdPath).canRead())) {
            String[] path = {tempPath};
//            String[] path = {tempPath, hisPath};
            for (String x : path) {
                File fi = new File(x);
                if (!fi.exists()) {
                    fi.mkdir();
                } else {
                    FileUtil.delDis(x);
                    fi.mkdir();
                }
            }
        }
    }


    public int saveToHis() {
        int rlt = 0;
        //首先查看当前队列中是否还有数据
        List<Integer> tempList = jjTempQueueThread.getTempList();
        LogUtil.ii("当前临时文件的数量是 " + tempList.size() + " " + jjTempQueueThread.queueSize() + " " + tempList.size());
        jjTempQueueThread.doWriteIt();

        //将数据保存到His文件中
        List<File> list = FileUtil.getSubFilesByModfiyTime(tempPath);
        if (list != null) {
            //清空HIS文件夹下原来的数据
            clearDis(hisPath);
            long time1 = System.currentTimeMillis();
            for (File item : list
                    ) {
                //Rdb3.bin
                String srcName = item.getName();
                int endPos = srcName.indexOf(".");
                int num = Integer.valueOf(srcName.substring(3, endPos));
                String hisName = "Hdb" + num + ".bin";
                FileUtil.copyFile(item, new File(hisPath + hisName));
            }
            long time2 = System.currentTimeMillis();
            LogUtil.ii("保存时间 " + (time2 - time1));

            //重新初始化历史数据
            jjHisQueueThread.init();
            //清空临时文件夹
            jjTempQueueThread.init();

            rlt = list.size();

        }
        return rlt;
    }

    public static void clearDis(String in_dis) {
        if ((!TextUtils.isEmpty(sdPath) && new File(sdPath).canRead())) {
            File fi = new File(in_dis);
            if (!fi.exists()) {
                fi.mkdir();
            } else {
                FileUtil.delDis(in_dis);
                fi.mkdir();
            }
        }
    }


    //读取历史数据
    public int readHisData() {
        //先判断是否有历史数据，如果没有，则为0
        return jjHisQueueThread.getOne();
    }

    public int getTempQueueSize() {
        return jjTempQueueThread.queueSize();
    }

    public int getHisQueueNum() {
        return jjHisQueueThread.queueSize();
    }
}
