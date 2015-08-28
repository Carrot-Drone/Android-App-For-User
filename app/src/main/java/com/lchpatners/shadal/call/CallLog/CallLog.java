package com.lchpatners.shadal.call.CallLog;

/**
 * Created by youngKim on 2015. 8. 26..
 */
public class CallLog {
    private int campus_id;
    private int category_id;
    private int restaurant_id;
    private String uuid;
    private int number_of_calls;
    private int has_recent_call;

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

    public int getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(int restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getNumber_of_calls() {
        return number_of_calls;
    }

    public void setNumber_of_calls(int number_of_calls) {
        this.number_of_calls = number_of_calls;
    }

    public int getHas_recent_call() {
        return has_recent_call;
    }

    public void setHas_recent_call(int has_recent_call) {
        this.has_recent_call = has_recent_call;
    }
}
