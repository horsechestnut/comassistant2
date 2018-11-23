package com.example.administrator.comassistant2;

import android.app.Application;

import com.example.administrator.comassistant2.simulation.tool.LogUtil;

public class ApplicationController extends Application {
    private static ApplicationController sInstance;

    public static synchronized ApplicationController getInstance() {
        return sInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (sInstance == null) {
            sInstance = this;
        }
        LogUtil.ii("自定义上下文");
    }
}
