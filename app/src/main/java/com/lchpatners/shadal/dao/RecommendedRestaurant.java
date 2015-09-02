package com.lchpatners.shadal.dao;

/**
 * Created by eunhyekim on 2015. 8. 26..
 */
public class RecommendedRestaurant {
    String reason;
    Restaurant mRestaurant;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Restaurant getRestaurant() {
        return mRestaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        mRestaurant = restaurant;
    }
}
