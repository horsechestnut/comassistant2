package com.example.administrator.comassistant2.simulation.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class XLimitLineListBean implements Serializable {
    private static final long serialVersionUID = 1L;

    public XLimitLineListBean() {
        lists = new HashMap<>();
    }

    private Map<String, XLimitLineBean> lists;

    public Map<String, XLimitLineBean> getLists() {
        return lists;
    }

    public void setLists(Map<String, XLimitLineBean> lists) {
        this.lists = lists;
    }
}
