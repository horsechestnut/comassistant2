package com.example.administrator.comassistant2.simulation.chart;

import android.graphics.Color;
import android.util.Log;

import com.example.administrator.comassistant2.simulation.Config;
import com.example.administrator.comassistant2.simulation.bean.LimitLineBean;
import com.example.administrator.comassistant2.simulation.bean.PageChartDataBean;
import com.example.administrator.comassistant2.simulation.bean.XLimitLineBean;
import com.example.administrator.comassistant2.simulation.bean.XLimitLineListBean;
import com.example.administrator.comassistant2.simulation.filesave.LocalSeter;
import com.example.administrator.comassistant2.simulation.tool.LogUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.MPPointD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.content.ContentValues.TAG;

/**
 * Created by dongdaxing on 2018/7/29.
 */

public class PageBarChartManager {
    public LineChart lineChart;
    private YAxis leftAxis;
    private YAxis rightAxis;
    private XAxis xAxis;
    public LineData lineData;
    private LineDataSet lineDataSet;
    public List<ILineDataSet> lineDataSets = new ArrayList<>();
    public static int index = -10;


    private boolean D = false;
    private int LimitLinuNum = 1;
    private LimitLine baseLine = null;

    private List<String> names = new ArrayList<>(); //折线名字集合
    private List<Integer> colour = new ArrayList<>();//折线颜色集合
    private List<Float> bd1 = new ArrayList<>();//折线颜色集合
    private List<Float> bd2 = new ArrayList<>();//折线颜色集合

    private int axTaskid = 0;
    private int yxTaskid = 0;
    private int blTaskid = 0;
    private int mkTaskid = 0;
    private int yScale = 3;
    private int disCounter = 3;
    private int xspace[] = {10, 20, 50, 10, 200, 500};
    private int yspace[] = {1, 2, 5, 10, 20, 50, 100, 200, 500};
    private String xsName[] = {"1mS/div", "2mS/div", "5mS/div", "10mS/div", "20mS/div", "50mS/div"};
    private String ysName[] = {"1mV/div", "2mV/div", "5mV/div", "10mV/div", "20mV/div", "50mV/div", "0.1V/div", "0.2V/div", "0.5V/div"};
    private int xspaceMax = 7;
    private int yspaceMax = 7;
    Map<String, XLimitLineBean> needDrawXLimitMap = new HashMap<>();

    //多条曲线
    public PageBarChartManager(LineChart mLineChart) {
        this.lineChart = mLineChart;
        leftAxis = lineChart.getAxisLeft();
        rightAxis = lineChart.getAxisRight();
        xAxis = lineChart.getXAxis();
        xspaceMax = xspace.length;
        yspaceMax = yspace.length;
        initLineChart();
        initLineDataSet();
//        setYAxis(yspace[yScale], 0, 10);
        setYAxis(3.6f, 3.2f, 5);
        mLineChart.setLogEnabled(false);
    }

    /**
     * 初始化LineChar
     */
    public void initLineChart() {
        lineChart.setDrawGridBackground(false);
        //显示边界
        lineChart.setDrawBorders(false);
        lineChart.setGridBackgroundColor(Color.WHITE);
        lineChart.setBackgroundColor(Color.BLACK);
        lineChart.setDescription(null);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setHighlightPerDragEnabled(false);
        lineChart.setHighlightPerTapEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);


        //折线图例 标签 设置
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(9f);
        //显示位置
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(10);
        legend.setTextColor(Color.WHITE);

        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(0.3f);
        xAxis.setLabelCount(11, true);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(true);
        xAxis.setGridLineWidth(2f);
        xAxis.setTextSize(5f);
        xAxis.setDrawLabels(true);
        xAxis.enableGridDashedLine(2f, (float) (357.5 / 50.3), 0f);

