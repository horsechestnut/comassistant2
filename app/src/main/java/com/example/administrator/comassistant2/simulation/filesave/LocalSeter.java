package com.example.administrator.comassistant2.simulation.filesave;

import com.example.administrator.comassistant2.simulation.bean.LimitLineBean;
import com.example.administrator.comassistant2.simulation.bean.XLimitLineBean;
import com.example.administrator.comassistant2.simulation.bean.XLimitLineListBean;
import com.example.administrator.comassistant2.simulation.tool.LocalInfoUtil;
import com.example.administrator.comassistant2.simulation.tool.LogUtil;
import com.github.mikephil.charting.components.LimitLine;

public class LocalSeter {
    public static void saveLimitLine(float high_info,float low_info) {
        LimitLineBean item = new LimitLineBean();
        item.setHighValue(high_info);
        item.setLabel(String.valueOf(high_info));
        item.setLowValue(low_info);
        LocalInfoUtil.setLimitLine(item);
    }

    public static void clearLimitLine() {
        LocalInfoUtil.setLimitLine(null);
    }

    public static LimitLineBean getLimitLine() {
        return LocalInfoUtil.getLimitLine();
    }

    public static void addOneXLimitLineBean(XLimitLineBean xbean) {
        XLimitLineListBean localLine = LocalInfoUtil.getXLimitLine();
        if (localLine == null) {
            localLine = new XLimitLineListBean();
        }

        localLine.getLists().put(xbean.getLabel(), xbean);
        LogUtil.ii("添加MARK: " + xbean.getLabel() + " : " + xbean.getAbsValue());
        LocalInfoUtil.setXLimitLine(localLine);
    }

    public static void clearXLimitLines() {
        LogUtil.ii("清空MARK ");
        LocalInfoUtil.setXLimitLine(null);
    }

    public static XLimitLineListBean getXLimitLines() {
        return  LocalInfoUtil.getXLimitLine();
    }

    public static void removeOneXLimitLine(LimitLine xxll) {
        XLimitLineListBean localLine = LocalInfoUtil.getXLimitLine();
        if (localLine == null || localLine.getLists() == null) {
            return;
        }

        if (localLine.getLists().containsKey(xxll.getLabel())) {
            LogUtil.ii("删除MARK: " + xxll.getLabel() + " : " + localLine.getLists().get(xxll.getLabel()).getAbsValue());
            localLine.getLists().remove(xxll.getLabel());
        }

        LocalInfoUtil.setXLimitLine(localLine);
    }
}
