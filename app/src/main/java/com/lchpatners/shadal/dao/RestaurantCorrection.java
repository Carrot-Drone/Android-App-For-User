package com.lchpatners.shadal.dao;

/**
 * Created by eunhyekim on 2015. 8. 30..
 */
public class RestaurantCorrection {
    private String uuid;
    private String major_correction;
    private String details;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMajor_correction() {
        return major_correction;
    }

    public void setMajor_correction(String major_correction) {
        this.major_correction = major_correction;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
