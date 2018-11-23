package com.example.administrator.comassistant2.simulation.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.administrator.comassistant2.simulation.ComAssistantActivity;
import com.example.administrator.comassistant2.simulation.bean.PageFileQueueBean;
import com.example.administrator.comassistant2.simulation.tool.LogUtil;

import static com.example.administrator.comassistant2.simulation.tool.IConstant.Broad_DoPageChart;
import static com.example.administrator.comassistant2.simulation.tool.IConstant.IntentKey_PageChart_PageIndex;

public class BgmReceiver extends BroadcastReceiver {
    ComAssistantActivity jjActivity;

    public BgmReceiver(ComAssistantActivity in_activity) {
        jjActivity = in_activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            switch (intent.getAction()) {
                case Broad_DoPageChart:
                    PageFileQueueBean fileNum = (PageFileQueueBean) intent.getSerializableExtra(IntentKey_PageChart_PageIndex);
                    jjActivity.do4BroadPageChart(fileNum);
                    break;

            }
        } catch (Exception e) {
            LogUtil.ee(e);
        }
    }
}