        leftAxis.setLabelCount(5, true);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setGridLineWidth(0f);
        leftAxis.enableGridDashedLine(3f, (float) (500 / 51.4), -1f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextSize(10f);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setDrawGridLines(false);
        rightAxis.setEnabled(true);
        rightAxis.setDrawLabels(false);

        leftAxis.removeAllLimitLines();
        xAxis.removeAllLimitLines();
        xAxis.setAvoidFirstLastClipping(false);

        // 设置是否可以触摸
        lineChart.setTouchEnabled(true);
        // 是否可以拖拽
        lineChart.setDragEnabled(true);
        // 是否可以缩放
        lineChart.setScaleEnabled(false);

    }

    /**
     * 初始化折线（多条线）
     */
    public void initLineDataSet() {
        names.clear();
        colour.clear();
        //折线名字
        names.add("RealPage");
        names.add("HisPage");
        //折线颜色
        colour.add(Color.RED);
        colour.add(Color.GREEN);
        lineDataSets.clear();

        for (int i = 0; i < names.size(); i++) {
            lineDataSet = new LineDataSet(null, names.get(i));
            lineDataSet.setColor(colour.get(i));
//            lineDataSet.setbWidth(1.5f);
//            lineDataSet.setCircleRadius(1.5f);
            lineDataSet.setColor(colour.get(i));

//            lineDataSet.setDrawFilled(false);
//            lineDataSet.setCircleColor(colour.get(i));
            lineDataSet.setHighLightColor(colour.get(i));
//            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet.setValueTextSize(10f);

//            lineDataSet.setDrawCircleHole(false);
            if (i == 0) {
                lineDataSet.setDrawValues(false);
                lineDataSet.setHighlightEnabled(true);
                lineDataSet.setValueTextColor(Color.WHITE);
            } else {
                lineDataSet.setDrawValues(false);
                lineDataSet.setHighlightEnabled(false);
            }


//            lineDataSet.setDrawCircles(false);
            lineDataSets.add(lineDataSet);
        }
        //添加一个空的 LineData
        lineData = new LineData();
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    public void addAxChangTask(int counter, boolean scoprun) {
        axTaskid = counter;
        if (!scoprun) {
            axTaskid = 0;
            axisDisCounterChange(counter);
            this.upGrid();
        }
    }

    public void axisDisCounterChange(int counter) {
        if (counter == 1)//time ++
        {
            //修改discounter 防止触摸操作缩放
            if (disCounter == xspaceMax - 1) return;
            disCounter++;

        } else if (counter == 2)//timer --
        {
            if (disCounter == 0) return;
            disCounter--;
        }
    }

    public void addYxChangeTask(int counter, boolean scoprun) {
        if (scoprun) {
            yxTaskid = counter;
        } else {
            Log.d(TAG, "addYxChangeTask: do y change");
            yxTaskid = 0;
            YaxisDisCounterChange(counter);
            this.upGrid();
        }
    }

    public void YaxisDisCounterChange(int counter) {
        if (counter == 12)//放大
        {
            if (yScale == yspaceMax - 1) return;
            yScale++;
            lineChart.setVisibleYRange(0, yspace[yScale], leftAxis.getAxisDependency());
        } else if (counter == 11)//缩小
        {
            if (yScale == 0) return;
            yScale--;
            lineChart.setVisibleYRange(0, yspace[yScale], leftAxis.getAxisDependency());

        }
    }

    public void OpayMarkLineTask(int timeDiv, boolean isScopeRunning) {
        if (isScopeRunning) {
            mkTaskid = timeDiv;
        } else {
            OpayMarkLine(timeDiv);
            mkTaskid = 0;
        }
    }

    //设置横向限制线
    public void OpayMarkLine(int timeDiv) {
        float x = lineChart.getCenter().x;
        float y = lineChart.getCenter().y;
        MPPointD po = lineChart.getValuesByTouchPoint(x, y, leftAxis.getAxisDependency());
        x = (float) (po.x);
        y = (float) (po.y);

        if (timeDiv == 43) {
            // Log.d(TAG, "OpayMarkLine: "+timeDiv);
            if (baseLine == null) {
                //设置markline
                leftAxis.removeAllLimitLines();
                //Log.d(TAG, "OpayMarkLine: add");
                baseLine = new LimitLine(y, String.valueOf(y));
                baseLine.setTextColor(Color.WHITE);
                baseLine.setLineColor(Color.YELLOW);
                baseLine.setTextSize(20f);
                leftAxis.addLimitLine(baseLine);
                baseLine.isEnabled();
//                LocalSeter.saveLimitLine(y);
            } else {
                leftAxis.removeAllLimitLines();
                LocalSeter.clearLimitLine();
                baseLine = null;
            }
        } else if (timeDiv == 42)//down
        {
            if (baseLine == null) {
                return;
            } else {
                float yy = baseLine.getLimit();
                leftAxis.removeLimitLine(baseLine);
                baseLine = null;
                baseLine = new LimitLine((float) (yy / 1.05), String.valueOf((float) (yy / 1.05)));
                baseLine.setTextColor(Color.WHITE);
                baseLine.setLineColor(Color.YELLOW);
                baseLine.setTextSize(20f);
                leftAxis.addLimitLine(baseLine);
//                LocalSeter.saveLimitLine((float) (yy / 1.05));

            }
        } else if (timeDiv == 41)//up
        {
            if (baseLine == null) {
                return;
            } else {
                float yy = baseLine.getLimit();
                leftAxis.removeLimitLine(baseLine);
                baseLine = null;
                baseLine = new LimitLine((float) (yy * 1.05), String.valueOf((float) (yy * 1.05)));
                baseLine.setTextColor(Color.WHITE);
                baseLine.setLineColor(Color.YELLOW);
                baseLine.setTextSize(20f);
                leftAxis.addLimitLine(baseLine);
//                LocalSeter.saveLimitLine((float) (yy * 1.05));
            }
        }
        lineChart.invalidate();
    }

    public void OpaxBaseLineTask(int baselinAct, boolean isScopeRunning) {
        if (isScopeRunning) {
            blTaskid = baselinAct;
        } else {
            OpaxBaseLine(baselinAct);
            blTaskid = 0;
        }
    }

    public void OpaxBaseLine(int baselinAct) {
        float x1 = lineChart.getLowestVisibleX();
        // float x2 = lineChart.getHighestVisibleX();
        float xrange = lineChart.getVisibleXRange();
        float startpos = lineChart.getLineData().getDataSetByIndex(0).getEntryForIndex(0).getX();
        int end_X = Double.valueOf(Math.ceil(x1 + xrange / 2)).intValue();
        int start_X = Double.valueOf(startpos).intValue();
        LogUtil.ii("第一个X位置 : " + start_X + " : " + end_X + " : " + (end_X - start_X));

        if (baselinAct == 31) {
            String label = "R" + String.valueOf(LimitLinuNum);
            int abs_value = (end_X - start_X);

            LimitLine xLimitLine = new LimitLine(end_X, "R" + String.valueOf(LimitLinuNum++));
            xLimitLine.setLineColor(Color.BLUE);
            xLimitLine.setTextColor(Color.BLUE);
            xLimitLine.setLineWidth(2.0f);
            xAxis.addLimitLine(xLimitLine);
            xAxis.setDrawLimitLinesBehindData(true);

            //添加一个本地记录
            XLimitLineBean xbean = new XLimitLineBean();
            xbean.setAbsValue(abs_value);
            xbean.setLabel(label);

            LocalSeter.addOneXLimitLineBean(xbean);

        } else if (baselinAct == 32) {
            DelBaseLine();
        }
        lineChart.invalidate();

    }

    public void DelBaseLine() {
        try {

            float x1 = lineChart.getLowestVisibleX();
            float x2 = lineChart.getHighestVisibleX();
            List<LimitLine> xl = xAxis.getLimitLines();
            List<LimitLine> inview = new ArrayList<>();
            for (int i = 0; i < xl.size(); i++) {
                if (xl == null || xl.size() < 1) return;
                LimitLine xxll = xl.get(i);
                float xpos = xxll.getLimit();
                if (x1 <= xpos && xpos <= x2) {
                    inview.add(xxll);
                }
            }
            if (inview.size() > 0) {
                LimitLine xxll = inview.get(0);
                for (int j = 1; j < inview.size(); j++) {
                    if (xxll.getLimit() > inview.get(j).getLimit()) {
                        xxll = inview.get(j);
                    }
                }
                xAxis.removeLimitLine(xxll);
                //删除一条本地数据
                LocalSeter.removeOneXLimitLine(xxll);
            }
        } catch (Exception e) {
            Log.d(TAG, "DelBaseLine: Error");
        }
    }


    public void addEntry(List<Float> numbers, int upMax) {
        try {
            for (int i = 0; i < numbers.size(); i++) {
                if (lineDataSets == null || lineDataSets.size() <= 0 || lineDataSets.get(i).getEntryCount() == 0) {
                    lineData = new LineData(lineDataSets);
                    lineChart.setData(lineData);
                }
                Entry entry = new Entry(index, numbers.get(i));
//                Entry entry = new Entry(lineDataSet.getEntryCount(), numbers.get(i));
                lineData.getDataSetByIndex(i).addEntry(entry);
            }
            index++;
        } catch (Exception e) {
            Log.d(TAG, "addEntry: Error...." + e.toString());
        }
    }

    public void upGrid() {
        if (mkTaskid != 0) {
            if (D) Log.d(TAG, "upGrid: mkTask");
            OpayMarkLine(mkTaskid);
            mkTaskid = 0;
        } else if (blTaskid != 0) {
            if (D) Log.d(TAG, "upGrid: blTaskid");
            OpaxBaseLine(blTaskid);
            blTaskid = 0;
        } else if (axTaskid != 0) {
            if (D) Log.d(TAG, "upGrid: axTaskid");
            axisDisCounterChange(axTaskid);
            axTaskid = 0;
        } else if (yxTaskid != 0) {
            if (D) Log.d(TAG, "upGrid: yxTaskid");
            YaxisDisCounterChange(yxTaskid);
            yxTaskid = 0;
        } else {
            //  Log.d(TAG, "upGrid: disCounter" + xspace[disCounter]);
            try {
                lineData.notifyDataChanged();
                lineChart.notifyDataSetChanged();
                lineChart.setVisibleXRangeMinimum(xspace[disCounter]);
                lineChart.setVisibleXRangeMaximum(xspace[disCounter]);
//                lineChart.moveViewToX(lineData.getDataSetByIndex(0).getEntryCount() - xspace[disCounter]);
                lineChart.moveViewToX(index - xspace[disCounter]);

                //自动添加
//                autoAddXLimitLines();
            } catch (Exception e) {
                if (D) Log.d(TAG, "upGrid: Error" + e.toString());
                if (D) Log.d(TAG, "upGrid: Error" + xspace[disCounter]);
            }
        }
    }


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

    public String GetXDisCounter() {
        return xsName[disCounter];
    }

    public String GetYDisCounter() {
        return ysName[yScale];
    }

    //清空图表数据
    public void rstCurve() {
        try {
            refreshXAxisLimitLines();
            lineChart.getData().clearValues();
            initLineDataSet();
        } catch (Exception e) {
            Log.d(TAG, "linchart-->: Rst data error");
        }
    }

    public void saveData() {

        bd1.clear();
        bd2.clear();

        for (int i = 0; i < lineChart.getLineData().getDataSets().get(0).getEntryCount(); i++) {
            Float y = lineChart.getLineData().getDataSets().get(0).getEntryForIndex(i).getY();
            bd1.add(y);
        }
        for (int i = 0; i < lineChart.getLineData().getDataSets().get(1).getEntryCount(); i++) {
            Float y = lineChart.getLineData().getDataSets().get(1).getEntryForIndex(i).getY();
            bd2.add(y);
        }
        Log.d(TAG, "saveData: " + bd1.size());
        Log.d(TAG, "saveData: " + bd2.size());

    }

    public void clearBd() {
        bd1.clear();
        bd2.clear();
    }

    public void restoreData() {
        Log.d(TAG, "restoreData: " + bd1.size());
        Log.d(TAG, "restoreData: " + bd2.size());
        rstCurve();
        List<Float> listd = new ArrayList<>(); //数据集合
        try {
            for (int i = 0; i < bd1.size(); i++) {
                listd.add(bd1.get(i));
                listd.add(bd2.get(i));
                //  Log.d(TAG, "restoreData: "+listd.toString());
                this.addEntry(listd, 100);
                listd.clear();
            }
        } catch (Exception e) {
            Log.d(TAG, "linchart-->restoreData: Add data Error");
        }
    }


    //------------------------------------------------------
    Config jjConfig = new Config();

    public int getEntryCounts() {
        if (lineData == null || lineData.getDataSetByIndex(0) == null || lineData.getDataSetByIndex(0).getEntryCount() == 0) {
            return 0;
        }
        return lineData.getDataSetByIndex(0).getEntryCount();
    }


    //分页显示时清空图表信息
    public void refreshV2() {
        int pos = Double.valueOf(Math.ceil(lineChart.getLowestVisibleX())).intValue();
        List<Float> list1 = new ArrayList<>();
        List<Float> list2 = new ArrayList<>();
        list1.clear();
        list2.clear();

        int endXpos = Double.valueOf(Math.ceil(lineChart.getLineData().getDataSetByIndex(0).getEntryForIndex(getEntryCounts() - 1).getX())).intValue();
        int length = endXpos - pos;
        int startIndexPos = lineChart.getLineData().getDataSetByIndex(0).getEntryCount() - length - 1;

        for (int i = startIndexPos; i < lineChart.getLineData().getDataSetByIndex(0).getEntryCount(); i++) {
            Entry item = lineChart.getLineData().getDataSetByIndex(0).getEntryForIndex(i);
            list1.add(item.getY());
            Entry item2 = lineChart.getLineData().getDataSetByIndex(1).getEntryForIndex(i);
            list2.add(item2.getY());
        }

        int beforeNum = getEntryCounts();


        try {
            index = pos;
            refreshXAxisLimitLines();
            lineChart.getData().clearValues();
            lineChart.invalidate();
            lineChart.setVisibleXRangeMinimum(xspace[disCounter]);
            lineChart.setVisibleXRangeMaximum(xspace[disCounter]);
            initLineDataSet();
        } catch (Exception e) {
            Log.e(TAG, "refreshV2: ", e);
        }

//        //引入数据
        List<Float> list = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++) {
            list.clear();
            list.add(list1.get(i));
            list.add(list2.get(i));
            addEntry(list, 100);
        }

        upGrid();

//        Log.d(TAG, "gxtframe:  " + pos + " " + endXpos + " " + index + " " + beforeNum + " " + getEntryCounts());

    }


