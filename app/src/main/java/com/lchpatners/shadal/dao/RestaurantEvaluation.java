package com.lchpatners.shadal.dao;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by YoungKim on 2015. 8. 26..
 */
public class RestaurantEvaluation extends RealmObject{
    @PrimaryKey
    private int restaurant_id;

    private int evaluation;

    public int getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(int restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public int getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(int evaluation) {
        this.evaluation = evaluation;
    }
}
