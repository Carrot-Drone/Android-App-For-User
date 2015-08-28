package com.lchpatners.shadal.recommend;

/**
 * Created by eunhyekim on 2015. 8. 26..
 */
public class RecommendedRestaurantInfo {
    private int id;
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
