package com.lchpatners.shadal.restaurant.flyer;

import io.realm.RealmObject;

/**
 * Created by YoungKim on 2015. 8. 24..
 */
public class Flyer extends RealmObject {
    private String url;

    public Flyer() {
    }

    public Flyer(String stringValue) {
        this.url = stringValue;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
