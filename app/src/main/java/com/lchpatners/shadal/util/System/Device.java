package com.lchpatners.shadal.util.System;

/**
 * Created by YoungKim on 2015. 8. 28..
 */
public class Device {
    private int campus_id;
    private String uuid;
    private String device_type = "android";

    public int getCampus_id() {
        return campus_id;
    }

    public void setCampus_id(int campus_id) {
        this.campus_id = campus_id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
