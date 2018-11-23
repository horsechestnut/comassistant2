package com.example.administrator.comassistant2.simulation.tool;

import com.example.administrator.comassistant2.ApplicationController;
import com.example.administrator.comassistant2.simulation.bean.LimitLineBean;
import com.example.administrator.comassistant2.simulation.bean.XLimitLineListBean;

//持久化文件
public class LocalInfoUtil {
    final static String Key_LimitLine = "LimitLineBean";
    final static String Key_XLimitLine = "Key_XLimitLine";

    public static LimitLineBean getLimitLine() {
        try {
            SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(
                    ApplicationController.getInstance().getApplicationContext(),
                    Key_LimitLine);

            LimitLineBean loginmodel = sharedPreferencesUtils.getObject(Key_LimitLine,
                    LimitLineBean.class);
            return loginmodel;
        } catch (Exception e) {
            LogUtil.ee(e);
        }
        return null;
    }

    public static boolean setLimitLine(LimitLineBean in_info) {
        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(
                ApplicationController.getInstance().getApplicationContext(),
                Key_LimitLine);
        sharedPreferencesUtils.setObject(Key_LimitLine, in_info);

        return true;
    }

    public static boolean setXLimitLine(XLimitLineListBean in_info) {
        SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(
                ApplicationController.getInstance().getApplicationContext(),
                Key_XLimitLine);
        sharedPreferencesUtils.setObject(Key_XLimitLine, in_info);

        return true;
    }

    public static XLimitLineListBean getXLimitLine() {
        try {
            SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(
                    ApplicationController.getInstance().getApplicationContext(),
                    Key_XLimitLine);

            XLimitLineListBean loginmodel = sharedPreferencesUtils.getObject(Key_XLimitLine,
                    XLimitLineListBean.class);
            return loginmodel;
        } catch (Exception e) {
            LogUtil.ee(e);
        }
        return null;
    }


}