    //装载list数据
    public void addLineData(List<Float> line_list, int line_index, int startX, PageChartDataBean pageChartDataBean) {
        if (line_list == null || line_list.size() <= 0) {
            return;
        }

        if (lineDataSets == null || lineDataSets.size() <= 0 || lineDataSets.get(line_index).getEntryCount() == 0) {
            lineData = new LineData(lineDataSets);
            lineChart.setData(lineData);
        }

        for (int i = 0; i < line_list.size(); i++) {
            Entry entry = new Entry(startX, line_list.get(i));
            //设置下相关的存储数据
            entry.setData(pageChartDataBean);
            lineData.getDataSetByIndex(line_index).addEntry(entry);
            startX++;
        }
    }

    public void upGrid(int startX) {
        try {
            lineData.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMinimum(xspace[disCounter]);
            lineChart.setVisibleXRangeMaximum(xspace[disCounter]);
            lineChart.moveViewToX(startX - xspace[disCounter]);
        } catch (Exception e) {
            if (D) Log.d(TAG, "upGrid: Error" + e.toString());
            if (D) Log.d(TAG, "upGrid: Error" + xspace[disCounter]);
        }
    }


    //从持久化文件中获取相关配置，并描绘之,如果存在且绘制成功，则返回true
    public boolean drawLimitLine() {
        LimitLineBean item = LocalSeter.getLimitLine();
        if (item != null) {
            leftAxis.removeLimitLine(baseLine);
//            baseLine = new LimitLine(item.getValue(), item.getLabel());
            baseLine.setTextColor(Color.WHITE);
            baseLine.setLineColor(Color.YELLOW);
            baseLine.setTextSize(20f);
            leftAxis.addLimitLine(baseLine);
//            LogUtil.ii("横向限制线: 存在Local数据 : " + item.getValue());
            return true;
        } else {
            LogUtil.ii("横向限制线: 没有Local数据");
            return false;
        }
    }

