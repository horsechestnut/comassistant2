package com.example.administrator.comassistant2.simulation.tool;

public class TimeStatisIt {
    int nums = 0;
    int avagetime = 0;
    long time1 = -1;
    long time2 = -1;

    long totalTime;
    String jjBus;
    long jjThreadId;

    public TimeStatisIt(String in_bus) {
        jjBus = in_bus;
    }

    public void startIt() {
        synchronized (TimeStatisIt.class) {
            time1 = System.currentTimeMillis();
        }
    }

    public void endIt() {
        synchronized (TimeStatisIt.class) {
            if (time1 == -1) {
                return;
            }
            jjThreadId = Thread.currentThread().getId();
            totalTime += (System.currentTimeMillis() - time1);
            nums++;
            time1 = -1;
        }
    }

    public void clear() {
        synchronized (TimeStatisIt.class) {
            nums = 0;
            time1 = -1;
            time2 = -1;
            totalTime = 0;
        }
    }

    public String showIt() {
        long average = nums == 0 ? 0 : totalTime / nums;
        String rlt = jjBus + " " + jjThreadId + " 总耗时 " + totalTime + " 次数 " + nums + " 均耗时 " + average;
        return rlt;
    }
}
