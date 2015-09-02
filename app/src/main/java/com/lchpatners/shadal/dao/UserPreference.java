package com.lchpatners.shadal.dao;

/**
 * Created by YoungKim on 2015. 8. 27..
 */
public class UserPreference {
    private int preference;
    private String uuid;

    public int getPreference() {
        return preference;
    }

    public void setPreference(int preference) {
        this.preference = preference;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }
}
