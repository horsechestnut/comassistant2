package com.example.administrator.comassistant2.simulation.tool;

import android.content.Context;

import com.example.administrator.comassistant2.ApplicationController;

public class DataSwitcher {

    public static String switchToKM(int in_num) {
        if (in_num > 1000000) {
            return String.format("%.2f", (float) (in_num) / 1000000.0) + "M";
        } else {
            return String.format("%.1f", (float) (in_num) / 1000.0) + "K";
        }
    }

    public static float getDimenPxFloat(int in_dimen) {
        Context ctx = ApplicationController.getInstance().getApplicationContext();
        float cc=  ctx.getResources().getDimension(in_dimen);
        LogUtil.ii("dimen 尺寸 "+cc);
        return  cc;
    }

}
