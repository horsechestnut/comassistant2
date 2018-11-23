package com.example.administrator.comassistant2.simulation.tool;

import com.example.administrator.comassistant2.simulation.bean.StatusOPS;

public class StatisIt {
    private int status_index = 0;

    public synchronized void ops(StatusOPS in_type) {
        switch (in_type) {
            case AddOne:
                status_index++;
                break;
            case Clear:
                status_index = 0;
                break;
        }
    }

    public int getStatus_index() {
        return status_index;
    }
}
