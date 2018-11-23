package com.example.administrator.comassistant2.simulation.tool;

/**
 * Created by dongdaxing on 2018/7/30.
 */

public class NoDoubleClick {
    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
