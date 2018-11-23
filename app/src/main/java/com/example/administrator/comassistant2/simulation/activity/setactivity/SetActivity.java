package com.example.administrator.comassistant2.simulation.activity.setactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.dld.view.SegmentedControlItem;
import com.dld.view.SegmentedControlView;
import com.example.administrator.comassistant2.R;
import com.example.administrator.comassistant2.simulation.filesave.LocalSeter;
import com.example.administrator.comassistant2.simulation.tool.IConstant;

import java.util.ArrayList;
import java.util.List;

//设置页面
public class SetActivity extends Activity implements IConstant {
    SegmentedControlView pagechart_scv;
    SegmentedControlView log_scv;
    SegmentedControlView pagetype_scv;
    SegmentedControlView hori_scv;
    SegmentedControlView file_scv;
    SegmentedControlView mark_scv;

    public static void switchToThis(Activity in_context) {
        Intent intent = new Intent(in_context,
                SetActivity.class);
        in_context.startActivityForResult(intent, Result_SetActivity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_activity);
        initViews();
    }

    private void initViews() {
        pagechart_scv = findViewById(R.id.pagechart_scv);
        log_scv = findViewById(R.id.log_scv);
        pagetype_scv = findViewById(R.id.pagetype_scv);
        hori_scv = findViewById(R.id.hori_scv);
        file_scv = findViewById(R.id.file_scv);
        mark_scv = findViewById(R.id.mark_scv);
        addItems(pagechart_scv, "线状图", "柱状图");
        addItems(log_scv, "OFF", "ON");
        addItems(pagetype_scv, "均值", "中位数");
        addItems(hori_scv, "OFF", "ON");
        addItems(mark_scv, "OFF", "ON");
        addItems(file_scv, "4K", "400K", "4000K");
        if (LocalSeter.getXLimitLines() != null) {
            mark_scv.setSelectedItem(1);
        } else {
            mark_scv.setSelectedItem(0);
        }
        mark_scv.setOnSegItemClickListener(new SegmentedControlView.OnSegItemClickListener() {
            @Override
            public void onItemClick(SegmentedControlItem item, int position) {
                LocalSeter.clearXLimitLines();
            }
        });


    }

    private void addItems(SegmentedControlView in_scv, String... args) {
        List<SegmentedControlItem> items = new ArrayList<>();
        for (String item : args
                ) {
            items.add(new SegmentedControlItem(item));
        }
        in_scv.addItems(items);
    }
}
