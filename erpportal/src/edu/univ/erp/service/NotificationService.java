package edu.univ.erp.service;

import edu.univ.erp.data.SettingsData;

public class NotificationService {

    private final SettingsData data = new SettingsData();

    public String get() {
        String msg = data.getMsg();
        return (msg == null) ? "" : msg;
    }

    public boolean post(String msg) {
        if (msg == null) msg = "";
        return data.setMsg(msg);
    }
}
