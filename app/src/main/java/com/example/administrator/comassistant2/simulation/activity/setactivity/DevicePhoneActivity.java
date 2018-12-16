package com.example.administrator.comassistant2.simulation.activity.setactivity;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.comassistant2.R;

public class DevicePhoneActivity extends AppCompatActivity {
    TextView txt;
    TextView copytxt;
    String tag = "GXT";
    String hhf = System.getProperty("line.separator");
    String deviceinfo = "";

    public static void switchToThis(Activity in_context) {
        Intent intent = new Intent(in_context,
                DevicePhoneActivity.class);
        in_context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicephone);
        txt = findViewById(R.id.testtext);
        copytxt = findViewById(R.id.btncopy);
        handlePhoneDevice();
        copytxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cmb = (ClipboardManager) DevicePhoneActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(deviceinfo);
                Toast.makeText(DevicePhoneActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void handlePhoneDevice() {
        try {
            DisplayMetrics display = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(display);
            int height4dp = Integer.valueOf((int) (display.heightPixels / display.density));
            int width4dp = Integer.valueOf((int) (display.widthPixels / display.density));

            Log.i(tag, "DPI 为 " + display.densityDpi);


            PackageManager pm = this.getApplicationContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(this.getApplicationContext().getPackageName(), 0);

            Log.i(tag, "APP version: " + pi.versionName);
            StringBuffer phone = new StringBuffer(200);
            phone.append("手机型号: ").append(android.os.Build.MODEL).append(hhf).append(hhf);
            phone.append("PX分辨率 : ").append(display.heightPixels).append("X").append(display.widthPixels).append(hhf).append(hhf);
            phone.append("DP分辨率 : ").append(height4dp).append("X").append(width4dp).append(hhf).append(hhf);
            phone.append("DPI : ").append(display.densityDpi).append(hhf).append(hhf);
            phone.append("Density : ").append(display.density).append(hhf).append(hhf);
            PackageInfo info = null;
            PackageManager manager = getPackageManager();
            info = manager.getPackageInfo(getPackageName(), 0);
            phone.append("版本号 : ").append(info.versionName).append(hhf).append(hhf);
            deviceinfo = phone.toString();
            txt.setText(phone.toString());

        } catch (Exception e) {
            Log.i(tag, e.toString());
        }
    }
}
