package com.lchpatners.shadal.dao;

/**
 * Created by eunhyekim on 2015. 8. 30..
 */
public class Request {
    String uuid;
    String email;
    String details;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
