package com.example.administrator.comassistant2.simulation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.administrator.comassistant2.R;
import com.example.administrator.comassistant2.simulation.activity.setactivity.SetActivity;
import com.example.administrator.comassistant2.simulation.bean.PageChartDataBean;
import com.example.administrator.comassistant2.simulation.bean.PageFileQueueBean;
import com.example.administrator.comassistant2.simulation.broadcast.BgmReceiver;
import com.example.administrator.comassistant2.simulation.chart.DynamicLineChartManager;
import com.example.administrator.comassistant2.simulation.chart.PageLineChartManager;
import com.example.administrator.comassistant2.simulation.collector.Collector;
import com.example.administrator.comassistant2.simulation.filesave.FileOpster;
import com.example.administrator.comassistant2.simulation.filesave.LocalSeter;
import com.example.administrator.comassistant2.simulation.tool.IConstant;
import com.example.administrator.comassistant2.simulation.tool.LogUtil;
import com.example.administrator.comassistant2.simulation.tool.MyFunc;
import com.example.administrator.comassistant2.simulation.tool.NoDoubleClick;
import com.example.administrator.comassistant2.simulation.tool.binOperate;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.example.administrator.comassistant2.simulation.tool.MyFunc.byteToInt;

/**
 */
public class ComAssistantActivity extends Activity implements View.OnClickListener, View.OnLongClickListener, IConstant {

    Collector ComA;
    boolean D = false;
    public boolean isScopeRunning = true;
    boolean isShowBaseline = false;
    private ToggleButton tbtn_stop;
    private TextView RunStopText, chvText, clvText, ccvText, rhvText, rlvText, rcvText, tcText, recText, conText, warText, tyText, text_moni;
    private Button td_zoom_btn, td_unzoom_btn, btn_volt_zoom, btn_volt_unzoom, btn_Mvleft, btn_Mvright, btn_Resetwar;
    private int timeDiv = -1;
    private Button btn_gxtset, btn_mooni;

    private Button btn_setting, btn_remove, btn_mvup, btn_mvdown, btn_save, tbtn_baseline;
    private float ccv = 0;
    private float rcv = 0;
    public float chv = -1;
    public float rhv = -1;
    public float clv = 100;
    public float rlv = 100;
    int ircv = -1;
    int rccv = -1;
    String tcc;
    String tcy;
    private boolean ccvf = true;
    private boolean rcvf = true;
    public boolean chvf = true;
    public boolean rhvf = true;
    public boolean clvf = true;
    public boolean rlvf = true;
    private boolean tccf = true;
    public boolean wcof = true;
    public boolean recf = true;
    private boolean tcyf = true;
    public int wcounter;
    public int rec = 0;
    private ImageView StateImage, warImage;
    public boolean warFlag = false;
    private boolean warBlink = true;
    private boolean curConnFlag = false;
    private LineChart mChart2;
    private LineChart jjPageChart;
    private BarChart jjBarChart;

    private boolean dataFlag = false;

    private int PageSize = 64 * 1024;//byte
    private int FileMaxLength = 128 * 1024 * 1024;//byte
    private int PageCounter = FileMaxLength / PageSize;//byte
    public List<Integer> recDataDis = new ArrayList<>();
    public List<Byte> recDataBuf = new ArrayList<>();
    private int ReadHdbName = 0, startHdbPos = 0;
    private List<Integer> hdbArray1 = new ArrayList<>();
    private List<Integer> hdbArray2 = new ArrayList<>();

    private int HdbPageId = 0;

    private int Hispageid = -1;
    private int CurPageid = 0;

    private int WriteRdbName = 0, startRdbPos = 0;
    private List<Integer> acquire1 = new ArrayList<>();
    private List<Integer> acquire2 = new ArrayList<>();
    private int RdbPageId = 0;

    public int AddDataCounter = 0;

    public DynamicLineChartManager dynamicLineChartManager2;
    public PageLineChartManager jjPageChartManager;
    public List<Float> list = new ArrayList<>(); //数据集合
    public List<Float> pageList = new ArrayList<>(); //数据集合

    private ProgressDialog pd;
    public AveHandler avgHandler;
    Handler handler2;
    private Dialog dialog;

