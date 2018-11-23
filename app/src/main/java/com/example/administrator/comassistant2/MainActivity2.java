package com.example.administrator.comassistant2;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity2 extends AppCompatActivity {
    private LineChart lineChart;
    private YAxis leftAxis;
    private YAxis rightAxis;
    private XAxis xAxis;
    public LineData lineData;
    private LineDataSet lineDataSet;
    private int rangeMax = 100;
    Random random = new Random();
    int timeLock = 10;
    String TAG = "GXT";
    int index = 0;
    TextView btn_start;
    TextView btn_txtHis;
    TextView toHis;
    boolean isRunning = true;

    private List<ILineDataSet> lineDataSets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideToolbar();
        setContentView(R.layout.main_activity3);
        lineChart = (LineChart) findViewById(R.id.dynamic_chart2);
        btn_txtHis = findViewById(R.id.txtHis);
        btn_start = findViewById(R.id.start);
        toHis = findViewById(R.id.toHis);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jjhandler.removeMessages(1);
                if (isRunning) {
                    btn_start.setText("s点击运行");
                    isRunning = false;
                } else {
                    btn_start.setText("点击停止");
                    isRunning = true;
                    jjhandler.sendEmptyMessageDelayed(1, 300);
                }

            }
        });

        toHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_start.setText("点击运行");
                isRunning = false;
                jjhandler.removeMessages(1);
                jjhandler.sendEmptyMessageDelayed(4, 300);
            }
        });

        lineChart.setLogEnabled(true);
        initLineChart();
        initLineDataSet();
        setYAxis(10, 0, 10);
        jjhandler.sendEmptyMessageDelayed(1, timeLock);
        jjhandler.sendEmptyMessageDelayed(2, 1000);
    }

    private void toHisBus() {
        Entry startItem = lineData.getDataSetByIndex(0).getEntryForIndex(0);
        int startX = Float.valueOf(startItem.getX()).intValue();
        float visibleStartX = lineChart.getLowestVisibleX();


        Log.d(TAG, "toHisBus: 表起始位置 " + startX + " 表可视位置 " + visibleStartX);
        for (int i = 1; i <= 100; i++) {
            ChartBean bean = new ChartBean();
            bean.x = startX - i;
            bean.y = random.nextInt(3);
            addOnlyEntry(bean);
        }
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
//        lineChart.invalidate();
        lineChart.setVisibleXRangeMinimum(rangeMax);
        lineChart.setVisibleXRangeMaximum(rangeMax);
        moveXTo(visibleStartX);

        showNums();
    }

    private void addData() {
//        for (int i = 0; i < 10000; i++) {
//            addOne();
//            moveXTo(getNums() - rangeMax);
//        }

//        addOne();
//        moveXTo(getNums() - rangeMax);

        if (getNums() < 4000) {
            for (int i = 0; i < 100; i++) {
                addOne();
                moveXTo(getNums() - rangeMax);
            }

        } else {
            for (int i = 0; i < 100; i++) {
                do4Limit();
            }
        }


        showNums();
        jjhandler.sendEmptyMessageDelayed(1, 50);
    }

    private void addOne() {
        ChartBean item = new ChartBean();
        int page = index / 200;
        int pageMode = page % 3;
        int min = 0;
        int max = 3;
        if (pageMode == 0) {
            min = 0;
            max = 3;
        } else if (pageMode == 1) {
            min = 4;
            max = 6;
        } else if (pageMode == 2) {
            min = 7;
            max = 9;
        } else {
            min = 0;
            max = 3;
        }
        item.x = index;
        int flag = random.nextInt(max) % (max - min + 1) + min;
        item.y = flag;
        addEntry(item);
        index++;
    }

    private void do4Limit() {
        //先增加一个，然后再减少一个
        addOne();
        //
        deleteOne();
        //
        moveXTo(index - rangeMax);

    }

    private void deleteOne() {
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
        lineChart.setVisibleXRangeMinimum(rangeMax);
        lineChart.setVisibleXRangeMaximum(rangeMax);
    }

    private void moveXTo(int pos) {
        lineChart.moveViewToX(pos);
    }

    private void moveXTo(float pos) {
        lineChart.moveViewToX(pos);
    }


    private Handler jjhandler = new Handler() {
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case 4:
                        toHisBus();
                        break;
                    case 2:
                        int num = lineData.getDataSetByIndex(0).getEntryCount();
                        String txt = "Chart数量 " + num + " 累计元数 " + index + " Chart可视位置 " + lineChart.getLowestVisibleX();
                        btn_txtHis.setText(txt);
                        jjhandler.sendEmptyMessageDelayed(2, 1000);
                        break;
                    case 1:
                        if (isRunning) {
                            addData();
                        } else {
                            jjhandler.removeMessages(1);
                        }
                        break;

                }
            } catch (Exception e) {
                Log.e(TAG, "handleMessage: ", e);
            }
        }
    };


    //---------------------工具类------------------------------------

    public void addOnlyEntry(ChartBean bean) {
        if (lineDataSets.get(0).getEntryCount() == 0) {
            lineData = new LineData(lineDataSets);
            lineChart.setData(lineData);
        }
        Entry entry = new Entry(bean.x, bean.y);
        lineData.getDataSetByIndex(0).addEntryOrdered(entry);
    }

    public void addEntry(ChartBean bean) {
        addEntry(bean.x, bean.y);
    }

    private int getNums() {
        if (lineData.getDataSetByIndex(0) == null) {
            return 0;
        }
        int num = lineData.getDataSetByIndex(0).getEntryCount();
        return num;
    }

    private void showNums() {
        int num = lineData.getDataSetByIndex(0).getEntryCount();
        Log.d(TAG, "Chart数量 " + num + " 元数据量 " + index + " 可视位置 " + lineChart.getLowestVisibleX());
    }


    public void addEntry(Float x, Float y) {
        try {
            if (lineDataSets.get(0).getEntryCount() == 0) {
                lineData = new LineData(lineDataSets);
                lineChart.setData(lineData);
            }
            Entry entry = new Entry(x, y);
            lineData.getDataSetByIndex(0).addEntry(entry);
            lineData.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
            lineChart.setVisibleXRangeMinimum(rangeMax);
            lineChart.setVisibleXRangeMaximum(rangeMax);

//            Log.d(TAG, "当前数值: " + lineData.getDataSetByIndex(0).getEntryCount());

        } catch (Exception e) {
            Log.d(TAG, "addEntry: Error...." + e.toString());
        }
    }


    private List<String> names = new ArrayList<>(); //折线名字集合
    private List<Integer> colour = new ArrayList<>();//折线颜色集合

    /**
     * 设置Y轴值
     *
     * @param max
     * @param min
     * @param labelCount
     */
    public void setYAxis(float max, float min, int labelCount) {
        if (max < min) {
            return;
        }
        leftAxis.setAxisMaximum(max);
        leftAxis.setAxisMinimum(min);
        rightAxis.setAxisMaximum(max);
        rightAxis.setAxisMinimum(min);
        lineChart.invalidate();
    }

    public void initLineDataSet() {
        names.clear();
        colour.clear();
        //折线名字
        names.add("RealTime");
//        names.add("Standard");
        //折线颜色
//        colour.add(Color.RED);
        colour.add(Color.GREEN);
        lineDataSets.clear();

        for (int i = 0; i < names.size(); i++) {
            lineDataSet = new LineDataSet(null, names.get(i));
            lineDataSet.setColor(colour.get(i));
            lineDataSet.setLineWidth(1.5f);
            lineDataSet.setCircleRadius(1.5f);
            lineDataSet.setColor(colour.get(i));

            lineDataSet.setDrawFilled(false);
            lineDataSet.setCircleColor(colour.get(i));
            lineDataSet.setHighLightColor(colour.get(i));
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet.setValueTextSize(10f);

            lineDataSet.setDrawCircleHole(false);
            lineDataSet.setDrawValues(false);
            lineDataSet.setDrawCircles(false);
            lineDataSets.add(lineDataSet);
        }
        //添加一个空的 LineData
        lineData = new LineData();
        lineChart.setData(lineData);
        lineChart.invalidate();
    }


    public void initLineChart() {
        leftAxis = lineChart.getAxisLeft();
        rightAxis = lineChart.getAxisRight();
        xAxis = lineChart.getXAxis();


        lineChart.setDrawGridBackground(false);
        //显示边界
        lineChart.setDrawBorders(false);
        lineChart.setGridBackgroundColor(Color.WHITE);
        lineChart.setBackgroundColor(Color.BLACK);
        lineChart.setDescription(null);
        // lineChart.setHardwareAccelerationEnabled(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setHighlightPerDragEnabled(false);
        lineChart.setHighlightPerTapEnabled(false);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);

        //折线图例 标签 设置
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(11f);
        //显示位置
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(18);
        legend.setTextColor(Color.WHITE);

        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(0.3f);
        xAxis.setLabelCount(11, true);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(true);
        xAxis.setGridLineWidth(2f);
        xAxis.setTextSize(6f);
//        xAxis.setDrawLabels(false);
        xAxis.enableGridDashedLine(2f, (float) (357.5 / 50.3), 0f);

        leftAxis.setLabelCount(11, true);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setGridLineWidth(0f);
        leftAxis.enableGridDashedLine(3f, (float) (500 / 51.4), -1f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextSize(20f);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setDrawGridLines(false);
        rightAxis.setEnabled(true);
        rightAxis.setDrawLabels(false);

        leftAxis.removeAllLimitLines();
        xAxis.removeAllLimitLines();
        xAxis.setAvoidFirstLastClipping(false);
    }


    private void hideToolbar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏

        setContentView(R.layout.main_activity);
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
}
