package com.lchpatners.shadal;

import android.database.Cursor;

/**
 * Created by eunhyekim on 2015. 7. 13..
 */
public class Call {
    private String restaurantName;
    private int restaurantId;
    private int count;

    public Call(Cursor cursor) {
        restaurantId = cursor.getInt(cursor.getColumnIndex(cursor.getColumnName(0)));
        restaurantName = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)));
        count = cursor.getInt(cursor.getColumnIndex(cursor.getColumnName(2)));

    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


}