    private Toast toast;
    Config jjConfig = new Config();
    public BufferManager jjBufferManager;
    BgmReceiver jjReceiver;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.main_activity);
        //		hideBottomUIMenu();

        initViews();
        RunStopToggle(); //RunStop

        new Thread(new FileOperatThread()).start();     //图标操作
        avgHandler.sendEmptyMessage(Event_OpenCollector);  //开始采集
        avgHandler.sendEmptyMessageDelayed(Event_TimingStatisLog, jjConfig.getTimer_Statis()); //定时打印日志


    }


    //总控制器
    public class AveHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (D) Log.d(TAG, "Get handleMessage");
                // TODO Auto-generated method stub
                switch (msg.what) {
                    case Event_DrawPageChart:
                        PageChartDataBean pageChartDataBean = (PageChartDataBean) msg.obj;
                        doDrawPageChartDataBean(pageChartDataBean);
                        break;
                    case 15:
                        long time1 = System.currentTimeMillis();
                        dynamicLineChartManager2.upGrid();
                        //换帧处理
                        int nums = dynamicLineChartManager2.getEntryCounts();
                        if (nums > jjConfig.getUI_Chart_ShowLimit()) {
                            //为了保证性能，需要清空初始化图表
                            dynamicLineChartManager2.refreshV2();
                        }
                        long time2 = System.currentTimeMillis();
//                        LogUtil.ii("时间 ChartDraw " + (time2 - time1) + " avahandle15 " + Thread.currentThread().getId());
                        break;
                    case 1:
                        if (D) Log.d(TAG, "handleMessage: RefreshInforBar");
                        RefreshInforBar();
                        break;
                    case 2:
                        if (D) Log.d(TAG, "handleMessage: RefreshCurve");
                        try {
                            time1 = System.currentTimeMillis();
                            RefreshCurve();
                            time2 = System.currentTimeMillis();
                            LogUtil.ii("时间 DrawChart " + (time2 - time1));
                        } catch (Exception e) {
                            if (D) Log.d(TAG, "handleMessage: RefreshCurve");
                        }
                        break;
                    case 3:
                        if (D) Log.d(TAG, "handleMessage: RefreshInforBar");
                        BtnClickEvent();
                    case Event_OpenCollector:
                        if (D) Log.d(TAG, "handleMessage: OpenComA");
                        OpenComA();
                        break;
                    case 5:
                        if (D) Log.d(TAG, "handleMessage: SaveData");
                        SaveData();
                        break;
                    case 6:
                        if (D) Log.d(TAG, "handleMessage: ReadData");
                        ReadData();
                        break;
                    case 7:
                        if (D) Log.d(TAG, "handleMessage: upGrid");
                        dynamicLineChartManager2.upGrid();
                        break;
                    case Event_TimingStatisLog:
                        jjBufferManager.status();
                        avgHandler.sendEmptyMessageDelayed(Event_TimingStatisLog, jjConfig.getTimer_Statis());
                        break;

                }
                if (D) Log.d(TAG, "handleMessage: ....");
            } catch (Exception e) {
                if (D) Log.d(TAG, "handleMessage: Error");
            }
        }
    }


    private void SaveData() {
        WreteRelDb();
        ccvf = true;
    }

    private void ReadData() {
        ReadHDb();
        rcvf = true;
    }

    private void OpenComA() {
        try {
            if (!ComA.isOpen()) {
                if (D) Log.d(TAG, "onCreate:CommA is Closed ,Now open ComA");
                conText.setText("DIS");
                StateImage.setImageResource(R.drawable.disconnect);
                ComA.open();
                curConnFlag = false;
            } else {
                if (D) Log.d(TAG, "onCreate:CommA Connected");
                conText.setText("CON");
                if (isScopeRunning) ComA.sendTxt("From Host...\r\n");
                else ComA.sendTxt("stop");
                if (curConnFlag == false) {
                    StateImage.setImageResource(R.drawable.connected);
                    curConnFlag = true;
                }
            }
        } catch (Exception e) {
            if (D) Log.d(TAG, "onCreate: open ComA Error");
        }
    }


    private void BtnClickEvent() {
        if (timeDiv < 10) {
            dynamicLineChartManager2.addAxChangTask(timeDiv, isScopeRunning);
            if (dataFlag == false) dynamicLineChartManager2.upGrid();
            tccf = true;
        } else if (timeDiv < 20) {
            dynamicLineChartManager2.addYxChangeTask(timeDiv, isScopeRunning);
            if (dataFlag == false) dynamicLineChartManager2.upGrid();
            tcyf = true;
        } else if (timeDiv < 30) {
            RefreshInforBar();
            disHisdata(timeDiv);
        } else if (timeDiv < 40) {
            dynamicLineChartManager2.OpaxBaseLineTask(timeDiv, isScopeRunning);
            if (dataFlag == false) dynamicLineChartManager2.upGrid();

        } else if (timeDiv < 50) {
            onCreateAlertDialog();
//            dynamicLineChartManager2.OpayMarkLineTask(timeDiv, isScopeRunning);
//            if (dataFlag == false) dynamicLineChartManager2.upGrid();
        } else if (timeDiv < 60) {
            if (isScopeRunning) {
                setDiaMsg("请先停止采集，再进行保存！");
            } else {
//                oldSave();
                pd = ProgressDialog.show(ComAssistantActivity.this, "提示", "数据保存中，请稍后……");
                jjBufferManager.saveToHis(); //保存数据

                dynamicLineChartManager2.index = 0;
                dynamicLineChartManager2.rstCurve(); //更新Chart 表

                jjPageChartManager.index = -10;
                jjPageChartManager.rstCurve(); //更新PageChart
                jjBufferManager.initPageIndex();

                addStart();
                initPageStart();
                handler2.sendEmptyMessage(0);//关闭对话框
            }
        }
        timeDiv = -1;
    }

    private void RefreshCurve() {
        if (isScopeRunning) {
            //读取历史数据,两个缓存各1M,当第一个块结束的时候通知第二个去取数据，交替使用存去。his1,his2,hisActflag,curHis,startPos
            //显示数据
            synchronized (recDataDis) {
                if (recDataDis.size() < 1 || recDataDis == null) {
                    if (D) Log.d(TAG, "RefreshCurve: Rec Zero Data");
                    return;
                }

                int nums = dynamicLineChartManager2.getEntryCounts();
                if (nums > jjConfig.getUI_Chart_ShowLimit()) {
                    //为了保证性能，需要清空初始化图表
                    dynamicLineChartManager2.refreshV2();
                }

                for (int i = 0; i < recDataDis.size(); i++) {
                    ircv = recDataDis.get(i);
                    if (ircv >= 0) {
                        WriteToRdb(ircv);
                        jjBufferManager.writeToTemp(ircv);
                        rcv = cmpRelaData(ircv);

                        AddDataCounter++;

                        rec++;
                        recf = true;

                        if (rlv > rcv) {
                            rlvf = true;
                            rlv = rcv;
                        }
                        if (rhv < rcv) {
                            rhvf = true;
                            rhv = rcv;
                        }

                        rccv = jjBufferManager.readHisData(); //读取历史数据
//                        rccv = readHdbData(); //读取历史数据
                        ccv = cmpRelaData(rccv);

                        if (clv > ccv) {
                            clvf = true;
                            clv = ccv;
                        }
                        if (chv < ccv) {
                            chvf = true;
                            chv = ccv;
                        }

                        list.add(rcv);
                        list.add(ccv);

                        if (list.get(0) > list.get(1)) {
                            wcounter++;
                            warFlag = true;
                            wcof = true;
                        }
                    }
                    dynamicLineChartManager2.addEntry(list, 100);
                    list.clear();
                }

                dynamicLineChartManager2.upGrid();

                recDataDis.clear();
            }

        } else {
            if (D) Log.d(TAG, "run: recDataDis" + recDataDis.size());
            try {
                recDataDis.clear();
            } catch (Exception e) {
                Log.d(TAG, "RefreshCurve: " + e);
            }
        }
    }


    //将采集到的数据插入不同的队列中
    public void WriteToRdb(int ircv) {
        //两个队列轮番插入
        if (WriteRdbName == 2) acquire2.add(ircv);
        else if (WriteRdbName == 1) acquire1.add(ircv);

        //达到一个文件大小进行保存
        startRdbPos++;
        if (startRdbPos * 4 == PageSize || startRdbPos * 4 > PageSize) {
            startRdbPos = 0;
            if (WriteRdbName == 1) WriteRdbName = 2;
            else WriteRdbName = 1;
            avgHandler.sendEmptyMessage(5);
        }

    }

    private void RefreshInforBar() {
        if (tccf) {
            tcc = dynamicLineChartManager2.GetXDisCounter();
            tcText.setText("X:" + (tcc));
            tccf = false;
        }
        if (tcyf) {
            tcy = dynamicLineChartManager2.GetYDisCounter();
            tyText.setText("Y:" + (tcy));
            tccf = false;
        }

        if (rlvf) rlvText.setText("LV:" + String.format("%.3f", rlv) + "V");
        rlvf = false;
        if (rhvf) rhvText.setText("HV:" + String.format("%.3f", rhv) + "V");
        rhvf = false;

        if (clvf) clvText.setText("LV:" + String.format("%.3f", clv) + "V");
        clvf = false;
        if (chvf) chvText.setText("HV:" + String.format("%.3f", chv) + "V");
        chvf = false;

        if (wcounter > 1000000) {
            if (wcof)
                warText.setText("CC:" + String.format("%.2f", (float) (wcounter) / 1000000.0) + "M");
            wcof = false;
        } else {
            if (wcof)
                warText.setText("CC:" + String.format("%.1f", (float) (wcounter) / 1000.0) + "K");
            wcof = false;
        }

        if (rec > 1000000) {
            if (recf)
                recText.setText("REC:" + String.format("%.2f", (float) (rec) / 1000000.0) + "M");
            recf = false;
        } else {
            if (recf) recText.setText("REC:" + String.format("%.1f", (float) (rec) / 1000.0) + "K");
            recf = false;
        }

        if (rcvf) rcvText.setText("PgID:" + String.valueOf(RdbPageId));
        rcvf = false;
        if (ccvf) ccvText.setText("PgID:" + String.valueOf(HdbPageId));
        ccvf = false;

        if (warFlag) {
            if (warBlink)
                warImage.setImageResource(R.drawable.war2);
            warBlink = false;
        } else {
//            Log.d(TAG, "RefreshInforBar: false");
            if (!warBlink)
                warImage.setImageResource(R.drawable.war4);
            warBlink = true;
        }
    }

    private void WreteRelDb() {
        if (RdbPageId == PageCounter) {
            setMsg("数据保存达到上限");
            acquire1.clear();
            acquire2.clear();
            return;
        }
        if (WriteRdbName == 2) {
            int alen = acquire1.size();
            byte[] rd = new byte[alen * 4];
            for (int ii = 0; ii < alen; ii++) {
                int temp = acquire1.get(ii);
                for (int i = 0; i < 4; i++) {
                    rd[i + ii * 4] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
                    temp = temp >> 8;// 向右移8位
                }
            }
            binOperate.writeRdbFile(this.getApplication(), rd, rd.length, RdbPageId);
            acquire1.clear();
        } else if (WriteRdbName == 1) {
            int alen = acquire2.size();
            byte[] rd = new byte[alen * 4];
            for (int ii = 0; ii < alen; ii++) {
                int temp = acquire2.get(ii);
                for (int i = 0; i < 4; i++) {
                    rd[i + ii * 4] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
                    temp = temp >> 8;// 向右移8位
                }
            }
            binOperate.writeRdbFile(this.getApplication(), rd, rd.length, RdbPageId);
            acquire2.clear();
        }
        RdbPageId++;
    }

    private void ReadHDb() {
        if (HdbPageId == PageCounter) {
            for (int i = 0; i < PageSize / 4; i++) {
                if (D) Log.d(TAG, "ReadHDb: MemeryFull...");
                hdbArray1.add(0x00);
            }
            return;
        }
        byte[] rb = binOperate.readHdbFile(this.getApplication(), HdbPageId, PageSize);
        if (ReadHdbName == 2) {
            hdbArray1.clear();
            for (int i = 0; i < rb.length / 4; i++) {
                int data = byteToInt(rb, i * 4);
                hdbArray1.add(data);
            }
        } else if (ReadHdbName == 1) {
            hdbArray2.clear();
            for (int i = 0; i < rb.length / 4; i++) {
                int data = byteToInt(rb, i * 4);
                hdbArray2.add(data);
            }
        }
        HdbPageId++;
    }

    public class FileOperatThread implements Runnable {
        @Override
        public void run() {
            int tc = 20;

            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tc++;
                if (tc % 2 == 0) {
                    System.gc();
                }
                if (tc % 5 == 0) {
                    avgHandler.sendEmptyMessage(1);
                }
                if (tc % 31 == 0) {
//                    Log.d(TAG, "run: Com Check");
//                    avgHandler.sendEmptyMessage(4);
                    tc = 0;
                }
            }
        }
    }


    //create end
    protected void hideBottomUIMenu() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {

            Window _window = getWindow();
            WindowManager.LayoutParams params = _window.getAttributes();
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
            _window.setAttributes(params);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.tbtn_stop)
            RunStopToggle();

        if (NoDoubleClick.isFastClick()) return;
        if (D) Log.d(TAG, "onClick");
        switch (id) {
            // Time scale+ button
            case R.id.btn_gxtset:
                SetActivity.switchToThis(ComAssistantActivity.this);
                break;
            case R.id.btn_mooni:
                do4BtnMoni();
                break;
            case R.id.text_moni:
                do4BtnMoni();
                break;
            case R.id.btn_time_zoom:
                timeDiv = 1;
                break;
            case R.id.btn_rstwar:
                warFlag = false;
                wcounter = 0;
                wcof = true;
                break;
            // Time scale- button
            case R.id.btn_time_unzoom:
                timeDiv = 2;
                break;
            // Voltage scale - button
            case R.id.btn_volt_unzoom:
                timeDiv = 11;
                break;
            // Voltage scale+ button
            case R.id.btn_volt_zoom:
                timeDiv = 12;
                break;
            case R.id.btn_mvright:
                StopToggle();
                toRightPage();
                timeDiv = 21;
                break;
            // Voltage scale+ button
            case R.id.btn_mvleft:
                StopToggle();
                toLeftPage();
                timeDiv = 22;
                break;
            //add make line
            case R.id.btn_add:
                timeDiv = 31;
                break;
            //remove make line
            case R.id.btn_remove:
                timeDiv = 32;
                break;
            //add make line
            case R.id.tbtn_baseline:
                timeDiv = 43;
                isShowBaseline = !isShowBaseline;
                break;
            case R.id.btn_mvup:
                if (!isShowBaseline) break;
                timeDiv = 41;
                break;
            //remove make line
            case R.id.btn_down:
                if (!isShowBaseline) break;
                timeDiv = 42;
                break;
            case R.id.btn_save:
                timeDiv = 51;
                break;
        }
        avgHandler.sendEmptyMessage(3);
    }


    private void toRightPage() {
        try {
            //文件数据
            List<Integer> tempList = jjBufferManager.getRightPageTempDataList();
            List<Integer> hisList = jjBufferManager.getPageHisDataList();

            //展示数据
            List<Float> tempChartList = jjBufferManager.genChartList(tempList);
            List<Float> hisChartList = jjBufferManager.genChartList(hisList);

            if (tempChartList == null || tempChartList.size() <= 0) {
                return;
            }

            //获取当前的可视起始值
            int old_startPox = Double.valueOf(Math.ceil(dynamicLineChartManager2.lineChart.getLowestVisibleX())).intValue();

            int startX = (jjBufferManager.getPageFileIndex() - 1) * jjConfig.getFile_MaxSize() + 1000;

            LogUtil.ii("旧的可视 " + old_startPox + " index " + jjBufferManager.getPageFileIndex() + " 新的起始 " + startX);
            //清空原来的图表
            dynamicLineChartManager2.rstCurve();

            //装载实时数据
            dynamicLineChartManager2.addLineData(tempChartList, 0, startX);
            //装载历史数据
            dynamicLineChartManager2.addLineData(hisChartList, 1, startX);

            //绘制数据
            dynamicLineChartManager2.upGrid(startX);
        } catch (Exception e) {
            LogUtil.ee(e);
        }
    }

    private void toLeftPage() {
        try {
            //文件数据
            List<Integer> tempList = jjBufferManager.getLeftPageTempDataList();
            List<Integer> hisList = jjBufferManager.getPageHisDataList();

            //展示数据
            List<Float> tempChartList = jjBufferManager.genChartList(tempList);
            List<Float> hisChartList = jjBufferManager.genChartList(hisList);

            if (tempChartList == null || tempChartList.size() <= 0) {
                return;
            }

            //获取当前的可视起始值
            int old_startPox = Double.valueOf(Math.ceil(dynamicLineChartManager2.lineChart.getLowestVisibleX())).intValue();

            int startX = (jjBufferManager.getPageFileIndex() - 1) * jjConfig.getFile_MaxSize() + 1000;

            LogUtil.ii("旧的可视 " + old_startPox + " index " + jjBufferManager.getPageFileIndex() + " 新的起始 " + startX);
            //清空原来的图表
            dynamicLineChartManager2.rstCurve();

            //装载实时数据
            dynamicLineChartManager2.addLineData(tempChartList, 0, startX);
            //装载历史数据
            dynamicLineChartManager2.addLineData(hisChartList, 1, startX);

            //绘制数据
            dynamicLineChartManager2.upGrid(old_startPox);


        } catch (Exception e) {
            LogUtil.ee(e);
        }
    }

    private void do4NextPage() {
        //获取当前的可视位置，缓存
        int pos = dynamicLineChartManager2.getEntryCounts();
        int startPox = Double.valueOf(Math.ceil(dynamicLineChartManager2.lineChart.getLowestVisibleX())).intValue();
        int endXpos = Double.valueOf(Math.ceil(dynamicLineChartManager2.lineChart.getLineData().getDataSetByIndex(0).getEntryForIndex(pos - 1).getX())).intValue();
        int tempSize = jjBufferManager.jjFileOpster.jjTempQueueThread.getTempList().size();

        int allSize = tempSize - pos;
        int startX = endXpos - tempSize + 1;
        int endX = startX + allSize - 1;

        int CX = startX;
        LogUtil.ii("表数 " + "之前数据 " + pos);
        List<Integer> tempList = jjBufferManager.jjFileOpster.jjTempQueueThread.getTempList();
        //装载临时数据
        ILineDataSet lineData = dynamicLineChartManager2.lineChart.getLineData().getDataSetByIndex(0);
        ILineDataSet lineData2 = dynamicLineChartManager2.lineChart.getLineData().getDataSetByIndex(1);
        LogUtil.ii("表数 " + "旧第一 " + lineData.getEntryForIndex(0).getX() + " " + lineData.getEntryForIndex(0).getY());
        //清空数据表，然后一次加载当前数据
        try {
            dynamicLineChartManager2.lineChart.getData().clearValues();
            dynamicLineChartManager2.lineChart.invalidate();
            dynamicLineChartManager2.lineChart.setVisibleXRangeMinimum(100);
            dynamicLineChartManager2.lineChart.setVisibleXRangeMaximum(100);
            dynamicLineChartManager2.initLineDataSet();
        } catch (Exception e) {
            Log.e(TAG, "refreshV2: ", e);
        }


        dynamicLineChartManager2.lineData = new LineData(dynamicLineChartManager2.lineDataSets);
        dynamicLineChartManager2.lineChart.setData(dynamicLineChartManager2.lineData);

        lineData = dynamicLineChartManager2.lineChart.getLineData().getDataSetByIndex(0);
        lineData2 = dynamicLineChartManager2.lineChart.getLineData().getDataSetByIndex(1);


        for (int i = 0; i < tempList.size(); i++) {
            int CXX = startX;
            Entry entry = new Entry(CXX, tempList.get(i) / 1000);
            lineData.addEntry(entry);
            Entry entry2 = new Entry(CXX, 0);
            lineData2.addEntry(entry2);
            startX++;
        }


        LogUtil.ii("表数 " + "新第一 " + lineData.getEntryForIndex(0).getX() + " " + lineData.getEntryForIndex(0).getY());
        //更新数据
        dynamicLineChartManager2.upGrid();
        LogUtil.ii("表数 " + pos + " 可视开始位置 " + startPox + " 可视结束位置 " + endXpos + " index " + dynamicLineChartManager2.index + " tempList " + tempSize);
        LogUtil.ii("表数 " + pos + " startX " + CX + " endX " + endX + " 新增数量 " + allSize);
        LogUtil.ii("表数 " + "之后数据 " + dynamicLineChartManager2.getEntryCounts());

    }


    @Override
    public boolean onLongClick(View v) {

        int id = v.getId();
        Log.d(TAG, "onLongClick");
        switch (id) {
            case R.id.btn_mvright:
                timeDiv = 23;
                break;
            // Voltage scale+ button
            case R.id.btn_mvleft:
                timeDiv = 24;
                break;
        }
        return false;
    }

    public void RunStopToggle() {
        if (Hispageid != -1) {
            //Log.d(TAG, "RunStopToggle: Hispageid"+Hispageid);
            Log.d(TAG, "RunStopToggle:RdbPageId " + RdbPageId);
            //回复现场数据
            Hispageid = -1;
            dynamicLineChartManager2.restoreData();
            avgHandler.sendEmptyMessage(7);
        }
        isScopeRunning = !isScopeRunning;
        if (isScopeRunning) {
            try {
                Hispageid = -1;
                dynamicLineChartManager2.clearBd();
                dynamicLineChartManager2.lineChart.setDragEnabled(false);
            } catch (Exception e) {

            }
            RunStopText.setText("RUN");
            RunStopText.setTextColor(Color.GREEN);

            if (!tbtn_stop.isChecked()) {
                tbtn_stop.setChecked(false);
            }

            //分页索引初始化
            jjBufferManager.initPageIndex();
            //横线限制线配置,是否之前绘制过，如果是则恢复
            boolean isContainDraw = dynamicLineChartManager2.drawLimitLine();
            if (isContainDraw) {
                isShowBaseline = true;
            }

        } else {
            try {
                dynamicLineChartManager2.lineChart.setDragEnabled(true);
            } catch (Exception e) {

            }
            RunStopText.setText("STOP");
            RunStopText.setTextColor(Color.RED);
        }
        if (tbtn_stop.isChecked()) {
            tbtn_stop.setChecked(true);
        }
    }

    public void StopToggle() {
        try {
            isScopeRunning = false;
            dynamicLineChartManager2.lineChart.setDragEnabled(true);
            RunStopText.setText("STOP");
            RunStopText.setTextColor(Color.RED);
            tbtn_stop.setChecked(false);
        } catch (Exception e) {
            LogUtil.ee(e);
        }
    }


    @Override
    public void onDestroy() {
        CloseComPort(ComA);
        super.onDestroy();
        if (D) Log.i(TAG, "- ON onDestroy -");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(jjReceiver);
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        CloseComPort(ComA);
//        setContentView(R.layout.main_activity);
//        setControls();
//    }


    @Override
    protected void onPause() {
        super.onPause();
        //暂停采集
        CloseComPort(ComA);
        //初始化展示，正在运行就停止
        if (isScopeRunning) {
            RunStopToggle();
        }
    }

    //----------------------------------------------------
    private void setControls() {
        String appName = getString(R.string.app_name);
        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo("com.example.administrator.comassistant2", PackageManager.GET_CONFIGURATIONS);
            String versionName = pinfo.versionName;
            setTitle(appName + " V" + versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void AddDataToDisList(byte[] dbyte) {
        synchronized (recDataDis) {
            if (!isScopeRunning) {
                if (recDataDis.size() > 1) recDataDis.clear();
                else if (D) Log.d(TAG, "throw data=========> " + recDataDis.size());
                return;
            }

            for (int i = 0; i < (dbyte.length - 5) / 3; i++) {
                double data = (double) (MyFunc.byte3ToInt(dbyte, 3 + i * 3)) * 250000.0 / 131071;
                data = data / 2000;
                recDataDis.add((int) data);
            }
        }
        avgHandler.sendEmptyMessage(2);
    }

    //----------------------------------------------------
    private void CloseComPort(Collector ComPort) {
        if (ComPort != null) {
            ComPort.stopSend();
            ComPort.close();
        }
    }

    private void disHisdata(int td) {
        rcvf = true;
        //no history data , return;
        if (RdbPageId == 0) return;

        //保存现场数据
        if (Hispageid == -1) {
            //保存现场数据,setdescription;
            dynamicLineChartManager2.saveData();
            Hispageid = RdbPageId + 1;
            CurPageid = -1;
        }

        if (td == 22)//前一页
        {
            Hispageid--;
            if (Hispageid < 1) Hispageid = 1;
        } else if (td == 21)//后一页
        {
            Hispageid++;
            if (Hispageid > RdbPageId + 1) Hispageid = RdbPageId + 1;
        }
        Log.d(TAG, "linchart-->RdbPageId:" + RdbPageId);
        Log.d(TAG, "linchart-->Hispageid:" + Hispageid);
        if (Hispageid == 1) {
            if (CurPageid == Hispageid) return;
            CurPageid = Hispageid;
            readHdbRdbDataAndDis(0);
            setMsg("首页");
        } else if (Hispageid == RdbPageId + 1) {
            if (CurPageid == Hispageid) return;
            CurPageid = Hispageid;
            Log.d(TAG, "linchart-->restore data");
            dynamicLineChartManager2.restoreData();
            setMsg("末页");
        } else {
            CurPageid = Hispageid;
            readHdbRdbDataAndDis(Hispageid - 1);
            setMsg("第" + String.valueOf(Hispageid) + "页");
        }
        avgHandler.sendEmptyMessage(7);
    }

    private void readHdbRdbDataAndDis(int hispageid) {
        Log.d(TAG, "readHdbRdbDataAndDis: hisPageid" + hispageid);
        dynamicLineChartManager2.rstCurve();
        List<Integer> hdbArrayH = new ArrayList<>();
        byte[] rb = binOperate.readHdbFile(this.getApplication(), hispageid, PageSize);
        for (int i = 0; i < rb.length / 4; i++) {
            int data = byteToInt(rb, i * 4);
            hdbArrayH.add(data);
        }
        List<Integer> hdbArrayR = new ArrayList<>();
        byte[] rb2 = binOperate.readRdbFile(this.getApplication(), hispageid, PageSize);
        for (int i = 0; i < rb2.length / 4; i++) {
            int data = byteToInt(rb2, i * 4);
            hdbArrayR.add(data);
        }
        List<Float> listd = new ArrayList<>(); //数据集合

        int len = hdbArrayH.size();
        if (hdbArrayH.size() >= hdbArrayR.size()) len = hdbArrayR.size();

        for (int i = 0; i < len; i++) {
            float rr = cmpRelaData(hdbArrayR.get(i));
            float hh = cmpRelaData(hdbArrayH.get(i));
            listd.add(rr);
            listd.add(hh);
            dynamicLineChartManager2.addEntry(listd, 100);
            listd.clear();
        }
    }

    public static float cmpRelaData(int dt) {
        dt = dt / 1000;
        return dt;
    }

    private void setMsg(String msg) {
        if (D) Log.d(TAG, "setMsg: " + msg);
        toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);

//        toast.setText(msg);
//        toast.setGravity(Gravity.BOTTOM, 0, 0);
//        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    private void setDiaMsg(String msg) {
        if (D) Log.d(TAG, "setMsg: " + msg);
        toast.setText(msg);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }


    private void addStart() {
        for (int i = 0; i < 1000; i++) {
            list.add((float) 0);
            list.add((float) 0);
            dynamicLineChartManager2.addEntry(list, 100);
            list.clear();
        }
        dynamicLineChartManager2.upGrid();
    }

    //绘制pageIndex数据
    private void doDrawPageChartDataBean(PageChartDataBean pageChartDataBean) {
        int line_index = pageChartDataBean.getType() == Type_Temp ? 0 : 1;
        int allNum = jjConfig.getFile_MaxSize() / jjConfig.getPage_threshold_num();

        int startX = allNum * (pageChartDataBean.getFileIndex() - 1) + pageChartDataBean.getPageIndex();
        jjPageChartManager.addLineData(pageChartDataBean.getChartData(), line_index, startX, pageChartDataBean);
        int end_x = jjPageChartManager.getLastXPos();
        jjPageChartManager.upGrid(end_x);
    }


    //处理模拟按钮
    private void do4BtnMoni() {
        int time = ComA.getTime_Interval();
        switch (time) {
            case 1000:
                ComA.setTimeInterval(400);
                text_moni.setText("模拟: 0.5K/s");
                btn_mooni.setText("0.5K/s");
                break;
            case 400:
                ComA.setTimeInterval(200);
                text_moni.setText("模拟: 1K/s");
                btn_mooni.setText("1K/s");
                break;
            case 200:
                ComA.setTimeInterval(100);
                text_moni.setText("模拟: 2K/s");
                btn_mooni.setText("2K/s");
                break;
            case 100:
                ComA.setTimeInterval(1000);
                text_moni.setText("模拟: 0.2K/s");
                btn_mooni.setText("0.2K/s");
                break;
        }
    }


    //首次加载时更新信息
    private void initPageStart() {
        for (int i = 0; i < 11; i++) {
            pageList.add((float) 0);
            pageList.add((float) 0);
            jjPageChartManager.addEntry(pageList, 100);
            pageList.clear();
        }
        jjPageChartManager.upGrid();
    }

    public void do4BroadPageChart(PageFileQueueBean in_fileNum) {
        jjBufferManager.do4BroadPageChart(in_fileNum);
    }

    private void initViews() {
        jjReceiver = new BgmReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Broad_DoPageChart);
        LocalBroadcastManager.getInstance(this).registerReceiver(jjReceiver, filter); // 注册服务
        FileOpster.init(this);
        avgHandler = new AveHandler();  //调度器

        jjBufferManager = new BufferManager(this);
        jjBufferManager.init();
        toast = Toast.makeText(getApplicationContext(), "infog", Toast.LENGTH_SHORT);
        ComA = new Collector(ComAssistantActivity.this); //采集类 UI线程


        pd = new ProgressDialog(this);
        pd.setCancelable(false);

        tbtn_stop = (ToggleButton) findViewById(R.id.tbtn_stop);
        tbtn_stop.setOnClickListener(this);

        tbtn_baseline = (Button) findViewById(R.id.tbtn_baseline);
        tbtn_baseline.setOnClickListener(this);
        tbtn_baseline.setOnLongClickListener(this);

        RunStopText = (TextView) findViewById(R.id.text_run_stop);
        ccvText = (TextView) findViewById(R.id.text_cvc);
        chvText = (TextView) findViewById(R.id.text_chv);
        clvText = (TextView) findViewById(R.id.text_clv);
        rcvText = (TextView) findViewById(R.id.text_rcv);
        rhvText = (TextView) findViewById(R.id.text_rhv);
        rlvText = (TextView) findViewById(R.id.text_rlv);
        recText = (TextView) findViewById(R.id.text_rec);
        tcText = (TextView) findViewById(R.id.text_tc);
        conText = (TextView) findViewById(R.id.text_con);
        conText.setText("DIS");
        warText = (TextView) findViewById(R.id.text_wcounter);
        text_moni = (TextView) findViewById(R.id.text_moni);
        wcounter = 0;
        tyText = (TextView) findViewById(R.id.text_ty);
        StateImage = (ImageView) findViewById(R.id.img_connect);
        warImage = (ImageView) findViewById(R.id.waring);
        warImage.setImageResource(R.drawable.war4);
        td_zoom_btn = (Button) findViewById(R.id.btn_time_zoom);
        td_zoom_btn.setOnClickListener(this);
        td_zoom_btn.setOnLongClickListener(this);
        td_unzoom_btn = (Button) findViewById(R.id.btn_time_unzoom);
        td_unzoom_btn.setOnClickListener(this);
        td_unzoom_btn.setOnLongClickListener(this);
        btn_volt_zoom = (Button) findViewById(R.id.btn_volt_zoom);
        btn_volt_zoom.setOnClickListener(this);
        text_moni.setOnClickListener(this);
        btn_volt_zoom.setOnLongClickListener(this);
        btn_volt_unzoom = (Button) findViewById(R.id.btn_volt_unzoom);
        btn_volt_unzoom.setOnClickListener(this);
        btn_volt_unzoom.setOnLongClickListener(this);
        btn_Mvleft = (Button) findViewById(R.id.btn_mvleft);
        btn_Mvleft.setOnClickListener(this);
        btn_Mvleft.setOnLongClickListener(this);
        btn_Mvright = (Button) findViewById(R.id.btn_mvright);
        btn_Mvright.setOnClickListener(this);
        btn_Mvright.setOnLongClickListener(this);
        btn_setting = (Button) findViewById(R.id.btn_add);
        btn_gxtset = (Button) findViewById(R.id.btn_gxtset);
        btn_mooni = (Button) findViewById(R.id.btn_mooni);
        btn_setting.setOnClickListener(this);
        btn_setting.setOnLongClickListener(this);
        btn_remove = (Button) findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(this);
        btn_remove.setOnLongClickListener(this);


        btn_mvup = (Button) findViewById(R.id.btn_mvup);
        btn_mvup.setOnClickListener(this);
        btn_mvup.setOnLongClickListener(this);

        btn_mvdown = (Button) findViewById(R.id.btn_down);
        btn_mvdown.setOnClickListener(this);
        btn_mvdown.setOnLongClickListener(this);

        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        btn_save.setOnLongClickListener(this);

        btn_Resetwar = (Button) findViewById(R.id.btn_rstwar);
        btn_Resetwar.setOnClickListener(this);
        btn_Resetwar.setOnLongClickListener(this);

        mChart2 = (LineChart) findViewById(R.id.dynamic_chart2);
        jjPageChart = (LineChart) findViewById(R.id.pageChart);
        jjBarChart = findViewById(R.id.barChart);
        dynamicLineChartManager2 = new DynamicLineChartManager(mChart2);
        jjPageChartManager = new PageLineChartManager(jjPageChart);
//        jjBarChart = new PageBarChartManager(jjPageChart);

        btn_gxtset.setOnClickListener(this);
        btn_mooni.setOnClickListener(this);
        text_moni.setText("模拟: 1K/s");

        addStart();//起始页面添加1000个空数据
        initPageStart();

        handler2 = new Handler() {
            @Override
            public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
                pd.dismiss();// 关闭ProgressDialog
            }
        };

        jjPageChartManager.lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (isScopeRunning) {
                    setDiaMsg("请先停止采集，再进行查看！");
                } else {
                    do4ClickPageChart(e);
                }
            }

            @Override
            public void onNothingSelected() {
                LogUtil.ii("onNothingSelected ");
            }
        });

    }

    private void do4ClickPageChart(Entry e) {
        PageChartDataBean data = null;
        if (e.getData() == null) {
            setMsg("当前位置无数据");
            return;
        }

        data = (PageChartDataBean) e.getData();

        String info = "当前页信息： 总数[" + jjConfig.getPage_threshold_num() + "], 类型[实时], 文件索引[" + data.getFileIndex() + "], 分页值[" + data.getPageIndex() + "], 数值[" + data.getChartData().get(0) + "].";
        setMsg(info);
        LogUtil.ii("点击事件 " + e.getX() + " " + e.getY() + " filenum " + data.getFileIndex() + " aver " + data.getAverageValue());

        //切换到指定页数据
        toPage(data.getFileIndex(), data.getPageIndex());
    }

    private void toPage(int in_file, int in_page) {
        try {
            jjBufferManager.setPageFileIndex(in_file, in_page);
            //文件数据
            List<Integer> tempList = jjBufferManager.getAssignedPageList();
//            List<Integer> hisList = new ArrayList<>();
            List<Integer> hisList = jjBufferManager.getPageHisDataList();

            //展示数据
            List<Float> tempChartList = jjBufferManager.genChartList(tempList);
            List<Float> hisChartList = jjBufferManager.genChartList(hisList);

            if (tempChartList == null || tempChartList.size() <= 0) {
                return;
            }

            //获取当前的可视起始值
            int old_startPox = Double.valueOf(Math.ceil(dynamicLineChartManager2.lineChart.getLowestVisibleX())).intValue();
//            int startX = (jjBufferManager.getPageFileIndex() - 1) * jjConfig.getFile_MaxSize() + 1000;
            int startX = jjConfig.getFile_MaxSize() * (in_file - 1) + (in_page - 1) * jjConfig.getPage_threshold_num() + 1000;

            LogUtil.ii("旧的可视 " + old_startPox + " index " + jjBufferManager.getPageFileIndex() + " 新的起始 " + startX);
            //清空原来的图表
            dynamicLineChartManager2.rstCurve();

            //装载实时数据
            dynamicLineChartManager2.addLineData(tempChartList, 0, startX);
            //装载历史数据
            dynamicLineChartManager2.addLineData(hisChartList, 1, startX);

            //绘制数据
            dynamicLineChartManager2.upGrid(startX);
        } catch (Exception e) {
            LogUtil.ee(e);
        }
    }


    private void onCreateAlertDialog() {
        // 使用LayoutInflater来加载dialog_setname.xml布局
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View nameView = layoutInflater.inflate(R.layout.alert_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // 使用setView()方法将布局显示到dialog
        alertDialogBuilder.setView(nameView);

        final EditText highInput = (EditText) nameView.findViewById(R.id.high_edit);
        final EditText lowInput = (EditText) nameView.findViewById(R.id.lower_edit);
//        final TextView name = (TextView) findViewById(R.id.changename_textview);


        // 设置Dialog按钮
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 获取edittext的内容,显示到textview
                                do4BaseLine(highInput, lowInput);
//                                setMsg(userInput.getText().toString());
//                                name.setText(userInput.getText());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    //BaseLine 线
    private void do4BaseLine(EditText highInput, EditText lowInput) {
        //先判断数据是否
        float highValue = Float.valueOf(isStrNotEmpty(highInput.getText().toString()) ? highInput.getText().toString() : "0.0");
        float lowValue = Float.valueOf(isStrNotEmpty(lowInput.getText().toString()) ? lowInput.getText().toString() : "0.0");

        if (lowValue > highValue) {
            setMsg("上限值要不小于下限值");
            return;
        }
        LogUtil.ii("highValue " + highValue + " lowValue " + lowValue);
        if (highValue <= 0 && lowValue <= 0) {
            LocalSeter.clearLimitLine();
            dynamicLineChartManager2.removeXAllLimitLines();
        } else {
            LocalSeter.saveLimitLine(highValue, lowValue);
            boolean isContainDraw = dynamicLineChartManager2.drawLimitLine();
            if (isContainDraw) {
                isShowBaseline = true;
            }

            if (!isScopeRunning) {
                dynamicLineChartManager2.upGrid();
            }
        }
    }

    public static boolean isStrNotEmpty(String str) {
        return str != null && !str.equals("");
    }
}