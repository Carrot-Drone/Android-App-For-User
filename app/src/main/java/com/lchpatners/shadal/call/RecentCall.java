package com.lchpatners.shadal.call;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Created by youngkim on 2015. 8. 22..
 */
public class RecentCall extends RealmObject {
    @Index
    private int restaurant_id;
    private Date resent_call_date;

    private String restaurant_name;
    private int campus_id;
    private int category_id;

    public int getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(int restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public Date getResent_call_date() {
        return resent_call_date;
    }

    public void setResent_call_date(Date resent_call_date) {
        this.resent_call_date = resent_call_date;
    }

    public int getCampus_id() {
        return campus_id;
    }

    public void setCampus_id(int campus_id) {
        this.campus_id = campus_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }
}
