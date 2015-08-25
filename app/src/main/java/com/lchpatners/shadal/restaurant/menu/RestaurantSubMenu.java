package com.lchpatners.shadal.restaurant.menu;

import io.realm.RealmObject;

/**
 * Created by YoungKim on 2015. 8. 24..
 */
public class RestaurantSubMenu extends RealmObject {
    private String name;
    private int price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
