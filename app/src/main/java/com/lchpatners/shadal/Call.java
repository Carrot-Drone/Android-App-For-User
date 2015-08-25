package com.lchpatners.shadal;

import android.content.Context;
import android.database.Cursor;

import java.util.Date;

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

    public static void updateCallLog(Context context, Restaurant restaurant) {
        DatabaseHelper DBhelper = DatabaseHelper.getInstance(context);
        Server server = new Server(context);
        Date date = new Date();
        int categoryServerId = DBhelper.getCategoryServerIdFromId(restaurant.getCategoryId());
        if (!DBhelper.hasRecent(date.getTime(), restaurant.getRestaurantId())) {
            DBhelper.insertRecentCalls(restaurant.getRestaurantId(), restaurant.getCategoryId());
            server.sendCallLog(restaurant, categoryServerId, 0);
        } else {
            server.sendCallLog(restaurant, categoryServerId, 1);
        }

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
