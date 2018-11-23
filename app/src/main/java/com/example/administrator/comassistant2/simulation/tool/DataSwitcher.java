package com.example.administrator.comassistant2.simulation.tool;

public class DataSwitcher {

    public static String switchToKM(int in_num) {
        if (in_num > 1000000) {
            return String.format("%.2f", (float) (in_num) / 1000000.0) + "M";
        } else {
            return String.format("%.1f", (float) (in_num) / 1000.0) + "K";
        }
    }
}