    private void autoAddXLimitLines() {
        //获取当前分页的最后一个点
        int nums = lineChart.getLineData().getDataSetByIndex(0).getEntryCount();

        if (nums <= 0 || needDrawXLimitMap == null || needDrawXLimitMap.size() <= 0) {
            return;
        }

        //获取当前分页的起始点
        float startpos = lineChart.getLineData().getDataSetByIndex(0).getEntryForIndex(0).getX();
        int start_X = Double.valueOf(startpos).intValue();
        float endpos = lineChart.getLineData().getDataSetByIndex(0).getEntryForIndex((nums - 1)).getX();
        int end_X = Double.valueOf(endpos).intValue();

        int abs_value = end_X - start_X;

        Set<Map.Entry<String, XLimitLineBean>> set = needDrawXLimitMap.entrySet();
        List<String> keys_needel = new ArrayList<>();
        for (Iterator<Map.Entry<String, XLimitLineBean>> it = set.iterator(); it.hasNext(); ) {
            Map.Entry<String, XLimitLineBean> entry = it.next();
            XLimitLineBean item = entry.getValue();
            if (item.getAbsValue() < abs_value) {
                //需要绘制一个abs线
                int vvx = start_X + item.getAbsValue();
                LimitLine xLimitLine = new LimitLine(vvx, item.getLabel());
                xLimitLine.setLineColor(Color.BLUE);
                xLimitLine.setTextColor(Color.BLUE);
                xLimitLine.setLineWidth(2.0f);
                xAxis.addLimitLine(xLimitLine);
                xAxis.setDrawLimitLinesBehindData(true);
                keys_needel.add(item.getLabel());
            }
        }

        for (String item : keys_needel
                ) {
            XLimitLineBean ccc = needDrawXLimitMap.get(item);
            LogUtil.ii("需要绘制MARK " + ccc.getLabel() + " " + ccc.getAbsValue() + " " + (ccc.getAbsValue() + start_X));
            needDrawXLimitMap.remove(item);
        }
    }


    private void refreshXAxisLimitLines() {
        xAxis.getLimitLines().clear(); //清空图表中的XLimitLines
        needDrawXLimitMap = new HashMap<>();
        XLimitLineListBean localInfo = LocalSeter.getXLimitLines();
        if (localInfo != null && localInfo.getLists() != null && localInfo.getLists().size() > 0) {
            needDrawXLimitMap = localInfo.getLists();
            LogUtil.ii("重新初始化MARK");
        }
    }

    //获取最后一个点的X坐标
    public int getLastXPos() {
        if (lineChart.getLineData().getDataSetByIndex(0) == null) {
            return 0;
        }

        int nums = lineChart.getLineData().getDataSetByIndex(0).getEntryCount();
        if (nums <= 0) {
            return 0;
        }

        Entry lastEntry = lineChart.getLineData().getDataSetByIndex(0).getEntryForIndex(nums - 1);
        int endXpos = Double.valueOf(Math.ceil(lastEntry.getX())).intValue();

        return endXpos;

    }
//------------------------------------------------------
}