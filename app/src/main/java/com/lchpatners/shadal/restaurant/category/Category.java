package com.lchpatners.shadal.restaurant.category;

import com.lchpatners.shadal.restaurant.Restaurant;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by YoungKim on 2015. 8. 24..
 */
public class Category extends RealmObject {
    @Ignore
    private int length = 8;

    @PrimaryKey
    private int id;

    private int campus_id;
    private String title;
    private RealmList<Restaurant> restaurants;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCampus_id() {
        return campus_id;
    }

    public void setCampus_id(int campus_id) {
        this.campus_id = campus_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RealmList<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(RealmList<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }
}
